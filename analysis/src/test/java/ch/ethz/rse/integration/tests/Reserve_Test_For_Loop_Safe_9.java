package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_9 {

  public static void m1() {
    Store s = new Store(2, 8);
    s.get_delivery(1);
    s.get_delivery(1);
    s.get_delivery(1);
    s.get_delivery(1);
    for(int i = 0; i < 4; i++) {
        s.get_delivery(1);
    }

  }
}


