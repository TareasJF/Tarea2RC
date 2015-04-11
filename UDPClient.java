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

  public void open(String ip) throws Exception{
    System.out.println("UDP Conectando con "+ ip +"...");
    serverAdd = InetAddress.getByName(ip);
    serverIp = ip;
    clientSocket = new DatagramSocket();

    send("open");
    String answer = receive();

    if (answer.contains("220")) {
      String input =  System.console().readLine("Ingrese Usuario > ");
      send(input);
      answer = receive();
      if (answer.contains("331")) {
        input =  System.console().readLine("Ingrese Password > ");
        send(input);
        answer = receive();
        if (answer.contains("230")) {
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
    clientSocket.close();
  }
  public void cd(String dir) {
    System.out.println("UDP cd "+ dir +"...");
    if (!conected) {
      help(1);
      return;
    }

  }
  public void ls() {
    System.out.println("UDP ls");
    if (!conected) {
      help(1);
      return;
    }

  }
  public void get(String fname) {
    System.out.println("UDP get "+ fname +"...");
    if (!conected) {
      help(1);
      return;
    }

  }
  public void put(String fname) {
    System.out.println("UDP put "+ fname +"...");
    if (!conected) {
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
    return answer;
  }

}
