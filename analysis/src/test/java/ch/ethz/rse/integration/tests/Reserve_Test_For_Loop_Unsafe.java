package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe {

  public static void m1() {
    Store s = new Store(3, 3);
    for(int i = 0; i < 3; i++) {
      s.get_delivery(2);
    }
  }
}


