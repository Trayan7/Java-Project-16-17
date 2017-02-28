package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import common.Player;
import common.World;

public class GUIControl extends JFrame implements ControlInterface, ActionListener {

	World world;
	
	//Null pointer
//	int mapWidth = world.getWidth();
//	int mapHeigth = world.getHeight();

	int mapWidth = 5;
	int mapHeight = 5;
	
	//Needs some cleaning. Don't even need xPos and yPos
	private static final long serialVersionUID = 1L;
	static int height = 600;
	static int width = 800;
	int xPos = 0;
	int yPos = 0;
	static String title = "FortProg: Java Project";
	static JFrame frame = null;
	int health = 100;
	JProgressBar hp = hpBar(health);
	static int maxHealth = 100;
	static int BTN_SIZE = 50;
	static String uName = "not updated, yet";
	JScrollPane map;
	JPanel[][] tileMap;
	Random rgen = new Random();
	int target = 0;
	JPanel playerIcon;

	private Color cEmpty = Color.black;
	private Color cForest = new Color(111, 232, 86);
	private Color cPlains = new Color(177, 255, 95);
	private Color cMountain = new Color(102, 88, 69);
	private Color cSwamp = new Color(89, 84, 43);
	private Color cIsland = new Color(255, 228, 45);

	String dir;

	JButton north;
	JButton east;
	JButton west;
	JButton south;
	JButton stay;
	
	JFrame window;

	public GUIControl(World world) {
		this.world = world;
		tileMap = new JPanel[world.getHeight()][world.getWidth()];
		for(int i = 0; i < world.getHeight(); i++){
			for(int j = 0; j < world.getWidth(); j++){
				tileMap[i][j] = null;
			}
		}
//		window = createGUI();
		createGUI();
	}

	public JFrame createGUI() {
		JFrame window = new JFrame(title);
		window.setVisible(true);
		window.setSize(width, height);
		window.setResizable(false);
		window.setLayout(null);

		north = new JButton("N");
		north.addActionListener(this);
		north.setVisible(true);
		north.setBounds(675, 25, BTN_SIZE, BTN_SIZE);
		east = new JButton("E");
		east.addActionListener(this);
		east.setVisible(true);
		east.setBounds(725, 75, BTN_SIZE, BTN_SIZE);
		west = new JButton("W");
		west.addActionListener(this);
		west.setVisible(true);
		west.setBounds(625, 75, BTN_SIZE, BTN_SIZE);
		south = new JButton("S");
		south.addActionListener(this);
		south.setVisible(true);
		south.setBounds(675, 125, BTN_SIZE, BTN_SIZE);
		stay = new JButton("+");
		stay.addActionListener(this);
		stay.setVisible(true);
		stay.setBounds(675, 75, BTN_SIZE, BTN_SIZE);

		window.add(north);
		window.add(east);
		window.add(west);
		window.add(south);
		window.add(stay);

		JLabel name = new JLabel(uName);
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
		map.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		map.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		map.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 15));
		map.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
		
		playerIcon = new JPanel();
		playerIcon.setBounds(xPos * 25 + 100 - 5, yPos * 25 + 100 - 5, 9, 9);
		playerIcon.setBackground(Color.red);
		playerIcon.setLayout(null);
		map.add(playerIcon);
		playerIcon.setVisible(true);

		return window;

	}

	public void updateGui(int health, int x, int y, HashMap<String, Byte> area) {
		hp.setValue(maxHealth - health);
		hp.setString(health + "/" + maxHealth);
		if (health <= 0.2 * maxHealth) {
			hp.setBackground(Color.RED);
		} else {
			hp.setBackground(Color.GREEN);
		}
		
		this.health = health;
		this.xPos = x;
		this.yPos = y;
		playerIcon.setLocation(x * 25 + 100 + 7, y * 25 + 100 + 7);
		
		if (area.containsKey("North") && tileMap[this.yPos - 1][this.xPos] == null) {
			createTile(this.xPos, this.yPos - 1, area.get("North"));
		}
		if (area.containsKey("East") && tileMap[this.yPos][this.xPos + 1] == null) {
			createTile(this.xPos + 1, this.yPos, area.get("East"));			
		}
		if (area.containsKey("West") && tileMap[this.yPos][this.xPos - 1] == null) {
			createTile(this.xPos - 1, this.yPos, area.get("West"));			
		}
		if (area.containsKey("South") && tileMap[this.yPos + 1][this.xPos] == null) {
			createTile(this.xPos, this.yPos + 1, area.get("South"));			
		}
		if (area.containsKey("Stay") && tileMap[this.yPos][this.xPos] == null){
			createTile(this.xPos, this.yPos, area.get("Stay"));			
		}
		repaint();
	}

	public JProgressBar hpBar(int health) {
		hp = new JProgressBar();
		hp.setString(health + "/" + maxHealth);
		hp.setStringPainted(true);
		hp.setValue(maxHealth - health);
		hp.setMaximum(maxHealth);
		hp.setVisible(true);
		hp.setBounds(625, 250, 150, 50);
		if (health <= 0.2 * maxHealth) {
			hp.setBackground(Color.RED);
		} else {
			hp.setBackground(Color.GREEN);
		}
		return hp;
	}

	public String getUsername() {
		uName = JOptionPane.showInputDialog(frame, "Enter username: ", title, JOptionPane.QUESTION_MESSAGE);
		return uName;
	}

	public String getHost() {
		String host = JOptionPane.showInputDialog(frame, "Enter server address: ", title, JOptionPane.QUESTION_MESSAGE);
		if (host.length() == 0) {
			return "localhost";
		} else {
			return host;
		}
	}

	public String getPort() {
		String sPort = JOptionPane.showInputDialog(frame, "Enter server port: ", title, JOptionPane.QUESTION_MESSAGE);
		if (sPort.length() == 0) {
			return "1099";
		}
		try {
			int port = Integer.parseInt(sPort);
			if (port <= 0 || port > 65536) {
				return getPortFailed();
			} else {
				return sPort;
			}
		} catch (NumberFormatException exception) {
			return getPortFailed();
		}
	}

	public static String getPortFailed() {
		String sPort = JOptionPane.showInputDialog(frame, "That wasn't a valid port: ", title,
				JOptionPane.ERROR_MESSAGE);
		if (sPort.length() == 0) {
			return "1099";
		}
		try {
			int port = Integer.parseInt(sPort);
			if (port <= 0 || port > 65536) {
				return getPortFailed();
			} else {
				return sPort;
			}
		} catch (NumberFormatException exception) {
			return getPortFailed();
		}
	}

	void waitUntilReady() {
		JOptionPane.showConfirmDialog(frame, "Are you ready?", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	void youDied() {
		JOptionPane.showConfirmDialog(frame, "YOU DIED!", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	void youWin() {
		JOptionPane.showConfirmDialog(frame, "YOU WIN!", "title", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public String getMoveDirection() {
		String answer = dir;
		dir = "";
		return answer;
	}

	public String getUsernameFailed() {
		return JOptionPane.showInputDialog(frame, "That username is already taken: ", title, JOptionPane.ERROR_MESSAGE);
	}

	public void createTile(int x, int y, Byte n) {
		JPanel tile = new JPanel();
		switch (n) {
		case 0:
			tile.setBackground(cEmpty);
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
		tile.setLayout(null);
		tile.setBounds(x * 25 + 100, y * 25 + 100, 25, 25);
		tile.setVisible(true);
		tileMap[y][x] = tile;
		map.add(tileMap[y][x]);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(north)) {
			dir = "North";
		} else if (e.getSource().equals(east)) {
			dir = "East";
		} else if (e.getSource().equals(west)) {
			dir = "West";
		} else if (e.getSource().equals(south)) {
			dir = "South";
		} else if (e.getSource().equals(stay)) {
			dir = "Stay";
		}
	}

	public int getTarget(ArrayList<Player> targets) {
		JFrame battle = new JFrame("Battle");
		battle.setLayout(null);
		battle.setSize(250, targets.size() * 25 + 75);
		battle.setVisible(true);
		//Stack may be better
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int temp = target;
		target = 0;
		battle.dispatchEvent(new WindowEvent(battle, WindowEvent.WINDOW_CLOSING));
		return temp;
	}
}
