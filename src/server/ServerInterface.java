package server;


import java.rmi.*;
import java.util.UUID;

import client.ClientInterface;

/**
 * This is an interface for the RMI game server.
 */
public interface ServerInterface extends Remote {
	public UUID join(String name, ClientInterface client) throws RemoteException;
	public void getReady(UUID id) throws RemoteException;
	public void makeMove(UUID id, String dir) throws RemoteException;
	public void attack(UUID target) throws RemoteException;
	public int getWorldWidth() throws RemoteException;
	public int getWorldHeight() throws RemoteException;
}