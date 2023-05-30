package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_18 {

  public static void m1() {
    Store s = new Store(1,342); // SAFE but UNSAFE for 341
    int i = 1;
    while(i <= 1024) { 
        s.get_delivery(1);
        i = i+3;
    }

  }
}


