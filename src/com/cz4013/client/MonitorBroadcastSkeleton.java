package com.cz4013.client;

import javax.management.monitor.Monitor;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by danielseetoh on 3/30/17.
 */

public class MonitorBroadcastSkeleton implements MonitorBroadcast, RemoteObject{


    private final int MAX_BYTE_SIZE = 1024;
    private final int BYTE_CHUNK_SIZE = 4;
    private MonitorBroadcast mb;
    private CommunicationModule cm;

    public MonitorBroadcastSkeleton(){

    }

    // unmarshall byte array
    public void displayAvailability(String availability){
        this.mb.displayAvailability(availability);
    }

    public byte[] handleRequest (byte[] requestBody){
        Data data = MarshalModule.unmarshal(requestBody);
        String objectReference = data.getObjectReference();
        String methodId = data.getMethodId();
        ArrayList<String> strings = data.getStringList();
        ArrayList<Integer> ints = data.getIntList();
        byte[] result = null;

        switch (methodId){
            case "displayAvailability":
                displayAvailability(data.stringListToString());
                result = null;
                break;
            default:
                System.out.println("MethodId " + methodId + " does not exist.");
        }

        return result;
    }

    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

    public void setMonitorBroadcast(MonitorBroadcast mb){
        this.mb = mb;
    }

}

