package com.cz4013.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by danielseetoh on 3/30/17.
 */
public class BookingSystemProxy implements BookingSystem, RemoteObject {

    // This class performs marshalling and sends to communication module

    private CommunicationModule cm;


    final int MAX_BYTE_SIZE = 1024;
    final int BYTE_CHUNK_SIZE = 4;
    CommunicationModule communicationModule;
    private String objectReference;
    private InetAddress serverIPAddress;
    private int serverPort;

    /**
     * Contructor with object reference of remote object, server IP address and server port number
     * @param objectReference
     * @param serverIPAddress
     * @param serverPort
     * @throws IOException
     */
    public BookingSystemProxy (String objectReference, String serverIPAddress, int serverPort) throws IOException{
        this.objectReference = objectReference;
        this.serverIPAddress = InetAddress.getByName(serverIPAddress);
        this.serverPort = serverPort;
    }

    /**
     * retrieves the availability of the facility on the given days
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @param facilityName
     * @param days
     * @return
     */
    public String getFacilityAvailability (String facilityName, String days){

        boolean idempotent = true;
        String objectReference = this.objectReference;
        String methodId = "getFacilityAvailability";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        outData.addString(facilityName);
        outData.addString(days);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    /**
     * books a facility on a given day, from startMinute to endMinute
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @param facilityName
     * @param day
     * @param startMinute
     * @param endMinute
     * @return
     */
    public String bookFacility (String facilityName, int day, int startMinute, int endMinute){
        boolean idempotent = false;
        String objectReference = this.objectReference;
        String methodId = "bookFacility";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        outData.addString(facilityName);
        outData.addInt(day);
        outData.addInt(startMinute);
        outData.addInt(endMinute);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    /**
     * changes a booking by a given offset, both start time and end time
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @param confirmId
     * @param offset
     * @return
     */
    public String changeBooking (String confirmId, int offset){
        boolean idempotent = false;
        String objectReference = this.objectReference;
        String methodId = "changeBooking";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        outData.addString(confirmId);
        outData.addInt(offset);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    /**
     * registers this client to receive updates on the bookings
     * client is unable to send requests out during this time
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @param facilityName
     * @param address
     * @param intervalMinutes
     * @param port
     * @return
     */
    public String monitorFacility (String facilityName, String address, int intervalMinutes, int port){
        boolean idempotent = true;
        String objectReference = this.objectReference;
        String methodId = "monitorFacility";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        outData.addString(facilityName);
        outData.addString(address);
        outData.addInt(intervalMinutes);
        outData.addInt(port);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    /**
     * lists all the facilities available
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @return
     */
    public String listFacilities (){
        boolean idempotent = true;
        String objectReference = this.objectReference;
        String methodId = "listFacilities";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    /**
     * extends a booking by a given time, start time remains the same
     * marshals the data and sends the bytes over to the communication module
     * gets a response, unmarshals it, and sends it back to the caller
     * @param confirmId
     * @param durationExtension
     * @return
     */
    public String extendBooking (String confirmId, int durationExtension){
        boolean idempotent = false;
        String objectReference = this.objectReference;
        String methodId = "extendBooking";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        outData.addString(methodId);
        outData.addString(confirmId);
        outData.addInt(durationExtension);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf, serverIPAddress, serverPort);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    }

    /**
     * empty handle request for the Remote Object interface
     * @param requestBody
     * @return
     */
    public byte[] handleRequest (byte[] requestBody){

        return null;
    }

    /**
     * sets the communication module for this proxy to use
     * @param cm
     */
    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

}
