package common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import server.Server;

public class Battle extends UnicastRemoteObject implements BattleInterface {
	
	private Server server;
	
	HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
	private int x;
	private int y;

	public Battle(Server server, int x, int y) throws RemoteException {
		this.server = server;
		this.x = x;
		this.y = y;
	}
	
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