package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_For_Loop_Unsafe_7 {

  public static void m1() {
    Store s = new Store(2, 48); // also UNSAFE for reserve_size 100000000
    int f0 = 2;
    int f1 = 1;
    int tmp;
    while(f0 <= 47) { // f0 = 2, 1, 3, 4, 7, 11, 18, 29, 47, ...
        s.get_delivery(1);
        tmp = f1;
        f1 = f0+f1;
        f0 = tmp;
    }

  }
}


