package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;


public class Basic_Test_Variable_Safe{
    public static void m1() {
        Store s = new Store(4, 15);
        for(int i = 0; i<5; i++){
            s.get_delivery(i);
        }
        
    
    }
}