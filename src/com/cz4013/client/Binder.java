package com.cz4013.client;

import java.util.HashMap;

/**
 * Created by danielseetoh on 3/31/17.
 */
public class Binder {
    private HashMap<String, RemoteObject> objectReferenceHashMap = new HashMap<String, RemoteObject>();

    public void addObjectReference(String name, RemoteObject objRef){
        this.objectReferenceHashMap.put(name, objRef);
    }

    public RemoteObject getObjectReference(String name){
        return this.objectReferenceHashMap.get(name);
    }
}
