package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_20 {

  public static void m1(int k) {
    Store s = new Store(10, 100); 
    int i = 0;
    while(i < 100 && i < 50) {
      s.get_delivery(1); 
      i++;
    }
  }
}


