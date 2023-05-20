package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE


public class FitsReserver_If_Bound_Safe{
    public static void m1(int x) {
        Store s = new Store(15, 20);
        int i = 0; 
        if (x>0){
            i = 10; 
        }
        s.get_delivery(i);
    
    }
}


