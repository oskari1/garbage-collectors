package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Test_1_Safe {

  public static void m1() {
    Store s = new Store(2, 4);
    int a, b;
    a = 1;
    b = -1;
    // c = b+((a+b)*a); // -1 + (0*1) = -1 
    // d = c+((c+b)*a); // -1 + (-2 * 1) = -1 - 2 = -3
    // note that by directly passing a or b, the Verifier goes into the case
    // IntConstant. The only reason I can imagine for this is that during the 
    // translation to Jimple, some sort of optimization happens. But it's weird
    // nevertheless...
    s.get_delivery(b+((a+b)*a)); // -1 + (0*1) = -1 
  }
}