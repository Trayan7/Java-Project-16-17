package client;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
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
	
	private Scanner scanner = new Scanner(System.in);
	
	int health = 100;
	
	int x = 0;
	
	int y = 0;
	
	String nextAction = "";
	
	//TODO Ask Server for world
	World world = new World("empty");
	
	private BattleInterface battle;
	
	private HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
	String type;
	
	ConsoleControl console;
	GUIControl gui;
	
	
	public Client() throws RemoteException {
		System.out.println("To start with GUI enter: \"GUIControl\"");
		System.out.println("To start in console enter: \"ConsoleControl\"");
		
		type = scanner.next();
		scanner.nextLine();
		
		while(!type.equals("GUIControl") && !type.equals("ConsoleControl")) {
			System.out.println(type + " is not a valid control.");
			System.out.println("Enter: : \"GUIControl\" or \"ConsoleControl\"");
			type = scanner.nextLine();
		}
		
		String host;
		int port;
		if(type.equals("ConsoleControl")) {
			console = new ConsoleControl(world);
			host = console.getHost();
			port = console.getPort();
		} else {
			gui = new GUIControl(world);
			host = gui.getHost();
			port = gui.getPort();
		}
		
		String uri = "rmi://"+ host +":"+ port +"/ServerInterface";
		
		try {
			server = (ServerInterface) Naming.lookup(uri);
			
			UUID id = null;
			while (id == null) {
				String username;
				if(type.equals("ConsoleControl")) {
					username = console.getUsername();				
				} else {
					username = gui.getUsername();
				}
				id = server.join(username, this);
			}
			
			if(type.equals("ConsoleControl")) {
				console.waitUntilReady();				
			} else {
				gui.waitUntilReady();
			}
			//Ready
			server.getReady(id);
			
			//While we're in the game
			//(Busy waiting, but if we use wait() we need a lock)
			while (health > 0) {
				if (this.battle != null) {
					//Gonna fight
					ArrayList<Player> targets = battle.getPlayers();
					Collections.shuffle(targets);
					
					int decision;
					if(type.equals("ConsoleControl")) {
						decision = console.getTarget(targets);
					} else {
						decision = gui.getTarget(targets);
					}
					
					if (decision == 0) {
						if(type.equals("ConsoleControl")) {
						System.out.println("Decision is faulty.");							
						} //GUI already has to wait until decision!=0 to close window.
						continue;
					}
					
					if (this.battle == null) {
						if(type.equals("ConsoleControl")) {
						System.out.println("No battle running anymore.");							
						} //GUI if there is no battle there is no battle window
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
						if(type.equals("ConsoleControl")) {
						System.out.println("Target became invalid.");							
						}
						continue;
					}
					battle.attack(target.getID());
				} else {
					//Move it move it
					//Could add stuff like "pick up item" or something like that later					
					switch (nextAction) {
					case "move":
						String dir;
						if(type.equals("ConsoleControl")) {
							dir = console.getMoveDirection(x, y);							
						} else {
							dir = gui.getMoveDirection(x, y);
						}
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
		if(type.equals("ConsoleControl")) {
		System.out.println("Amount received fields: " + area.size());			
		} else {
			//Way easier to do this in GUIControl
			gui.updateGui(health, x, y, area);
		}
		if (area.containsKey("North")) world.setBiome(this.x, this.y - 1, area.get("North"));
		if (area.containsKey("East")) world.setBiome(this.x + 1, this.y, area.get("East"));
		if (area.containsKey("West")) world.setBiome(this.x - 1, this.y, area.get("West"));
		if (area.containsKey("South")) world.setBiome(this.x, this.y + 1, area.get("South"));
		if (area.containsKey("Stay")) world.setBiome(this.x, this.y, area.get("Stay"));
		
		if (health <= 0) {
			if(type.equals("ConsoleControl")) {
			System.out.println("YOU DIED");				
			} else {
				gui.youDied();
			}
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
		if(type.equals("ConsoleControl")) {
			System.out.println("YOU WIN");			
		} else {
			gui.youWin();
		}
		System.exit(0);
	}
}