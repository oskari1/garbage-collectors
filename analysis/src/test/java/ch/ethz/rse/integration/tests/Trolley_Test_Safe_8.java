package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_8 {

  public static void m1(int i) {
    Store s = new Store(500, 500); 
    if (false){ // unreachable 
      s = new Store(1,2); 
      s.get_delivery(-1); // to check NON_Negative
    }
    s.get_delivery(10);
  }
}