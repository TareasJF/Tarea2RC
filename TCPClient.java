import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient implements Client
{
  Boolean conected;
  Socket socket;
  String serverIp;
  DataOutputStream out;
  BufferedReader in;

  public TCPClient() {
    conected = false;
  }

  public void open(String ip) {
    System.out.println("TCP Abriendo conexión con "+ ip +"...");
    serverIp = ip;
    try{Socket socket = new Socket(ip, 21);}catch(Exception e){System.out.println(e.getMessage());}
    try{out = new DataOutputStream(socket.getOutputStream());}catch(Exception e){System.out.println(e.getMessage());}
    try{in = new BufferedReader(new InputStreamReader(socket.getInputStream()));}catch(Exception e){System.out.println(e.getMessage());}
    conected = true;
    try{send("open");}catch(Exception e){System.out.println(e.getMessage());}
    String answer = receive();
  }
  public void cd(String dir) {
    System.out.println("TCP cd "+ dir +"...");
    if (!conected) {
      help(1);
      return;
    }

  }
  public void ls() {
    send("ls");
    if (!conected) {
      help(1);
      return;
    }

  }
  public void get(String fname) {
    System.out.println("TCP get "+ fname +"...");
    if (!conected) {
      help(1);
      return;
    }

  }

  public void send(String s){
    byte[] sendData = new byte[1024];
    sendData = s.getBytes();
    try{out.write(sendData, 0, 1024);}catch(Exception e){System.out.println(e.getMessage());}
  }

  public String receive(){
    byte[] receiveData = new byte[1024];
    String data = null;
    try{data = in.readLine();}catch(Exception e){System.out.println(e.getMessage());}
    return data;
  }

  public void put(String fname) {
    System.out.println("TCP put "+ fname +"...");
    if (!conected) {
      help(1);
      return;
    }

  }

  public void quit() {
    System.out.println("TCP Terminando sesión.");
  }

  public void help(int n) {
    if (n == 0) {
      System.out.println("Manual TCPClient:");
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

}
