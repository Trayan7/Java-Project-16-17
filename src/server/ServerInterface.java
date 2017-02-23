package server;


import java.rmi.*;

/**
 * This is an interface for the RMI game server.
 */
public interface ServerInterface extends Remote {
	public void send(String msg) throws RemoteException;
}