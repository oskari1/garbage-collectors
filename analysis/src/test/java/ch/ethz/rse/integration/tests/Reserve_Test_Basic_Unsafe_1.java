package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_Basic_Unsafe_1 {

  public static void m1(int i) {
    Store s = new Store(2, 3);
    s.get_delivery(i);
  }
}


