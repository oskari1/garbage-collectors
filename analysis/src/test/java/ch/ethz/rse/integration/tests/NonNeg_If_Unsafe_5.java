// DISABLED (by removing this line, you can enable this test to check if you are sound)
package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Unsafe_5 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		// if(0 <= k) { 
		if(0 > k) { 
		} else {
			// here k is in [0, +oo]
			int j = k-1;
			s.get_delivery(j);
		}
	  }
}
