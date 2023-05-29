package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Variable_ForLoop_Safe {
    public static void m1() {
        Store s = new Store(10, 100);
        int j = 0; 
        for(int i = 0; i<10; i++){
            j++;
            // here j = 1 for i = 0, j = 2 for i = 1, ..., j = 10 for i = 9 
        }
        // note that since widening is applied, j is in [10, +oo]
        // at this point. Changing i<10 to i<5 and trolley_size to 5,
        // the widening threshold is not reached, so it computes the
        // bound [5,5] for j and passes. 
        s.get_delivery(j);
        
    
    }
}

