import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.math.BigInteger;
public class BumperServant extends UnicastRemoteObject implements Bumper {
    BigInteger val = new BigInteger("0");
    public BumperServant() throws RemoteException {
    }
    public boolean bump() throws RemoteException {
        // A call on bump() adds 1 to a BigInteger held by the service.
        // It then returns true on completion.
        // The BigInteger is changed by the call on bump(). That is,
        // 1 is added to the BigInteger and that value persists until
        // another call on bump occurs.
        val = val.add(new BigInteger("1"));
        return true;
    }

    public BigInteger get() throws RemoteException {
        // a call on get returns the BigInteger held by the service
        return val;
    }
}