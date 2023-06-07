package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_21 {

  public static void m1(int k) {
    Store s = new Store(10, 100); 
    for(int i = 0; i < 100 && i < 50; i++) {
      s.get_delivery(1);
    }
  }
}


