package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Unsafe_1 {

	public void m2(int j, int i) {
		Store s = new Store(1, 2);
		if(0 > i+j)
		  s.get_delivery(i+j);
	  }
}
