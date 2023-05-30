package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_11 {

  public static void m1() {
    Store s = new Store(1, 50);
    int i = 1;
    while(i <= 76) {
        s.get_delivery(1);
        i = 2*i+4;
    }


  }
}


