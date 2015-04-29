#!/bin/bash 
javac Server.java UDPServer.java Client.java UDPClient.java TCPClient.java -Xlint
rm -r ~/mount/SSLvm/root/jose/RC/*
cp *.class ~/mount/SSLvm/root/jose/RC/