package client;

public class Client {
	int health = 100;
	
	public Client() {
		ConsoleControl control = new ConsoleControl();
		
		String host = control.getHost();
		Integer port = control.getPort();
		
		//TODO Connect to host:port
		
		String username = control.getUsername();
		/**
		 * TODO
		 * Send username to server, get confirmation
		 * if username already used, repeat username question
		 */
		
		control.waitUntilReady();
		System.out.println("Ready");
		
		//While we're in the game
		while (health > 0) {
			
		}
	}
	
	public void updatePlayer() {
		
	}
	
	/**
	 * Called by server, asks the player for his next move
	 */
	public void askForMove() {
		
	}
}