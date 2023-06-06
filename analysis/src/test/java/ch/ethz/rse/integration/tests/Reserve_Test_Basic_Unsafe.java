package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_Basic_Unsafe {

  public static void m1() {
    Store s = new Store(2, 3);
    Store s1 = s;
    s.get_delivery(1);
    s1.get_delivery(1);
    s.get_delivery(1);
    s1.get_delivery(1);
  }
}


