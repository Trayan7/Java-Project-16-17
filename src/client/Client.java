package client;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import common.World;
import server.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ServerInterface server;
	
	int health = 100;
	
	int x = 0;
	
	int y = 0;
	
	String nextAction = "";
	
	World world = new World("empty");
	
	public Client() throws RemoteException {
		System.out.println("Started client");
		
		ConsoleControl control = new ConsoleControl(world);
		
		//String host = control.getHost();
		//Integer port = control.getPort();
		
		String uri = "rmi://localhost:1099/ServerInterface";
		
		try {
			server = (ServerInterface) Naming.lookup(uri);
			
			UUID id = null;
			while (id == null) {
				String username = control.getUsername();
				id = server.join(username, this);
			}
			
			control.waitUntilReady();
			//Ready
			server.getReady(id);
			
			//While we're in the game
			while (health > 0) {
				switch (nextAction) {
					case "move":
						String dir = control.getMoveDirection(x, y);
						server.makeMove(id, dir);
						nextAction = "";
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateData(int health, int x, int y, HashMap<String, Byte> area) throws RemoteException {
		this.health = health;
		this.x = x;
		this.y = y;
		System.out.println("Amount received fields: " + area.size());
		if (area.containsKey("North")) world.setBiome(this.x, this.y - 1, area.get("North"));
		if (area.containsKey("East")) world.setBiome(this.x + 1, this.y, area.get("East"));
		if (area.containsKey("West")) world.setBiome(this.x - 1, this.y, area.get("West"));
		if (area.containsKey("South")) world.setBiome(this.x, this.y + 1, area.get("South"));
		if (area.containsKey("Stay")) world.setBiome(this.x, this.y, area.get("Stay"));
		
		System.out.println("Updated data:");
		System.out.println("Health:\t\t" + this.health);
		System.out.println("X-Coord:\t" + this.x);
		System.out.println("Y-Coord:\t" + this.y);
	}
	
	/**
	 * Called by server, asks the player for his next move
	 */
	public void askForMove() throws RemoteException {
		this.nextAction = "move";
	}
}