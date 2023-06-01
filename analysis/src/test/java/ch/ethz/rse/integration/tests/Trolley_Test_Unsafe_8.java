package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Trolley_Test_Unsafe_8 {

  public static void m1(int i) {
    if (i<1000 && i>500){
      i = i-500; 
    }
    Store s = new Store(500, 500); 
    s.get_delivery(i);
  }
}