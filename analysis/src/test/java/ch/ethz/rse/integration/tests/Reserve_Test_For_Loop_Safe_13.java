package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_13 {

  public static void m1() {
    Store s = new Store(1, 100);
    int i = 2;
    while(i <= 100) { 
        i = 2*i;        
        i = i-1;   
        s.get_delivery(1);
    }

  }
}


