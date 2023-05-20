package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Trolley_Test_Unsafe {

  public static void m1() {
    Store s = new Store(2, 4);
    s.get_delivery(2);
    s.get_delivery(1);

    Store s2 = new Store(4, 4);
    s2.get_delivery(5);
  }
}