package com.cz4013.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by danielseetoh on 3/30/17.
 */
public class MonitorBroadcastSkeleton implements MonitorBroadcast{

    private final int MAX_BYTE_SIZE = 1024;
    private final int BYTE_CHUNK_SIZE = 4;
    private enum DATATYPE{STRING, INTEGER};

    // unmarshall byte array
    public void displayAvailability(String availability){
    }

    public Data unmarshal(byte[] byteArray){
        int startByte = 0;
        byte[] chunk = new byte[4];
        Data data = new Data();

        while(startByte < byteArray.length){
            System.arraycopy(byteArray, startByte, chunk, 0, chunk.length);
            startByte += BYTE_CHUNK_SIZE;
            if (isEmpty(chunk)){
                break;
            }
            ByteBuffer wrapped = ByteBuffer.wrap(chunk);
            try {
                DATATYPE dataType = DATATYPE.values()[wrapped.getInt()];
                if (dataType == DATATYPE.STRING){
                    System.arraycopy(byteArray, startByte, chunk, 0, chunk.length);
                    startByte += BYTE_CHUNK_SIZE;
                    wrapped = ByteBuffer.wrap(chunk);
                    int strLength = wrapped.getInt();
                    String str = "";
                    for (int i = 0; i < strLength; i+=4){
                        System.arraycopy(byteArray, startByte, chunk, 0, chunk.length);
                        startByte += BYTE_CHUNK_SIZE;
                        str += new String(chunk);
                    }
                    data.addString(str);
                } else if (dataType == DATATYPE.INTEGER){
                    System.arraycopy(byteArray, startByte, chunk, 0, chunk.length);
                    startByte += BYTE_CHUNK_SIZE;
                    wrapped = ByteBuffer.wrap(chunk);
                    int i = wrapped.getInt();
                    data.addInt(i);
                }
            } catch (Exception e){
                System.out.println("Data issue.");
            }

        }

        return data;
    }

    private boolean isEmpty(byte[] byteArray){
        boolean empty = true;
        for (byte b : byteArray){
            if (b != 0){
                empty = false;
            }
        }
        return empty;
    }

}

class Data {
    private ArrayList<String> stringList;
    private ArrayList<Integer> intList;

    public Data(){
        this.stringList = new ArrayList<String>();
        this.intList = new ArrayList<Integer>();
    }

    public void addString(String str){
        this.stringList.add(str);
    }

    public void addInt(int i){
        this.intList.add(i);
    }

    public ArrayList<String> getStringList(){
        return this.stringList;
    }

    public ArrayList<Integer> getIntList(){
        return this.intList;
    }
}