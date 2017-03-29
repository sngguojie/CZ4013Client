package com.cz4013.client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by melvynsng on 3/30/17.
 */
public class FacilityBookingClient extends Thread {
    protected boolean isRunning = true;
    protected DatagramSocket socket = null;
    protected HashMap<Byte, RemoteObject> objectReference = new HashMap<Byte, RemoteObject>();

    public FacilityBookingClient () throws IOException {
        // PORT 2222 is default for NTU computers
        this("FacilityBookingClient", 2222);
    }

    public FacilityBookingClient (String name, int PORT) throws IOException {
        super(name);
        socket = new DatagramSocket(new InetSocketAddress(PORT));
    }

    public void run () {
        System.out.println("FacilityBookingClient Running");
        while (this.isRunning) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                byte[] response = handleRequest(packet.getData(), address, port);
                if (response == null) {
                    continue;
                }
                buf = response;
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        socket.close();
    }

    private byte[] handleRequest(byte[] requestBytes, InetAddress address, int port) throws IOException {
        byte messageType = requestBytes[0];
        byte requestId = requestBytes[1];
        byte[] requestBody = Arrays.copyOfRange(requestBytes, 2, requestBytes.length);

        byte[] responseHead = new byte[2];
        byte[] responseBody = null;
        if (messageType == 2) { // idempotent request
            responseHead[0] = 1;
            responseHead[1] = requestId;
            responseBody = getResponseBody(requestBody, address, port);
            byte[] response = new byte[responseHead.length + responseBody.length];
            System.arraycopy(responseHead, 0, response, 0, responseHead.length);
            System.arraycopy(responseBody, 0, response, responseHead.length, responseBody.length);
            return response;
        }
        return null;
    }

    private byte[] getResponseBody (byte[] requestBody, InetAddress address, int port) {
        byte objectRef = requestBody[0];
        objectRef = 1;
        RemoteObject remoteObject = objectReference.get(objectRef);
        return remoteObject.handleRequest(Arrays.copyOfRange(requestBody,1,requestBody.length), address, port);
    }

    private void sendRequest (byte[] request, InetAddress address, int port) throws IOException {
        boolean timeout = true;
        DatagramPacket packetIn = null;
        while (timeout) {
            try {
                // send request
                byte[] buf = request;
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                socket.setSoTimeout(500);
                socket.send(packet);


                // get response
                byte[] bufIn = new byte[256];
                packetIn = new DatagramPacket(bufIn, bufIn.length);
                socket.receive(packetIn);
                timeout = false;
            } catch (SocketTimeoutException e) {
                timeout = true;
            }
        }

        // display response
        String received = new String(packetIn.getData(), 0, packetIn.getLength());
        System.out.println(received);
    }
}
