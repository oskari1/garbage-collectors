package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_12 {

  public static void m1() {
    Store s = new Store(1, 10000000);
    int i = 1;
    int j = 2;
    while(i*j <= 100) {
        s.get_delivery(1);
        i = i+1;
        j = 2*j;
    }



  }
}


