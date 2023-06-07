package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_InvertedForLoop_Safe_1 {
    public static void m1() {
        Store s = new Store(10, 150);
        for(int i = 10; i>-5; i--){
            s.get_delivery(i);
        }
        
    
    }
}

