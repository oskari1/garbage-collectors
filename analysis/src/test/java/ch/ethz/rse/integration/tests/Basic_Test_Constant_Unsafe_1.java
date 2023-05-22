package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Constant_Unsafe_1 {
    public static void m1() {
        Store s = new Store(2, 4);
        s.get_delivery(2);
        s.get_delivery(3);
    
    }
}


