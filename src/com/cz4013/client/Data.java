package com.cz4013.client;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by danielseetoh on 3/31/17.
 */
public class Data {
    private String objectReference;
    private String methodId;
    private ArrayList<String> stringList;
    private ArrayList<Integer> intList;

    public Data(){
        this.stringList = new ArrayList<String>();
        this.intList = new ArrayList<Integer>();
    }

    /**
     * Adds a string to the stringList. This string is an argument in the request.
     */
    public void addString(String str){
        this.stringList.add(str);
    }

    /**
     * Adds an integer to the intList. This integer is an argument in the request.
     * @param i
     */
    public void addInt(int i){
        this.intList.add(i);
    }

    /**
     * Sets the objectreference.
     * @param objectReference
     */
    public void setObjectReference(String objectReference){
        this.objectReference = objectReference;
    }

    /**
     * Sets the method ID.
     * @param methodId
     */
    public void setMethodId(String methodId){
        this.methodId = methodId;
    }

    /**
     * Retrieve the list of string arguments.
     * @return
     */
    public ArrayList<String> getStringList(){
        return this.stringList;
    }

    /**
     * Retrieve the list of integer arguments.
     * @return
     */
    public ArrayList<Integer> getIntList(){
        return this.intList;
    }

    /**
     * Retrieve the object reference.
     * @return
     */
    public String getObjectReference(){
        return this.objectReference;
    }

    /**
     * Retrieve the method ID.
     * @return
     */
    public String getMethodId(){
        return this.methodId;
    }

    /**
     * Returns the object reference, method ID, all string arguments and all integer arguments as a string
     * Useful for printing and debugging.
     * @return
     */
    public String toString(){
        String result = "ObjectReference: " + this.objectReference + "\n";
        result += "MethodId: " + this.methodId + "\n";
        for (String s : this.stringList){
            result += s + "\n";
        }

        for (int i : this.intList){
            result += i + "\n";
        }

        return result;
    }

    /**
     * Returns all the string arguments as a string.
     * @return
     */
    public String stringListToString(){
        String result = "";
        for (String s : this.stringList){
            result += s + "\n";
        }
        return result;
    }

}
