package com.google.android.gms.internal;

import android.content.Context;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@ez
/* loaded from: classes.dex */
public final class gq extends gg {
    private final Context mContext;
    private final String mv;
    private final String uR;
    private String vW;

    public gq(Context context, String str, String str2) {
        this.vW = null;
        this.mContext = context;
        this.mv = str;
        this.uR = str2;
    }

    public gq(Context context, String str, String str2, String str3) {
        this.vW = null;
        this.mContext = context;
        this.mv = str;
        this.uR = str2;
        this.vW = str3;
    }

    @Override // com.google.android.gms.internal.gg
    public void cp() {
        try {
            gs.V("Pinging URL: " + this.uR);
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.uR).openConnection();
            try {
                if (this.vW == null) {
                    gj.a(this.mContext, this.mv, true, httpURLConnection);
                } else {
                    gj.a(this.mContext, this.mv, true, httpURLConnection, this.vW);
                }
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    gs.W("Received non-success response code " + responseCode + " from pinging URL: " + this.uR);
                }
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            gs.W("Error while pinging URL: " + this.uR + ". " + e.getMessage());
        } catch (IndexOutOfBoundsException e2) {
            gs.W("Error while parsing ping URL: " + this.uR + ". " + e2.getMessage());
        }
    }

    @Override // com.google.android.gms.internal.gg
    public void onStop() {
    }
}
