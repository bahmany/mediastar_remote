package com.iflytek.cloud.a.f;

import java.security.MessageDigest;

/* loaded from: classes.dex */
public class d {
    public static synchronized String a(String str) {
        String string;
        synchronized (d.class) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                char[] charArray = str.toCharArray();
                byte[] bArr = new byte[charArray.length];
                for (int i = 0; i < charArray.length; i++) {
                    bArr[i] = (byte) charArray[i];
                }
                byte[] bArrDigest = messageDigest.digest(bArr);
                StringBuffer stringBuffer = new StringBuffer();
                for (byte b : bArrDigest) {
                    int i2 = b & 255;
                    if (i2 < 16) {
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(Integer.toHexString(i2));
                }
                string = stringBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                string = "";
            }
        }
        return string;
    }
}
