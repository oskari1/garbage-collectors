package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_20 {

  public static void m1() {
    Store s = new Store(1, 100000); // 11 + 29 = 40
    int i = 1;
    while(i <= 64) { 
        s.get_delivery(1);
        i = 2*i;
    }
    // here i = 128
    int j = 128;
    while(j<=156) {
        s.get_delivery(1);
        j++;
    }

  }
}


