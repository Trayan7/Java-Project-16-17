import java.rmi.*;
import java.rmi.server.*;
import java.util.List;

/**
 * This is an interface for the RMI game server.
 */
public interface GameInterface extends Remote {
	
	public final String RMI_NAME = "gameserver";
	
	/**
	 * Register a client at the server.
	 * 
	 * @param u the client to register
	 * @param name player name
	 * @return
	 * @throws RemoteException
	 */
	public boolean register(ConsoleControl u, String name) throws RemoteException;
	
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
	public void logout(ConsoleControl u) throws RemoteException;
	
	public void send(String msg) throws RemoteException;
}
