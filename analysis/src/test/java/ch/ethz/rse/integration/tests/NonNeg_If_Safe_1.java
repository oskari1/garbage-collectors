// DISABLED (by removing this line, you can enable this test to check if you are sound)
package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Safe_1 {

	public void m2(int j, int i) {
		Store s = new Store(1, 2);
		if(0 <= i+j)
		  s.get_delivery(i+j);
	  }
}
