package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import common.Battle;
import common.Player;
import common.World;
import client.ClientInterface;

public class Server extends UnicastRemoteObject implements ServerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Random rgen = new Random();
	/**
	 * List of players in the current game
	 */
	private HashMap<UUID, Player> players = new HashMap<UUID, Player>();

	/**
	 * List of players that are ready to play
	 */
	private ArrayList<UUID> readyPlayers = new ArrayList<UUID>();

	/**
	 * The world the game is taking place in
	 */
	private World world;

	/**
	 * List of players in the game roaming the world and not in battle
	 */
	private ArrayList<UUID> roamingPlayers = new ArrayList<UUID>();

	/**
	 * 
	 */
	private HashMap<UUID, String> playerActions;
	
	private ArrayList<Battle> battles = new ArrayList<Battle>();
	
	/**
	 * Server constructor
	 * 
	 * @param worldName The name of the world to be loaded, empty if new generated
	 */
	public Server(String worldName) throws RemoteException {
		System.out.println("Please enter the filename of the map you want to load");
		System.out.println("or enter \"random\" for a random map.");
		int columns = 0;
		int rows = 0;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.next();
		scanner.nextLine();
		if(input.equals("random")){
			worldName = "random";
			System.out.println("Choose map width (1-100):");
			//TODO Check input
			columns = Integer.parseInt(scanner.next());
			scanner.nextLine();
			while(columns <= 0 || columns > 100) {
				System.out.println("That was an invalid width.");
				System.out.println("Choose map width (1-100):");
				columns = Integer.parseInt(scanner.next());
				scanner.nextLine();
			}
			System.out.println("Choose map height (1-100):");
			rows = Integer.parseInt(scanner.next());
			scanner.nextLine();
			while(rows <= 0 || rows > 100) {
				System.out.println("That was an invalid height.");
				System.out.println("Choose map height (1-100):");
				columns = Integer.parseInt(scanner.next());
				scanner.nextLine();
			}
		} else {
			worldName = input;
		}
		
		this.world = new World(worldName, columns, rows);
		System.out.println("World size: " + world.getWidth() + "x" + world.getHeight());
	}

	/**
	 * Function allowing a client to join the game
	 * 
	 * @param name
	 */
	public UUID join(String name, ClientInterface client) throws RemoteException {
		System.out.print("Player " + name + " tries to join - ");

		UUID id = UUID.randomUUID();

		for (Player player : players.values()) {
			if (name.equals(player.getName())) {
				System.out.println("without success.");
				return null;
			}
		}
		
		//Make a list of all spots on the world 
		int width = world.getWidth();
		int height = world.getHeight();
		ArrayList<int[]> spots = new ArrayList<int[]>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int[] spot = {j, i};
				spots.add(spot);
			}
		}
		//Remove all used spots
		Iterator<int[]> iterator = spots.iterator();
		while(iterator.hasNext()) {
		    int[] spot = iterator.next();
		    for (Player player : players.values()) {
		    	if ((spot[0]+"-"+spot[1]).equals(player.getX()+"-"+player.getY())) {
			        iterator.remove();
			        break;
			    }
			}
		}
		//Grab a random spot from list of empty spots for our new player.
		int index = (new Random()).nextInt(spots.size());
		int[] spot = spots.get(index);
		
		//Player player = new Player(id, name, spot[0], spot[1]);
		Player player = new Player(id, name, 0, players.size());
		player.setClient(client);
		
		players.put(id, player);
		roamingPlayers.add(id);
		System.out.println("successfully.");
		System.out.println("Spots left: " + (spots.size() - 1));
		
		return id;
	}
	
	/**
	 * Function allowing the client to say "I'm ready to start the game"
	 */
	public void getReady(UUID id) throws RemoteException {
		System.out.println(players.get(id).getName() + " added as ready");
		if (players.containsKey(id) && !readyPlayers.contains(id)) {
			readyPlayers.add(id);
		}
		System.out.println(readyPlayers.size() + "/" + players.size() + " ready");
		
		if (players.size() == readyPlayers.size()) {
			//Everyone's ready
			updatePlayers();
			askForMoves();
		}
	}
	
	public void start() {
		while (players.size() < 2) {
			System.out.print(".");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		run();
	}
	
	/**
	 * Game main loop
	 */
	public void run() {
		System.out.println("Starting heartbeat");
		while (players.size() > 1) {
			System.out.print(".");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<UUID> dead = new ArrayList<UUID>();
			for (Player player : players.values()) {
				boolean connected = true;
				try {
					player.getClient().heartbeat();
				} catch (RemoteException e) {
					connected = false;
				}
				if (!connected) {
					//Player connection is dead
					dead.add(player.getID());
				}
			}
			for (UUID deadManWalking: dead) {
				handleDisconnect(deadManWalking);
			}
		}
		
		if (players.size() <= 1) {
			//Out of players
			//End game
			for (Player player : players.values()) {
				try {
					player.getClient().winGame();
				} catch (RemoteException e) {
					//Whatever
				}
				System.exit(0);
			}
		}
		
		updatePlayers();
	}

	public synchronized void updatePlayers() {
		ArrayList<UUID> dead = new ArrayList<UUID>();
		for (Player player : players.values()) {
			boolean disconnect = false;
			try {
				player.getClient().updateData(player.getHealth(), player.getX(), player.getY(), world.getArea(player.getX(), player.getY()));
			} catch (RemoteException e) {
				disconnect = true;
			}
			if (disconnect || player.getHealth() <= 0) {
				dead.add(player.getID());
			}
		}
		
		for (UUID id : dead) {
			for (Battle battle: battles) {
				battle.removePlayer(id);
			}
			players.remove(id);
		}
	}
	
	public synchronized void makeMove(UUID id, String dir) throws RemoteException {
		playerActions.put(id, dir);
		System.out.println(players.get(id).getName() + " chose " + dir);
		System.out.println(playerActions.size() + "/" + roamingPlayers.size() + " have chosen");
		
		if (roamingPlayers.size() == playerActions.size()) {
			System.out.println("Updating player positions");
			
			// Everyone set his move, so let's move it
			for (UUID playerID : roamingPlayers) {
				Player player = players.get(playerID);
				String action = playerActions.get(playerID);
				HashMap<String, Byte> playerArea = world.getArea(player.getX(), player.getY());
				if (action.equals("North") && playerArea.containsKey("North")) {
					player.setY(player.getY() - 1);
				} else if (action.equals("East") && playerArea.containsKey("East")) {
					player.setX(player.getX() + 1);
				} else if (action.equals("South") && playerArea.containsKey("South")) {
					player.setY(player.getY() + 1);
				} else if (action.equals("West") && playerArea.containsKey("West")) {
					player.setX(player.getX() - 1);
				} else {
					//Stay
				}
			}
			
			//Generate list of players by their spots
			@SuppressWarnings("unchecked")
			ArrayList<Player> spots[][] = new ArrayList[world.getWidth()][world.getHeight()];
			for (int i = 0; i < world.getWidth(); i++) {
				for (int j = 0; j < world.getHeight(); j++) {
					spots[i][j] = new ArrayList<Player>();
				}
			}
			for (UUID roamingid: roamingPlayers)  {
				Player player = players.get(roamingid);
				spots[player.getX()][player.getY()].add(player);
			}
			
			//Go over all spots and initiate new battles or add players to them
			for (int i = 0; i < spots.length; i++) {
				for (int j = 0; j < spots[i].length; j++) {
					boolean battleFound = false;
					for (Battle battle: battles) {
						if (battle.getX() == i && battle.getY() == j) {
							//We found a battle on this spot
							battleFound = true;
							System.out.println("Found an ongoing battle, joining");
							for (Player newBattler : spots[i][j]) {
								//Add all players battling
								battle.addPlayer(newBattler);
							}
						}
					}
					if (!battleFound && spots[i][j].size() > 1) {
						//More than one player on the spot, start battle
						System.out.println("Start a battle");
						try {
							Battle battle = new Battle(this, i, j);
							for (Player newBattler : spots[i][j]) {
								battle.addPlayer(newBattler);
								roamingPlayers.remove(newBattler.getID());
							}
							battles.add(battle);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			System.out.println("Updating players, asking for new moves");
			updatePlayers();
			askForMoves();
		}
	}
	
	public void endBattle(Battle battle) {
		System.out.println(roamingPlayers.size() + " roaming players.");
		System.out.println(players.size() + " total players.");
		battles.remove(battle);
		try {
			for (Player player : battle.getPlayers()) {
				if (player.getHealth() > 0) {
					roamingPlayers.add(player.getID());
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(roamingPlayers.size() + " roaming players.");
		System.out.println(players.size() + " total players.");
		updatePlayers();
	}
	
	public void attack(UUID target) throws RemoteException {
		players.get(target).setHealth(players.get(target).getHealth() - rgen.nextInt(20));
		updatePlayers();
	}

	public int getWorldWidth() throws RemoteException {
		return this.world.getWidth();
	}

	public int getWorldHeight() throws RemoteException {
		return this.world.getHeight();
	}
	
	private synchronized void askForMoves() {
		playerActions = new HashMap<UUID, String>();
		for (UUID playerName : roamingPlayers) {
			Player player = players.get(playerName);
			try {
				System.out.println("Asking " + player.getName() + " for move.");
				player.getClient().askForMove();
			} catch (RemoteException e) {
				handleDisconnect(player.getID());
			}
		}
	}
	
	public synchronized void handleDisconnect(UUID dead) {
		players.remove(dead);
		roamingPlayers.remove(dead);
		for (Battle battle : battles) {
			battle.removePlayer(dead);
		}
		//Remove from roaming players
		//Remove player from battles
	}
}