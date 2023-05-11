package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Safe_6 {

	public void m2() {
		Store s = new Store(1, 2);
		int k = -1;
		if(k != -1) { 
			// this call is unreachable, so it should be safe
			s.get_delivery(k);
		}
	  }
}
