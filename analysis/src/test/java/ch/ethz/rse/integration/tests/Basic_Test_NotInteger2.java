package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_NotInteger2 {
    public static void m1 (int x) {
        Store s = new Store(5, 15);
        int i = 3; 
        short j = 4; 
        byte k = 5; 
        boolean l = false; 
        if (x>0){
            i = 1; 
            j = 2; 
            k = 3; 
        }
        s.get_delivery(i);
        s.get_delivery(j+k);
        if (l){
            s.get_delivery(k); 
        }
      }
}
