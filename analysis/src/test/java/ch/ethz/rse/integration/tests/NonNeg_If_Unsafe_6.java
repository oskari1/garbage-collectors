package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Unsafe_6 {

  public static void m1() {
    Store s = new Store(2, 4);
    int a, b;
    a = 1;
    b = -1;
    s.get_delivery(b+((a+b)*a)); // -1 + (0*1) = -1 
  }
}