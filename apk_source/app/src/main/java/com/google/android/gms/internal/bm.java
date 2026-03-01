package com.google.android.gms.internal;

import android.text.TextUtils;

@ez
/* loaded from: classes.dex */
public final class bm {
    private String oU;
    private String oV;
    private String oW;

    public bm() {
        this.oU = null;
        this.oV = null;
        this.oW = null;
        this.oU = "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/sdk-core-v40.html";
        this.oV = null;
        this.oW = null;
    }

    public bm(String str, String str2, String str3) {
        this.oU = null;
        this.oV = null;
        this.oW = null;
        if (TextUtils.isEmpty(str)) {
            this.oU = "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/sdk-core-v40.html";
        } else {
            this.oU = str;
        }
        this.oV = str2;
        this.oW = str3;
    }

    public String bp() {
        return this.oU;
    }

    public String bq() {
        return this.oV;
    }

    public String br() {
        return this.oW;
    }
}
