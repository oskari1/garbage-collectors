package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Safe_7 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		if(k == 0) { 
			// here k is always 0, so safe
			s.get_delivery(k);
		}
	  }
}
