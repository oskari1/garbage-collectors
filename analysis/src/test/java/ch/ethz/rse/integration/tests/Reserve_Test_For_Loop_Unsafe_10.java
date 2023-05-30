package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_10 {

  public static void m1(int k) {
    Store s = new Store(10, 20); 
    int i = 0;
    if(k < 5) {
      s.get_delivery(10);
      return;
    } else {
      while(i<2) {
        s.get_delivery(1);
      }
    }
  }
}


