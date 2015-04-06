package com.xxmicloxx.znetworklib;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: ml
 * Date: 21.12.13
 * Time: 11:03
 */
public class ByteArrayUtils {
    public static Object fromByteArray(byte[] data) {
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException ignore) {
        } catch (ClassNotFoundException ignore) {/*this packet is not found*/} finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ignore) {}
            }
        }
        return null;
    }

    public static byte[] toByteArray(Object obj) {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            return baos.toByteArray();
        } catch (IOException ignore) {}
        return null;
    }
}
