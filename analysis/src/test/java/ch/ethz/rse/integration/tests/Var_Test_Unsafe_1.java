package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Var_Test_Unsafe_1 {

  public static void m1(int i) {
    Store s = new Store(2, 4);
    s.get_delivery(i);
  }
}