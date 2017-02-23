package client;
import java.util.Scanner;


public class ConsoleControl implements ControlInterface {
	Scanner scanner = new Scanner(System.in);
	
	public ConsoleControl() {
		/**
		 * TODO
		 * Connect
		 * While connected, ask for input
		 * if roaming, ask for roaming stuff
		 * else ask for battle stuff
		 * end loop on disconnect or notification of server
		 */
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
	
	/**
	 * Wait until user presses enter
	 */
	void waitUntilReady() {
		System.out.print("Press ENTER once you're ready to play.");
		scanner.nextLine();
	}
}