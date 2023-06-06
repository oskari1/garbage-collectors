package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_Reserve_Safe_1 {

  public static void m1(int i, int k) {
    Store s = new Store(3, 3);
    int l = 0;
    int j = k;
    s.get_delivery(l);
    if(k <= 0) {
      s.get_delivery(k);
    }
    if(j <= 0) {
      s.get_delivery(j);
    }
  }
}


