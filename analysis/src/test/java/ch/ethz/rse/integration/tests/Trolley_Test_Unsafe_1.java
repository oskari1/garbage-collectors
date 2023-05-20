package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Unsafe_1 {

  public static void m1() {
    Store s = new Store(2, 4);
    int i = 2;
    int j = i-1;
    s.get_delivery(i+j);
  }
}