package com.cz4013.client;

import java.io.IOException;

/**
 * Created by danielseetoh on 4/2/17.
 */
public class Naming {

    private int clientPortForRemoteBinder, remoteBinderPort;
    private String remoteBinderIpAddress;

    public Naming(int clientPortForRemoteBinder, String remoteBinderIpAddress,int remoteBinderPort){
        this.clientPortForRemoteBinder = clientPortForRemoteBinder;
        this.remoteBinderIpAddress = remoteBinderIpAddress;
        this.remoteBinderPort = remoteBinderPort;
    }

    /**
     * Gets a remote object reference from the remote binder
     * @param name
     * @return
     * @throws IOException
     */
    public RemoteObject lookup(String name) throws IOException{
        RemoteBinderCommunicationModule rbcm = new RemoteBinderCommunicationModule(clientPortForRemoteBinder, remoteBinderIpAddress, remoteBinderPort);
        rbcm.start();
        String remoteObjectReference = rbcm.sendGetRequest("BookingSystem");
        String[] RORArray = remoteObjectReference.split(",");
        String serverIPAddress = RORArray[0];
        int serverPort = Integer.parseInt(RORArray[1]);
        String remoteObjectID = RORArray[2];
        rbcm.setExit(true);

        RemoteObject bsp = new BookingSystemProxy(remoteObjectID, serverIPAddress, serverPort);

        return bsp;
    }
}
