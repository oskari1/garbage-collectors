package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Safe_19 {

  public static void m1() {
    Store s = new Store(1,342); // SAFE and precise but 341 is UNSAFE
    int i = 1;
    while(i <= 1024) { 
        s.get_delivery(1);
        for(int k = 0; k < 3; k++) {
            i++;
        }
    }

  }
}


