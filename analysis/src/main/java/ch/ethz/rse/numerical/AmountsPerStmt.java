package ch.ethz.rse.numerical;

import java.util.*;
import java.util.function.Predicate;

import apron.MpqScalar;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.toolkits.callgraph.TopologicalOrderer;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import ch.ethz.rse.pointer.StoreInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmountsPerStmt {
	private static final Logger logger = LoggerFactory.getLogger(AmountsPerStmt.class);

    public HashMap<Stmt, AmountsPerStore> amountsPerStmt;
    private HashSet<StoreInitializer> alreadyInit = new HashSet<StoreInitializer>();
    private UnitGraph g;
    private HashMap<String, StoreInitializer> unique_id_map = new HashMap<String, StoreInitializer>();
    private int total_nr_inits = 0;

    public AmountsPerStmt(UnitGraph g) {
        this.g = new BriefUnitGraph(g.getBody());
        logger.debug("CFG body: ");
        logger.debug(g.getBody().toString());
        UnitPatchingChain all_units = this.g.getBody().getUnits();
        logger.debug("all units: ");
        logger.debug(all_units.toString());
        int nr_calls_get_delivery = 0;
        for(Unit head : g.getHeads()) {
            Iterator<Unit> i = all_units.iterator(head);
            // only keep the units of calls to get_delivery in the CFG
            logger.debug(Boolean.toString(i.hasNext()));
            while(i.hasNext()) {
                Unit u = (Unit) i.next();
                Stmt s = (Stmt) u;
                if(s instanceof JInvokeStmt) {
                    JInvokeStmt jInvStmt = (JInvokeStmt) s;
                    InvokeExpr invokeExpr = jInvStmt.getInvokeExpr();
                    if(!(invokeExpr instanceof JVirtualInvokeExpr)) {
                        logger.debug("remvoing Stmt " + s);
                        logger.debug("type(s) = " + s.getClass());
                        i.remove();
                    } else {
                        nr_calls_get_delivery++;
                    }
                } else {
                    // all_units.remove(u);
                    i.remove();
                }
            }
        }
        logger.debug(all_units.toString());
        logger.debug("CFG body: ");
        logger.debug(g.getBody().toString());
        // now that we know how many calls to get_delivery there are
        // we can also initialize amountsPerStmt with a sufficiently
        // big capacity
        amountsPerStmt = new HashMap<Stmt, AmountsPerStore>(nr_calls_get_delivery);
    }

    public boolean fitsInReserve() {
        for(Map.Entry<Stmt, AmountsPerStore> entry : amountsPerStmt.entrySet()) {
            AmountsPerStore amountsPerStore = entry.getValue();
            for(String id : amountsPerStore.amountsPerStore.keySet()) {
                MpqScalar received_amt = amountsPerStore.received_amount_of(id);
                StoreInitializer store_id = unique_id_map.get(id);
                if(!store_id.checkFitsInReserve(received_amt)) {
                    return false;
                }
            }

        }
        return true;
	}

    public void receive(Stmt stmt, StoreInitializer store, MpqScalar amount) {
        if(amountsPerStmt.get(stmt) == null) {
            // if it's the first time we visit this node in the CFG
            // we need to either set all amounts to 0 for each StoreInitializer
            // in case stmt has no predecessors in the CFG
            // otherwise, we just merge the received amounts of its predecessors
            // by taking the maximum amounts for each object
            List<Unit> preds = g.getPredsOf((Unit) stmt); 
            AmountsPerStore stores = new AmountsPerStore(alreadyInit);
            if(preds.size() > 0) {
                // if stmt has predecessors, merge those amounts
                for(Unit pred : preds) {
                    AmountsPerStore predAmounts = amountsPerStmt.get((Stmt) pred); 
                    stores.merge(predAmounts);
                }
            }
            amountsPerStmt.put(stmt, stores);
        } 
        AmountsPerStore stores = amountsPerStmt.get(stmt);
        // add new StoreInitializer objects to stores if we have encountered
        // new initializers
        if(stores.amountsPerStore.keySet().size() < unique_id_map.keySet().size()) {
            stores.add_stores(unique_id_map.keySet());
        }
        // receive amounts 
        stores.receive(amount, store); 


    }

    public void add(StoreInitializer store) {
        total_nr_inits += 1;
        if(alreadyInit.size() < total_nr_inits) {
            HashSet<StoreInitializer> newInit = new HashSet<StoreInitializer>(total_nr_inits);
            newInit.addAll(alreadyInit);
            alreadyInit = newInit;
        }
        if(unique_id_map.size() < total_nr_inits) {
            HashMap<String, StoreInitializer> newMap = new HashMap<String, StoreInitializer>(total_nr_inits);
            newMap.putAll(unique_id_map);
            unique_id_map = newMap;
        }
        alreadyInit.add(store);
        unique_id_map.put(store.getUniqueLabel(), store);
    }

}
