package ui;

import comm.Server;

public class Main {

	public static void main (String[]args) {
		
	Server server= new Server();
	server.setPort(5000);
	
	server.start();
		
	}
}
