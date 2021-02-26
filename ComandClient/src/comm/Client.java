package comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{

	private BufferedWriter bwriter;
	public static final String[] COMMANDS= {"remoteIpConfig", "interface","whatTimeIsIt","RTT","speed"}; 
	@Override
	public void run() {
		try {
			while(true) {
			System.out.println("Type one command, please");
			Scanner scan = new Scanner(System.in);
			String msg=scan.nextLine();
			Socket socket= new Socket("127.0.0.1", 5000);
			
			if(msg.equalsIgnoreCase(COMMANDS[0])) {
				ipConfig(msg, socket);

			}else if(msg.equalsIgnoreCase(COMMANDS[1])) {
				interfaceData(msg, socket);

			}else if(msg.equalsIgnoreCase(COMMANDS[2])) {
				timeShow(msg, socket);
				
			}else if(msg.equalsIgnoreCase(COMMANDS[3])) {	
				long time=sendAndReceive(msg, socket);
				System.out.println("\n******************************" + "\nIt send and received the message in "+time+" miliseconds"+"\n******************************");
				
			}else if(msg.equalsIgnoreCase(COMMANDS[4])){
				long kbs= 8192/sendAndReceive(msg, socket);
				System.out.println("\n******************************"+ "\nIt send and received the message in "+kbs+" KB/s"+"\n******************************");
				
			}else{
				System.out.println("Invalid command");
			}
			socket.close();
			System.out.println("\n________________________________");	
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ipConfig(String msg, Socket socket) {
		sendMessage(msg, socket);
		System.out.println("\n******************************"+
				"\nThe IP address is : "+receiveMessage(socket)+
				"\n******************************");
	}
	
	private void interfaceData(String msg, Socket socket) {
		sendMessage(msg, socket);
		String[] data = receiveMessage(socket).split(",");
		System.out.println(
		"\n******************************"+"\nInterface Name : "+data[0]+ "\nMac Address : "+data[1]+				
		"\nIp Address: "+data[2]+"\n******************************"
		);
	}
	
	private void timeShow(String msg, Socket socket) {
		sendMessage(msg, socket);
		String time=receiveMessage(socket);
		System.out.println("\n******************************"+
				"\nThe time it's : "+time+
				"\n******************************");
	}
	
	private long sendAndReceive(String msg, Socket socket) {
		long initialTime=System.currentTimeMillis();

		sendMessage(msg, socket);
		receiveMessage(socket);
		
		long finalTime=System.currentTimeMillis()-initialTime;
		return finalTime;
	}
	
	private void sendMessage(String msg, Socket socket) {
		
		try {
			OutputStream os=socket.getOutputStream();
			bwriter = new BufferedWriter(new OutputStreamWriter(os));
			
			if(msg.equalsIgnoreCase("RTT")) {
				String n= new String(new byte[1024]);
				bwriter.write(n+"\n");
				
			}else if(msg.equalsIgnoreCase("speed")){
				String n= new String(new byte[8192]);
				bwriter.write(n+"\n");
				
			}else {
				bwriter.write(msg+"\n");
			}
				bwriter.flush();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private String receiveMessage(Socket socket) {
		String msg="";
		try {
			InputStream is = socket.getInputStream();
			BufferedReader breader = new BufferedReader(new InputStreamReader(is));		
			msg=breader.readLine();

		}catch(IOException e) {
			
		}
		return msg;
	}
}