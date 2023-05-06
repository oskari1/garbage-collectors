package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_ForLoop_Safe {
    public static void m1() {
        Store s = new Store(10, 100);
        for(int i = 0; i<10; i++){
            s.get_delivery(i);
        }
        
    
    }
}

