package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_Var_Safe_2 {

  public static void m1() {
    Store s = new Store(2, 4);
    //int a = 0;
    //int b = a;
    // int c = b+a;
    int a, b, c, d;
    a = 0;
    b = 0;
    c = a+b;
    d = c;
    // note that by directly passing a or b, the Verifier goes into the case
    // IntConstant. The only reason I can imagine for this is that during the 
    // translation to Jimple, some sort of optimization happens. But it's weird
    // nevertheless...
    s.get_delivery(c+d);
  }
}