package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Safe_4 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		if(0 > k) { 
		} else {
			// here k is in [0, +oo]
			s.get_delivery(k);
		}
	  }
}
