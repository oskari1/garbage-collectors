package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_15 {

  public static void m1(int k) {
    Store s = new Store(2, 4);
    int i = 1;
    if(k > 0) {
        while(i > 0) { // infinite loop
            s.get_delivery(-1);
        }
    } else {
        s.get_delivery(1);
    }

  }
}


