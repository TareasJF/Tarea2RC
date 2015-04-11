import java.io.*;
import java.net.*;
import java.util.*;

interface Client
{
  public void open(String ip) throws Exception;
  public void cd(String dir) throws Exception;
  public void ls() throws Exception;
  public void get(String fname) throws Exception;
  public void put(String fname) throws Exception;
  public void quit() throws Exception;
  public void help(int n);

  public static void main(String args[])throws Exception {
    int controlP = 2121;
    int dataP = 2020;

    Client client;
    if (args.length != 1) {
      System.out.println("Uso: ");
      System.out.println("    - java Client udp");
      System.out.println("          Inicia cliente con protocolo UDP.\n");
      System.out.println("    - java Client tcp");
      System.out.println("          Inicia cliente con protocolo TCP.\n");
      return;
    }
    else if (args[0].equals("udp")) {
      client = new UDPClient(controlP,dataP);
    }
    else if (args[0].equals("tcp")) {

      client = new TCPClient();
    }
    else {
      return;
    }
    while (true) {
      String input =  System.console().readLine("> ");
      String params[] = input.split(" ");
      if (params[0].equals("open")) {
        if (params.length == 1) {
          client.help(0);
          continue;
        }
        client.open(params[1]);
      }
      else if (params[0].equals("cd")) {
        if (params.length == 1) {
          client.help(0);
          continue;
        }
        client.cd(params[1]);
      }
      else if (params[0].equals("ls")) {
        client.ls();
        continue;
      } 
      else if (params[0].equals("get")) {
        if (params.length == 1) {
          client.help(0);
          continue;
        }
        client.get(params[1]);
      } 
      else if (params[0].equals("put")) {
        if (params.length == 1) {
          client.help(0);
          continue;
        }
        client.put(params[1]);
      }
      else if (params[0].equals("quit")) {
        client.quit();
        break;
      } 
      else {
        client.help(0);
      }
    }
  }
}