package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_Var_Unsafe_2 {

  public static void m1(int i) {
    Store s = new Store(2, 4);
    s.get_delivery(i);
  }
}