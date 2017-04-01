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

    public BookingSystemProxy (String objectReference){
        this.objectReference = objectReference;

    }

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
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

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
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

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
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

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
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

    public String listFacilities (){
        boolean idempotent = true;
        String objectReference = this.objectReference;
        String methodId = "listFacilities";
        Data outData = new Data();
        outData.addString(objectReference);
        outData.addString(methodId);
        byte[] outBuf = MarshalModule.marshal(outData);
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    };

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
        byte[] inBuf = cm.sendRequest(idempotent, outBuf);
        Data data = MarshalModule.unmarshal(inBuf);
        return data.stringListToString();
    }

    public byte[] handleRequest (byte[] requestBody){

        return null;
    }

    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

}
