package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_13 {

  public static void m1(int k) {
    Store s = new Store(10, 50); 
    int i = 0;
    while(i < 50 || i < 100) {
      s.get_delivery(1); 
      i++;
    }
  }
}


