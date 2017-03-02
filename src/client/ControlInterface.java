package client;

import java.util.ArrayList;

import common.Player;


public interface ControlInterface {

	String getHost();

	int getPort();

	String getUsername();

	void waitUntilReady();

	int getTarget(ArrayList<Player> targets);

	String getMoveDirection(int x, int y);

	void playerDeath();

	void playerWin();

	void updateData(int health);

	boolean disconnect();
}