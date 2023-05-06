package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE


public class Basic_Test_Constant_Addition_Safe{
    public static void m1() {
        Store s = new Store(3, 5);
        s.get_delivery(1+2);
        s.get_delivery(1+0);
    
    }
}


