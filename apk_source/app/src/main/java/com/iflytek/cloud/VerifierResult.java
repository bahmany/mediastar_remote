package com.iflytek.cloud;

import org.json.JSONObject;
import org.json.JSONTokener;

/* loaded from: classes.dex */
public class VerifierResult {
    public static final String TAG = "VerifyResult";
    public String dcs;
    public int err;
    public boolean ret;
    public int rgn;
    public String source;
    public String sst;
    public int suc;
    public String trs;
    public String vid;

    public VerifierResult(String str) {
        this.ret = false;
        this.dcs = "";
        this.vid = "";
        this.suc = 0;
        this.rgn = 0;
        this.trs = "";
        this.err = 0;
        this.source = "";
        try {
            this.source = str;
            JSONObject jSONObject = new JSONObject(new JSONTokener(this.source));
            com.iflytek.cloud.a.f.a.a.a("VerifyResult = " + this.source);
            if (jSONObject.has("ret")) {
                this.ret = jSONObject.getInt("ret") == 0;
            }
            if (jSONObject.has("sst")) {
                this.sst = jSONObject.getString("sst");
            }
            if (jSONObject.has("dcs")) {
                this.dcs = jSONObject.getString("dcs");
            }
            if (jSONObject.has("suc")) {
                this.suc = jSONObject.getInt("suc");
            }
            if (jSONObject.has("vid")) {
                this.vid = jSONObject.getString("vid");
            }
            if (jSONObject.has("rgn")) {
                this.rgn = jSONObject.getInt("rgn");
            }
            if (jSONObject.has("trs")) {
                this.trs = jSONObject.getString("trs");
            }
            if (jSONObject.has("err")) {
                this.err = jSONObject.getInt("err");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
