package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_5 {

  public static void m1() {
    Store s = new Store(10, 20);
    int i = 0;
    while(i<2) {
        s.get_delivery(i+1); 
    }

  }
}


