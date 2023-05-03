// DISABLED (by removing this line, you can enable this test to check if you are sound)
package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Unsafe_3 {

	public void m2(int k) {
		Store s = new Store(1, 2);
		int i = 1;
		if(0 <= i+k) { // true if k = -1 
		  s.get_delivery(k); // here k is in [-1, +oo]
		}
	  }
}
