package com.cz4013.client;
import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here

        CommunicationModule cm = new CommunicationModule();
//        cm.start();
        String clientAddress = InetAddress.getLocalHost().toString();
        int port = 2222;


        MonitorBroadcast mb = new MonitorBroadcastImpl();
        MonitorBroadcastSkeleton mbs = new MonitorBroadcastSkeleton();
        UserCommandLineImpl ucl = new UserCommandLineImpl(clientAddress, port);
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
