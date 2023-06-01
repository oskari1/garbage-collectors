package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Unsafe_6 {

  public static void m1() {
    Store s = new Store(2, 20);
    int i = 0;
    while(i < 4) {
        int j = 0;
        while(j < 5) {
            s.get_delivery(1);    
            j = j+1;
        }
        i = i+1;
    }

  }
}


