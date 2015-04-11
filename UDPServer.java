import java.io.*;
import java.net.*;

class UDPServer implements Server {

  Boolean conected;
  String clientIp;
  InetAddress clientAdd;
  DatagramSocket serverSocket;
  int clientP;
  int controlP;
  int dataP;

  public UDPServer () throws Exception {
  	serverSocket = new DatagramSocket(2121);
  }

  public void run() throws Exception {
  	while(true) {
  		String answer = receive();
  		if (answer.equals("open")) {
  			open();
  		}
    }
  }

	public void open() throws Exception {
		send("220");
		String answer = receive();
		if (answer.equals("admin")) {
			send("331");
			answer = receive();
			if (answer.equals("passwordSecreto")) {
				send("230");
			}
			else {
				send("530");
			}
		}
		else {
			send("530");
		}
  }

  public void cd(String dir) throws Exception {
    System.out.println("UDP cd "+ dir +"...");
  }

  public void ls() throws Exception {
    System.out.println("UDP ls");
  }

  public void get(String fname) throws Exception {
    System.out.println("UDP get "+ fname +"...");
  }

  public void put(String fname) throws Exception {
    System.out.println("UDP put "+ fname +"...");
  }

  public void quit() throws Exception {
    System.out.println("UDP Terminando sesión.");
  }

  public void send(String s) throws Exception {
    byte[] sendData = new byte[1024];
  	sendData = s.getBytes();
	  DatagramPacket sendPacket =
	  new DatagramPacket(sendData, sendData.length, clientAdd, clientP);
	  serverSocket.send(sendPacket);
    System.out.println(">>" + s);
  }

  public String receive() throws Exception {
    byte[] receiveData = new byte[1024];
  	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	  serverSocket.receive(receivePacket);
	  clientAdd = receivePacket.getAddress();
	  clientP = receivePacket.getPort();
	  String answer = new String( receivePacket.getData());
    System.out.println("< " + answer);
	  return answer.trim();
  }
}