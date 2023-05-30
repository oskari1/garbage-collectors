package ch.ethz.rse.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.rse.pointer.StoreInitializer;
import java.util.HashMap;
import java.rmi.NotBoundException;
import java.util.*;
import java.util.Collection;

import ch.ethz.rse.numerical.NumericalAnalysis;
import ch.ethz.rse.pointer.PointsToInitializer;
import soot.SootMethod;
import soot.ValueBox;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;
import soot.Local;
import soot.Unit;
import apron.MpqScalar;
import apron.Environment;
import apron.Manager;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.LoopNestTree;
import soot.jimple.toolkits.annotation.logic.Loop;

public class AmountsPerNode {
    private static final Logger logger = LoggerFactory.getLogger(Verifier.class);
    private HashMap<Unit,AmountsPerStore> amounts_per_node;
    private UnitGraph g;
    private SootMethod method;
    private PointsToInitializer pointsTo;
    private HashMap<Unit,Boolean> visited;
    private NumericalAnalysis an;
    private Manager man;
    private LoopAnalysis loopAnalysis;
    private LoopNestTree loopNestTree;

    public AmountsPerNode(UnitGraph g, PointsToInitializer pointsTo, SootMethod method, NumericalAnalysis an, Manager man, Environment env) {
        this.amounts_per_node = new HashMap<Unit, AmountsPerStore>(g.size());
        this.g = g;
        this.method = method;
        this.pointsTo = pointsTo;
        this.an = an;
        this.man = man;
        this.loopNestTree = new LoopNestTree(g.getBody());
        this.visited = new HashMap<Unit,Boolean>(g.size());
        Iterator<Unit> i = g.iterator();
        while(i.hasNext()) {
            Unit v = (Unit) i.next();
            amounts_per_node.put(v, new AmountsPerStore(pointsTo, method));
        }
        this.loopAnalysis = new LoopAnalysis(g, method, env, man, an);
    }

    public void compute_received_amounts() throws FitsInReserveException {
        // initialize visited-map
        logger.debug("entered compute_received_amounts");
        visited = new HashMap<Unit,Boolean>(g.size());
        Iterator<Unit> i = g.iterator();
        while(i.hasNext()) {
            Unit v = (Unit) i.next();
            visited.put(v, new Boolean(false));
        }
        // start computation
        logger.debug("Body of CFG");
        logger.debug(g.getBody().toString());
        logger.debug("before for-loop");
        List<Unit> tails = get_tails(); 
        for(Unit t : tails) {
            compute(t);
        }
        logger.debug("after for-loop");
    }

    private void compute(Unit u) throws FitsInReserveException {
        logger.debug("entered compute-function with Unit " + u);
        visited.put(u,new Boolean(true));
        if(g.getHeads().contains(u)) {
            // if u is a header, initialize all Stores to have received 0  
            amounts_per_node.put(u,new AmountsPerStore(pointsTo, method));
        } else {
            // else, first compute amounts for all predecessors
            for(Unit p : g.getPredsOf(u)) {
                if(!visited.get(p).booleanValue()) {
                    compute(p);
                }
            }
            // at this point, we have already computed the values for all predecessors
            // now we need to "merge" the amounts from the predecessors by taking the max
            AmountsPerStore amts = amounts_per_node.get(u);
            for(Unit p : g.getPredsOf(u)) {
                AmountsPerStore pred_amts = amounts_per_node.get(p);
                amts.merge_amounts(pred_amts);
            }
            amounts_per_node.put(u,amts);
        }
        // once we've done this, we need to check if u is a call to get_delivery
        // if so, we need to update the received amounts for each Store object
        // that has received some amount by that call
        if(Verifier.is_reachable_call_to_get_delivery(u, an, man)) {
            logger.debug("we are in call " + u);
            Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
            MpqScalar delivered_amt = Verifier.upper_bound_of(arg, an, u, man); 
            if(delivered_amt.isInfty() != 0) {
                // if received amount is unbounded, FITS_IN_RESERVE is certainly not SAFE 
                // logger.debug("delivered amount is infinite");
                throw new FitsInReserveException("doesn't fit in reserve");
            } else {
                // if received amount is finite, need to compare with reserve_size
                ValueBox store_reference = u.getUseBoxes().get(1); 
                AmountsPerStore currAmounts = amounts_per_node.get(u);
                Loop l = loopAnalysis.loop_of_unit(u);
                if(l != null) {
                    // logger.debug("call to get_delivery is contained in loop");
                    // l != null iff u is contained inside a loop 
                    if(!loopAnalysis.terminates(l) && is_strictly_positive(delivered_amt)) {
                        // if we have an infinite loop and the received amount is strictly positive
                        // it's for sure UNSAFE 
                        throw new FitsInReserveException("doesn't fit in reserve");
                    } else if(!loopAnalysis.terminates(l) && !delivered_amt.isZero()) {
                        // if we have an infinite loop and the received amount is
                        // negative, the received amount is "minus infinity"
                        MpqScalar minusInfty = new MpqScalar();
                        minusInfty.setInfty(-1);
                        delivered_amt = minusInfty;
                        amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference, 0));
                    } else if(!loopAnalysis.terminates(l) && delivered_amt.isZero()) {
                        // if we have an infinite loop and the received amount is
                        // zero, the received amount is "zero"
                        MpqScalar zero = new MpqScalar(); // this is set to 0 by default
                        delivered_amt = zero;
                        amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference, 0));
                    } else if(delivered_amt.sgn() == -1) {
                        // if we have a finite loop and the received amount is negative
                        // the maximum amount that can be received is 0 if the loop is never
                        // executed 
                        amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference, 0));
                    } else {
                        // if we have a finite loop and the received amount is >= 0 then
                        // we need to multiply the maximum amount with the maximum number of
                        // iterations
                        int iterations = loopAnalysis.max_iterations_of(l);
                        logger.debug("the computed max iterations are " + iterations);
                        amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference, iterations));
                    }
                } else {
                    // if get_delivery is not within a loop, it's called at most once
                    int iterations = 1;
                    // logger.debug("get_delivery only called once");
                    amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference, iterations));
                }
                // check if received amounts exceed reserve_size at this node
                if(!amounts_per_node.get(u).fit_in_reserve(store_reference)) {
                    throw new FitsInReserveException("doesn't fit in reserve");
                }
            }
        }


    }

    private boolean is_strictly_positive(MpqScalar amt) {
        // amt > 0 iff sign(amt) = 1 
        return amt.sgn() == 1;
    }

    private List<Unit> get_tails() {
        List<Unit> exit_nodes = g.getTails();
        List<Unit> jmp_back_nodes = new ArrayList<Unit>(); 
        for(Loop l : loopNestTree) {
            Stmt jmp_back_stmt = l.getBackJumpStmt();
            jmp_back_nodes.add((Unit) jmp_back_stmt);
        }
        jmp_back_nodes.addAll(exit_nodes);
        return jmp_back_nodes;
    }

}
