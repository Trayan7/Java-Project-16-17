package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import client.ClientInterface;
import client.ControlInterface;


public class Server extends UnicastRemoteObject implements ServerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Random rgen = new Random();
	//TODO: Add a server log
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
	private ArrayList<Thread> battles;
	
	/**
	 * Server constructor
	 * @param worldName The name of the world to be loaded, empty if new generated
	 */
	public Server(String worldName) throws RemoteException {
		this.world = new World(worldName);
	}
	
	/**
	 * Function allowing a client to join the game
	 * @param name
	 */
	public UUID join(String name, ClientInterface client) throws RemoteException {
		//Check if name is already used
		System.out.print("Player " + name + " tries to join - ");
		
		UUID id = UUID.randomUUID();
		
		for (Player player: players.values()) {
			if (name.equals(player.getName())) {
				System.out.println("without success");
		    	return null;
		    }
		}
		
		//TODO Return some kind of error to the client if world already contains a player on each spot
		
		/*
		 * TODO
		 * Generate a list of all unused spots on the world
		 * Randomly choose one
		 * Create new player with chosed coordinates (replace 0, 0 below)
		 */
		Player player = new Player(name, 0, 0);
		player.setClient(client);
		
		//TODO set client reference via player.setClient()
		
		players.put(id, player);
		roamingPlayers.add (id);
		//TODO Return some "yay you joined" message
		//Tell everyone _name_ joined
		System.out.println("successfully");
		
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
	 * Function waits until at least one player joined and every player is ready, the starts the game
	 */
	public void start() {
		System.out.println("Started server");
		//Seriously, why can't we just put this in run()?
		//Start is already used by java to start parallel threads. Maybe choose a different name and/or put it in run().
		while (players.size() < 1 || players.size() != readyPlayers.size()) {
			//Wait...
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
		//While there's more than one player left
		while (players.size() > 1) {
			//Reset old action list
			playerActions = new HashMap<UUID, String>();
			
			for (UUID playerName : roamingPlayers) {
				Player player = players.get(playerName);
				try {
					player.getClient().askForMove();
				} catch (RemoteException e) {
					// TODO Handle disconnects
					e.printStackTrace();
				}
				//Notify player that he has to give us his new action now
			}
			
			
			//Wait until all players set their next action
			while (roamingPlayers.size() > playerActions.size()) {
				//Wait...
				//I think this also works with people suddenly getting out of battle
			}
			
			//Everyone set his move, so let's move it
			for (UUID playerID: roamingPlayers) {
				Player player = players.get(playerID);
				String action = playerActions.get(playerID);
				switch (action) {
					case "north":
						player.setY(player.getY() - 1);
						break;
					case "east":
						player.setX(player.getX() + 1);
						break;
					case "south":
						player.setY(player.getY() + 1);
						break;
					case "west":
						player.setX(player.getX() - 1);
						break;
					default:
						//Stay
						break;
				}
			}
			
			/**
			 * TODO
			 * Iterate over all spots on the world
			 * Check if any players are on the same spot
			 */
			//why not iterate over all roaming players and check if their coordinates match?
			//Because you end up with n! checks for n players, while only n checks for n spots
			updatePlayers();
		}
		
		//TODO Notify last man standing of his win
		//The end
	}
	
	/**
	 * Starts battle between two players
	 * @param players List of players that are starting a battle
	 */
	public void initiateBattle(ArrayList<String> players) {
		//These players aren't roaming anymore
		for (String player : players) {
			roamingPlayers.remove(player);
		}
		
		/**
		 * TODO
		 * Start battle via something like Battle battle = new Battle(player1, player2);
		 * Add battle to list of battles
		 * battle.start(); to start the thread
		 */
	}
	
	public void updatePlayers(){
		for (UUID id : players.keySet()) {
			Player player = players.get(id);
			try {
				player.getClient().updateData(player.getHealth(), player.getX(), player.getY());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * TODO
		 * Notify all players of new player states (this also notifies killed players' clients about their deaths)
		 * Check all battles for killed players
		 * End those battles
		 * Return survivors to roamingPlayers
		 */
		//Check all battles for killed players -> Battles will terminate themselves, so we just need to wait for them.
	}
	
	public void makeMove(UUID id, String dir) throws RemoteException {
		playerActions.put(id, dir);
		System.out.println(players.get(id).getName() + " chose " + dir);
		System.out.println(playerActions.size() + "/" + roamingPlayers.size() + " have chosen");
	}
}
