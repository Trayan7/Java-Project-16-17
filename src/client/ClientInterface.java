package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ClientInterface extends Remote {
	public void updateData(int health, int x, int y, HashMap<String, Byte> area) throws RemoteException;
	public void askForMove() throws RemoteException;
}
