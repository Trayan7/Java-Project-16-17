import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This implements a simple chat server via rmi. The server has a map of clients
 * and can send messages to them.
 */
public class ServerImpl extends UnicastRemoteObject implements Server {

    private static final long serialVersionUID = 3445L;

    /** The map of connected clients */
    private Map<ConsoleControl, String> clients;

    /**
     * Creates a new {@code ChatServerImpl} instance.
     * 
     * @exception RemoteException
     *                if an error occurs
     */
    public ServerImpl() throws RemoteException {
        clients = new HashMap<>();
    }

	@Override
	public boolean register(ConsoleControl u, String name) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getUsers() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(ConsoleControl u) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
