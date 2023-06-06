package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_30 {

  public static void m1(int k) {
    Store s = new Store(3,150);
    int i = 105;
    while(i > 5) {
      s.get_delivery(3);
      i-=2;
    }
  }
}


