package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_If_Reserve_Safe_1 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    if(i > 5) {
        if(i > 10) {
            s.get_delivery(1);
        } else {
            s.get_delivery(1); // UNSAFE if replaced by s.get_delivery(2)
        }
        s.get_delivery(2);
    } else {
        s.get_delivery(3);
    }
  }
}


