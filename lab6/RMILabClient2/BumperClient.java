//*******************************************************
// CalculatorClient.java
// This client gets a remote reference from the rmiregistry
// that is listening on the default port of 1099.
// It allows the client to quit with a "!".
// Otherwise, it computes the sum of two integers
// using the remote calculator.

import java.rmi.*;
import java.rmi.server.*;
import java.math.BigInteger;

public class BumperClient {
    public static void main(String args[]) throws Exception {
        
        // connect to the rmiregistry and get a remote reference to the Calculator
        // object.
        Bumper c  = (Bumper) Naming.lookup("//localhost/bumper");
        System.out.println("Found bumper");
        BigInteger ctr = new BigInteger("0");
        BigInteger n = new BigInteger("10000");
        long start = System.currentTimeMillis();
        System.out.println("haha1");
        while(!ctr.equals(n)) {
            try {
                c.bump();
                ctr = c.get();
                System.out.println(ctr);
                System.out.println(c.get());
            }
            catch(RemoteException e) {
                System.out.println("allComments: " + e.getMessage());
            }
        }
        long stop = System.currentTimeMillis();
        long duration = (stop - start) / 1000;
        System.out.println("value of the BigInteger held on the server is: " + ctr);
        System.out.println("the number of seconds that it took to call this service 10,000 times is:" + duration);
        System.exit(0);
    }
}