package ch.ethz.rse.numerical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Manager;
import apron.MpqScalar;
import apron.Polka;
import apron.Tcons1;
import apron.Texpr1BinNode;
import apron.Texpr1CstNode;
import apron.Texpr1Intern;
import apron.Texpr1Node;
import apron.Texpr1UnNode;
import apron.Texpr1VarNode;
import ch.ethz.rse.VerificationProperty;
import ch.ethz.rse.pointer.StoreInitializer;
import ch.ethz.rse.pointer.PointsToInitializer;
import ch.ethz.rse.utils.Constants;
import ch.ethz.rse.verify.EnvironmentGenerator;
import soot.ArrayType;
import soot.DoubleType;
import soot.Local;
import soot.RefType;
import soot.SootHelper;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.MulExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

// added imports
import java.util.stream.Collectors;

/**
 * Convenience class running a numerical analysis on a given {@link SootMethod}
 */
public class NumericalAnalysis extends ForwardBranchedFlowAnalysis<NumericalStateWrapper> {

	private static final Logger logger = LoggerFactory.getLogger(NumericalAnalysis.class);

	private final SootMethod method;

	/**
	 * the property we are verifying
	 */
	private final VerificationProperty property;

	/**
	 * the pointer analysis result we are verifying
	 */
	private final PointsToInitializer pointsTo;

	/**
	 * all store initializers encountered until now
	 */
	private Set<StoreInitializer> alreadyInit;

	/**
	 * number of times this loop head was encountered during analysis
	 */
	private HashMap<Unit, IntegerWrapper> loopHeads = new HashMap<Unit, IntegerWrapper>();
	/**
	 * Previously seen abstract state for each loop head
	 */
	private HashMap<Unit, NumericalStateWrapper> loopHeadState = new HashMap<Unit, NumericalStateWrapper>();

	/**
	 * Numerical abstract domain to use for analysis: Convex polyhedra
	 */
	public final Manager man = new Polka(true);

	public final Environment env;

	/**
	 * We apply widening after updating the state at a given merge point for the
	 * {@link WIDENING_THRESHOLD}th time
	 */
	private static final int WIDENING_THRESHOLD = 6;

	/**
	 * 
	 * @param method   method to analyze
	 * @param property the property we are verifying
	 */
	public NumericalAnalysis(SootMethod method, VerificationProperty property, PointsToInitializer pointsTo) {
		super(SootHelper.getUnitGraph(method));

		UnitGraph g = SootHelper.getUnitGraph(method);

		this.property = property;

		this.pointsTo = pointsTo;
		
		this.method = method;

		this.alreadyInit = new HashSet<StoreInitializer>();

		this.env = new EnvironmentGenerator(method, pointsTo).getEnvironment();

		// initialize counts for loop heads
		for (Loop l : new LoopNestTree(g.getBody())) {
			loopHeads.put(l.getHead(), new IntegerWrapper(0));
		}

		// perform analysis by calling into super-class
		logger.info("Analyzing {} in {}", method.getName(), method.getDeclaringClass().getName());
		doAnalysis(); // calls newInitialFlow, entryInitialFlow, merge, flowThrough, and stops when a fixed point is reached
	}

	/**
	 * Report unhandled instructions, types, cases, etc.
	 * 
	 * @param task description of current task
	 * @param what
	 */
	public static void unhandled(String task, Object what, boolean raiseException) {
		String description = task + ": Can't handle " + what.toString() + " of type " + what.getClass().getName();

		if (raiseException) {
			logger.error("Raising exception " + description);
			throw new UnsupportedOperationException(description);
		} else {
			logger.error(description);

			// print stack trace
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = 1; i < stackTrace.length; i++) {
				logger.error(stackTrace[i].toString());
			}
		}
	}

	@Override
	protected void copy(NumericalStateWrapper source, NumericalStateWrapper dest) {
		source.copyInto(dest);
	}

	@Override
	protected NumericalStateWrapper newInitialFlow() {
		// should be bottom (only entry flows are not bottom originally)
		return NumericalStateWrapper.bottom(man, env);
	}

	@Override
	protected NumericalStateWrapper entryInitialFlow() {
		// state of entry points into function
		NumericalStateWrapper ret = NumericalStateWrapper.top(man, env);

		// TODO: MAYBE FILL THIS OUT

		return ret;
	}

	@Override
	protected void merge(Unit succNode, NumericalStateWrapper w1, NumericalStateWrapper w2, NumericalStateWrapper w3){
		// merge the two states from w1 and w2 and store the result into w3
		logger.debug("in merge: " + succNode);

		logger.info("We are using merge"); 

		IntegerWrapper loop_count = loopHeads.get(succNode); 
		
		// loopHeads only gets initialized for loops - if we are not in a loop it will not be initialized and thus null. In this case, merge normally 
		if (loop_count == null){
			try {
				logger.debug("Case loop_count == null");
				logger.debug("w1 = " + w1.toString());
				logger.debug("w2 = " + w2.toString());
				w3 = w1.join(w2);
				logger.debug("w3 = " + w3.toString());
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		} else if (loop_count.value<WIDENING_THRESHOLD){
			// WIDENING_THRESHOLD not reached - merge, increase counter and save new state
			loop_count.value+=1; 
			try {
				w3 = w1.join(w2);
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			loopHeadState.put(succNode, w3); 
			// Not sure if the line above works. if not, use new IntegerWrapper(loop_count.value+1)
			
		} else {
			// Widening threshold was reached - widen
			// First, calculate another merge
			NumericalStateWrapper w3_new = null;
			try {
				w3_new = w1.join(w2);
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			NumericalStateWrapper w3_old = loopHeadState.get(succNode); 

			// Then, compare the new and the old w3 and widen appropriately 
			Abstract1 w3_old_abstr = w3_old.get(); 
			Abstract1 w3_new_abstr = w3_new.get(); 
			Abstract1 w3_abstr = null;
			try {
				w3_abstr = w3_old_abstr.widening(man, w3_new_abstr);
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			w3 = new NumericalStateWrapper(man, w3_abstr); 

			// Don't know if this is actually necessary
			loopHeadState.put(succNode, w3); 
			loop_count.value+=1; 

			
		}
	}

	@Override
	protected void merge(NumericalStateWrapper src1, NumericalStateWrapper src2, NumericalStateWrapper trg) {
		// this method is never called, we are using the other merge instead
		throw new UnsupportedOperationException();
	}

	@Override
	protected void flowThrough(NumericalStateWrapper inWrapper, Unit op, List<NumericalStateWrapper> fallOutWrappers,
			List<NumericalStateWrapper> branchOutWrappers) {
		logger.debug(inWrapper + " " + op + " => ?");

		Stmt s = (Stmt) op;

		// fallOutWrapper is the wrapper for the state after running op,
		// assuming we move to the next statement. Do not overwrite
		// fallOutWrapper, but use its .set method instead
		assert fallOutWrappers.size() <= 1;
		NumericalStateWrapper fallOutWrapper = null;
		if (fallOutWrappers.size() == 1) {
			fallOutWrapper = fallOutWrappers.get(0);
			inWrapper.copyInto(fallOutWrapper);
		}

		// branchOutWrapper is the wrapper for the state after running op,
		// assuming we follow a conditional jump. It is therefore only relevant
		// if op is a conditional jump. In this case, (i) fallOutWrapper
		// contains the state after "falling out" of the statement, i.e., if the
		// condition is false, and (ii) branchOutWrapper contains the state
		// after "branching out" of the statement, i.e., if the condition is
		// true.
		assert branchOutWrappers.size() <= 1;
		NumericalStateWrapper branchOutWrapper = null;
		if (branchOutWrappers.size() == 1) {
			branchOutWrapper = branchOutWrappers.get(0);
			inWrapper.copyInto(branchOutWrapper);
		}

		try {
			if (s instanceof DefinitionStmt) {
				// handle assignment

				DefinitionStmt sd = (DefinitionStmt) s;
				Value left = sd.getLeftOp();
				Value right = sd.getRightOp();

				// We are not handling these cases:
				if (!(left instanceof JimpleLocal)) {
					unhandled("Assignment to non-local variable", left, true);
				} else if (left instanceof JArrayRef) {
					unhandled("Assignment to a non-local array variable", left, true);
				} else if (left.getType() instanceof ArrayType) {
					unhandled("Assignment to Array", left, true);
				} else if (left.getType() instanceof DoubleType) {
					unhandled("Assignment to double", left, true);
				} else if (left instanceof JInstanceFieldRef) {
					unhandled("Assignment to field", left, true);
				}

				if (left.getType() instanceof RefType) {
					// assignments to references are handled by pointer analysis
					// no action necessary
				} else {
					// handle assignment
					handleDef(fallOutWrapper, left, right);
				}

			} else if (s instanceof JIfStmt) {
				// handle if

				// TODO: FILL THIS OUT
				logger.debug("Entered case s instanceof JIfStmt");
				Value cond = ((JIfStmt) s).getCondition();
				Abstract1 e_branch = branchOutWrapper.get();
				Abstract1 e_fall = fallOutWrapper.get();

				int bool_op_true;
				int bool_op_false;
				if (cond instanceof JEqExpr) {
					bool_op_true = Tcons1.DISEQ;
					bool_op_false = Tcons1.EQ;
				} else if(cond instanceof JGeExpr || cond instanceof JLeExpr) {
					bool_op_true = Tcons1.SUP;
					bool_op_false = Tcons1.SUPEQ;
				} else if (cond instanceof JGtExpr || cond instanceof JLtExpr) {
					bool_op_true = Tcons1.SUPEQ;
					bool_op_false = Tcons1.SUP;
				} else {
					bool_op_true = Tcons1.EQ;
					bool_op_false = Tcons1.DISEQ;
				}
				Texpr1Node expr = normalFormExpr(cond); 
				Tcons1 cond_true = new Tcons1(env, bool_op_true, expr);;
				Tcons1 cond_false = new Tcons1(env, bool_op_false, new Texpr1UnNode(Texpr1UnNode.OP_NEG, expr)); 

				Abstract1 fallOutConstr =e_fall.meetCopy(man, cond_true);
				Abstract1 branchOutConstr =e_branch.meetCopy(man, cond_false);
				branchOutWrapper.set(branchOutConstr);
				fallOutWrapper.set(fallOutConstr);
				
			} else if (s instanceof JInvokeStmt) {
				// handle invocations
				JInvokeStmt jInvStmt = (JInvokeStmt) s;
				InvokeExpr invokeExpr = jInvStmt.getInvokeExpr();
				if (invokeExpr instanceof JVirtualInvokeExpr) {
					handleInvoke(jInvStmt, fallOutWrapper);
				} else if (invokeExpr instanceof JSpecialInvokeExpr) {
					// initializer for object
					handleInitialize(jInvStmt, fallOutWrapper);
				} else {
					unhandled("Unhandled invoke statement", invokeExpr, true);
				}
			} else if (s instanceof JGotoStmt) {
				// safe to ignore
			} else if (s instanceof JReturnVoidStmt) {
				// safe to ignore
			} else {
				unhandled("Unhandled statement", s, true);
			}

			// log outcome
			if (fallOutWrapper != null) {
				logger.debug(inWrapper.get() + " " + s + " =>[fallout] " + fallOutWrapper);
			}
			if (branchOutWrapper != null) {
				logger.debug(inWrapper.get() + " " + s + " =>[branchout] " + branchOutWrapper);
			}

		} catch (ApronException e) {
			throw new RuntimeException(e);
		}
	}

	public void handleInvoke(JInvokeStmt jInvStmt, NumericalStateWrapper fallOutWrapper) throws ApronException {
		// TODO: MAYBE FILL THIS OUT
		if (this.property == VerificationProperty.FITS_IN_RESERVE) {
			// TODO: MAYBE FILL THIS OUT

		}
	}

	public void handleInitialize(JInvokeStmt jInvStmt, NumericalStateWrapper fallOutWrapper) throws ApronException {
		// TODO: MAYBE FILL THIS OUT
	}

	// returns state of in after assignment
	private void handleDef(NumericalStateWrapper outWrapper, Value left, Value right) throws ApronException {
		// TODO: FILL THIS OUT
		logger.debug("entered handleDef");
		logger.debug("outWrapper = " + outWrapper.toString());
		Abstract1 e = outWrapper.get();
		logger.debug("e = " + e.toString());

		Texpr1Node right_expr = exprOfValue(right);
		// Abstract1 e_out = e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), e);
		Abstract1 e_out = e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), null);
		logger.debug("e_out = " + e_out.toString());
		// outWrapper.set(e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), e));
		outWrapper.set(e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), null));
		logger.debug("outWrapper after handleDef: " + outWrapper.toString());
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods
	private Texpr1Node exprOfValue(Value val) {
		if(val instanceof IntConstant) {
			return new Texpr1CstNode(new MpqScalar(((IntConstant) val).value));
		} else if(val instanceof JimpleLocal) {
			return new Texpr1VarNode(((JimpleLocal) val).getName());
		} else if (val instanceof AbstractBinopExpr) {
			Value op1 = ((AbstractBinopExpr) val).getOp1();
			Value op2 = ((AbstractBinopExpr) val).getOp2();
			Texpr1Node op1_exp = exprOfValue(op1);
			Texpr1Node op2_exp = exprOfValue(op2);
			int op;
			if(val instanceof JAddExpr) {
				op = Texpr1BinNode.OP_ADD; 
			} else if (val instanceof JSubExpr) {
				op = Texpr1BinNode.OP_SUB; 
			} else {
				op = Texpr1BinNode.OP_MUL; 
			}
			return new Texpr1BinNode(op, op1_exp, op2_exp);
		} else {
			String arg_name = this.method.getBytecodeParms();
			return new Texpr1VarNode(arg_name);
		}
	}

	private Texpr1Node normalFormExpr(Value val) {
		assert(val instanceof AbstractBinopExpr);
		Value op1 = ((AbstractBinopExpr) val).getOp1();
		Value op2 = ((AbstractBinopExpr) val).getOp2();
		if (val instanceof JGeExpr || val instanceof JGtExpr) {
			Value tmp = op1;
			op1 = op2;
			op2 = tmp;
		}	
		return new Texpr1BinNode(Texpr1BinNode.OP_SUB,
								 Texpr1BinNode.RTYPE_INT, 
								 Texpr1BinNode.RDIR_ZERO,
								 exprOfValue(op1),
								 exprOfValue(op2));
	}

}
