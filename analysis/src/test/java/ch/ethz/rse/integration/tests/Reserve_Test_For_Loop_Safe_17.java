package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Reserve_Test_For_Loop_Safe_17 {

  public static void m1() {
    int safe_reserve = 12034;
    int max_f1 = 12033;
    Store s = new Store(2, safe_reserve);
    int f0 = 0;
    int f1 = 1;
    int tmp;
    while(f1 <= max_f1) {
        s.get_delivery(1); // (f0,f1) = (0,1), (1,1), (1,2), (2,3), (3,5), ...
        tmp = f1;
        f1 = f0+f1;
        f0 = tmp;
    }

  }
}


