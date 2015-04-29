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
    socket = null;
    serverIp = null;
    out = null;
    in = null;
  }

  public void open(String ip) {
    System.out.println("TCP Abriendo conexión con "+ ip +"...");
    this.serverIp = ip;
    try{
      this.socket = new Socket(ip, 21);
      this.out = new DataOutputStream(socket.getOutputStream());
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    catch(Exception e){
      e.printStackTrace();
    }

    //Wait for answer!
    String answer = receive();
    String[] info = answer.split(" ");
    if (info[0].equals("220")){
      String input =  System.console().readLine("Ingrese Usuario > ");
      send("USER "+input+"\n");
      answer = receive();
      info = answer.split(" ");
      if (info[0].equals("331")) {
        input =  System.console().readLine("Ingrese Password > ");
        send("PASS "+input+"\n");
        send(input);
        answer = receive();
        info = answer.split(" ");
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
  public void cd(String dir) {
    if (!conected) {
      help(1);
      return;
    }
    send("CWD "+dir+"\n");
     String ans = receive();
    String[] info = ans.split(" ");
    if(info[0].equals("250"))
    {
      System.out.println(dir + " es el nuevo directorio de trabajo.");
    }
    else {
      System.out.println(dir + " no encontrado.");
    }

  }
  public void ls() {
    if (!conected) {
      help(1);
      return;
    }
    send("PASV\n");
    String ans = receive();
    String[] info = ans.split(" ");
    if(info[0].equals("227"))
    {
      String[] connection_info = info[4].split(",");
      connection_info[5] = connection_info[5].replace(").", "");
      int port = Integer.parseInt(connection_info[4])*256 + Integer.parseInt(connection_info[5]);
      Socket transferSocket = null;
      try{
        transferSocket = new Socket(serverIp, port);
      }
      catch(Exception e){
        System.out.println("Unable to open port for data transfer (Port: "+Integer.toString(port)+") >");
        return;
      }
      send("LIST\n");
      ans = receive();
      info = ans.split(" ");

      if(info[0].equals("150"))
      {
        System.out.println(" > ");
        try{
          BufferedReader reader = new BufferedReader(new InputStreamReader(transferSocket.getInputStream()));
          String line = null;
          while((line = reader.readLine()) != null)
          {
            System.out.println(line);
          }
          transferSocket.close();
          ans = receive();
          info = ans.split(" ");
        }
        catch(Exception e){
        }
      }
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
    byte[] sendData = s.getBytes();
    try{
      out.write(sendData, 0, sendData.length);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public String receive(){
    byte[] receiveData = new byte[1024];
    String data = null;
    try{data = in.readLine();}catch(Exception e){e.printStackTrace();}
    System.out.println("< " + data);
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
