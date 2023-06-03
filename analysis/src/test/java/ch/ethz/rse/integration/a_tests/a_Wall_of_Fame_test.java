package ch.ethz.rse.integration.a_tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE UNSAFE

public class a_Wall_of_Fame_test {

	public void m2(int j) {
		Store s = new Store(1, 2);
		for(int i = 0; i < 2; i++) {
			Store a = new Store(1,1);
			if(i==0) {
				s=a;
			}
			s.get_delivery(1);
		}
	  }
}
