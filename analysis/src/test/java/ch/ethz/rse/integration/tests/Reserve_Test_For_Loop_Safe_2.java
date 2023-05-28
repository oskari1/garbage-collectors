package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_2 {

  public static void m1() {
    Store s = new Store(3, 100);
    int i = 50;
    while(i > 0) {
      s.get_delivery(2);
      i--;
    }
  }
}


