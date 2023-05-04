package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_Var_Safe_1 {

  public static void m1() {
    Store s = new Store(2, 4);
    int a, b, c;
    a = 0;
    b = 0;
    c = a+b;
    s.get_delivery(c);
  }
}