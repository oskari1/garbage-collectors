package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Huge_Loop_Safe {
    public static void m1() {
        Store s = new Store(8, 30);
        int j = 1; 
        for(int i = -2; i<100_000_000; i++){
            j++; 
        }
        s.get_delivery(j);
        
    
    }
}
