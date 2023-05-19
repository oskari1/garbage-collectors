package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class FitsTrolley_If_Variable_Unsafe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        Store t = new Store(10, 100); 
        int i = 14; 
        if (x>0){
            s=t; 
            i++; 
        }

        s.get_delivery(i);
        
    
    }
}

