package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Trolley_Test_Unsafe_9 {

  public static void m1() {
    Store p = new Store(3, 4);

    for (int i = 0; i<5; i++){
      int j = i-2; 
      if (i>2 && j>i){
        p.get_delivery(-1); 
      } else {
        p.get_delivery(4); 
      }
    }
    
  }
}