package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE


public class FIR_bigLoop_Safe {
    public static void m1(int x) {
        Store s = new Store(1, 200);
        int i = 0; 
        int k = 0; 
        if (x>0){
            i = 1; 
        }
        for (int j = 0; j<100; j++){
            s.get_delivery(i);
            k++; 
        }
        s.get_delivery(k);
    
    }
}
