package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_2 {

  public static void m1() {
    Store s = new Store(3, 99);
    int i = 100;
    while(i > 0) {
      s.get_delivery(1);
      i--;
    }
  }
}


