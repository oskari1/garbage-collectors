package ch.ethz.rse.numerical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

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

	public boolean non_negative_satisfied = true;
	public boolean fits_in_trolley_satisfied = true;
	public boolean fits_in_reserve_satisfied = true;
	private HashMap<Loop, List<Stmt>> stmts_per_loop;
	private HashMap<JInvokeStmt, LoopAnalysis> loop_analysis_of_invoke;

	// private AlreadyInitMap alreadyInitMap;

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

		//initialize stmts_per_loop with sufficient capacity
		LoopNestTree loops = new LoopNestTree(g.getBody());
		this.stmts_per_loop = new HashMap<Loop, List<Stmt>>(loops.size()); 
		int total_nr_stmts = method.getActiveBody().getUnits().size();
		this.loop_analysis_of_invoke = new HashMap<JInvokeStmt, LoopAnalysis>(total_nr_stmts); 
		// initialize counts for loop heads
		for (Loop l : new LoopNestTree(g.getBody())) {
			loopHeads.put(l.getHead(), new IntegerWrapper(0));
			// initialize stmts_per_loop
			stmts_per_loop.put(l, l.getLoopStatements());
		}
		Iterator<Unit> i = g.iterator();
		while(i.hasNext()) {
			Stmt s = (Stmt) i.next();
			if(s instanceof JInvokeStmt) {
				JInvokeStmt jInvStmt = (JInvokeStmt) s;
				InvokeExpr invokeExpr = jInvStmt.getInvokeExpr();
				if (invokeExpr instanceof JVirtualInvokeExpr) {
					boolean contained_in_loop = false;
					for(Loop l : stmts_per_loop.keySet()) {
						if(l.getLoopStatements().contains(s)) {
							loop_analysis_of_invoke.put(jInvStmt, new LoopAnalysis(l, jInvStmt, stmts_per_loop));
							contained_in_loop = true;
						} 
					}
					if(!contained_in_loop) {
						loop_analysis_of_invoke.put(jInvStmt, new LoopAnalysis(jInvStmt));
					}
				}
			}
		}
		for (Loop l : new LoopNestTree(g.getBody())) {
			for(Stmt s : l.getLoopStatements()) {
				
			}
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
		for(StoreInitializer s : pointsTo.getInitializers(this.method)) {
			logger.debug("Adding the following store-init to the env ");
			logger.debug(s.getUniqueLabel());
			try {
				Abstract1 state = ret.get();
				ret.set(state.assignCopy(man, s.getUniqueLabel(), new Texpr1Intern(env, new Texpr1CstNode(new MpqScalar(0))), null));
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	@Override
	protected void merge(Unit succNode, NumericalStateWrapper w1, NumericalStateWrapper w2, NumericalStateWrapper w3){
		// merge the two states from w1 and w2 and store the result into w3, by using .copyInto(w3) 
		// logger.debug("in merge: " + succNode);
	
		IntegerWrapper loop_count = loopHeads.get(succNode); 
		if (loop_count != null){
			// logger.info("We are using merge, with Loop_count " + loop_count.value);
		} else {
			// logger.info("We are using merge, with None Loop_count");
		}


		// loopHeads only gets initialized for loops - if we are not in a loop it will not be initialized and thus null. In this case, merge normally 
		if (loop_count == null){
			try {
				NumericalStateWrapper w3_new = w1.join(w2);
				w3_new.copyInto(w3);
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			// logger.debug("Not in loop: Merged " + w1.get() + " and " + w2.get() + " into " + w3.get());


		} else if (loop_count.value<WIDENING_THRESHOLD){
			// WIDENING_THRESHOLD not reached - merge, increase counter and save new state
			// logger.debug("WIDENING THRESHOLD NOT YET REACHED");
			loop_count.value+=1; 
			try {
				NumericalStateWrapper w3_new = w1.join(w2);
				w3_new.copyInto(w3);
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			loopHeadState.put(succNode, w3); 
			// Not sure if the line above works. if not, use new IntegerWrapper(loop_count.value+1)
			// logger.debug("In loop, not widended: Merged " + w1.get() + " and " + w2.get() + " into " + w3.get());

			
		} else {
		// Widening threshold was reached - widen
		// First, calculate another merge
		// logger.debug("WIDENING THRESHOLD REACHED");
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
			NumericalStateWrapper w3_newest = new NumericalStateWrapper(man, w3_abstr); 
			w3_newest.copyInto(w3);
			// logger.debug("In widended: Merged " + w1.get() + " and " + w2.get() + " into " + w3.get());

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
		// logger.debug(inWrapper + " " + op + " => ?");

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
			// logger.debug("copied " + inWrapper + " into branchOutWrapper");
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
				// logger.debug("Entered case s instanceof JIfStmt");
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
				Tcons1 cond_true = new Tcons1(env, bool_op_true, expr);
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
					// logger.debug("entered instanceof JVirtualInvokeExpr");
					handleInvoke(jInvStmt, fallOutWrapper);
				} else if (invokeExpr instanceof JSpecialInvokeExpr) {
					// initializer for object
					handleInitialize(jInvStmt, fallOutWrapper);
				} else {
					unhandled("Unhandled invoke statement", invokeExpr, true);
				}
			} else if (s instanceof JGotoStmt) {
				// logger.debug("entered flowThrough with JGotoStmt");
				// safe to ignore
			} else if (s instanceof JReturnVoidStmt) {
				// safe to ignore
			} else {
				unhandled("Unhandled statement", s, true);
			}

			// log outcome
			if (fallOutWrapper != null) {
				// logger.debug(inWrapper.get() + " " + s + " =>[fallout] " + fallOutWrapper);
				// logger.debug("type of s is " + s.getClass().getName());
				if(s instanceof JInvokeStmt) {
					JInvokeStmt jInvStmt = (JInvokeStmt) s;
					InvokeExpr invokeExpr = jInvStmt.getInvokeExpr();
					if(invokeExpr instanceof JVirtualInvokeExpr) {
						String var = "i0";
						// logger.debug("Bound for " + var + ": " + fallOutWrapper.get().getBound(man, var).toString());
						var = "i1";
						// logger.debug("Bound for " + var + ": " + fallOutWrapper.get().getBound(man, var).toString());
					}
				}
				//
			}
			if (branchOutWrapper != null) {
				// logger.debug(inWrapper.get() + " " + s + " =>[branchout] " + branchOutWrapper);
				if(s instanceof JVirtualInvokeExpr) {
					// String var = ((DefinitionStmt) s).getLeftOp().toString();
					String var = "i0";
					// logger.debug("Bound for " + var + ": " + fallOutWrapper.get().getBound(man, var).toString());
				}
			}

		} catch (ApronException e) {
			throw new RuntimeException(e);
		}
	}

	public void handleInvoke(JInvokeStmt jInvStmt, NumericalStateWrapper fallOutWrapper) throws ApronException {
		// TODO: MAYBE FILL THIS OUT
		if(!fallOutWrapper.get().isBottom(man)) {
			if (this.property == VerificationProperty.FITS_IN_RESERVE || this.property == VerificationProperty.FITS_IN_TROLLEY) {
				// TODO: MAYBE FILL THIS OUT
				Value store_reference = (Value) jInvStmt.getUseBoxes().get(1).getValue();
				Value arg = jInvStmt.getInvokeExpr().getArg(0);
				for(StoreInitializer s : pointsTo.pointsTo((Local) store_reference)) {
					if(alreadyInit.contains(s)) {
					// if(alreadyInitMap.get_set_of(jInvStmt).contains(s)) {
						MpqScalar delivered_amt = upper_bound_of(arg, fallOutWrapper); 
						// alreadyInitMap.receiveat(jInvStmt, s, delivered_amt);
						if(this.property == VerificationProperty.FITS_IN_RESERVE) {
							// s.receive(delivered_amt);
							if(delivered_amt.isInfty() == 1) {
								fits_in_reserve_satisfied = false;
							} else if(delivered_amt.isInfty() == 0 && fits_in_reserve_satisfied) {
								// this variable stores, in how many loops jInvStmt is contained in,
								// if it isn't in a loop, loop_depth is 0
								// if it's in a nested for-loop it's 2, etc.
								LoopAnalysis loop_analysis = loop_analysis_of_invoke.get(jInvStmt); 
								int loop_depth = loop_analysis.get_loop_depth();
								// logger.debug("Computing loop depth of statement: " + jInvStmt);
								// logger.debug("computed loop depth: " + loop_depth);
								if(loop_depth == 0) {
									// construct the constraint rcvd_amt = rcvd_amt + delivered_amt and add it to the environment
									Texpr1Node op1_exp = new Texpr1VarNode(s.getUniqueLabel());
									Texpr1Node op2_exp = new Texpr1CstNode(delivered_amt);
									int op = Texpr1BinNode.OP_ADD;
									Texpr1Node updated_expr = new Texpr1BinNode(op, op1_exp, op2_exp);
									Abstract1 e = fallOutWrapper.get();
									fallOutWrapper.set(e.assignCopy(man, s.getUniqueLabel(), new Texpr1Intern(env, updated_expr), null));
									// check if received amount is still ok
									MpqScalar received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, s.getUniqueLabel()).sup();
									if(received_amt.isInfty() != 0 || !s.checkFitsInReserve(received_amt)) {
										fits_in_reserve_satisfied = false;
									}
								} else if(loop_depth == 1) {
									// if get_delivery is contained in exactly one loop (not in nested loop)
									// then we can find the loop conditional and construct the equation for 
									// relating the loop variable with the received amount
									// for this we need to visit this node at least twice to have all 
									// information necessary for computing the constraint
									// make use of Apron's forgetCopy to replace the old constraint on the 
									// received amount
									if(loopHeads.get(loop_analysis.loop_head).value == 1) {
										// the first time we visit this call to get_delivery
										MpqScalar received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, s.getUniqueLabel()).sup();
										if(received_amt.isInfty() == 0) {
											logger.debug("initially received amount is (iteration 1): " + received_amt);
											loop_analysis.set_init_rcvd_amt_map(alreadyInit.size());
											loop_analysis.set_rhs_expr_of_map(alreadyInit.size());
											loop_analysis.set_init_rcvd_amt(received_amt, s.getUniqueLabel());
											loop_analysis.set_rcvd_amt_var(s.getUniqueLabel());
											loop_analysis.set_arg(delivered_amt);
											String loop_var_name = loop_analysis.get_loop_var_name();
											logger.debug("loop variable is " + loop_var_name);
											if(loop_var_name != null) {
												MpqScalar init_loop_var;
												if(loop_analysis.is_growing_loop_variable()) {
													init_loop_var = (MpqScalar) fallOutWrapper.get().getBound(man, loop_var_name).sup();
												} else {
													init_loop_var = (MpqScalar) fallOutWrapper.get().getBound(man, loop_var_name).inf();
												}
												if(init_loop_var.isInfty() == 0) {
													logger.debug("the initial upper bound is " + init_loop_var);
													loop_analysis.set_init_val_loop_var(int_of(init_loop_var));
													loop_analysis.set_loop_var_name(loop_var_name);
												} else {
													fits_in_reserve_satisfied = false;
												}
											} else {
												fits_in_reserve_satisfied = false;
											}
										} else {
											fits_in_reserve_satisfied = false;
										}
									} else if(loopHeads.get(loop_analysis.loop_head).value == 2 && fits_in_reserve_satisfied) {
										String loop_var_name = loop_analysis.get_loop_var_name();
										logger.debug("loop variable is " + loop_var_name);
										MpqScalar second_loop_var;
										if(loop_analysis.is_growing_loop_variable()) {
											second_loop_var = (MpqScalar) fallOutWrapper.get().getBound(man, loop_var_name).sup();
										} else {
											second_loop_var = (MpqScalar) fallOutWrapper.get().getBound(man, loop_var_name).inf();
										}
										if(second_loop_var.isInfty() == 0) {
											loop_analysis.set_second_val_loop_var(int_of(second_loop_var));
											Abstract1 e = fallOutWrapper.get();
											logger.debug("fallOutWrapper is initially " + fallOutWrapper);
											// e.forget(man, s.getUniqueLabel(), false);
											logger.debug("fallOutWrapper is after forgetting " + e);
											loop_analysis.set_rhs_expr(s.getUniqueLabel());
											// evaluate the received amount so far based on the loop variable and the constructed equation 
											Texpr1Node rhs_expr = loop_analysis.get_rhs_expr(s.getUniqueLabel());
											MpqScalar received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, new Texpr1Intern(env, rhs_expr)).sup();
											// logger.debug("rhs_expr is " + rhs_expr);
											// fallOutWrapper.set(e.assignCopy(man, s.getUniqueLabel(), new Texpr1Intern(env, rhs_expr), null));
											Texpr1Node store_var = new Texpr1VarNode(s.getUniqueLabel());
											Texpr1Node received_amt_expr = new Texpr1CstNode(received_amt);
											Tcons1 cons = new Tcons1(env, Tcons1.SUPEQ, new Texpr1BinNode(Texpr1BinNode.OP_SUB, received_amt_expr, store_var)); 
											Tcons1 cons1 = new Tcons1(env, Tcons1.SUPEQ, new Texpr1BinNode(Texpr1BinNode.OP_SUB, store_var, rhs_expr)); 
											Abstract1 constraint = new Abstract1(man, new Tcons1[] {cons});
											// e.meet(man, constraint);
											logger.debug("fallOutWrapper after meeting: " + e);
											// fallOutWrapper.set(e.joinCopy(man, constraint));
											fallOutWrapper.set(e.assignCopy(man, s.getUniqueLabel(), new Texpr1Intern(env, received_amt_expr), null));
											logger.debug("fallOutWrapper is after creating new equation " + fallOutWrapper);
											// check if received amount is still ok

											received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, s.getUniqueLabel()).sup();
											logger.debug("upper bound for received amount is " + received_amt);
											if(received_amt.isInfty() != 0 || !s.checkFitsInReserve(received_amt)) {
												fits_in_reserve_satisfied = false;
											}
										} else {
											fits_in_reserve_satisfied = false;
										}
									} else if (fits_in_reserve_satisfied){
										// check if received amount is still ok
										Texpr1Node rhs_expr = loop_analysis.get_rhs_expr(s.getUniqueLabel());
										MpqScalar received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, new Texpr1Intern(env, rhs_expr)).sup();
										Texpr1Node store_var = new Texpr1VarNode(s.getUniqueLabel());
										Texpr1Node received_amt_expr = new Texpr1CstNode(received_amt);
										Tcons1 cons = new Tcons1(env, Tcons1.SUPEQ, new Texpr1BinNode(Texpr1BinNode.OP_SUB, received_amt_expr, store_var)); 
										Abstract1 constraint = new Abstract1(man, new Tcons1[] {cons});
										Abstract1 e = fallOutWrapper.get();
										// fallOutWrapper.set(e.joinCopy(man, constraint));
										fallOutWrapper.set(e.assignCopy(man, s.getUniqueLabel(), new Texpr1Intern(env, received_amt_expr), null));
										logger.debug("rhs_expr is " + rhs_expr);
										// MpqScalar received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, rhs_expr).sup();
										received_amt = (MpqScalar) fallOutWrapper.get().getBound(man, s.getUniqueLabel()).sup();
										logger.debug("upper bound for received amount is " + received_amt);
										received_amt = new MpqScalar((int) Math.floor(received_amt.get().doubleValue()));
										if(received_amt.isInfty() != 0 || !s.checkFitsInReserve(received_amt)) {
											fits_in_reserve_satisfied = false;
										}
									}
								} else {
									// here we fail directly, no attempt to make it precise
									fits_in_reserve_satisfied = false;
								}
							}
						} else {
							// logger.debug("delivered amount is " + delivered_amt.toString());
							if(!s.checkFitsInTrolley(delivered_amt)) {
								fits_in_trolley_satisfied = false;
							}
						}
					}
				}
			} else {
				Value arg = jInvStmt.getInvokeExpr().getArg(0);
				MpqScalar delivered_amt = lower_bound_of(arg, fallOutWrapper);
				if(delivered_amt.sgn() == -1) {
					non_negative_satisfied = false;
				}
			}
		}
	}

	public void handleInitialize(JInvokeStmt jInvStmt, NumericalStateWrapper fallOutWrapper) throws ApronException {
		// TODO: MAYBE FILL THIS OUT
		// there are two kinds of initializers: empty initializer, then we need
		// to use get(0) and initializers that are not empty, then we need get(2)
		Value store_reference = (Value) jInvStmt.getUseBoxes().get(0).getValue();
		if(!(store_reference instanceof JimpleLocal)) {
			store_reference = (Value) jInvStmt.getUseBoxes().get(2).getValue();
		}
		// logger.debug("type of jInvStmt is" + jInvStmt.getClass().getName());
		// logger.debug("jInvStmt: " + jInvStmt);
		// logger.debug("useBoxes: " + jInvStmt.getUseBoxes());
		// logger.debug("useBoxes.get(0): " + jInvStmt.getUseBoxes().get(0));
		// logger.debug("type of store_reference is " + store_reference.getClass().getName());
		for(StoreInitializer s : pointsTo.pointsTo((Local) store_reference)) {
			if(!fallOutWrapper.get().isBottom(man)) {
				alreadyInit.add(s);
			}
		}
	}

	// returns state of in after assignment
	private void handleDef(NumericalStateWrapper outWrapper, Value left, Value right) throws ApronException {
		// TODO: FILL THIS OUT
		// logger.debug("entered handleDef");
		// logger.debug("outWrapper = " + outWrapper.toString());
		Abstract1 e = outWrapper.get();
		// logger.debug("e = " + e.toString());

		Texpr1Node right_expr = exprOfValue(right);
		// Abstract1 e_out = e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), e);
		Abstract1 e_out = e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), null);
		// logger.debug("e_out = " + e_out.toString());
		// outWrapper.set(e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), e));
		outWrapper.set(e.assignCopy(man, left.toString(), new Texpr1Intern(env, right_expr), null));
		// logger.debug("outWrapper after handleDef: " + outWrapper.toString());
	}

	// TODO: MAYBE FILL THIS OUT: add convenience methods


	public boolean fitsInReserve() {
		for(StoreInitializer s : alreadyInit) {
			if (!s.satisfiesFitsInReserve()) {
				return false;
			} 
		}
		return true;
		// return alreadyInitMap.fitsInReserve();
	}

	private MpqScalar upper_bound_of(Value val, NumericalStateWrapper outWrapper) {
		if(val instanceof IntConstant) {
			return new MpqScalar(((IntConstant) val).value);
		} else {
			assert(val instanceof JimpleLocal);
			Abstract1 out = outWrapper.get();
			String var_name = ((JimpleLocal) val).getName();
			try {
				return (MpqScalar) out.getBound(man, var_name).sup();
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new MpqScalar();
			}
		}
	}

	private MpqScalar lower_bound_of(Value val, NumericalStateWrapper outWrapper) {
		if(val instanceof IntConstant) {
			return new MpqScalar(((IntConstant) val).value);
		} else {
			assert(val instanceof JimpleLocal);
			Abstract1 out = outWrapper.get();
			String var_name = ((JimpleLocal) val).getName();
			try {
				return (MpqScalar) out.getBound(man, var_name).inf();
			} catch (ApronException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new MpqScalar();
			}
		}
	}

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

	public Texpr1Node normalFormExpr(Value val) {
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

	private int int_of(MpqScalar s) {
		assert(s.isInfty() == 0);
		return Integer.valueOf(s.toString());
	}

}
