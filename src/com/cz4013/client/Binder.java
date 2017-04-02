package com.cz4013.client;

import java.util.HashMap;

/**
 * Created by danielseetoh on 3/31/17.
 */
public class Binder {
    private HashMap<String, RemoteObject> objectReferenceHashMap = new HashMap<String, RemoteObject>();

    /**
     * add an object reference
     * @param name
     * @param objRef
     */
    public void addObjectReference(String name, RemoteObject objRef){
        this.objectReferenceHashMap.put(name, objRef);
    }

    /**
     * get an object reference
     * @param name
     * @return
     */
    public RemoteObject getObjectReference(String name){
        return this.objectReferenceHashMap.get(name);
    }
}
