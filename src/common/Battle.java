package common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import server.Server;

public class Battle extends UnicastRemoteObject implements BattleInterface {
	
	/**
	 * The server the battle is running on
	 */
	private Server server;
	
	/**
	 * List of players participating in the battle
	 */
	HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
	/**
	 * X coordinate of the battle
	 */
	private int x;
	
	/**
	 * Y coordinate of the battle
	 */
	private int y;

	/**
	 * 
	 * @param server The server the battle is running on
	 * @param x the x coordinate of the battle
	 * @param y the y coordinate of the battle
	 * @throws RemoteException
	 */
	public Battle(Server server, int x, int y) throws RemoteException {
		this.server = server;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Adds a player to this battle
	 * @param player The player to be added to the battle
	 */
	public void addPlayer(Player player) {
		if (!players.containsKey(player.getID())) {
			//Player isn't already in the battle, add him
			players.put(player.getID(), player);
			try {
				player.getClient().startBattle(this);
			} catch (RemoteException e) {
				server.handleDisconnect(player.getID());
			}
		}
	}
	
	/**
	 * Returns all participating players
	 * @return ArrayList of all participating players
	 */
	public ArrayList<Player> getPlayers() throws RemoteException {
		ArrayList<Player> ret = new ArrayList<Player>();
		ret.addAll(players.values());
		return ret;
	}
	
	/**
	 * Performs an attack on a player
	 * @param target The UUID of the target that's been attacked
	 */
	public void attack(UUID target) throws RemoteException {
		server.attack(target);
	}
	
	/**
	 * Returns the X coordinate of the battle
	 * @return the x coordinate of the battle
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the Y coordinate of the battle
	 * @return the y coordinate of the battle
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Removes a player from the game
	 * @param id The ID of the player that's been removed
	 */
	public void removePlayer(UUID id) {
		System.out.println("Removing a player from battle");
		players.remove(id);
		
		if (players.size() < 2) {
			for (Player player : players.values()) {
				try {
					player.getClient().stopBattle();
				} catch (RemoteException e) {
					server.handleDisconnect(player.getID());
				}
			}
			server.endBattle(this);
		}
	}
}