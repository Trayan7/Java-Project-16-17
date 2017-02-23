package server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.List;

import client.ControlInterface;

/**
 * This is an interface for the RMI game server.
 */
public interface ServerInterface extends Remote {
	
	public final String RMI_NAME = "gameserver";
	
	/**
	 * Register a client at the server.
	 * 
	 * @param u the client to register
	 * @param name player name
	 * @return
	 * @throws RemoteException
	 */
	public boolean register(ControlInterface u, String name) throws RemoteException;
	
	/**
	 * Retrieve a list of users
	 * 
	 * @return the list of all logged in users
	 * @throws RemoteException
	 */
	public List<String> getUsers() throws RemoteException;
	
	/**
	 * Removes a client from the server
	 * 
	 * @param u the client to remove
	 * @throws RemoteException
	 */
	public void logout(ControlInterface u) throws RemoteException;
	
	public void send(String msg) throws RemoteException;
}
