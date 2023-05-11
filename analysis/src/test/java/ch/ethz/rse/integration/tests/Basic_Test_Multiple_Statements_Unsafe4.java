
package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_Multiple_Statements_Unsafe4 {
    public static void m1() {
        Store s = new Store(20, 100);
        int k = -2; 
        s.get_delivery(3);
        s.get_delivery(k); 
        
        
    }
}
