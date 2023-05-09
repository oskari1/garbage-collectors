package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_InfiniteWhileLoop {
    public static void m1() {
        Store s = new Store(10, 100);
        int i = 1; 
        while (i>0){ 
            i++; 
        }
        s.get_delivery(i);
        
    
    }
}

