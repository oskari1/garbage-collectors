package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_Basic_Unsafe_2 {

  public static void m1() {
    Store s = new Store(4, 20);
    s.get_delivery(4);
    for(int i = 0; i < 10; i+=2) {
      s.get_delivery(3);
    }
  }
}


