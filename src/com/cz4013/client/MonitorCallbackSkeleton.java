package com.cz4013.client;

import java.util.ArrayList;

/**
 * Created by danielseetoh on 3/30/17.
 */

public class MonitorCallbackSkeleton implements MonitorCallback, RemoteObject{


    private final int MAX_BYTE_SIZE = 1024;
    private final int BYTE_CHUNK_SIZE = 4;
    private MonitorCallback mc;
    private CommunicationModule cm;

    public MonitorCallbackSkeleton(){

    }

    /**
     * calls monitorCallback to display availability
     * @param availability
     */
    public void displayAvailability(String availability){
        this.mc.displayAvailability(availability);
    }

    /**
     * handle the calls from server to display the availability when some other client books a facility.
     * unmarshals the data to display to the user. marshals a response back to communication module.
     * @param requestBody
     * @return
     */
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

    /**
     * Sets communication module used by this object.
     */
    public void setCommunicationModule(CommunicationModule cm){
        this.cm = cm;
    }

    /**
     * Sets MonitorCallback used by this object
     * @param mc
     */
    public void setMonitorCallback(MonitorCallback mc){
        this.mc = mc;
    }

}

