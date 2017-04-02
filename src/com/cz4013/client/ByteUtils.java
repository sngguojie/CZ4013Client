package com.cz4013.client;

/**
 * Created by melvynsng on 4/1/17.
 */
public class ByteUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * To convert the byte array to a hexadecimal string
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * To get the integer representation of a byte array of length 2
     * @param bytes
     * @return
     */
    public static int getBytesAsHalfWord (byte[] bytes) {
        return ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
    }

    /**
     * To get the 2 byte array representation of an integer
     * @param halfword
     * @return
     */
    public static byte[] getHalfWordAsBytes (int halfword) {
        byte[] data = new byte[2];
        data[0] = (byte) (halfword & 0xFF);
        data[1] = (byte) ((halfword >> 8) & 0xFF);
        return data;
    }

    /**
     * To combine 2 byte arrays together
     * @param a
     * @param b
     * @return
     */
    public static byte[] combineByteArrays (byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
