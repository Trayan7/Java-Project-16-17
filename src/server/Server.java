package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

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

	/**
	 * List of the battles taking place
	 */
	//private ArrayList<Thread> battles;

	/**
	 * Server constructor
	 * 
	 * @param worldName
	 *            The name of the world to be loaded, empty if new generated
	 */
	public Server(String worldName) throws RemoteException {
		this.world = new World(worldName);
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
		
		Player player = new Player(name, spot[0], spot[1]);
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
	}

	/**
	 * Function waits until at least one player joined and every player is
	 * ready, the starts the game
	 */
	public void start() {
		System.out.println("Started server");
		// Seriously, why can't we just put this in run()?
		// Start is already used by java to start parallel threads. Maybe choose
		// a different name and/or put it in run().
		while (players.size() < 1 || players.size() != readyPlayers.size()) {
			// Wait...
			System.out.print(".");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updatePlayers();
		run();
	}

	/**
	 * Game main loop
	 */
	public void run() {
		System.out.println("Started game");
		// While there's more than one player left
		while (players.size() > 1) {
			// Reset old action list
			playerActions = new HashMap<UUID, String>();

			for (UUID playerName : roamingPlayers) {
				Player player = players.get(playerName);
				try {
					player.getClient().askForMove();
				} catch (RemoteException e) {
					// TODO Handle disconnects
					e.printStackTrace();
				}
				// Notify player that he has to give us his new action now
			}

			System.out.println("Wait for players to take action");
			
			// Wait until all players set their next action
			while (roamingPlayers.size() > playerActions.size()) {
				// Wait...
				// I think this also works with people suddenly getting out of
				// battle
				System.out.print(".");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
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

			/**
			 * TODO Iterate over all spots on the world Check if any players are
			 * on the same spot
			 */
			// why not iterate over all roaming players and check if their
			// coordinates match?
			// Because you end up with n! checks for n players, while only n
			// checks for n spots
			// You don't need to compare everyone to anyone.I already
			// found a solution.
			updatePlayers();
		}

		// TODO Notify last man standing of his win
		// The end
	}

	public void updatePlayers() {
		for (UUID id : players.keySet()) {
			Player player = players.get(id);
			try {
				player.getClient().updateData(player.getHealth(), player.getX(), player.getY(), world.getArea(player.getX(), player.getY()));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * TODO Notify all players of new player states (this also notifies
		 * killed players' clients about their deaths) Check all battles for
		 * killed players End those battles Return survivors to roamingPlayers
		 */
		// Check all battles for killed players -> Battles will terminate
		// themselves, so we just need to wait for them.
	}

	public void makeMove(UUID id, String dir) throws RemoteException {
		playerActions.put(id, dir);
		System.out.println(players.get(id).getName() + " chose " + dir);
		System.out.println(playerActions.size() + "/" + roamingPlayers.size() + " have chosen");
	}
}

/*
//	 public void sendAll(String msg) throws RemoteException {
//	 synchronized (players) {
//	 for (Entry<String, Player> p : players.entrySet()) {
//	 try {
//	 p.getValue().getControlInterface().send(msg);
//	 } catch (RemoteException _) {
//	 System.out.println(p.getKey() + " failed to receive :\"" + msg + "\"");
//	 }
//	 }
//	 }
//	 }

//	/ **
//	 * Checks if all tiles are taken.
//	 * @return
//	 */
//	 public boolean isFull(){
//	 Boolean[][] isUsed = new Boolean[world.zeilen][world.spalten];
//	 for(int i = 0; i < world.zeilen; i++) {
//	 for(int j = 0; j < world.spalten; j++){
//	 isUsed[i][j] = false;
//	 }
//	 }
//	 for (Entry<String, Player> p : players.entrySet()) {
//	 isUsed[p.getValue().getY()][p.getValue().getY()] = true;
//	 }
//	 for(int i = 0; i < world.zeilen; i++) {
//	 for(int j = 0; j < world.spalten; j++){
//	 if(isUsed[i][j] == false) {
//	 return false;
//	 }
//	 }
//	 }
//	 return true;
//	 }

//	/**
//	 * Randomly selects a position. If there is already a Player on that tile,
//	 * selects a new random position up to MAX_RANDOM_POSITION_TRIES.
//	 * Then goes through all possible position and selects the first free one.
//	 *
//	 * @return int[0] = zeilen/y, int[1] = spalten/x, null if failed.
//	 */
//	 public int[] randomPlayerPos() {
//	 Boolean[][] isUsed = new Boolean[world.zeilen][world.spalten];
//	 for(int i = 0; i < world.zeilen; i++) {
//	 for(int j = 0; j < world.spalten; j++){
//	 isUsed[i][j] = false;
//	 }
//	 }
//	 for (Entry<String, Player> p : players.entrySet()) {
//	 isUsed[p.getValue().getY()][p.getValue().getY()] = true;
//	 }
//	 for(int i = 0; i < MAX_RANDOM_POSITION_TRIES; i++){
//	 int y = rgen.nextInt(world.zeilen);
//	 int x = rgen.nextInt(world.spalten);
//	 if(isUsed[y][x] == false) {
//	 int[] position = {y, x};
//	 return position;
//	 }
//	 }
//	 for(int i = 0; i < world.zeilen; i++) {
//	 for(int j = 0; j < world.spalten; j++){
//	 if(isUsed[i][j] == false){
//	 int[] position = {i, j};
//	 return position;
//	 }
//	 }
//	 }
//	 return null;
//	 }
//	
//	public ArrayList<ArrayList<Player>> allBattles() {
//		Player[][] playerMap = new Player[world.zeilen][world.spalten];
//		ArrayList<Player> battles = new ArrayList<Player>();
//		ArrayList<ArrayList<Player>> all = new ArrayList<ArrayList<Player>>();
//		for (Entry<UUID, Player> p : players.entrySet()) {
//			ArrayList<Player> battle = new ArrayList<Player>();
//			int y = p.getValue().getY();
//			int x = p.getValue().getX();
//			if (playerMap[y][x] == null) {
//				playerMap[y][x] = p.getValue();
//			} else {
//				battle.add(playerMap[y][x]);
//				battle.add(p.getValue());
//			}
//		}
//		// slow but faster than checking each tile for every player
//		for (Player p1 : battles) {
//			ArrayList<Player> l = new ArrayList<Player>();
//			l.add(p1);
//			battles.remove(p1);
//			for (Player p2 : battles) {
//				if (p1.getX() == p2.getX() && p1.getY() == p2.getY() && !(p1.getName() == p2.getName())) {
//					l.add(p2);
//					battles.remove(p2);
//				}
//				all.add(l);
//			}
//			return all;
//		}
//	}}
//public void initiateBattle(ArrayList<Player> ps) {
	// for (Player player : ps) {
	// roamingPlayers.remove(player);
	// }
	// Battle battle = new Battle(ps);
	// }
	//
	// public void initiateAllBattles(ArrayList<ArrayList<Player>> all){
	// for(ArrayList<Player> ps : all) {
	// initiateBattle(ps);
	// }
	// }
	//