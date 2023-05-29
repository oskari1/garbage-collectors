package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Multiple_Statement_Loop_Safe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        int j = 0; 
        int k = 12; 
        for(int i = 10; i>0; i--){
            s.get_delivery(1); // called 10 times (10, 9, ..., 1)
            if (x>0){
                j++; // if x >= 1, j is incremented 10 times
                if (x>1){
                    k++; // if x >= 2, k is incremented 10 times
                }
                
            }
             
        }
        s.get_delivery(j); // here j could be at most 10
        s.get_delivery(k); // here k could be at most 22 => FITS_IN_TROLLEY UNSAFE
        
    
    }
}

