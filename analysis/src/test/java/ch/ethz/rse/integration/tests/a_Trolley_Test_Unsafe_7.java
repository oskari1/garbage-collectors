package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class a_Trolley_Test_Unsafe_7 {

  public static void m1() {
    int x = 0; 
    for (int i = 0; i<10; i++){
      x++; 
    }
    while (x>=0){
      x--; 
    }
    Store s = new Store(-3, 5); 
    s.get_delivery(x);

  }
}