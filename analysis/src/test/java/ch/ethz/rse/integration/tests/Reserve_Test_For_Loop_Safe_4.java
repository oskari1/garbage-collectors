package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_4 {

  public static void m1() {
    Store s = new Store(3, 200);
    int i = 0;
    while(i < 50) {
      s.get_delivery(2); // here i = 0, 1, ..., 49, so s receives 2*50 = 100
      i++;
    }
    while(i > 0) {
      s.get_delivery(2); // here i = 50, 49, ..., 1, so s receives 2*50 = 100
      i--;
    }
  }
}


