package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class Reserve_Test_Basic_Unsafe_1 {

  public static void m1() {
    Store s = new Store(2, 3);
    Store s1 = s;
    Store s2 = new Store(1, 3);
    Store s3 = s2;
    s.get_delivery(1);
    s1.get_delivery(1);
    // AS at line 13 hs received 2 so far

    s2.get_delivery(1);
    s3.get_delivery(1);
    s3.get_delivery(1);
    // AS at line 15 has received 3 so far

    s3 = s1;
    s3.get_delivery(2);
    // AS at line 13 has received 4 so unsafe
    // AS at line 15 should not be affected by this so safe
  }
}


