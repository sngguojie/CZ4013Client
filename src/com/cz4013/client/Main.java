package com.cz4013.client;
import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
//        if (args.length != 1) {
//            System.out.println("Usage: java QuoteClient <hostname>");
//            return;
//        }
//        String serverIP = args[0];
//        String serverPort = args[1];
//        InetAddress serverAddress = InetAddress.getByName(serverIP);
        CommunicationModule cm = new CommunicationModule();
        cm.start();
        String clientAddress = InetAddress.getLocalHost().toString();
        int port = 2222;
//        System.out.println(clientAddress);


        MonitorBroadcast mb = new MonitorBroadcastImpl();
        MonitorBroadcastSkeleton mbs = new MonitorBroadcastSkeleton();
        UserCommandLineImpl ucl = new UserCommandLineImpl(clientAddress, port);
        BookingSystemProxy BSP = new BookingSystemProxy();
        Binder binder = new Binder();

        ucl.setBookingSystemProxy(BSP);
        BSP.setCommunicationModule(cm);
        cm.addObjectReference("BookingSystemProxy", BSP);
        cm.addObjectReference("MonitorBroadcastSkeleton", mbs);
        cm.setBinder(binder);

        ucl.getUserInput();



//        // get a datagram socket
//        DatagramSocket socket = new DatagramSocket();
//        String[] requests = {"0 0 List ",
//                "0 1 Get LectureTheatre1 1 ",
//                "0 2 Book LectureTheatre1 1 60 120 ",
//                "0 3 Create LT2 ",
//                "0 4 Change 0 60 ",
//                "0 4 Change 0 60 ",
//                "0 4 Change 0 60 ",
//                "0 4 Change 0 60 ",
//                "0 5 List ",
//                "0 6 Get LectureTheatre1 1 ",
//                "0 7 Monitor LT2 1 ",
//                "0 8 Change 0 60 ",
//                "0 9 Book LT2 1 60 120 ",
//                "0 10 Book LT2 2 60 120 ",
//        };
//        for (String r : requests) {
//            boolean timeout = true;
//            DatagramPacket packetIn = null;
//            while (timeout) {
//                try {
//                    // send request
//                    byte[] bufOut = new byte[256];
//                    bufOut = r.getBytes();
//                    DatagramPacket packetOut = new DatagramPacket(bufOut, bufOut.length, address, port);
//                    socket.setSoTimeout(500);
//                    socket.send(packetOut);
//                    // get response
//                    byte[] bufIn = new byte[256];
//                    packetIn = new DatagramPacket(bufIn, bufIn.length);
//                    socket.receive(packetIn);
//                    timeout = false;
//                } catch (SocketTimeoutException e) {
//                    timeout = true;
//                }
//            }
//
//            // display response
//            String received = new String(packetIn.getData(), 0, packetIn.getLength());
//            System.out.println(received);
//        }
//
//        socket.close();
    }





}
