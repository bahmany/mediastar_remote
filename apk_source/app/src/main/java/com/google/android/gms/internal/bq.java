package com.google.android.gms.internal;

import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class bq {
    private u pw;
    private ah px;
    private JSONObject py;

    public interface a {
        void a(bq bqVar);
    }

    public bq(u uVar, ah ahVar, JSONObject jSONObject) {
        this.pw = uVar;
        this.px = ahVar;
        this.py = jSONObject;
    }

    public void as() {
        this.pw.aj();
    }

    public void b(String str, int i) throws JSONException {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("asset", i);
            jSONObject.put("template", str);
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("ad", this.py);
            jSONObject2.put("click", jSONObject);
            this.px.a("google.afma.nativeAds.handleClick", jSONObject2);
        } catch (JSONException e) {
            gs.b("Unable to create click JSON.", e);
        }
    }
}
