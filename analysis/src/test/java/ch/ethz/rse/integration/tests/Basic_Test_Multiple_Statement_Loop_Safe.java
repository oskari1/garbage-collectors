package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_Multiple_Statement_Loop_Safe {
    public static void m1(int x) {
        Store s = new Store(20, 100);
        int j = 0; 
        int k = 12; 
        for(int i = 10; i>0; i--){
            s.get_delivery(1);
            j++;
        }
        s.get_delivery(j);
        
    
    }
}

