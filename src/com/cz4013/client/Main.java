package com.cz4013.client;
import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here

        // user should input server ip address, server port
        String serverIpAddress;
        int serverPort;
        int clientPort;
        if (args.length >= 2) {
            serverIpAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
        } else {
            serverIpAddress = "192.168.0.108";
            serverPort = 2222;
        }
        if (args.length >= 3){
            clientPort = Integer.parseInt(args[2]);
        } else {
            clientPort = 2220;
        }
        String[] clientAddressArr = InetAddress.getLocalHost().toString().split("/");
        String clientAddress = clientAddressArr[clientAddressArr.length-1];



        System.out.println("Using serverIpAddress: " + serverIpAddress);
        System.out.println("Using serverPort: " + serverPort);
        System.out.println("Using clientAddress: " + clientAddress);
        System.out.println("Using clientPort: " + clientPort);

        CommunicationModule cm = new CommunicationModule(clientPort, serverIpAddress, serverPort);


        MonitorBroadcast mb = new MonitorBroadcastImpl();
        MonitorBroadcastSkeleton mbs = new MonitorBroadcastSkeleton();
        UserCommandLineImpl ucl = new UserCommandLineImpl(clientAddress, clientPort);
        BookingSystemProxy bsp = new BookingSystemProxy();
        Binder binder = new Binder();

        mbs.setMonitorBroadcast(mb);
        ucl.setBookingSystemProxy(bsp);
        bsp.setCommunicationModule(cm);
        mbs.setCommunicationModule(cm);
        ucl.setCommunicationModule(cm);
        cm.setBinder(binder);
        cm.addObjectReference("BookingSystemProxy", bsp);
        cm.addObjectReference("MonitorBroadcastSkeleton", mbs);


        ucl.getUserInput();

    }

}
