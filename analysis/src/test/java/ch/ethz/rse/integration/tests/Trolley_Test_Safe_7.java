package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_7 {

  public static void m1() {
    Store s= new Store(-2,1); 

    for (int i = 0; i<3; i++){
      if (i<0){
        s.get_delivery(-1); 
      }
    }
    s = new Store(1,1); 
    s.get_delivery(1);
    
  }
}