package client;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import common.BattleInterface;
import common.Player;
import common.World;
import server.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ServerInterface server;
	
	int health = 100;
	
	int x = 0;
	
	int y = 0;
	
	String nextAction = "";
	
	World world = new World("empty");
	
	private BattleInterface battle;
	
	private HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
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
				if (this.battle != null) {
					//Gonna fight
					ArrayList<Player> targets = battle.getPlayers();
					Collections.shuffle(targets);
					int decision = control.getTarget(targets);
					if (decision == 0) {
						System.out.println("Decision is faulty.");
						continue;
					}
					if (this.battle == null) {
						System.out.println("No battle running anymore.");
						continue;
					}
					Player target = targets.get(decision - 1);
					boolean validTarget = false;
					for (Player player: this.battle.getPlayers()) {
						if (player.getName().equals(target.getName())) {
							validTarget = true;
							break;
						}
					}
					if (!validTarget) {
						System.out.println("Target became invalid.");
						continue;
					}
					battle.attack(target.getID());
				} else {
					//Move it move it
					//Could add stuff like "pick up item" or something like that later					
					switch (nextAction) {
					case "move":
						String dir = control.getMoveDirection(x, y);
						server.makeMove(id, dir);
						nextAction = "";
						break;
					}
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
		
		if (health <= 0) {
			System.out.println("YOU DIED");
		    System.exit(0);
		}
	}
	
	/**
	 * Called by server, asks the player for his next move
	 */
	public void askForMove() throws RemoteException {
		this.nextAction = "move";
	}
	
	public void startBattle(BattleInterface battle) throws RemoteException {
		this.battle = battle;
	}
	
	public void stopBattle() throws RemoteException {
		this.battle = null;
	}
	
	public void winGame() throws RemoteException {
		System.out.println("YOU WIN");
		System.exit(0);
	}
}