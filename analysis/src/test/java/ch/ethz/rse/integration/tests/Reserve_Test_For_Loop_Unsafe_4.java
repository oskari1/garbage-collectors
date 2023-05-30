package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_4 {

  public static void m1() {
    Store s = new Store(2, 4);
    int i = 0;
    while(i < 5) {
        s.get_delivery(1);
        if(i==3) {
            break;
        }
        i = i+1;
    }


  }
}


