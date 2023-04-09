import java.rmi.*;
import java.math.BigInteger;
public class BumperServer {
    public static void main(String args[]){
        System.out.println("Bumper Server Running");
        try{
            // create the servant
            Bumper b = new BumperServant();
            System.out.println("Created Bumper object");
            System.out.println("Placing in registry");
            // publish to registry
            Naming.rebind("bumper", b);
            System.out.println("CalculatorServant object ready");
        }catch(Exception e) {
            System.out.println("CalculatorServer error main " + e.getMessage());
        }
    }
}