package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Trolley_Test_Unsafe_4 {

  public static void m1(int x) {
    Store p = new Store(2, 4);
    for (int i = 0; i<x; i++){
      p.get_delivery(i);
    }
  }
}