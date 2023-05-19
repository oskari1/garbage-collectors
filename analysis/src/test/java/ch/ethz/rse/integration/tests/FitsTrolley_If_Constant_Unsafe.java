package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE SAFE

public class FitsTrolley_If_Constant_Unsafe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        Store t = new Store(10, 100); 
        
        if (x>0){
            s=t; 
        }
        s.get_delivery(15);
        
    
    }
}

