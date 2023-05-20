package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe {

  public static void m1() {
    Store s = new Store(2, 4);
    int i = 1;
    int j = i;
    s.get_delivery(i+j);
  }
}