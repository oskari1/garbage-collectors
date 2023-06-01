package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Safe_25 {

  public static void m1() {
    Store s = new Store(1, 3);
    int i = 0;
    while(i < 6) {
        if(i < 3) {
            // deemed SAFE since here i in [0,2] (3 possible states) and indeed SAFE
            s.get_delivery(1);
        }
        i++;
    }  
  }
}



