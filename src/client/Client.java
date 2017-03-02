package client;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
	
	World world;
	
	private BattleInterface battle;
	
	private HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
	String type;
	
	ControlInterface control;
	
	ReentrantLock lock = new ReentrantLock();
	Condition cond = lock.newCondition();
	
	Thread heartbeat;
	
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
			control = new ConsoleControl(this);
		} else {
			control = new GUIControl(this);
		}
		
		host = control.getHost();
		port = control.getPort();
		
		String uri = "rmi://"+ host +":"+ port +"/ServerInterface";
		//type = "GUIControl";
		//gui = new GUIControl(world);
		//String uri = "rmi://localhost:1099/ServerInterface";
		

			try {
				server = (ServerInterface) Naming.lookup(uri);
				
				UUID id = null;
				while (id == null) {
					String username;
					username = control.getUsername();
					id = server.join(username, this);
				}
				
				//Start heartbeat thread
				heartbeat = new Thread()
				{
				    public void run() {
				    	while (true) {
				    		try {
					        	System.out.print(".");
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					        boolean connected = true;
							try {
								server.heartbeat();
							} catch (RemoteException e) {
								connected = false;
							}
							if (!connected) {
								//Server connection is dead
								handleDisconnect();
							}
				    	}
				    }
				};
				heartbeat.start();
				
				world = new World("empty", server.getWorldWidth(), server.getWorldHeight());
				
				control.waitUntilReady();
				//Ready
				server.getReady(id);
				
				//While we're in the game
				while (health > 0) {
					System.out.print(".");
					if (this.battle != null) {
						//Gonna fight
						ArrayList<Player> targets = battle.getPlayers();
						Collections.shuffle(targets);
						
						int decision = control.getTarget(targets);
						
						if (decision == 0) {
							continue;
						}
						
						if (this.battle == null) {
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
							continue;
						}
						battle.attack(target.getID());
					} else {
						//Move it move it
						//Could add stuff like "pick up item" or something like that later
						
						lock.lock();
						try {
							if (!nextAction.equals("move") && this.battle == null) {
								cond.await();
							}
						} finally {
							lock.unlock();
						}
						
						switch (nextAction) {
						case "move":
							nextAction = "";
							String dir = control.getMoveDirection(x, y);
							server.makeMove(id, dir);
							break;
						}
					}
				}
			} catch (Exception e) {
				handleDisconnect();
			}
		}
	
	public void updateData(int health, int x, int y, HashMap<String, Byte> area) throws RemoteException {
		this.health = health;
		this.x = x;
		this.y = y;
		if (area.containsKey("North")) world.setBiome(this.x, this.y - 1, area.get("North"));
		if (area.containsKey("East")) world.setBiome(this.x + 1, this.y, area.get("East"));
		if (area.containsKey("West")) world.setBiome(this.x - 1, this.y, area.get("West"));
		if (area.containsKey("South")) world.setBiome(this.x, this.y + 1, area.get("South"));
		if (area.containsKey("Stay")) world.setBiome(this.x, this.y, area.get("Stay"));
		
		control.updateData(health);
		
		if (health <= 0) {
			control.playerDeath();
			System.exit(0);
		}
	}
	
	/**
	 * Called by server, asks the player for his next move
	 */
	public void askForMove() throws RemoteException {
		lock.lock();
		try{
			this.nextAction = "move";
			cond.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public void startBattle(BattleInterface battle) throws RemoteException {
		this.battle = battle;
	}
	
	public void stopBattle() throws RemoteException {
		this.battle = null;
	}
	
	public void winGame() throws RemoteException {
		control.playerWin();
		System.exit(0);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public HashMap<String, Byte> getArea(int x, int y) {
		return world.getArea(x, y);
	}
	
	public World getWorld() {
		return world;
	}
	
	public void disconnect() {
		control.playerDeath();
		System.exit(0);
	}
	
	public void heartbeat() {
		//Just to check the connection
	}
	
	public void handleDisconnect() {
		control.playerDisconnect();
		System.exit(0);
	}
}