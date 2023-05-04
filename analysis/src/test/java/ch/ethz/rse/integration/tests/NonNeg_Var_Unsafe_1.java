package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_Var_Unsafe_1 {

  public static void m1() {
    Store s = new Store(2, 4);
    //int a = 0;
    //int b = a;
    // int c = b+a;
    int a, b, c;
    a = 0;
    b = -1;
    c = a+b; // c = -1
    s.get_delivery(c);
  }
}