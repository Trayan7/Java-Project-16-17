package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import common.BattleInterface;
import common.Player;
import common.World;
import server.ServerInterface;

public class GUIClient extends UnicastRemoteObject implements ClientInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ServerInterface server;

	int health = 100;

	int x = 0;

	int y = 0;

	String nextAction = "";

	static World world = new World("empty");
	int width = world.getWidth();
	int heigth = world.getHeight();

	GUIControl control;

	private BattleInterface battle;

	private HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	
//	 //TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST
//	static World wtest = new World("random");
//	
//	public static void main(String[] args) {
//		GUIControl test = new GUIControl(wtest);
//		test.updateGui(15, 2, 2, wtest.getArea(2, 2));
//		test.updateGui(15, 1, 2, wtest.getArea(1, 2));
//		test.updateGui(15, 0, 2, wtest.getArea(0, 2));
//		
//		Player test1 = new Player(UUID.randomUUID(), "test1", 2, 2);
//		Player test2 = new Player(UUID.randomUUID(), "test2", 2, 2);
//		ArrayList<Player> testt = new ArrayList<Player> ();
//		testt.add(test1);
//		testt.add(test2);
//		
//		System.out.println(test.getTarget(testt));
//	}
//
//	//TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST
	
	
	
	public GUIClient() throws RemoteException {

		control = new GUIControl(world);

		String host = control.getHost();
		String port = control.getPort();

		String uri = "rmi://" + host + ":" + port + "/ServerInterface";

		try {
			server = (ServerInterface) Naming.lookup(uri);

			UUID id = null;
			while (id == null) {
				String username = control.getUsername();
				id = server.join(username, this);
			}

			control.waitUntilReady();
			// Ready
			server.getReady(id);

			// While we're in the game
			while (health > 0) {
				if (this.battle != null) {
					// Gonna fight
					ArrayList<Player> targets = battle.getPlayers();
					Collections.shuffle(targets);
					int decision = control.getTarget(targets);
					if (decision == 0) {
						// If decision = 0 battle window will not close.
						continue;
					}
					if (this.battle == null) {
						// Maybe add message
						continue;
					}
					Player target = targets.get(decision - 1);
					boolean validTarget = false;
					for (Player player : this.battle.getPlayers()) {
						if (player.getName().equals(target.getName())) {
							validTarget = true;
							break;
						}
					}
					if (!validTarget) {
						// Add window Attack failed
						continue;
					}
					battle.attack(target.getID());
				} else {
					switch (nextAction) {
					case "move":
						String dir = control.getMoveDirection();
						server.makeMove(id, dir);
						nextAction = "";
						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateData(int health, int x, int y, HashMap<String, Byte> area) throws RemoteException {
		control.updateGui(health, x, y, area);
		if (health <= 0) {
			control.youDied();
			System.exit(0);
		}
	}

	/**
	 * Called by server, asks the player for his next move
	 */
	public void askForMove() throws RemoteException {
		this.nextAction = "move";
	}

	public void startBattle(BattleInterface battle) throws RemoteException {
		this.battle = battle;
	}

	public void stopBattle() throws RemoteException {
		this.battle = null;
	}

	public void winGame() throws RemoteException {
		control.youWin();
		System.exit(0);
	}
}