package ch.ethz.rse.verify;

import ch.ethz.rse.numerical.NumericalAnalysis;
import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Interval;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.*;

public class LoopAnalysis {
    private LoopNestTree loops;
    private HashMap<Unit, Loop> loop_of_unit;
    private HashMap<Loop, Integer> max_iterations_of_loop;
    private SootMethod method;
    private Environment env;
    private Manager man;
    private UnitGraph g;
    private NumericalAnalysis an;
    private static final Logger logger = LoggerFactory.getLogger(Verifier.class);

    public LoopAnalysis(UnitGraph g, SootMethod m, Environment env, Manager man, NumericalAnalysis an) {
        this.method = m;
        this.env = env;
        this.man = man;
        this.g = g;
        this.an = an;
        this.loops = new LoopNestTree(g.getBody());
        this.max_iterations_of_loop = new HashMap<Loop, Integer>(loops.size());
        this.loop_of_unit = new HashMap<Unit, Loop>(g.size());
        for(Loop l : loops) {
            List<Stmt> stmts = l.getLoopStatements();
            for(Stmt s : stmts) {
                loop_of_unit.put((Unit) s, l);
            }
        }
    }

    public Loop loop_of_unit(Unit u) {
        return this.loop_of_unit.get(u);
    }

    public boolean terminates(Loop l) {
        // logger.debug("entered terminates-func");
        if(l.loopsForever()) {
            // this is the case if the loop has no exit-nodes
            return false;
        } else {
            // if the loop has exit nodes then we 
            // extract conditional expression (assume there is only one)
            // of the form exp > 0 or exp >= 0
            // check that exp.getBound() (: Interval) in the JumpBackStmt
            // isLeq() than in the loopHead and that it's not isEqual() 
            // to it
            // in other words, we check that the domain of the conditional
            // expression is certainly strictly smaller after executing 
            // the loop body

            // assumption: every loop with a conditional must contain
            // at least one JIfStmt
            // I assume (don't know if this is always true) that if 
            // the loop-header is a JIfStmt then we know that it's
            // the conditional jump 
            if(l.getHead() instanceof JIfStmt) {
                Stmt header_stmt = (Stmt) (l.getHead());
                Stmt jmp_back_stmt = (Stmt) (l.getBackJumpStmt());
                // logger.debug("header_stmt: " + header_stmt);
                // logger.debug("jmp_back_stmt: " + jmp_back_stmt);
                Value cond = ((JIfStmt) header_stmt).getCondition();
                // logger.debug("cond: " + cond);

				if(cond instanceof JEqExpr || cond instanceof JNeExpr) {
                    // can't deal with loop where the conditional predicate 
                    // contains == or != (seems to be consistent with
                    // master solution)
                    return false;
				}
				Texpr1Node expr = normalFormExpr(cond); 
                // logger.debug("normal form expression: " + expr.toString());
                Texpr1Intern expr_intern = new Texpr1Intern(env, expr);
                // Abstract1 header_state = an.getFallFlowAfter((Unit) header_stmt).get();
                Abstract1 header_state = get_header_state(l);
                Abstract1 jmp_back_state = get_tail_state(l);
                // Abstract1 jmp_back_state = an.getFlowBefore((Unit) jmp_back_stmt).get();
                // logger.debug("header_state: " + header_state);
                // logger.debug("jmp_back_state: " + jmp_back_state);
                if(abs_expr_monotonically_decreasing(expr_intern, header_state, jmp_back_state, l)) {
                    // if we have a loop conditional of the form exp > 0 or exp >= 0 
                    // and we see that the upper bound of exp in the header
                    // is strictly greater than its upper bound after executing 
                    // the loop body, we know the loop must terminate
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public int max_iterations_of(Loop l) {
        // check how much smaller exp is in JumpBackStmt compared 
        // to loopHeader. Find the upper and lower bound of exp
        // in loopHeader and divide this number by the minimum
        // decrement of exp
        // this yields the max_iterations_of(loop l)
        return max_iterations_of_loop.get(l).intValue();
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
		if (val instanceof JLeExpr || val instanceof JLtExpr) {
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

    private boolean abs_expr_monotonically_decreasing(Texpr1Intern expr_intern, Abstract1 header_state, Abstract1 jmp_back_state, Loop l) {
        try {
            Interval header_domain = header_state.getBound(man, expr_intern);
            Interval jmp_back_domain = jmp_back_state.getBound(man, expr_intern);
            MpqScalar header_sup = (MpqScalar) header_domain.sup();
            MpqScalar header_inf = (MpqScalar) header_domain.inf();
            // logger.debug("header domain: " + header_domain);

            int abs_exp_sup_header; 
            int abs_exp_inf_header;
            int flipped_exp_sup_jmp_back;
            if(header_sup.sgn() == -1) {
                // logger.debug("flipped sign");
                header_inf.neg();
                header_sup.neg();
                abs_exp_sup_header = Integer.valueOf(header_inf.toString());
                abs_exp_inf_header = Integer.valueOf(header_sup.toString());
                // logger.debug("jmp_back_domain before neg: " + jmp_back_domain);
                jmp_back_domain.neg();
                // logger.debug("jmp_back_domain after neg: " + jmp_back_domain);
            } else {
                abs_exp_sup_header = Integer.valueOf(header_sup.toString());
                abs_exp_inf_header = Integer.valueOf(header_inf.toString());
            }

            // logger.debug("jmp_back_domain after neg: " + jmp_back_domain);
            flipped_exp_sup_jmp_back = Integer.valueOf(jmp_back_domain.sup().toString());
            int min_abs_exp_dec = abs_exp_sup_header - flipped_exp_sup_jmp_back;
            // logger.debug("abs_exp_sup_header = " + abs_exp_sup_header);
            // logger.debug("abs_exp_inf_header = " + abs_exp_inf_header);
            // logger.debug("min_abs_exp_dec = " + min_abs_exp_dec);
            int max_loop_iterations = (abs_exp_sup_header - abs_exp_inf_header + 1)/min_abs_exp_dec;
            max_iterations_of_loop.put(l,new Integer(max_loop_iterations));
            return abs_exp_sup_header > flipped_exp_sup_jmp_back;

        } catch (ApronException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        
    }

    private Abstract1 get_header_state(Loop l) {
        Stmt header_stmt = l.getHead();
        List<Stmt> loop_stmts = l.getLoopStatements();
        Unit header_node = (Unit) header_stmt;
        List<Unit> header_succs = g.getSuccsOf(header_node); 
        for(Unit succ : header_succs) {
            if (loop_stmts.contains((Stmt) succ)) {
                return an.getFlowBefore(succ).get(); 
            }
        }
        return null;
    }

    private Abstract1 get_tail_state(Loop l) {
        Stmt jmp_back_stmt = l.getBackJumpStmt();
        if(jmp_back_stmt instanceof JGotoStmt) {
            return an.getFlowBefore((Unit)jmp_back_stmt).get();
        } else {
            return an.getFallFlowAfter((Unit) jmp_back_stmt).get();
        }
        // List<Unit> tail_preds = g.getPredsOf((Unit) jmp_back_stmt);
        // List<Stmt> loop_stmts = l.getLoopStatements();
        // for(Unit pred : tail_preds) {
        //     if(loop_stmts.contains((Stmt) pred)) {
        //         return an.getFallFlowAfter(pred).get();
        //     }
        // }
        // return null;
    }
    
}
