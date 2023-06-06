package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_28 {

  public static void m1(int k) {
    Store s = new Store(10, 49); 
    int i = 0;
    for(;;) {
      s.get_delivery(0);
    }
  }
}


