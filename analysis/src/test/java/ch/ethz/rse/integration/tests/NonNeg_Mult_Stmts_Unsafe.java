package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

public class NonNeg_Mult_Stmts_Unsafe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        int j = 7;
        int k = -2;
        s.get_delivery(j);
        s.get_delivery(k);
    }
}
