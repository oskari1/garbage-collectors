package ch.ethz.rse.integration.tests;

import ch.ethz.rse.Store;


public class Basic_Test_Constant_Safe{
    public static void m1() {
        Store s = new Store(2, 4);
        s.get_delivery(2);
        s.get_delivery(1);
    
    }
}


