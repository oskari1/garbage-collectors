package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_If_Reserve_Safe_4 {

  public static void m1(int i) {
    Store s = new Store(4, 10);
    if(i > 5) {
        if(i > 10) {
            s.get_delivery(1);
            s.get_delivery(1);
            s.get_delivery(1);
            s.get_delivery(1);
            // at most 4 received until here
        } else {
            s.get_delivery(1); 
            // at most 1 received until here
        }
        s.get_delivery(2);
        // at most 6 received until here
    } else {
        s.get_delivery(3);
        // at most 3 received until here
    }
    s.get_delivery(4);
    // at most 10 received until here
  }
}


