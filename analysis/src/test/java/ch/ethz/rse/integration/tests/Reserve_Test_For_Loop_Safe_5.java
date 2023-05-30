package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_5 {

  public static void m1() {
    Store s = new Store(8, 30);
    for(int i = 2; i<10; i++){
      s.get_delivery(1);
    }

  }
}


