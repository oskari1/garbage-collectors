package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_10 {

  public static void m1() {
    Store s = new Store(2, 17); // 16 is UNSAFE but 17 is SAFE
    int i = 100;
    int j = 1;
    while(j <= i) {
        s.get_delivery(1);
        i = i+1;
        j = 2*j;
    }

  }
}


