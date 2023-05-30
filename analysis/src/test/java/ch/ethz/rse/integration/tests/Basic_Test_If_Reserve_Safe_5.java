package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_If_Reserve_Safe_5 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    if(i > 5) {
        s.get_delivery(2);
    } else if (i == 6) { // here i <= 5 so this is always false since it's unreachable
        s.get_delivery(100);
    } else {
        s.get_delivery(1);
    }

  }
}


