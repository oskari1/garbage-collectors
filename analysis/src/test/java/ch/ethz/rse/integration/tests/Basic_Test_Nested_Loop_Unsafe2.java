package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;

// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Nested_Loop_Unsafe2 {
    public static void m1(int x) {
        Store s = new Store(14, 100);
        int j = 0; 
        int l = -1; 

        for(int i = 0; i<1000; i++){
            j++; 
            if (x<123){
                for (int k = 0; k<1000; k++){
                    j++;
                    l++; 
                }  
            }
            
        }

        s.get_delivery(j);
        s.get_delivery(l); 
        
    
    }
}