package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_Basic_Safe_2 {

  public static void m1() {
    Store s = new Store(2, 4);
    Store s1 = s;
    s.get_delivery(-1);
    s1.get_delivery(5);
  }
}


