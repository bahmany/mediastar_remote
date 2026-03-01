package com.google.android.gms.tagmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.gms.internal.c;
import com.google.android.gms.tagmanager.bg;
import com.google.android.gms.tagmanager.ce;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
class co implements Runnable {
    private final String anR;
    private volatile String aon;
    private final bn aqg;
    private final String aqh;
    private bg<c.j> aqi;
    private volatile r aqj;
    private volatile String aqk;
    private final Context mContext;

    co(Context context, String str, bn bnVar, r rVar) {
        this.mContext = context;
        this.aqg = bnVar;
        this.anR = str;
        this.aqj = rVar;
        this.aqh = "/r?id=" + str;
        this.aon = this.aqh;
        this.aqk = null;
    }

    public co(Context context, String str, r rVar) {
        this(context, str, new bn(), rVar);
    }

    private boolean oK() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        bh.V("...no network connectivity");
        return false;
    }

    private void oL() {
        if (!oK()) {
            this.aqi.a(bg.a.NOT_AVAILABLE);
            return;
        }
        bh.V("Start loading resource from network ...");
        String strOM = oM();
        bm bmVarOv = this.aqg.ov();
        try {
            try {
                InputStream inputStreamCA = bmVarOv.cA(strOM);
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    cr.b(inputStreamCA, byteArrayOutputStream);
                    c.j jVarB = c.j.b(byteArrayOutputStream.toByteArray());
                    bh.V("Successfully loaded supplemented resource: " + jVarB);
                    if (jVarB.gs == null && jVarB.gr.length == 0) {
                        bh.V("No change for container: " + this.anR);
                    }
                    this.aqi.l(jVarB);
                    bmVarOv.close();
                    bh.V("Load resource from network finished.");
                } catch (IOException e) {
                    bh.d("Error when parsing downloaded resources from url: " + strOM + " " + e.getMessage(), e);
                    this.aqi.a(bg.a.SERVER_ERROR);
                    bmVarOv.close();
                }
            } catch (FileNotFoundException e2) {
                bh.W("No data is retrieved from the given url: " + strOM + ". Make sure container_id: " + this.anR + " is correct.");
                this.aqi.a(bg.a.SERVER_ERROR);
                bmVarOv.close();
            } catch (IOException e3) {
                bh.d("Error when loading resources from url: " + strOM + " " + e3.getMessage(), e3);
                this.aqi.a(bg.a.IO_ERROR);
                bmVarOv.close();
            }
        } catch (Throwable th) {
            bmVarOv.close();
            throw th;
        }
    }

    void a(bg<c.j> bgVar) {
        this.aqi = bgVar;
    }

    void cG(String str) {
        bh.S("Setting previous container version: " + str);
        this.aqk = str;
    }

    void cr(String str) {
        if (str == null) {
            this.aon = this.aqh;
        } else {
            bh.S("Setting CTFE URL path: " + str);
            this.aon = str;
        }
    }

    String oM() {
        String str = this.aqj.ob() + this.aon + "&v=a65833898";
        if (this.aqk != null && !this.aqk.trim().equals("")) {
            str = str + "&pv=" + this.aqk;
        }
        return ce.oH().oI().equals(ce.a.CONTAINER_DEBUG) ? str + "&gtm_debug=x" : str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.aqi == null) {
            throw new IllegalStateException("callback must be set before execute");
        }
        this.aqi.nZ();
        oL();
    }
}
