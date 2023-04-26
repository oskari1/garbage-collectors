package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Var_Test_Safe {

  public static void m1() {
    Store s = new Store(2, 4);
    int a = 0;
    int b = a;
    s.get_delivery(a+b);
  }
}