package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

public class Basic_Test_Variable_Unsafe {
    public static void m1() {
        Store s = new Store(3, 10);
        for(int i = -1; i<5; i++){
            s.get_delivery(i);
        }
        
    
    }
}
