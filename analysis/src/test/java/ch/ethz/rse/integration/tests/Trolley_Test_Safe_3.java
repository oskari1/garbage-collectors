package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_3{

  public static void m1(int i) {
    Store s = new Store(3, 3);
    if(i>5) {
      s = new Store(-1, 12);
    } else if(i == 6) {
      s = new Store(-12, 123); // NOT REACHABLE hence SAFE
    } 

    s = new Store(1,1); 
    s.get_delivery(1);
    
  }
}