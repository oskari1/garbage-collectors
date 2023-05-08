package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Unsafe_3 {

	public void m2(int j) {
		Store s = new Store(1, 2);
		int i = 5;
		if(-1 <= j) {
			int k = 2*j;
		}
		i = j+i;
		s.get_delivery(i);
	  }
}
