package ch.ethz.rse.numerical;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JNeExpr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import soot.jimple.ConditionExpr;
import soot.jimple.internal.JimpleLocal;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apron.MpqScalar;
import apron.Texpr1BinNode;
import apron.Texpr1CstNode;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import ch.ethz.rse.pointer.StoreInitializer;
import ch.qos.logback.core.joran.conditional.Condition;

public class LoopAnalysis {
	private static final Logger logger = LoggerFactory.getLogger(LoopAnalysis.class);
    private Loop loop = null;
    public Unit loop_head;
    private JInvokeStmt invStmt;
    private int loop_depth = 0;
    // init_rcvd_amt might need to be a map instead for each
    // unique label for Store initializers, since the different
    // stores might have received a different amount, e.g.,
    // due to later initialization
    private HashMap<String, MpqScalar> init_rcvd_amt_of;
    private MpqScalar arg;
    private String rcvd_amt_var;
    private int init_val_loop_var;
    private int second_val_loop_var;
    private String loop_var_name;
    private List<ConditionExpr> loop_conditionals;
    // public Texpr1Node rhs_expr;
    public HashMap<String, Texpr1Node> rhs_expr_of;

    public LoopAnalysis(Loop loop, JInvokeStmt invStmt, HashMap<Loop, List<Stmt>> stmts_per_loop) {
        this.loop = loop;
        this.loop_head = loop.getHead();
        this.invStmt = invStmt;
		int depth = 0;
		for(Loop l : stmts_per_loop.keySet()) {
			List<Stmt> loop_stmts = stmts_per_loop.get(l);
			if(loop_stmts.contains(invStmt)) {
				depth++;
			}
		}
        this.loop_depth = depth; 
        this.loop_conditionals = set_loop_conditionals();
    }

    public LoopAnalysis(JInvokeStmt invStmt) {
        this.invStmt = invStmt;
    }

    public int get_loop_depth() {
        return loop_depth;
    }

    public void set_init_rcvd_amt_map(int size) {
        if(this.init_rcvd_amt_of == null) {
            this.init_rcvd_amt_of = new HashMap<String, MpqScalar>(size);
        }
    }

    public void set_rhs_expr_of_map(int size) {
        if(this.rhs_expr_of == null) {
            this.rhs_expr_of = new HashMap<String, Texpr1Node>(size);
        }
    }

    public void set_init_rcvd_amt(MpqScalar amt, String store_id) {
        this.init_rcvd_amt_of.put(store_id, amt);
    }

    public void set_arg(MpqScalar amt) {
        this.arg = amt;
    }

    public void set_init_val_loop_var(int amt) {
        this.init_val_loop_var = amt;
    }

    public void set_second_val_loop_var(int amt) {
        this.second_val_loop_var = amt;
    }

    public void set_loop_var_name(String name) {
        this.loop_var_name = name;
    }

    public void set_rcvd_amt_var(String var) {
        this.rcvd_amt_var = var;
    }

    public boolean is_growing_loop_variable() {
        ConditionExpr c = loop_conditionals.get(0); 
        Value op1 = c.getOp1();
        if((c instanceof JLeExpr || c instanceof JLtExpr) && op1 instanceof JimpleLocal) {
            return true;
        } else {
            return false;
        }

    }
    
    public String get_loop_var_name() {
        if(this.loop_var_name == null) {
            ConditionExpr c = loop_conditionals.get(0);
            if(c instanceof JLtExpr || c instanceof JLeExpr ||
            c instanceof JGtExpr || c instanceof JGeExpr) {
                Value op1 = c.getOp1();
                Value op2 = c.getOp2();
                if(op1 instanceof JimpleLocal) {
                    return ((JimpleLocal) op1).getName();
                } else if(op2 instanceof JimpleLocal){
                    return ((JimpleLocal) op2).getName();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return this.loop_var_name;
        }
    }

	private List<ConditionExpr> set_loop_conditionals() {
        List<Stmt> loop_stmts = loop.getLoopStatements();
		Collection<Stmt> loop_exits = loop.getLoopExits(); 
		loop_exits.add(loop.getHead());
		List<Stmt> cond_stmts = loop_exits.stream()
		.filter(s -> s instanceof JIfStmt).collect(Collectors.toList());
        // need to extract the correct conditionals, i.e., make sure
        // that if the condition is satisfied, we make another iteration
        // to do this, need to check whether the branchOutStmt is
        // outside of the loop or not and if so, negate the condition

        // for each statement, check if the target-stmt is part of the 
        // same loop, if so, just take getCondition of that JIfStmt
        // else, construct the negated condition
        List<Stmt> ready_cond_stmts = cond_stmts.stream()
        .filter(s -> loop_stmts.contains(((JIfStmt) s).getTarget())).collect(Collectors.toList());
        cond_stmts.removeAll(ready_cond_stmts);
        List<ConditionExpr> conds = cond_stmts.stream()
        .map(c -> (ConditionExpr) ((JIfStmt) c).getCondition()).collect(Collectors.toList());
        List<ConditionExpr> ready_conds = ready_cond_stmts.stream()
        .map(c -> (ConditionExpr) ((JIfStmt) c).getCondition()).collect(Collectors.toList());
        List<ConditionExpr> flipped_conds = conds.stream()
        .map(c -> flip(c)).collect(Collectors.toList());
        ready_conds.addAll(flipped_conds);
        logger.debug("the conditionals are: " + ready_conds.toString());
        return ready_conds; 
	}

    private ConditionExpr flip(ConditionExpr c) {
        Value op1 = c.getOp1();
        Value op2 = c.getOp2();
        if(c instanceof JLeExpr) {
            return new JGtExpr(op1, op2);
        } else if(c instanceof JLtExpr) {
            return new JGeExpr(op1, op2);
        } else if(c instanceof JGeExpr) {
            return new JLtExpr(op1, op2);
        } else if(c instanceof JGtExpr) {
            return new JLeExpr(op1, op2);
        } else if(c instanceof JEqExpr) {
            return new JNeExpr(op1, op2);
        } else {
            return new JEqExpr(op1, op2);
        }
    }

    public void set_rhs_expr(String store_id) {
        Texpr1Node op1 = new Texpr1CstNode(init_rcvd_amt_of.get(store_id));
        Texpr1Node arg = new Texpr1CstNode(this.arg);
        Texpr1Node const1 = new Texpr1CstNode(new MpqScalar(1));
        Texpr1Node const2 = new Texpr1CstNode(new MpqScalar(init_val_loop_var, second_val_loop_var-init_val_loop_var));
        Texpr1Node const_expr = new Texpr1BinNode(Texpr1BinNode.OP_SUB, const1, const2); 
        Texpr1Node frac1 = new Texpr1CstNode(new MpqScalar(1, second_val_loop_var-init_val_loop_var));
        Texpr1Node i_expr = new Texpr1VarNode(loop_var_name);
        Texpr1Node var_expr = new Texpr1BinNode(Texpr1BinNode.OP_MUL, frac1, i_expr);
        Texpr1Node frac = new Texpr1BinNode(Texpr1BinNode.OP_ADD, var_expr, const_expr); 
        Texpr1Node op2 = new Texpr1BinNode(Texpr1BinNode.OP_MUL, arg, frac);
        this.rhs_expr_of.put(store_id, new Texpr1BinNode(Texpr1BinNode.OP_ADD, op1, op2));
    }

    public Texpr1Node get_rhs_expr(String store_id) {
        return this.rhs_expr_of.get(store_id);
    }
    
}
