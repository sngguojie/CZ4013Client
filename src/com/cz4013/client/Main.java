package com.cz4013.client;
import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here

        // user should input remote binder ip address, remote binder port
        String remoteBinderIpAddress, atLeastOnce;
        int remoteBinderPort;
        int clientPort;
        int clientPortForRemoteBinder;
        boolean atLeastOnceBool;
        if (args.length >= 2) {
            remoteBinderIpAddress = args[0];
            remoteBinderPort = Integer.parseInt(args[1]);
        } else {
            remoteBinderIpAddress = "192.168.0.108";
            remoteBinderPort = 2219;
        }
        if (args.length >= 4){
            clientPort = Integer.parseInt(args[2]);
            clientPortForRemoteBinder = Integer.parseInt(args[2]);
            atLeastOnce = args[3];
        } else {
            clientPort = 2220;
            clientPortForRemoteBinder = 2220;
            atLeastOnce = "ATLEASTONCE";
        }
        atLeastOnceBool = atLeastOnce.equals("ATLEASTONCE");
        String[] clientAddressArr = InetAddress.getLocalHost().toString().split("/");
        String clientAddress = clientAddressArr[clientAddressArr.length-1];

        System.out.println("Using remoteBinderIpAddress: " + remoteBinderIpAddress);
        System.out.println("Using remoteBinderPort: " + remoteBinderPort);
        System.out.println("Using clientAddress: " + clientAddress);
        System.out.println("Using clientPort: " + clientPort);


        // get the booking system proxy by name
        Naming naming = new Naming(clientPortForRemoteBinder, remoteBinderIpAddress, remoteBinderPort);
        BookingSystemProxy bsp = (BookingSystemProxy)naming.lookup("BookingSystem");

        // give client time to close udp socket
        try {
            Thread.sleep(800);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        // instantiate client objects
        CommunicationModule cm = new CommunicationModule(clientPort, atLeastOnceBool);
        MonitorCallback mb = new MonitorCallbackImpl();
        MonitorCallbackSkeleton mbs = new MonitorCallbackSkeleton();
        UserCommandLineImpl ucl = new UserCommandLineImpl(clientAddress, clientPort);
        Binder binder = new Binder();

        // add dependencies
        mbs.setMonitorCallback(mb);
        ucl.setBookingSystemProxy(bsp);
        bsp.setCommunicationModule(cm);
        mbs.setCommunicationModule(cm);
        ucl.setCommunicationModule(cm);
        cm.setBinder(binder);
        cm.addObjectReference("BookingSystemProxy", bsp);
        cm.addObjectReference("MonitorCallbackSkeleton", mbs);
        cm.setPrintMessageHead(true);
        cm.setLossRate(0.2f);

        // begin terminal user interface
        ucl.getUserInput();

    }

}
