// DISABLED (by removing this line, you can enable this test to check if you are sound)
package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class NonNeg_If_Safe_2 {

	public void m2() {
		Store s = new Store(1, 2);
		int a = 0;
		int b = 0;
		// int j = a*(a+b);
		if(0 <= a*(a+b))
		  s.get_delivery(a*(a+b));
	  }
}
