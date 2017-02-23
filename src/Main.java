import client.*;
import server.*;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0 || args[0] != "server" && args[0] != "client") {
			System.out.println("You need to call this either as client or as server");
			System.exit(0);
		}
		if (args[0] == "server") {
			Server server = new Server();
		} else {
			Client client = new Client();
		}
	}
}
