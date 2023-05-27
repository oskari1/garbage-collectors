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
import soot.jimple.internal.JInvokeStmt;
import soot.Local;
import soot.Unit;
import apron.MpqScalar;
import apron.Manager;
import soot.toolkits.graph.UnitGraph;

public class AmountsPerNode {
    private HashMap<Unit,AmountsPerStore> amounts_per_node;
    private UnitGraph g;
    private SootMethod method;
    private PointsToInitializer pointsTo;
    private HashMap<Unit,Boolean> visited;
    private NumericalAnalysis an;
    private Manager man;

    public AmountsPerNode(UnitGraph g, PointsToInitializer pointsTo, SootMethod method, NumericalAnalysis an, Manager man) {
        this.amounts_per_node = new HashMap<Unit, AmountsPerStore>(g.size());
        this.g = g;
        this.method = method;
        this.pointsTo = pointsTo;
        this.an = an;
        this.man = man;
        this.visited = new HashMap<Unit,Boolean>(g.size());
        Iterator<Unit> i = g.iterator();
        while(i.hasNext()) {
            Unit v = (Unit) i.next();
            amounts_per_node.put(v, new AmountsPerStore(pointsTo, method));
        }


    }

    public void compute_received_amounts() throws FitsInReserveException {
        // initialize visited-map
        visited = new HashMap<Unit,Boolean>(g.size());
        Iterator<Unit> i = g.iterator();
        while(i.hasNext()) {
            Unit v = (Unit) i.next();
            visited.put(v, new Boolean(false));
        }
        // start computation
        for(Unit t : g.getTails()) {
            compute(t);
        }
    }

    private void compute(Unit u) throws FitsInReserveException {
        if(g.getHeads().contains(u)) {
            // if u is a header, initialize all Stores to have received 0  
            amounts_per_node.put(u,new AmountsPerStore(pointsTo, method));
        } else {
            // else, first compute amounts for all predecessors
            for(Unit p : g.getPredsOf(u)) {
                if(!visited.get(u).booleanValue()) {
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
            Value arg = ((JInvokeStmt) u).getInvokeExpr().getArg(0);
            MpqScalar delivered_amt = Verifier.upper_bound_of(arg, an, u, man); 
            if(delivered_amt.isInfty() != 0) {
                // if received amount is unbounded, FITS_IN_RESERVE is certainly not SAFE 
                throw new FitsInReserveException("doesn't fit in reserve");
            } else {
                // if received amount is finite, need to compare with reserve_size
                ValueBox store_reference = u.getUseBoxes().get(1); 
                AmountsPerStore currAmounts = amounts_per_node.get(u);
                amounts_per_node.put(u,currAmounts.receive_amount(delivered_amt, store_reference));
                // check if received amounts exceed reserve_size at this node
                if(!amounts_per_node.get(u).fit_in_reserve(store_reference)) {
                    throw new FitsInReserveException("doesn't fit in reserve");
                }
            }
        }


    }

}
