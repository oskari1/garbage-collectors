package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_6 {

  public static void m1(int i) {
    Store s = new Store(-5, 3);
    s.get_delivery(-6);
    
  }
}