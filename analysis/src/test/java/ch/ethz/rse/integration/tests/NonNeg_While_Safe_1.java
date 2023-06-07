package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_While_Safe_1 {
    public static void m1(int i) {
        Store s = new Store(3, 10);
        int y = 7;
        while(i >= 0) {
            y++;
            s.get_delivery(i);
            i--;
            s.get_delivery(y);
        }
    }
}
