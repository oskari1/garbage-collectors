package ch.ethz.rse.numerical;

import java.util.*;
import apron.MpqScalar;

import ch.ethz.rse.pointer.StoreInitializer;

public class AmountsPerStore {

    public HashMap<String, MpqScalar> amountsPerStore;

    public AmountsPerStore(HashSet<StoreInitializer> set) {
        amountsPerStore = new HashMap<String, MpqScalar>(set.size());
        for(StoreInitializer s : set) {
            amountsPerStore.put(s.getUniqueLabel(), new MpqScalar(0));
        }
    }

    public HashMap<String, MpqScalar> get_map() {
        return amountsPerStore;
    }

    public void merge(AmountsPerStore other) {
        Set<String> intersecting_stores = amountsPerStore.keySet(); 
        intersecting_stores.retainAll(other.amountsPerStore.keySet());
        for(String id : intersecting_stores) {
            int this_amt = int_of(amountsPerStore.get(id));
            int other_amt = int_of(other.amountsPerStore.get(id));
            if(this_amt < other_amt) {
                this.amountsPerStore.put(id, new MpqScalar(other_amt));
            }
        }


    }
    
    public MpqScalar received_amount_of(String id) {
        return amountsPerStore.get(id);
    }

    private int int_of(MpqScalar s) {
		assert(s.isInfty() == 0);
		return Integer.valueOf(s.toString());
	}

    public void add_stores(Set<String> id_set) {
        HashMap<String, MpqScalar> new_map = new HashMap<String, MpqScalar>(id_set.size());
        new_map.putAll(amountsPerStore);
        Set<String> current_store_ids = amountsPerStore.keySet(); 
        id_set.removeAll(current_store_ids);
        for(String id : id_set) {
           new_map.put(id, new MpqScalar(0)); 
        }
        this.amountsPerStore = new_map;
    }

    public void receive(MpqScalar delivered_amount, StoreInitializer store) {
        String store_id = store.getUniqueLabel();
        MpqScalar current_amount = amountsPerStore.get(store_id);
        if(delivered_amount.isInfty() != 0) {
            amountsPerStore.put(store_id, delivered_amount);
        } else if(current_amount.isInfty() == 0) {
            int new_amount = int_of(current_amount) + int_of(delivered_amount);
            amountsPerStore.put(store_id, new MpqScalar(new_amount));
        }
    }
    
}
