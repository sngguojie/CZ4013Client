package com.cz4013.client;
import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here

        // user should input remote binder ip address, remote binder port
        String remoteBinderIpAddress;
        int remoteBinderPort;
        int clientPort;
        int clientPortForRemoteBinder;
        if (args.length >= 2) {
            remoteBinderIpAddress = args[0];
            remoteBinderPort = Integer.parseInt(args[1]);
        } else {
            remoteBinderIpAddress = "192.168.1.41";
            remoteBinderPort = 2219;
        }
        if (args.length >= 3){
            clientPort = Integer.parseInt(args[2]);
            clientPortForRemoteBinder = Integer.parseInt(args[2]);
        } else {
            clientPort = 2220;
            clientPortForRemoteBinder = 2220;
        }
        String[] clientAddressArr = InetAddress.getLocalHost().toString().split("/");
        String clientAddress = clientAddressArr[clientAddressArr.length-1];

        System.out.println("Using remoteBinderIpAddress: " + remoteBinderIpAddress);
        System.out.println("Using remoteBinderPort: " + remoteBinderPort);
        System.out.println("Using clientAddress: " + clientAddress);
        System.out.println("Using clientPort: " + clientPort);

        // instantiate remote binder comms module and retrieve server object reference
        RemoteBinderCommunicationModule rbcm = new RemoteBinderCommunicationModule(clientPortForRemoteBinder, remoteBinderIpAddress, remoteBinderPort);
        rbcm.start();
        String remoteObjectReference = rbcm.sendGetRequest("BookingSystem");
        String[] RORArray = remoteObjectReference.split(",");
        String serverIPAddress = RORArray[0];
        int serverPort = Integer.parseInt(RORArray[1]);
        String remoteObjectID = RORArray[2];
        System.out.println(remoteObjectReference);
        rbcm.setExit(true);

        // give client time to close udp socket
        try {
            Thread.sleep(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        // instantiate client objects
        CommunicationModule cm = new CommunicationModule(clientPort, serverIPAddress, serverPort);
        MonitorBroadcast mb = new MonitorBroadcastImpl();
        MonitorBroadcastSkeleton mbs = new MonitorBroadcastSkeleton();
        UserCommandLineImpl ucl = new UserCommandLineImpl(clientAddress, clientPort);
        BookingSystemProxy bsp = new BookingSystemProxy(remoteObjectID);
        Binder binder = new Binder();

        // add dependencies
        mbs.setMonitorBroadcast(mb);
        ucl.setBookingSystemProxy(bsp);
        bsp.setCommunicationModule(cm);
        mbs.setCommunicationModule(cm);
        ucl.setCommunicationModule(cm);
        cm.setBinder(binder);
        cm.addObjectReference("BookingSystemProxy", bsp);
        cm.addObjectReference("MonitorBroadcastSkeleton", mbs);

        // begin terminal user interface
        ucl.getUserInput();

    }

}
