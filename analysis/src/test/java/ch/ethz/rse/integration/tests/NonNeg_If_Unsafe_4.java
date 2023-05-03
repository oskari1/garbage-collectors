package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Unsafe_4 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		// if(0 <= k) { 
		if(0 > k) { 
			s.get_delivery(k); 
		} else {
			// here k is in [0, +oo]
			s.get_delivery(k);
		}
	  }
}
