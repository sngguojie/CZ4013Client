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

    public BookingSystemProxy (){

    }

    public String getFacilityAvailability (String facilityName, String days){

        boolean idempotent = true;
        String objectReference = "BookingSystem";
        String methodId = "Get";
        String[] strings = new String[]{objectReference, methodId, facilityName, days};
        int[] ints = new int[]{};

        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    };

    public String bookFacility (String facilityName, int day, int startMinute, int endMinute){
        boolean idempotent = true;
        String objectReference = "BookingSystem";
        String methodId = "Book";
        String[] strings = new String[]{objectReference, methodId, facilityName};
        int[] ints = new int[]{day, startMinute, endMinute};
        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    };

    public String changeBooking (String confirmID, int offset){
        boolean idempotent = false;
        String objectReference = "BookingSystem";
        String methodId = "Change";
        String[] strings = new String[]{objectReference, methodId, confirmID};
        int[] ints = new int[]{offset};
        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    };

    public String monitorFacility (String facilityName, String address, int intervalMinutes, int port){
        boolean idempotent = true;
        String objectReference = "BookingSystem";
        String methodId = "Monitor";
        String[] strings = new String[]{objectReference, methodId, facilityName, address};
        int[] ints = new int[]{intervalMinutes, port};
        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    };

    public String listFacilities (){
        boolean idempotent = true;
        String objectReference = "BookingSystem";
        String methodId = "List";
        String[] strings = new String[]{objectReference, methodId};
        int[] ints = new int[]{};
        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    };

    public String extendBooking (String confirmId, int durationExtension){
        boolean idempotent = false;
        String objectReference = "BookingSystem";
        String methodId = "Extend";
        String[] strings = new String[]{objectReference, methodId, confirmId};
        int[] ints = new int[]{durationExtension};
        byte[] outBuf = MarshalModule.marshal(strings, ints);
        return "PASS";
    }

    public byte[] handleRequest (byte[] requestBody){
        return null;
    }

    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

}
