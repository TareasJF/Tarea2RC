import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient implements Client
{
  public void open(String ip) {
    System.out.println("TCP Abriendo conexión con "+ ip +"...");
  }
  public void cd(String dir) {
    System.out.println("TCP cd "+ dir +"...");

  }
  public void ls() {
    System.out.println("TCP ls");

  }
  public void get(String fname) {
    System.out.println("TCP get "+ fname +"...");

  }
  public void put(String fname) {
    System.out.println("TCP put "+ fname +"...");

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
