package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Safe_5 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		if(0 > 1) { 
			// this call is unreachable, so it should be safe
			s.get_delivery(k);
		}
	  }
}
