package common;

import java.rmi.Naming;

import server.Server;
import client.Client;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0
				|| !args[0].equals("server") && !args[0].equals("client") && !args[0].equals("GUIClient")) {
			System.out.println("You need to call this as client, GUIClient or as server");
			System.exit(0);
		}
		if (args[0].equals("server")) {
			String uri = "rmi://localhost:1099/ServerInterface";
			try {
				Server server = new Server("random");
				Naming.rebind(uri, server);
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				@SuppressWarnings("unused")
				Client client = new Client();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
