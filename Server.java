import java.io.*;
import java.net.*;
import java.util.*;

interface Server
{
  public void run() throws Exception;
  public void open() throws Exception;
  public void cd(String dir);
  public void ls();
  public void get(String fname);
  public void put(String fname);
  public void quit();
  public void help(int n);

  public static void main(String args[])throws Exception {
    Server server;
    if (args.length != 1) {
      System.out.println("Uso: ");
      System.out.println("    - java Server udp");
      System.out.println("          Inicia servidor con protocolo UDP.\n");
      System.out.println("    - java Server tcp");
      System.out.println("          Inicia servidor con protocolo TCP.\n");
      return;
    }
    else if (args[0].equals("udp")) {
      server = new UDPServer();
      server.run();
    }
    else if (args[0].equals("tcp")) {
      // server = new TCPServer();
    }
    else {
      System.out.println("Uso: ");
      System.out.println("    - java Server udp");
      System.out.println("          Inicia servidor con protocolo UDP.\n");
      System.out.println("    - java Server tcp");
      System.out.println("          Inicia servidor con protocolo TCP.\n");
      return;
    }
  }
}