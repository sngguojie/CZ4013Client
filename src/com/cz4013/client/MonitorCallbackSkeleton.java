package com.cz4013.client;

import java.util.ArrayList;

/**
 * Created by danielseetoh on 3/30/17.
 */

public class MonitorCallbackSkeleton implements MonitorCallback, RemoteObject{


    private final int MAX_BYTE_SIZE = 1024;
    private final int BYTE_CHUNK_SIZE = 4;
    private MonitorCallback mb;
    private CommunicationModule cm;

    public MonitorCallbackSkeleton(){

    }

    // unmarshall byte array
    public void displayAvailability(String availability){
        this.mb.displayAvailability(availability);
    }

    public byte[] handleRequest (byte[] requestBody){
        Data inData = MarshalModule.unmarshal(requestBody);
        String objectReference = inData.getObjectReference();
        String methodId = inData.getMethodId();
        ArrayList<String> strings = inData.getStringList();
        ArrayList<Integer> ints = inData.getIntList();
        String result = "Error MethodID ";
        switch (methodId){
            case "displayAvailability":
                displayAvailability(inData.stringListToString());
                result = "Success";
                break;
            default:
                System.out.println("MethodId " + methodId + " does not exist.");
        }

        System.out.print("result ");
        System.out.println(result);
//        String[] StrList = {"MonitorCallbackProxy", "displayAvailability", result};
//        int[] intList = {1};
        Data outData = new Data();
        outData.addString("MonitorCallbackProxy");
        outData.addString("displayAvailability");
        outData.addString(result);
        return MarshalModule.marshal(outData);
    }

    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

    public void setMonitorCallback(MonitorCallback mb){
        this.mb = mb;
    }

}

