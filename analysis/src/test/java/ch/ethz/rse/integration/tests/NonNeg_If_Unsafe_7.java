package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class NonNeg_If_Unsafe_7 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		if(k != 0 && k < 1) { 
			// here k <= -1, so unsafe BUT passing this to RSE server, it should be UNSAFE (master solution is not more precise)
			s.get_delivery(k);
		}
	  }
}
