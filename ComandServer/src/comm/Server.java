package comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Enumeration;
import java.util.List;

public class Server extends Thread{
	
	private ServerSocket server;
	private Socket socket;
	private int port;
	
	public static final String[] COMMANDS= {"remoteIpConfig", "interface","whatTimeIsIt","RTT","speed"};
	
	
	@Override
	public void run() {
		
		try {
		server = new ServerSocket(port);
		
		while(true) {
			socket= server.accept();
			System.out.println("Conected"+"\n");
			
			InputStream is = socket.getInputStream();
			BufferedReader breader = new BufferedReader(new InputStreamReader(is));
			String msg= breader.readLine();			

			if(msg!=null) {
			if(msg.length()<1024) {
				if(msg.equalsIgnoreCase(COMMANDS[0])) {
					sendMessage(scanIpAddress(), socket);/////
				}else if(msg.equalsIgnoreCase(COMMANDS[1])){
					sendMessage(scanInterface(), socket);/////
				}else if(msg.equalsIgnoreCase(COMMANDS[2])){
					sendMessage(getCurrentTime(),socket);////					
				}
			}else{
				sendMessage(msg,socket);
			}
		}
		}
		
	}catch(IOException e) {
			
	}
}
	private String scanIpAddress() {
		String msg="";
		try {
			InetAddress myAddress = InetAddress.getLocalHost();
			msg="/"+myAddress.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	
	private String scanInterface() {
		String ip=scanIpAddress();
		String msg="";
		Enumeration<NetworkInterface> interfaces;
		try {
		interfaces = NetworkInterface.getNetworkInterfaces();
				while(interfaces.hasMoreElements()) {
					NetworkInterface netN= interfaces.nextElement();
					if(netN.isUp()) {
						if(netN.getHardwareAddress()!=null) {
							String mac= new BigInteger(1, netN.getHardwareAddress()).toString(16);
							List <InterfaceAddress> list= netN.getInterfaceAddresses();
							String interfaceIp= list.get(0).getAddress().toString();
							if(interfaceIp.equals(ip)) {
								msg= netN.getName()+","+mac+","+list.get(0).getAddress();
								
							}
						}
					}
				}	
		} catch (SocketException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
	}
	
	private String getCurrentTime() {
		LocalTime now = LocalTime.now();
		String msg=now.getHour()+":"+now.getMinute()+":"+now.getSecond();
		return msg;
	}
	
	private void sendMessage(String msg, Socket socket) {
		try {
			OutputStream os = socket.getOutputStream();
			BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(os));
			bwriter.write(msg+"\n");
			bwriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPort(int port) {
		this.port=port;
	}
}
