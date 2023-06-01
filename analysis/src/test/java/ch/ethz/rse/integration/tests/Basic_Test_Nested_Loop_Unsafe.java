package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Nested_Loop_Unsafe {
    public static void m1() {
        Store s = new Store(10, 100);
        int j = 0; 

        for(int i = 0; i<10; i++){
            j++; 
            for (int k = 0; k<2; k++){
                j--;
            }
        }

        s.get_delivery(j);
        
    
    }
}