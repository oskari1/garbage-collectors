package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

public class NonNeg_While_Unsafe {
    public static void m1() {
        Store s = new Store(3, 10);
        int i = 3;
        while(i >= -1) {
            s.get_delivery(i);
            i--;
        }
    
    }
}
