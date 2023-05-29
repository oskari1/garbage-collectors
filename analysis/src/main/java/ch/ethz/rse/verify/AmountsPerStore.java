package ch.ethz.rse.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.rse.pointer.StoreInitializer;
import java.util.HashMap;
import java.util.Collection;
import ch.ethz.rse.pointer.PointsToInitializer;
import soot.SootMethod;
import soot.ValueBox;
import soot.Local;
import apron.MpqScalar;


public class AmountsPerStore {
    private static final Logger logger = LoggerFactory.getLogger(Verifier.class);
    private HashMap<StoreInitializer, Integer> amts_map;
    private final PointsToInitializer pointsTo;
    
    public AmountsPerStore(PointsToInitializer pointsTo, SootMethod method) {
        this.pointsTo = pointsTo;
        Collection<StoreInitializer> allInits = pointsTo.getInitializers(method);
        int total_nr_inits = allInits.size(); 
        HashMap<StoreInitializer, Integer> zeroMap = new HashMap<StoreInitializer, Integer>(total_nr_inits);
        for(StoreInitializer store : allInits) {
            zeroMap.put(store, new Integer(0));
        }
        this.amts_map = zeroMap;
    }

    public void merge_amounts(AmountsPerStore amounts) {
        for(StoreInitializer store : amounts.amts_map.keySet()) {
            int this_amt = this.amts_map.get(store).intValue();
            int pred_amt = amounts.amts_map.get(store).intValue();
            this.amts_map.put(store,new Integer(Math.max(this_amt,pred_amt)));
        }
    }

    public AmountsPerStore receive_amount(MpqScalar delivered_amt, ValueBox store_reference, int iterations) {
        for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
            int prev_amt = this.amts_map.get(store).intValue();
            if(delivered_amt.isInfty() == -1) {
                // if delivered amount is minus infty, we take the smallest int regardless 
                // of the nr. of iterations
                this.amts_map.put(store, Integer.MIN_VALUE);
            } else {
                // else, if the delivered amount is finite and >= 0, we 
                // need to multiply and add to the previous amount
                int new_amt = prev_amt + iterations * Integer.valueOf(delivered_amt.toString());
                // logger.debug("prev_amt, new_amt = " + prev_amt + ", " + new_amt);
                this.amts_map.put(store, new Integer(new_amt));
            }
        }
        return this;
    }

    public boolean fit_in_reserve(ValueBox store_reference) {
        for(StoreInitializer store : pointsTo.pointsTo((Local) store_reference.getValue())) {
            // logger.debug("Checking fit_in_reserve for StoreInitializer " + store.getUniqueLabel() + " with reserve_size " + store.reserve_size);
            int stored_amt = this.amts_map.get(store).intValue();
            // logger.debug("and the stored amount is " + stored_amt);
            if(store.reserve_size < this.amts_map.get(store).intValue()) {
                // received amount exceeds reserve_size, so FITS_IN_RESERVE is UNSAFE
                return false;
            }
        }
        return true;
    }
    
}
