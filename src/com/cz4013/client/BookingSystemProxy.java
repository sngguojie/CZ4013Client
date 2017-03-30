package com.cz4013.client;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by danielseetoh on 3/30/17.
 */
public class BookingSystemProxy implements BookingSystem {

    // This class performs marshalling and sends to communication module

    final int MAX_BYTE_SIZE = 1024;
    final int BYTE_CHUNK_SIZE = 4;

    public BookingSystemProxy(){

    }

    public String getFacilityAvailability (String facilityName, String days){

        boolean idempotent = true;
        String objectReference = "BookingInterface";
        String methodId = "Get";
        String[] strings = new String[]{objectReference, methodId, facilityName, days};
        int[] ints = new int[]{};
        byte[] outBuf = marshal(strings, ints);

        return "PASS";
    };

    public String bookFacility (String facilityName, int day, int startMinute, int endMinute){
        boolean idempotent = true;
        String objectReference = "BookingInterface";
        String methodId = "Book";
        String[] strings = new String[]{objectReference, methodId, facilityName};
        int[] ints = new int[]{day, startMinute, endMinute};
        byte[] outBuf = marshal(strings, ints);
        return "PASS";
    };

    public String changeBooking (String confirmID, int offset){
        boolean idempotent = false;
        String objectReference = "BookingInterface";
        String methodId = "Change";
        String[] strings = new String[]{objectReference, methodId, confirmID};
        int[] ints = new int[]{offset};
        byte[] outBuf = marshal(strings, ints);
        return "PASS";
    };

    public String monitorFacility (String facilityName, String address, int intervalMinutes, int port){
        boolean idempotent = true;
        String objectReference = "BookingInterface";
        String methodId = "Monitor";
        String[] strings = new String[]{objectReference, methodId, facilityName, address};
        int[] ints = new int[]{intervalMinutes, port};
        byte[] outBuf = marshal(strings, ints);
        return "PASS";
    };

    public String listFacilities (){
        boolean idempotent = true;
        String objectReference = "BookingInterface";
        String methodId = "List";
        String[] strings = new String[]{objectReference, methodId};
        int[] ints = new int[]{};
        byte[] outBuf = marshal(strings, ints);
        return "PASS";
    };

    public String extendBooking (String confirmId, int durationExtension){
        boolean idempotent = false;
        String objectReference = "BookingInterface";
        String methodId = "Extend";
        String[] strings = new String[]{objectReference, methodId, confirmId};
        int[] ints = new int[]{durationExtension};
        byte[] outBuf = marshal(strings, ints);
        return "PASS";
    }

    private byte[] marshal(String[] strings, int[] ints){
        // MessageType 2 bytes, requestId 2 bytes, type of object ref (string 0) 4 bytes, Length of object ref 4 bytes
        // Object ref 4 bytes, type of string (0) 4 bytes, length of string 4 bytes, string chunks of 4 bytes
        // type of int (1) 4 bytes, int chunks of 4 bytes

        // java is big-endian by default
        // network byte order is big-endian as well

        byte[] outBuf = new byte[MAX_BYTE_SIZE];

        // leave space for messageType and requestId to be filled in by communication module
        int startByte = 0;
        int strType = 0;
        int strTypePadding = 3;
        int intType = 1;
        int intTypePadding = 3;

        for (String str : strings){
            int strLength = str.length();
            outBuf = addIntToByteArray(outBuf, strType, startByte);
            startByte = incrementByteIndex(startByte);

            outBuf = addIntToByteArray(outBuf, strLength, startByte);
            startByte = incrementByteIndex(startByte);

            char[] ch = str.toCharArray();
            for (int i = 0; i < ch.length; i++){
                outBuf[startByte] = (byte)ch[i];
                startByte++;
            }

            startByte = incrementByteIndex(startByte);
        }

        for (int i : ints){
            outBuf = addIntToByteArray(outBuf, intType, startByte);
            startByte = incrementByteIndex(startByte);

            outBuf = addIntToByteArray(outBuf, i, startByte);
            startByte = incrementByteIndex(startByte);
        }

        System.out.println(new String(outBuf));

        return outBuf;
    }

    private int incrementByteIndex(int index){
        return index += BYTE_CHUNK_SIZE-(index%BYTE_CHUNK_SIZE);
    }

    private byte[] addIntToByteArray(byte[] byteArray, int i, int startIndex){
        byte[] intByteArray = ByteBuffer.allocate(BYTE_CHUNK_SIZE).putInt(i).array();
        System.arraycopy(intByteArray, 0, byteArray, startIndex, intByteArray.length);
        return byteArray;
    }

}
