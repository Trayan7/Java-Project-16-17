package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import common.Player;
import common.World;


public class ConsoleControl implements ControlInterface {
	private Scanner scanner = new Scanner(System.in);
	
	private World world;
	
	public ConsoleControl(World world) {
		this.world = world;
	}
	
	public String getUsername() {
		System.out.print("Choose your username: ");
		String input = scanner.next();
		scanner.nextLine();
		return input;
	}
	
	public String getHost() {
		System.out.print("Choose the host to connect to: ");
		String input = scanner.next();
		scanner.nextLine();
		return input;
	}
	
	public Integer getPort() {
		System.out.print("Choose the port to connect to: ");
		String input = scanner.next();
		scanner.nextLine();
		try {
			Integer port = Integer.valueOf(input);
			if (port.intValue() <= 0 || port.intValue() > 65536) {
				System.out.println("That's not a valid port.");
				return getPort();
			}
			return port;
		} catch (NumberFormatException exception) {
			System.out.println("That's not a valid port.");
			return getPort();
		}
	}
	
	public String getMoveDirection(int column, int row) {
		HashMap<String, Byte> area = world.getArea(column, row);
		List<String> directions = Arrays.asList("North","East","South","West");
		System.out.println("You're standing on a " + world.biomeNames(area.get("Stay")));
		for (String direction : directions) {
			System.out.print(direction + " of you is: ");
			if (!area.containsKey(direction)) {
				System.out.println("absolutely nothing.");
			} else {
				System.out.println(world.biomeNames(area.get(direction)));
			}
		}
		
		String dir = "";
		while (!dir.equals("North") && !dir.equals("East") && !dir.equals("South") && !dir.equals("West") && !dir.equals("Stay")) {
			System.out.println();
			System.out.println("Please choose one of the following moves:");
			System.out.println("North");
			System.out.println("East");
			System.out.println("South");
			System.out.println("West");
			System.out.println("Stay");
			System.out.println();
			dir = scanner.next();
			scanner.nextLine();
			System.out.println();
		}
		
		return dir;
	}
	
	/**
	 * Wait until user presses enter
	 */
	void waitUntilReady() {
		System.out.print("Press ENTER once you're ready to play.");
		scanner.nextLine();
		System.out.println("READY");
	}
	
	public int getTarget(ArrayList<Player> players) {
		System.out.println();
		System.out.println("Please choose a target to attack:");
		for (int i = 0; i < players.size(); i++) {
			Player target = players.get(i);
			System.out.println((i + 1) + " - " + target.getName() + " -> " + target.getHealth() + "%");
		}
		System.out.println();
		try {
			int n = Integer.parseInt(System.console().readLine());
			if (n < 1 || n > players.size()) {
				return 0;
			}
			return n;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}