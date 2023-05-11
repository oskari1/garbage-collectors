package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_If_Multiple_Statements_Unsafe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        int j = 0; 
        if (x>0){
            s.get_delivery(1);
            j--;
        }
        s.get_delivery(j);
        
    }
}
