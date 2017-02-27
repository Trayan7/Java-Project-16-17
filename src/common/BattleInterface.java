package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface BattleInterface extends Remote {
	public void attack(UUID target) throws RemoteException;
	public ArrayList<Player> getPlayers() throws RemoteException;
	public void removePlayer(UUID id) throws RemoteException;
}