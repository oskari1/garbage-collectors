package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_21 {

  public static void m1() {
    Store s = new Store(2, 29); // 28 is UNSAFE, 29 is SAFE 
    int i = 1;
    // i in [1,1]
    while(i < 30) {
        // i in [1,29]
        s.get_delivery(1);
        // i in [1,29]
        if(i < 10) {
            // i in [1,9]
            i = 2*i;
            // i in [2,18]
        } else {
            // i in [10,29]
            i = i+1;
            // i in [11,30]
        }
        // i in [2,30]
    }

  }
}


