package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Unsafe_1 {

	public void m2(int j) {
		Store s = new Store(1, 2);
		int i = 5;
		if(-1 <= j) {
		  i = j;
		}
		s.get_delivery(i);
	  }
}
