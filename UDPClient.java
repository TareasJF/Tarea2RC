import java.io.*;
import java.net.*;
import java.util.*;

class UDPClient implements Client
{
  Boolean conected;
  String serverIp;
  InetAddress serverAdd;
  DatagramSocket clientSocket;
  int controlP;
  int dataP;

  public UDPClient(int control, int data) {
    conected = false;
    controlP = control;
    dataP = data;
  }

  public void open(String ip) throws Exception {
    System.out.println("UDP Conectando con "+ ip +"...");
    serverAdd = InetAddress.getByName(ip);
    serverIp = ip;
    clientSocket = new DatagramSocket();

    send("open");
    String answer = receive();
    
    char pass[];
    String[] info = answer.split(" ");
    if (info[0].equals("220")){
      String input =  System.console().readLine("Ingrese Usuario > ");
      send(input);
      answer = receive();
      if (info[0].equals("331")) {
        pass =  System.console().readPassword("Ingrese Password > ");
        send(new String(pass));
        answer = receive();
        if (info[0].equals("230")) {
          System.out.println("Login OK");
          conected = true;
        }
        else {
          System.out.println("Login Error");    
        }
      }
      else {
        System.out.println("Login Error");    
      }
    }
  }
  public void cd(String dir) throws Exception {
    if (!conected) {
      help(1);
      return;
    }
    send("cd "+dir);
    String ans = receive();
    if (ans.equals("250")) {
      System.out.println(dir + " es el nuevo directorio de trabajo.");
    }
    else {
      System.out.println(dir + " no encontrado.");
    }

  }
  public void ls() throws Exception {
    if (!conected) {
      help(1);
      return;
    }
    send("ls");
    String ans = receive();
    System.out.println(ans);
    ans = receive();
    System.out.println(ans);
  }
  public void get(String fname) throws Exception {
    if (!conected) {
      help(1);
      return;
    }
    send("get "+fname);
    String ans[] = receive().split(" ");
    if (ans[0].equals("150")) {
      int size = Integer.parseInt( ans[2].replace("(","").replace(")","") );
      receiveFile(fname, size);
    }
  }
  public void put(String fname) throws Exception {
    System.out.println("UDP put "+ fname +"...");
    if (!conected) {
      help(1);
      return;
    }
    sendFile(fname);
  }

  public void quit() throws Exception {
    System.out.println("UDP Terminando sesión.");
    clientSocket.close();
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

  public void send(String s)  throws Exception {
    byte[] sendData = new byte[1024];
    sendData = s.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAdd, controlP);
    clientSocket.send(sendPacket);
  }

  public String receive()  throws Exception {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String answer = new String(receivePacket.getData());
    return answer.trim();
  }

  public void sendFile(String fname) throws Exception {
    byte b[] = new byte[1024];
    FileInputStream f = new FileInputStream(fname);
    int size = (int) f.getChannel().size();
    send("put "+fname+" ("+String.valueOf(size)+")");
    DatagramSocket dsoc = new DatagramSocket(dataP);
    
    while(f.available()!=0) {
      f.read(b);
      dsoc.send(new DatagramPacket( b, 1024, clientAdd,clientP));
    }
                         
    f.close();
    dsoc.close();
  }

  public void receiveFile(String file, int size) throws Exception {
    byte b[] = new byte[2048];
    DatagramPacket dp = new DatagramPacket( b, b.length );

    FileOutputStream f = new FileOutputStream("received/"+file);
    int bytesReceived = 0;
    System.out.print("Receiving file...");
    while(bytesReceived < size) {
      clientSocket.receive(dp);
      bytesReceived = bytesReceived + dp.getLength();
      int bytes = dp.getLength();
      if (bytesReceived - size > 0) {
        bytes = bytesReceived - size;
        bytesReceived = size;
      }

      System.out.print("\r     " + bytesReceived + "/" + size + "bytes     "); 
      f.write(dp.getData(), 0,  bytes);
    }
    System.out.println(); 

    f.close();
  }

}
