package com.google.android.gms.tagmanager;

import android.net.Uri;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/* loaded from: classes.dex */
class ce {
    private static ce apS;
    private volatile String anR;
    private volatile a apT;
    private volatile String apU;
    private volatile String apV;

    enum a {
        NONE,
        CONTAINER,
        CONTAINER_DEBUG
    }

    ce() {
        clear();
    }

    private String cF(String str) {
        return str.split("&")[0].split("=")[1];
    }

    private String j(Uri uri) {
        return uri.getQuery().replace("&gtm_debug=x", "");
    }

    static ce oH() {
        ce ceVar;
        synchronized (ce.class) {
            if (apS == null) {
                apS = new ce();
            }
            ceVar = apS;
        }
        return ceVar;
    }

    void clear() {
        this.apT = a.NONE;
        this.apU = null;
        this.anR = null;
        this.apV = null;
    }

    String getContainerId() {
        return this.anR;
    }

    synchronized boolean i(Uri uri) {
        boolean z = true;
        synchronized (this) {
            try {
                String strDecode = URLDecoder.decode(uri.toString(), "UTF-8");
                if (strDecode.matches("^tagmanager.c.\\S+:\\/\\/preview\\/p\\?id=\\S+&gtm_auth=\\S+&gtm_preview=\\d+(&gtm_debug=x)?$")) {
                    bh.V("Container preview url: " + strDecode);
                    if (strDecode.matches(".*?&gtm_debug=x$")) {
                        this.apT = a.CONTAINER_DEBUG;
                    } else {
                        this.apT = a.CONTAINER;
                    }
                    this.apV = j(uri);
                    if (this.apT == a.CONTAINER || this.apT == a.CONTAINER_DEBUG) {
                        this.apU = "/r?" + this.apV;
                    }
                    this.anR = cF(this.apV);
                } else if (!strDecode.matches("^tagmanager.c.\\S+:\\/\\/preview\\/p\\?id=\\S+&gtm_preview=$")) {
                    bh.W("Invalid preview uri: " + strDecode);
                    z = false;
                } else if (cF(uri.getQuery()).equals(this.anR)) {
                    bh.V("Exit preview mode for container: " + this.anR);
                    this.apT = a.NONE;
                    this.apU = null;
                } else {
                    z = false;
                }
            } catch (UnsupportedEncodingException e) {
                z = false;
            }
        }
        return z;
    }

    a oI() {
        return this.apT;
    }

    String oJ() {
        return this.apU;
    }
}
