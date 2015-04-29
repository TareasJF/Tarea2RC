#!/bin/bash 
rm -r ~/mount/SSLvm/root/jose/RC/*
javac Server.java UDPServer.java Client.java UDPClient.java TCPClient.java -Xlint
cp *.class ~/mount/SSLvm/root/jose/RC/