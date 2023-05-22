package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class Trolley_Test_Safe_1 {

  public static void m1() {
    Store p = new Store(3, 4);
    Store q = new Store(2, 4);
    Store z;
    int i = 0;
    int j = -1;
    if(i == j+1) {
      z = p;
    } else {
      z = q;
    }
    z.get_delivery(3); 
    // this is UNSAFE since our analysis does not check the program
    // semantics (i.e., not like in lecture with abstract interpretation)
    // that's why z is believed to either alias p or q and in the latter
    // case this would be UNSAFE
  }
}