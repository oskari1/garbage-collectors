package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_NotInteger {
    public static void m1 () {
        Store s = new Store(5, 15);
        int i = 3; 
        short j = 4; 
        byte k = 5; 
        boolean l = false; 
        s.get_delivery(i);
        s.get_delivery(j);
        s.get_delivery(k);
        if (!l){
            s.get_delivery(k); 
        }
      }
}
