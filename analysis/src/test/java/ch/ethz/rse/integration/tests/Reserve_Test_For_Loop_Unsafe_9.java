package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_9 {

  public static void m1() {
    Store s = new Store(2, 199); 
    int i = 1;
    while(i <= 200) {
        s.get_delivery(1);
        if(i < 128) {
            i = 2*i;
        } else {
            i = i+1;
        }
    }


  }
}


