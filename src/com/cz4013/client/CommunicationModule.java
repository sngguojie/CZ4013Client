package com.cz4013.client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by melvynsng on 3/30/17.
 */
public class CommunicationModule extends Thread {
    protected boolean isRunning = true;
    protected DatagramSocket socket = null;
    protected HashMap<byte[], byte[]> messageHistory = new HashMap<byte[], byte[]>();
    protected HashMap<byte[], Boolean> receivedRequest = new HashMap<byte[], Boolean>();
    protected enum MSGTYPE {IDEMPOTENT_REQUEST, NON_IDEMPOTENT_REQUEST, IDEMPOTENT_RESPONSE, NON_IDEMPOTENT_RESPONSE};
    protected InetAddress serverAddress;
    protected int serverPort;
    protected HashMap<Integer, byte[]> requestHistory = new HashMap<Integer,byte[]>();
    protected HashMap<Integer, Boolean> receivedResponse = new HashMap<Integer, Boolean>();
    private Binder binder;
    private final int MAX_BYTE_SIZE = 1024;
    private boolean printMessageHeadOn = false;
    private float lossRate;
    Random random = new Random();
    boolean atLeastOnce;


    public CommunicationModule(int clientPort, boolean atLeastOnce) throws IOException {
        // PORT 2222 is default for NTU computers

        this("CommunicationModule", clientPort, atLeastOnce);

    }

    public CommunicationModule(String name, int clientPORT, boolean atLeastOnce) throws IOException {
        super(name);
        socket = new DatagramSocket(new InetSocketAddress(clientPORT));
        this.atLeastOnce = atLeastOnce;

    }

    /**
     * For listening for incoming packets while the thread is running
     */
    public void waitForPacket () {
        while (this.isRunning) {
            try {
                byte[] buf = new byte[MAX_BYTE_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                if (random.nextFloat() <= this.lossRate) {
                    continue;
                }
                printMessageHead(packet,true);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                handlePacketIn(packet.getData(), address, port);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }

    /**
     * To set the loss rate for packets (probability that a packet is loss during sending/receiveing)
     * @param lossRate
     */
    public void setLossRate (float lossRate) {
        this.lossRate = lossRate;
    }

    /**
     * To start listening for requests when the thread starts
     */
    public void run () {
        System.out.println("CommunicationModule Running");
        waitForPacket();
        socket.close();
    }

    /**
     * Returns the byte array for a speified message type
     * @param messageType
     * @return
     */
    private byte[] getMessageTypeAsBytes (MSGTYPE messageType) {
        byte messageTypeByte = (byte)0x00;
        byte idempotentTypeByte = (byte)0x00;
        byte[] result = new byte[2];
        switch (messageType) {
            case IDEMPOTENT_REQUEST:
                messageTypeByte = (byte)0x00;
                idempotentTypeByte = (byte)0x00;
                break;
            case NON_IDEMPOTENT_REQUEST:
                messageTypeByte = (byte)0x00;
                idempotentTypeByte = (byte)0x01;
                break;
            case IDEMPOTENT_RESPONSE:
                messageTypeByte = (byte)0x01;
                idempotentTypeByte = (byte)0x00;
                break;
            case NON_IDEMPOTENT_RESPONSE:
                messageTypeByte = (byte)0x01;
                idempotentTypeByte = (byte)0x01;
                break;
            default:
                break;
        }
        result[0] = messageTypeByte;
        result[1] = idempotentTypeByte;
        return result;

    }

    /**
     * To construct a response head to be appended to the packet given a message type and request id
     * @param messageType
     * @param requestId
     * @return
     */
    private byte[] getResponseHead (MSGTYPE messageType, int requestId) {
        byte[] requestIdBytes = ByteUtils.getHalfWordAsBytes(requestId);
        byte[] messageTypeBytes = getMessageTypeAsBytes(messageType);
        byte[] result = new byte[4];
        System.arraycopy(messageTypeBytes, 0, result, 0, 2);
        System.arraycopy(requestIdBytes, 0, result, 2, 2);
        return result;
    }

    /**
     * To identify and retrieve the Remote Object from the payload data of the packet
     * @param payload
     * @return
     */
    private RemoteObject getRemoteObject (byte[] payload) {
        String objectRefName = MarshalModule.unmarshal(payload).getObjectReference();
        return binder.getObjectReference(objectRefName);
    }

    /**
     * To handle an incoming packet by deciding what actions to execute based on the contents of the packet data
     * @param payload
     * @param address
     * @param port
     * @throws IOException
     */
    private void handlePacketIn(byte[] payload, InetAddress address, int port) throws IOException {
        MSGTYPE messageType = getMessageType(payload);
        int requestId = getRequestId(payload);
        byte[] inHead = Arrays.copyOfRange(payload, 0, 4);
        byte[] inBody = Arrays.copyOfRange(payload, 4, payload.length - 4);
        byte[] outHead, outBody, out;

        switch (messageType) {
            case IDEMPOTENT_REQUEST:
                if (!this.atLeastOnce && messageHistory.containsKey(inHead)) {
                    out = messageHistory.get(inHead);
                    sendReponsePacketOut(out, address, port);
                    break;
                }
                if (!this.atLeastOnce && receivedRequest.containsKey(inHead)) {
                    break;
                }
                receivedRequest.put(inHead, true);
                outHead = getResponseHead(MSGTYPE.IDEMPOTENT_RESPONSE, requestId);
                outBody = getRemoteObjectResponse(inBody);
                out = ByteUtils.combineByteArrays(outHead, outBody);
                messageHistory.put(inHead,out);
                sendReponsePacketOut(out, address, port);
                break;
            case NON_IDEMPOTENT_REQUEST:
                if (!this.atLeastOnce && messageHistory.containsKey(inHead)) {
                    out = messageHistory.get(inHead);
                    sendReponsePacketOut(out, address, port);
                    break;
                }
                if (!this.atLeastOnce && receivedRequest.containsKey(inHead)) {
                    break;
                }
                receivedRequest.put(inHead, true);
                outHead = getResponseHead(MSGTYPE.IDEMPOTENT_RESPONSE, requestId);
                outBody = getRemoteObjectResponse(inBody);
                out = ByteUtils.combineByteArrays(outHead, outBody);
                messageHistory.put(inHead,out);
                sendReponsePacketOut(out, address, port);
                break;
            case IDEMPOTENT_RESPONSE:
                if (receivedResponse.containsKey(getRequestId(inHead))) {
                    receivedResponse.put(getRequestId(inHead), true);
                }
                break;
            case NON_IDEMPOTENT_RESPONSE:
                if (receivedResponse.containsKey(getRequestId(inHead))) {
                    receivedResponse.put(getRequestId(inHead), true);
                }
                break;
            default:
                break;
        }

    }

    /**
     * To get the response from a remote object given the byte array data from the packet
     * Returns the repsonse of the remote object in the form of a byte array
     * @param requestBody
     * @return
     */
    private byte[] getRemoteObjectResponse (byte[] requestBody) {
        RemoteObject remoteObject = getRemoteObject(requestBody);
        return remoteObject.handleRequest(requestBody);
    }

    /**
     * To generate a new request ID for each new request initiated by the client
     * @return
     */
    private int getNewRequestId () {
        int i = 0;
        while (requestHistory.containsKey(i)) {
            i++;
        }
        if (i > Short.MAX_VALUE) {
            i = 0;
        }
        return i;
    }

    /**
     * To send a request to the server
     * @param isIdempotent
     * @param data
     * @return
     */
    public byte[] sendRequestToServer(boolean isIdempotent, byte[] data) {
        return sendRequest(isIdempotent, data, serverAddress, serverPort);
    }

    /**
     * To send a response to the server
     * @param isIdempotent
     * @param data
     */
    public void sendResponse(boolean isIdempotent, byte[] data) {
        sendResponse(isIdempotent, data, serverAddress, serverPort);
    }

    /**
     * To send a request to a specified address and port
     * @param isIdempotent
     * @param data
     * @param address
     * @param port
     * @return
     */
    public byte[] sendRequest(boolean isIdempotent, byte[] data, InetAddress address, int port) {
        try {
            byte[] payload = makePayload(isIdempotent, true, data);
            return sendRequestPacketOut(payload, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To send a response to a specified address and port
     * @param isIdempotent
     * @param data
     * @param address
     * @param port
     */
    public void sendResponse(boolean isIdempotent, byte[] data, InetAddress address, int port) {
        try {
            byte[] payload = makePayload(isIdempotent, false, data);
            sendReponsePacketOut(payload, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * To construct the payload for a packet given the marshalled byte array data and the message type
     * @param isIdempotent
     * @param isRequest
     * @param data
     * @return
     * @throws IOException
     */
    private byte[] makePayload (boolean isIdempotent, boolean isRequest, byte[] data) throws IOException {
        int requestIdInt = getNewRequestId();
        MSGTYPE messageType;
        if (isRequest)
            messageType = isIdempotent ? MSGTYPE.IDEMPOTENT_REQUEST : MSGTYPE.NON_IDEMPOTENT_REQUEST;
        else
            messageType = isIdempotent ? MSGTYPE.IDEMPOTENT_RESPONSE : MSGTYPE.NON_IDEMPOTENT_RESPONSE;
        byte[] outHead = getResponseHead(messageType, requestIdInt);
        byte[] out = ByteUtils.combineByteArrays(outHead, data);
        requestHistory.put(requestIdInt,out);
        return out;
    }

    /**
     * To send a response packet to the specified address and port
     * @param payload
     * @param address
     * @param port
     * @throws IOException
     */
    private void sendReponsePacketOut (byte[] payload, InetAddress address, int port) throws IOException {
        if (payload == null) {
            return;
        }
        // send request
        byte[] buf = payload;
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        printMessageHead(packet, false);
        if (random.nextFloat() <= this.lossRate) {
            return;
        }
        socket.send(packet);
    }

    /**
     * To send a request packed out to the specified address and port
     * The difference with the response packet is the request implements a timeout which will resend the request if no response has been received
     * @param payload
     * @param address
     * @param port
     * @return
     * @throws IOException
     */
    private byte[] sendRequestPacketOut (byte[] payload, InetAddress address, int port) throws IOException {
        if (payload == null) {
            return null;
        }
        boolean resend = true;
        byte[] requestIdBytesOut = new byte[2];
        System.arraycopy(payload, 2, requestIdBytesOut, 0, 2);
        int requestIdOut = ByteUtils.getBytesAsHalfWord(requestIdBytesOut);
        // send request
        do {
            try {
                byte[] buf = payload;

                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                printMessageHead(packet, false);
                receivedResponse.put(requestIdOut, false);
                new TimerThread(this, socket, packet, requestIdOut, 5000l).start();

                byte[] bufIn = new byte[MAX_BYTE_SIZE];
                packet = new DatagramPacket(bufIn, bufIn.length);

                socket.receive(packet);
                printMessageHead(packet, true);
                InetAddress addressIn = packet.getAddress();
                int portIn = packet.getPort();
                byte[] data = packet.getData();
                if (isResponse(data) && getRequestId(data) == requestIdOut) {
                    byte[] inBody = Arrays.copyOfRange(data, 4, payload.length);
                    receivedResponse.put(requestIdOut, true);
                    return  inBody;
                } else {
                    handlePacketIn(data, addressIn, portIn);
                }
            } catch (SocketTimeoutException ste) {
                sendRequestPacketOut(payload, address, port);
            }
        } while(resend);
        return null;
    }

    /**
     * To add an object reference to the binder
     * @param name
     * @param objRef
     */
    public void addObjectReference(String name, RemoteObject objRef){
        this.binder.addObjectReference(name, objRef);
    }

    /**
     * To set the reference to the binder
     * @param binder
     */
    public void setBinder(Binder binder){
        this.binder = binder;
    }

    /**
     * To set whether to wait for an incoming packet
     * @param wait
     */
    public void setWaitingForPacket(boolean wait){
        this.isRunning = wait;
    }

    /**
     * To get the message type given the byte array payload from a packet
     * @param payload
     * @return
     */
    private MSGTYPE getMessageType (byte[] payload) {
        return getMessageType(payload[0], payload[1]);
    }

    /**
     * To identify the message type of the packet given
     * @param messageTypeByte
     * @param idempotentTypeByte
     * @return
     */
    private MSGTYPE getMessageType (byte messageTypeByte, byte idempotentTypeByte) {
        if (messageTypeByte == (byte)0x00 && idempotentTypeByte == (byte)0x00) {
            return MSGTYPE.IDEMPOTENT_REQUEST; // Request
        }
        if (messageTypeByte == (byte)0x00 && idempotentTypeByte == (byte)0x01) {
            return MSGTYPE.NON_IDEMPOTENT_REQUEST;
        }
        if (messageTypeByte == (byte)0x01 && idempotentTypeByte == (byte)0x00) {
            return MSGTYPE.IDEMPOTENT_RESPONSE;
        }
        if (messageTypeByte == (byte)0x01 && idempotentTypeByte == (byte)0x01) {
            return MSGTYPE.NON_IDEMPOTENT_RESPONSE;
        }
        return null;
    }

    /**
     * To check if the payload is of message type response
     * @param payload
     * @return
     */
    private boolean isResponse(byte[] payload) {
        MSGTYPE messageType = getMessageType(payload[0], payload[1]);
        return (messageType == MSGTYPE.NON_IDEMPOTENT_RESPONSE || messageType == MSGTYPE.IDEMPOTENT_RESPONSE);
    }

    /**
     * To get the integer request id from the payload
     * @param payload
     * @return
     */
    private int getRequestId (byte[] payload) {
        return ByteUtils.getBytesAsHalfWord(Arrays.copyOfRange(payload, 2, 4));
    }

    /**
     * To print the message head information onto the console
     * @param packet
     * @param isIncoming
     */
    private void printMessageHead (DatagramPacket packet, boolean isIncoming) {
        if (this.printMessageHeadOn) {
            String arrow = isIncoming ? " IN  " : " OUT ";
            System.out.println(arrow + messageHeadString(packet));
        }
    }

    /**
     * To set whether to show the message header information on the console
     * @param on
     */
    public void setPrintMessageHead (boolean on) {
        this.printMessageHeadOn = on;
    }

    /**
     * To print whether a message is executed
     * @param packet
     * @param isExecuted
     */
    public void printMessageHistory (DatagramPacket packet, boolean isExecuted) {
        if (this.printMessageHeadOn) {
            String retrieve = isExecuted ? " STR_MSG   " : " RTRV_MSG ";
            System.out.println(retrieve + messageHeadString(packet));
        }
    }

    /**
     * To retuen the message header string for printing
     * @param packet
     * @return
     */
    private String messageHeadString (DatagramPacket packet) {
        String ipAddress = packet.getAddress().toString();
        int port = packet.getPort();
        byte[] payload = packet.getData();
        MSGTYPE messageType = getMessageType(payload);
        int requestId = getRequestId(payload);
        return "t: " + (System.currentTimeMillis() % 600000l) + " " + messageType.toString() + " " + ipAddress + ":" + port +" requestID {" + requestId + "} ";
    }


    /**
     * Returns true if the response has been received for the request ID specified
     * @param requestId
     * @return
     */
    public boolean gotResponse(int requestId) {
        return receivedResponse.containsKey(requestId) && receivedResponse.get(requestId);
    }

    /**
     * Returns true if the packet is simulated to be loss based on a random function and predefined loss rate
     * @return
     */
    public boolean isPacketLoss() {
        return random.nextFloat() <= this.lossRate;
    }

}
