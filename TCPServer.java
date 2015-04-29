import java.io.*;
import java.net.*;
import java.util.*;

class TCPServer implements Server
{
	Boolean auth_complete;
	Boolean transferReady;
	String clientIp;

	//Control sockets
	ServerSocket socket;
	Socket listenSocket;

	//Modo PASV
	ServerSocket pasvSocket;

	//Modo PORT
	Socket transferSocket;
	String currentDir;
	String user;
	String pass;

	DataOutputStream out;
  	BufferedReader in;

	public TCPServer(){
		socket = null;
		listenSocket = null;
		pasvSocket = null;
		transferSocket = null;

		currentDir = null;
		auth_complete = false;
		transferReady = false;
		try{
			socket = new ServerSocket(21);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void run() throws Exception{
		//Wait for connection
		while(true)
		{
			System.out.println("FTP Server running");
			//Someone entered!
			listenSocket = socket.accept();
			System.out.println("Connection established");
			this.out = new DataOutputStream(listenSocket.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(listenSocket.getInputStream()));
			send("220 (FTP custom Server)\n");
			auth_complete = false;
			transferReady = false;
			this.user = null;
			this.pass = null;
			while(true)
			{
				String answer = receive();
				if(answer == null)
				{
					send("221 Goodbye.\n");
					quit();
					break;
				}
				String[] info = answer.split(" ");
				if(info[0].equals("USER"))
				{
					setUser(info[1]);
				}
				else if(info[0].equals("PASS"))
				{
					setPassword(info[1]);
				}
				else if(info[0].equals("CWD"))
				{
					cd(info[1]);
				}
				else if(info[0].equals("PWD"))
				{
					pwd();
				}
				else if(info[0].equals("RETR"))
				{
					get(info[1]);
				}
				else if(info[0].equals("STOR"))
				{
					put(info[1], "0");
				}
				else if(info[0].equals("PASV"))
				{
					pasv();
				}
				else if(info[0].equals("PORT"))
				{
					prt(info[1]);
				}
				else if(info[0].equals("LIST"))
				{
					ls();
				}
				else if(info[0].equals("quit"))
				{
					send("221 Goodbye.\n");
					quit();
					break;
				}
			}
		}
	}

	public void setUser(String usr){
		user = usr;
		send("331 Please specify the password.\n");
	}

	public void setPassword(String pass){
		if(this.user == null)
		{
			send("503 Login with USER first.\n");
			return;
		}
		this.pass = pass;
		System.out.println("User: "+this.user+"Pass: "+this.pass);
		if(this.user.equals("admin") && this.pass.equals("123456"))
		{
			this.auth_complete = true;
			this.currentDir = "/home";
			send("230 Login successful.\n");
			return;
		}
		this.user = null;
		this.pass = null;
		send("530 Login incorrect.\n");
	}

	public void get(String s){

	}

	public void put (String s, String size){

	}

	public void cd(String dir){
		if(!auth_complete)
		{
			send("530 Please login with USER and PASS.\n");
			return;
		}
		File f;
		if(dir.charAt(dir.length()-1) == '/')
		{
			dir = dir.substring(0, dir.length()-1);
		}
		if(dir.startsWith("/"))
		{
			f = new File(dir);
			if(f.isDirectory())
			{
				send("250 Directory successfully changed.\n");
				currentDir = dir;
			}
			else
			{
				send("550 Failed to change directory.\n");
			}
		}
		else
		{
			f = new File(currentDir+"/"+dir);
			if(f.isDirectory())
			{
				send("250 Directory successfully changed.\n");
				currentDir = currentDir+"/"+dir;
			}
			else
			{
				send("550 Failed to change directory.\n");
			}
		}
	}

	public void pwd(){
		if(!auth_complete)
		{
			send("530 Please login with USER and PASS.\n");
			return;
		}
		send("257 \""+currentDir+"\"\n");
	}

	public void prt(String text) throws Exception{
		if(!auth_complete)
		{
			send("530 Please login with USER and PASS.\n");
			return;
		}
		if(transferSocket != null)
		{
			transferSocket.close();
			transferSocket = null;
		}
		if(pasvSocket != null)
		{
			pasvSocket.close();
			pasvSocket = null;
		}
		String[] connection_info = text.split(",");
        connection_info[5] = connection_info[5].replace(").", "");
		int port = Integer.parseInt(connection_info[4])*256 + Integer.parseInt(connection_info[5]);
		String address = listenSocket.getRemoteSocketAddress().toString();
        address = address.split("/")[1];
        address = address.split(":")[0];
        try{
          transferSocket = new Socket(address, port, listenSocket.getLocalAddress(), 20);
          send("200 PORT command successful.\n");
          transferReady = true;
        }
        catch(Exception e){
          send("425	Can't open data connection.\n");
          return;
        }
	}

	public void pasv() throws Exception{
		if(!auth_complete)
		{
			send("530 Please login with USER and PASS.\n");
			return;
		}
		if(transferSocket != null)
		{
			transferSocket.close();
			transferSocket = null;
		}
		if(pasvSocket != null)
		{
			transferSocket.close();
			transferSocket = null;
		}
		pasvSocket = new ServerSocket(0);
		int port = pasvSocket.getLocalPort();
		String address = listenSocket.getLocalSocketAddress().toString();
        address = address.split("/")[1];
        address = address.split(":")[0];
        String[] ipChunks = address.split("\\.");
        String sendCmd = String.format("(%s,%s,%s,%s,%d,%d)", ipChunks[0], ipChunks[1], ipChunks[2], ipChunks[3], (port - port%256)/256, port%256);
        send("227 Entering Passive Mode "+sendCmd+".\n");

        //Wait for connection! c:
        transferSocket = pasvSocket.accept();
        transferReady = true;

	}

	public void ls() throws Exception{
		if(!auth_complete)
		{
			send("530 Please login with USER and PASS.\n");
			return;
		}
		if(!transferReady){
			send("425 Use PORT or PASV first.\n");
			return;
		}
		send("150 Here comes the list.\n");
	    String list = " ";
	    File dir = new File(this.currentDir);
	    for (String d : dir.list()) {
	    	File f = new File(this.currentDir+"/"+d);
	    	if (f.isFile()) {
	    		list = list + "\nfile   " + d;
	    	}
	    	else {
	    		list = list + "\ndir    " + d;
	    	}
	    }
	    DataOutputStream dout = new DataOutputStream(transferSocket.getOutputStream());
	    dout.writeBytes(list);
	    dout.close();
		send("226 Transfer complete.\n");

		if(transferSocket != null)
		{
			transferSocket.close();
			transferSocket = null;
		}
		if(pasvSocket != null)
		{
			pasvSocket.close();
			pasvSocket = null;
		}
		transferReady = false;
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

    public String receive() throws Exception
    {
	    byte[] receiveData = new byte[1024];
	    String data = null;
	    try{data = in.readLine();}catch(Exception e){e.printStackTrace();}
	    System.out.println("< " + data);
	    return data;
  	}
  	public void quit() throws Exception{
  		if(transferSocket != null)
		{
			transferSocket.close();
			transferSocket = null;
		}
		if(pasvSocket != null)
		{
			pasvSocket.close();
			pasvSocket = null;
		}
		listenSocket.close();
  	}
}