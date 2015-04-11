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
  		if (answer.contains("open")) {
  			open();
  		}
    }
  }

	public void open() throws Exception {
		send("220");
		String answer = receive();
		if (answer.contains("admin")) {
			send("331");
			answer = receive();
			if (answer.contains("passwordSecreto")) {
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

  public void cd(String dir) {
    System.out.println("UDP cd "+ dir +"...");
    if (!true) {
      help(1);
      return;
    }

  }
  public void ls() {
    System.out.println("UDP ls");
    if (!true) {
      help(1);
      return;
    }

  }
  public void get(String fname) {
    System.out.println("UDP get "+ fname +"...");
    if (!true) {
      help(1);
      return;
    }

  }
  public void put(String fname) {
    System.out.println("UDP put "+ fname +"...");
    if (!true) {
      help(1);
      return;
    }

  }

  public void quit() {
    System.out.println("UDP Terminando sesión.");
  }

  public void help(int n) {
    if (n == 0) {
      System.out.println("Manual UDPClient:");
      System.out.println("  Opciones:");
      System.out.println("    - open  <dirección ip>");
      System.out.println("          Abre una conexión con <dirección ip>.\n");
      System.out.println("    - cd <directorio>");
      System.out.println("          Cambio directorio.\n");
      System.out.println("    - ls");
      System.out.println("          Contenido del directorio actual.\n");
      System.out.println("    - get  <archivo>");
      System.out.println("          Extrae <archivo> del servidor.\n");
      System.out.println("    - put  <archivo>");
      System.out.println("          Sube <archivo> al servidor.\n");
      System.out.println("    - quit");
      System.out.println("          Termina la sesión.\n");
    }
    else if (n == 1) {
      System.out.println("Error 1:");
      System.out.println("  Debe abrir una conexión primero.");
    }
  }

  public void send(String s) throws Exception {
    byte[] sendData = new byte[1024];
  	sendData = s.getBytes();
	  DatagramPacket sendPacket =
	  new DatagramPacket(sendData, sendData.length, clientAdd, clientP);
	  serverSocket.send(sendPacket);
  }

  public String receive() throws Exception {
    byte[] receiveData = new byte[1024];
  	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	  serverSocket.receive(receivePacket);
	  clientAdd = receivePacket.getAddress();
	  clientP = receivePacket.getPort();
	  String answer = new String( receivePacket.getData());
    System.out.println(answer);
	  return answer;
  }
}