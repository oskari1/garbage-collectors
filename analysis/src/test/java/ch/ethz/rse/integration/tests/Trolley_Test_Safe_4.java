package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_4 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    if(i>5) {
      s.get_delivery(2);
    } else if (i==6) { // unreachable
      s = new Store(-1, 3); 
    }
    s.get_delivery(1);
  }
}