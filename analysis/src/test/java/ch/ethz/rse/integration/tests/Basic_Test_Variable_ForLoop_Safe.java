package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_Variable_ForLoop_Safe {
    public static void m1() {
        Store s = new Store(10, 100);
        int j = 0; 
        for(int i = 0; i<10; i++){
            j++; 
        }
        s.get_delivery(j);
        
    
    }
}

