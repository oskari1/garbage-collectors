package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_2 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    if(i>5) {
      s.get_delivery(2);
    } else if(i == 6) {
      s.get_delivery(100); // NOT REACHABLE hence SAFE
    } else {
      s.get_delivery(1);
    }
    
  }
}