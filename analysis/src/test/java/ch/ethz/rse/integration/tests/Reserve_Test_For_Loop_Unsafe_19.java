package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_19 {

  public static void m1(int k) {
    Store s = new Store(10, 24); 
    int i = 52;
    while(i > 2) {
      s.get_delivery(1); 
      i-=2;
    }
  }
}


