package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class cm {
    public final List<cl> qd;
    public final long qe;
    public final List<String> qf;
    public final List<String> qg;
    public final List<String> qh;
    public final String qi;
    public final long qj;
    public int qk;
    public int ql;

    public cm(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (gs.u(2)) {
            gs.V("Mediation Response JSON: " + jSONObject.toString(2));
        }
        JSONArray jSONArray = jSONObject.getJSONArray("ad_networks");
        ArrayList arrayList = new ArrayList(jSONArray.length());
        int i = -1;
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            cl clVar = new cl(jSONArray.getJSONObject(i2));
            arrayList.add(clVar);
            if (i < 0 && a(clVar)) {
                i = i2;
            }
        }
        this.qk = i;
        this.ql = jSONArray.length();
        this.qd = Collections.unmodifiableList(arrayList);
        this.qi = jSONObject.getString("qdata");
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("settings");
        if (jSONObjectOptJSONObject == null) {
            this.qe = -1L;
            this.qf = null;
            this.qg = null;
            this.qh = null;
            this.qj = -1L;
            return;
        }
        this.qe = jSONObjectOptJSONObject.optLong("ad_network_timeout_millis", -1L);
        this.qf = cr.a(jSONObjectOptJSONObject, "click_urls");
        this.qg = cr.a(jSONObjectOptJSONObject, "imp_urls");
        this.qh = cr.a(jSONObjectOptJSONObject, "nofill_urls");
        long jOptLong = jSONObjectOptJSONObject.optLong("refresh", -1L);
        this.qj = jOptLong > 0 ? jOptLong * 1000 : -1L;
    }

    private boolean a(cl clVar) {
        Iterator<String> it = clVar.pY.iterator();
        while (it.hasNext()) {
            if (it.next().equals("com.google.ads.mediation.admob.AdMobAdapter")) {
                return true;
            }
        }
        return false;
    }
}
