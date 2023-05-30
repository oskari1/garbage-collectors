package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_8 {

  public static void m1() {
    Store s = new Store(2, 6);
    int i = 0;
    while(i < 11) {
        s.get_delivery(1);
        i = i+2;
    }



  }
}


