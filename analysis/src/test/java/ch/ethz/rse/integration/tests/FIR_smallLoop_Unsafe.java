package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE


public class FIR_smallLoop_Unsafe {
    public static void m1(int x) {
        Store s = new Store(1, 4);
        int i = 0; 
        if (x>0){
            i = 1; 
        }
        for (int j = 0; j<5; j++){
            s.get_delivery(i);
        }
    
    }
}
