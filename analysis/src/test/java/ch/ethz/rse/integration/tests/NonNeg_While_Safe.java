package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

public class NonNeg_While_Safe {
    public static void m1() {
        Store s = new Store(3, 10);
        int i = 0;
        while(i < 3) {
            s.get_delivery(i);
            i++;
        }
    
    }
}
