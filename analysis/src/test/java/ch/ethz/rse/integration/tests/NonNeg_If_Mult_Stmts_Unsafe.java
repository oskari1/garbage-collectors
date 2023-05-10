package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

public class NonNeg_If_Mult_Stmts_Unsafe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        int j = 0;
        if(x > 0) {
            s.get_delivery(1);
            j--;
        }
        s.get_delivery(j);
    }
}
