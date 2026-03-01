package com.google.android.gms.internal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public abstract class ao {
    private static MessageDigest nI = null;
    protected Object mw = new Object();

    protected MessageDigest ba() {
        MessageDigest messageDigest;
        synchronized (this.mw) {
            if (nI != null) {
                messageDigest = nI;
            } else {
                for (int i = 0; i < 2; i++) {
                    try {
                        nI = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                    }
                }
                messageDigest = nI;
            }
        }
        return messageDigest;
    }

    abstract byte[] l(String str);
}
