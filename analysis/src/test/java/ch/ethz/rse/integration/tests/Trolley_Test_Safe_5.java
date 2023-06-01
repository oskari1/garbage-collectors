package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_5 {

  public static void m1(int i) {
    Store s = new Store(3, 3);
    Store t = new Store(1, 2); 
    Store temp = new Store (12, 12); 
    temp = s; 
    s = t; 
    t = temp; 
    t.get_delivery(2);
    
  }
}