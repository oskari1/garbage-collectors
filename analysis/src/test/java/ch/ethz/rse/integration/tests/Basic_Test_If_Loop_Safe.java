package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_If_Loop_Safe {
    public static void m1(int j) {
        Store s = new Store(10, 100);
        int k = 1; 
        if (j>0){
            for (int i = 0; i<j; i++){
                k*=i; 
            }
        }
        
        s.get_delivery(k);
    
    }
}

