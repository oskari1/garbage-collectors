package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Reserve_Unsafe {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    s.get_delivery(1);
    s.get_delivery(1);
    s.get_delivery(1);
    s.get_delivery(1);
  }
}


