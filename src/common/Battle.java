package common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import server.Server;

public class Battle extends UnicastRemoteObject implements Runnable, BattleInterface {
	
	private static final long serialVersionUID = 1L;
	
	private Server server;
	
	HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
	private int x;
	private int y;

	public Battle(Server server, int x, int y) throws RemoteException {
		this.server = server;
		this.x = x;
		this.y = y;
	}

	public void run() {
		while (players.size() > 1) {
			ArrayList<UUID> dead = new ArrayList<UUID>();
			for (Player player : players.values()) {
				if (player.getHealth() <= 0) {
					//dead
					dead.add(player.getID());
				}
			}
			for (UUID id : dead) {
				players.remove(id);
				System.out.println("Removed a player");
				server.updatePlayers();
			}
		}
		System.out.println("Out of players");
		for (Player player : players.values()) {
			try {
				player.getClient().stopBattle();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		server.endBattle(this);
	}
	
	public void addPlayer(Player player) {
		if (!players.containsKey(player.getID())) {
			//Player isn't already in the battle, add him
			players.put(player.getID(), player);
			try {
				player.getClient().startBattle(this);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Player> getPlayers() throws RemoteException {
		ArrayList<Player> ret = new ArrayList<Player>();
		ret.addAll(players.values());
		return ret;
	}
	
	public void attack(UUID target) throws RemoteException {
		server.attack(target);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void removePlayer(UUID id) {
		System.out.println("Removing a player from battle");
		players.remove(id);
	}
}