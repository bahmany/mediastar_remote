package com.jcraft.jzlib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class GsZilb {
    private static final int BUFFERSIZE = 1024;
    private static final int MAXLENGTH = 3145728;

    public static byte[] Compress(byte[] object) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZOutputStream zOut = new ZOutputStream(out, 9);
            DataOutputStream objOut = new DataOutputStream(zOut);
            objOut.write(object);
            objOut.flush();
            zOut.close();
            byte[] data = out.toByteArray();
            out.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static byte[] UnCompress(byte[] object) throws IOException {
        byte[] data = new byte[MAXLENGTH];
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(object);
            ZInputStream zIn = new ZInputStream(in);
            DataInputStream objIn = new DataInputStream(zIn);
            int len = 0;
            int nextReadCount = 1024;
            while (true) {
                int count = objIn.read(data, len, nextReadCount);
                if (count != -1) {
                    len += count;
                    nextReadCount += len;
                    while (nextReadCount > MAXLENGTH - len) {
                        nextReadCount /= 2;
                    }
                } else {
                    byte[] trueData = new byte[len];
                    System.arraycopy(data, 0, trueData, 0, len);
                    objIn.close();
                    zIn.close();
                    in.close();
                    return trueData;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
