package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Safe_8 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		if(k == 0 || k > 10) { 
			// here k >= 0, so safe
			s.get_delivery(k);
		}
	  }
}
