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
  String dir;

  public UDPServer (int control, int data) throws Exception {
    dir = ".";
    controlP = control;
    dataP = data;
  	serverSocket = new DatagramSocket(controlP);
  }

  public void run() throws Exception {
  	while(true) {
  		String answer = receive();
      String cm[] = answer.split(" "); 
  		if (cm[0].equals("open")) {
  			open();
  		}
  		else if (cm[0].equals("ls")) {
  			ls();
  		}
  		else if (cm[0].equals("cd")) {
  			cd(cm[1]);
  		}
  		else if (cm[0].equals("get")) {
  			get(cm[1]);
  		}
    }
  }

	public void open() throws Exception {
		send("220");
		String answer = receive();
		if (answer.equals("admin")) {
			send("331");
			answer = receive();
      if (answer.equals("p")) {
			// if (answer.equals("passwordSecreto")) {
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
    if (dir.startsWith("/")) {
    	File f = new File(dir);
			if (f.exists() && f.isDirectory()) {
			  this.dir = dir;
			  send("250");
			  return;
			}
			send("550");
    }
    else {
    	File f = new File(this.dir + "/" + dir);
			if (f.exists() && f.isDirectory()) {
				if (this.dir.equals(".")) {
					this.dir = "";
				}
			  this.dir = this.dir + dir;
			  send("250");
			  return;
			}
			send("550");
    }
  }

  public void ls() throws Exception {
    System.out.println("UDP ls");
    String list = " ";
    File dir = new File(this.dir);
    for (String d : dir.list()) {
    	File f = new File(this.dir+"/"+d);
    	if (f.isFile()) {
    		list = list + "\nfile   " + d;
    	}
    	else {
    		list = list + "\ndir    " + d;
    	}
    }
		send(list);
		send("226");
  }

  public void get(String fname) throws Exception {
    System.out.println("UDP get "+ fname +"...");
    sendFile(fname);
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
	  DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAdd, clientP);
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

  public void sendFile(String fname) throws Exception {
    byte b[] = new byte[1024];
    FileInputStream f = new FileInputStream(fname);
    int size = (int) f.getChannel().size();
    send("150 "+fname+" ("+String.valueOf(size)+")");
    DatagramSocket dsoc = new DatagramSocket(dataP);
    while(f.available()!=0) {
      f.read(b);

      dsoc.send(new DatagramPacket( b, 1024, clientAdd,clientP));
    }                     
    f.close();
    dsoc.close();
  }
}