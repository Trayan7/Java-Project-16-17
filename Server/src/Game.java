import java.util.ArrayList;
import java.util.HashMap;


public class Game implements Runnable {
	/**
	 * List of players in the current game
	 */
	private HashMap<String, Player> players = new HashMap<String, Player>();
	
	/**
	 * 
	 */
	private ArrayList<String> readyPlayers;
	
	/**
	 * The world the game is taking place in
	 */
	private World world;
	
	/**
	 * List of players in the game roaming the world and not in battle
	 */
	private ArrayList<String> roamingPlayers;
	
	/**
	 * 
	 */
	private HashMap<String, String> playerActions;
	
	/**
	 * List of the battles taking place
	 */
	private ArrayList<Thread> battles;
	
	/**
	 * Game constructor
	 * @param worldName The name of the world to be loaded, empty if new generated
	 */
	public Game(String worldName) {
		this.world = new World(worldName);
		
	}
	
	/**
	 * Function allowing a client to join the game
	 * @param name
	 */
	public void join(String name) {
		//Check if name is already used
		for (String playerName : players.keySet()) {
			if (name == playerName) {
		    	//TODO Tell client "nah, username already used"
				return;
		    }
		}
		
		//TODO Return some kind of error to the client if world already contains a player on each spot
		
		/*
		 * TODO
		 * Generate a list of all unused spots on the world
		 * Randomly choose one
		 * Create new player with chosed coordinates (replace 0, 0 below)
		 */
		Player player = new Player(0, 0);
		
		//TODO set client reference via player.setClient()
		
		players.put(name, player);
		//TODO Return some "yay you joined" message
		//Tell everyone _name_ joined
	}
	
	/**
	 * Function allowing the client to say "I'm ready to start the game"
	 */
	public void getReady() {
		//TODO Get name of player out of already joined players
		String name = "";
		if (!readyPlayers.contains(name)) {
			readyPlayers.add(name);
		}
	}
	
	/**
	 * Function waits until at least one player joined and every player is ready, the starts the game
	 */
	public void start() {
		//Seriously, why can't we just put this in run()?
		while (players.size() < 1 || players.size() != readyPlayers.size()) {
			//Wait...
		}
		run();
	}
	
	/**
	 * Game main loop
	 */
	public void run() {
		//While there's more than one player left
		while (players.size() > 1) {
			//Reset old action list
			playerActions = new HashMap<String, String>();
			
			for (String playerName : roamingPlayers) {
				Player player = players.get(playerName);
				//TODO Notify player that he has to give us his new action now
			}
			
			//Wait until all players set their next action
			while (roamingPlayers.size() > playerActions.size()) {
				//Wait...
				//I think this also works with people suddenly getting out of battle
			}
			
			for (String playerName : roamingPlayers) {
				Player player = players.get(playerName);
				String action = playerActions.get(playerName);
				//TODO Update player object according to action
				players.put(playerName, player);
				//TODO Also, these lines should probably be synched, dunno
			}
			
			/**
			 * TODO
			 * Iterate over all spots on the world
			 * Check if any players are on the same spot
			 */
			ArrayList<String> playersOnSpot = new ArrayList<String>();
			if (playersOnSpot.size() > 1) {
				initiateBattle(playersOnSpot);
			}
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
	
	/**
	 * Function called by a battle whenever a player got hit and updates are necessary
	 */
	public void updatePlayers() {
		/**
		 * TODO
		 * Notify all players of new player states (this also notifies killed players' clients about their deaths)
		 * Check all battles for killed players
		 * End those battles
		 * Return survivors to roamingPlayers
		 */
	}
}