package client;

import java.util.ArrayList;

import common.Player;


public interface ControlInterface {
	public String getHost();
	public int getPort();
	public String getUsername();
	public void waitUntilReady();
	public int getTarget(ArrayList<Player> targets);
	public String getMoveDirection(int x, int y) throws InterruptedException;
	public void playerDeath();
	public void playerWin();
	public void updateData(int health);
	public void playerDisconnect();
}