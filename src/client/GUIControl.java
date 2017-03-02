package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import common.Player;
import common.World;

public class GUIControl extends JFrame implements ControlInterface, ActionListener {

	/*
	 * TODO
	 * Come up with good fix for busy wait
	 * create text field for status of game e.g. choose a direction
	 * Make the battle window close itself if the target is invalid
	 * make windows only close when appropriate
	 * check whether or not the scrolling works
	 */
	
	private JPanel[][] tileMap;
	private static final long serialVersionUID = 1L;
	private String dir = "";
	private int height = 600;
	private int width = 800;
	private String title = "FortProg: Java Project";
	private JFrame frame = null;
	private int health = 100;
	private JProgressBar hp = hpBar(health);
	private int maxHealth = 100;
	private String uName = "not updated, yet";
	private JLabel name = new JLabel(uName);
	private JScrollPane map;
	private int target = 0;
	private JPanel playerIcon;

	private Color cEmpty = Color.black;
	private Color cForest = new Color(111, 232, 86);
	private Color cPlains = new Color(177, 255, 95);
	private Color cMountain = new Color(102, 88, 69);
	private Color cSwamp = new Color(89, 84, 43);
	private Color cIsland = new Color(255, 228, 45);

	private JButton north;
	private JButton east;
	private JButton west;
	private JButton south;
	private JButton stay;
	
	private JFrame window;
	
	private World world;
	
	private Client client;
/**
 * Creates GUIControl object and starts the main window of the GUI.
 * @param world an empty world to fill while playing
 */
	public GUIControl(Client client) {
		this.client = client;
		this.world = client.getWorld();
		tileMap = new JPanel[world.getHeight()][world.getWidth()];
		for (int i = 0; i < world.getHeight(); i++){
			for (int j = 0; j < world.getWidth(); j++){
				tileMap[i][j] = null;
			}
		}
		createGUI();
	}

	/**
	 * Starts the main window of the GUI
	 * @return JFrame of the main window of the GUI
	 */
	public  JFrame createGUI() {
		window = new JFrame(title);
		window.setVisible(true);
		window.setSize(width, height);
		window.setResizable(false);
		window.setLayout(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		north = new JButton("N");
		north.addActionListener(this);
		north.setVisible(true);
		north.setBounds(675, 25, 50, 50);
		east = new JButton("E");
		east.addActionListener(this);
		east.setVisible(true);
		east.setBounds(725, 75, 50, 50);
		west = new JButton("W");
		west.addActionListener(this);
		west.setVisible(true);
		west.setBounds(625, 75, 50, 50);
		south = new JButton("S");
		south.addActionListener(this);
		south.setVisible(true);
		south.setBounds(675, 125, 50, 50);
		stay = new JButton("+");
		stay.addActionListener(this);
		stay.setVisible(true);
		stay.setBounds(675, 75, 50, 50);
		window.add(north);
		window.add(east);
		window.add(west);
		window.add(south);
		window.add(stay);

		name = new JLabel(uName);
		window.add(name);
		name.setBounds(625, 225, 150, 25);
		name.setVisible(true);

		hp = hpBar(health);
		window.add(hp);

		map = new JScrollPane();
		window.add(map);
		map.setBounds(0, 0, 602, 572);
		map.setLayout(null);
		map.setVisible(true);
//		map.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		map.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		map.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 15));
//		map.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
		
		playerIcon = new JPanel();
		playerIcon.setBounds(0,0, 9, 9);
		playerIcon.setBackground(Color.red);
		playerIcon.setLayout(null);
		map.add(playerIcon);
		playerIcon.setVisible(true);
		
		return window;
	}

	/**
	 * Updates the GUI
	 * @param health Player health
	 * @param x Player x position
	 * @param y Player y position
	 * @param area Area around and including player
	 */
	//, int x, int y, HashMap<String, Byte> area
	public void updateData(int health) {
		hp.setValue(maxHealth - health);
		hp.setString(health + "/" + maxHealth);
		if (health <= 0.2 * maxHealth) {
			hp.setBackground(Color.RED);
		} else {
			hp.setBackground(Color.GREEN);
		}
		
		this.health = health;
		
		int x = client.getX();
		int y = client.getY();
		
		int worldHeight = world.getHeight();
		int worldWidth = world.getWidth();
		for (int i = 0; i < worldWidth; i++) {
			for (int j = 0; j < worldHeight; j++) {
				createTile(i, j);
			}
		}
		
		playerIcon.setLocation(x * 25 + 100 + 7, y * 25 + 100 + 7);
		
		validate();
		repaint();
	}
	
	/**
	 * Creates a health bar for the player
	 * @param health Current health
	 * @return JProgressBar as health bar
	 */
	public JProgressBar hpBar(int health) {
		hp = new JProgressBar();
		hp.setString(health + "/" + maxHealth);
		hp.setStringPainted(true);
		hp.setValue(maxHealth - health);
		hp.setMaximum(maxHealth);
		hp.setVisible(true);
		//Since we have no Player list
		hp.setBounds(625, 250, 150, 50);
		if (health <= 0.2 * maxHealth) {
			hp.setBackground(Color.RED);
		} else {
			hp.setBackground(Color.GREEN);
		}
		return hp;
	}

	/**
	 * Opens a window to ask for user name.
	 * @return The chosen name
	 */
	public String getUsername() {
		uName = JOptionPane.showInputDialog(frame, "Enter username: ", title, JOptionPane.QUESTION_MESSAGE);
		name.setText(uName);
		repaint();
		return uName;
	}
	
	/**
	 * Opens a window to ask for a different user name
	 * @return
	 */
	public String getUsernameFailed() {
		uName = JOptionPane.showInputDialog(frame, "That username is already taken: ", title, JOptionPane.ERROR_MESSAGE);
		return uName;
	}

	/**
	 * Opens a window to ask for server address.
	 * @return The chosen address
	 */
	public String getHost() {
		String host = JOptionPane.showInputDialog(frame, "Enter server address: ", title, JOptionPane.QUESTION_MESSAGE);
		if (host.length() == 0) {
			return "localhost";
		} else {
			return host;
		}
	}

	/**
	 * Opens a window to ask for server port
	 * @return The chosen port
	 */
	public int getPort() {
		String sPort = JOptionPane.showInputDialog(frame, "Enter server port: ", title, JOptionPane.QUESTION_MESSAGE);
		if (sPort.length() == 0) {
			return 1099;
		}
		try {
			int port = Integer.parseInt(sPort);
			if (port <= 0 || port > 65536) {
				return getPortFailed();
			} else {
				return Integer.parseInt(sPort);
			}
		} catch (NumberFormatException exception) {
			return getPortFailed();
		}
	}

	/**
	 * Opens a window to ask for another server port
	 * @return The chosen port
	 */
	public int getPortFailed() {
		String sPort = JOptionPane.showInputDialog(frame, "That wasn't a valid port: ", title,
				JOptionPane.ERROR_MESSAGE);
		if (sPort.length() == 0) {
			return 1099;
		}
		try {
			int port = Integer.parseInt(sPort);
			if (port <= 0 || port > 65536) {
				return getPortFailed();
			} else {
				return Integer.parseInt(sPort);
			}
		} catch (NumberFormatException exception) {
			return getPortFailed();
		}
	}

	/**
	 * Opens a window to wait for the user to be ready
	 */
	public void waitUntilReady() {
		JOptionPane.showConfirmDialog(frame, "Are you ready?", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Opens a window to let the user know that he died
	 */
	public void playerDeath() {
		JOptionPane.showConfirmDialog(frame, "YOU DIED!", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Opens a window to let the user know that he won
	 */
	public void playerWin() {
		JOptionPane.showConfirmDialog(frame, "YOU WIN!", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Returns the last direction button the user clicked
	 * @param x irrelevant
	 * @param y irrelevant
	 * @return String of direction
	 */
	public String getMoveDirection(int x, int y) {
		//(Busy waiting, but if we use wait() we need a lock)
		while(!dir.equals("North") && !dir.equals("East") && !dir.equals("South") && !dir.equals("West") && !dir.equals("Stay")) {
			System.out.println("BUSY WAIT");
		}
		String answer = dir;
		dir = "";
		return answer;
	}

/**
 * Creates a new tile at a given position in map with the background color defined by the biome
 * @param x X position of tile
 * @param y Y position of tile
 * @param n Biome of the tile
 */
	public void createTile(int x, int y) {
		JPanel tile;
		if (tileMap[y][x] == null) {
			tile = new JPanel();
			map.add(tile);
			tile.setVisible(true);
			tile.setBounds((x * 25) + 100, (y * 25) + 100, 25, 25);
			tile.setLayout(null);
			
			tileMap[y][x] = tile;
		} else {
			tile = tileMap[y][x];
		}
		
		Byte n = world.getBiome(x, y);
		switch (n) {
		case 0:
			System.out.print("empty");
			break;
		case 1:
			tile.setBackground(cForest);
			break;
		case 2:
			tile.setBackground(cPlains);
			break;
		case 3:
			tile.setBackground(cMountain);
			break;
		case 4:
			tile.setBackground(cSwamp);
			break;
		case 5:
			tile.setBackground(cIsland);
			break;
		}
		
		validate();
		repaint();
	}

	/**
	 * Action Events of the direction buttons
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(north)) {
			dir = "North";
			System.out.println("dir = " + dir);
		} else if (e.getSource().equals(east)) {
			dir = "East";
			System.out.println("dir = " + dir);
		} else if (e.getSource().equals(west)) {
			dir = "West";
			System.out.println("dir = " + dir);
		} else if (e.getSource().equals(south)) {
			dir = "South";
			System.out.println("dir = " + dir);
		} else if (e.getSource().equals(stay)) {
			dir = "Stay";
			System.out.println("dir = " + dir);
		}
	}

	/**
	 * Opens a window of all possible targets in a scrambled order and lets the user click any of those targets
	 * @param targets ArrayList of the players that are in the battle
	 * @return Returns the index + 1 of the chosen target
	 */
	public int getTarget(ArrayList<Player> targets) {
		JFrame battle = new JFrame("Battle");
		battle.setLayout(null);
		battle.setSize(250, targets.size() * 25 + 75);
		battle.setVisible(true);
		ArrayList<Integer> positions = new ArrayList<>();
		for (int i = 0; i < targets.size(); i++) {
			positions.add(i);
		}
		Collections.shuffle(positions);
		for (int i = 0; i < targets.size(); i++) {
			int cur = positions.get(0);
			positions.remove(0);
			JButton att = new JButton(targets.get(i).getName());
			att.setLayout(null);
			att.setBounds(10, cur * 25 + 25, 200, 20);
			att.setVisible(true);
			battle.add(att);
			final int id = i + 1;
			att.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					target = id;
				}
			});
		}
		while (target == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int temp = target;
		target = 0;
		battle.dispatchEvent(new WindowEvent(battle, WindowEvent.WINDOW_CLOSING));
		return temp;
	}
}