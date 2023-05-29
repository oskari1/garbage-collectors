package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Variable_Unsafe_2 {
    public static void m1() {
        Store s = new Store(4, 19);
        for(int i = 0; i<5; i++){
            s.get_delivery(i);
        }
        
    
    }
}