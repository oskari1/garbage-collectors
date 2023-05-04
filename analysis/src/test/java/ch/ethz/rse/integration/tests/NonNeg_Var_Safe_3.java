package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_Var_Safe_3 {

  public static void m1() {
    Store s = new Store(2, 4);
    //int a = 0;
    //int b = a;
    // int c = b+a;
    int a, b, c, d;
    a = 1;
    b = 2;
    c = b+((a+b)*a); // c = 2+((1+2)*1) = 2+(3*1) = 5
    d = c+((c+b)*b); // d > 0
    s.get_delivery(c+d); // c + d > 5, so FITS_IN_TROLLEY and FITS_IN_RESERVE are both unsafe
  }
}