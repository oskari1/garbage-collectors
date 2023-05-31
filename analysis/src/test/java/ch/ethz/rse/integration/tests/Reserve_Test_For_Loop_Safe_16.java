package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Safe_16 {

  public static void m1(int k) {
    Store s = new Store(1, 100);
    int i = 5;
    int j = 5;
    while(i*j >= 1) { // 5*5, 4*4, 3*3, 2*2, 1*1, 0*0
        i = i-1;
        j = j-1;
        s.get_delivery(1);
    }

  }
}


