package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_If_Reserve_Safe_3 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    Store s1 = new Store(3, 4);
    if(i > 5) {
        if(i > 10) {
            s.get_delivery(1);
            s1.get_delivery(1);
        } else {
            s.get_delivery(1); // UNSAFE if replaced by s.get_delivery(2)
            s1.get_delivery(1); // UNSAFE if replaced by s.get_delivery(2)
        }
        s.get_delivery(2);
        s1.get_delivery(3);
    } else {
        s.get_delivery(3);
        s1.get_delivery(4);
    }
  }
}


