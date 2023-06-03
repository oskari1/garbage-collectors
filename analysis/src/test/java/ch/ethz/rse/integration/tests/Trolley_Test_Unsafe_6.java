package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Unsafe_6 {

  public static void m1() {
    Store p = new Store(-5, 4);
    p.get_delivery(-4);    
  }
}