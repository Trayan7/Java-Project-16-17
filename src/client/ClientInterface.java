package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	public void updateData(int health, int x, int y) throws RemoteException;
	public void askForMove() throws RemoteException;
}
