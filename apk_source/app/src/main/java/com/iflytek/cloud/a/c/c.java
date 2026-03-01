package com.iflytek.cloud.a.c;

import android.os.SystemClock;
import android.text.TextUtils;
import com.iflytek.speech.UtilityConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class c {
    JSONObject a = new JSONObject();
    long b = 0;
    long c = 0;

    public synchronized String a() {
        return this.a.toString();
    }

    public void a(com.iflytek.cloud.b.a aVar) {
        this.c = System.currentTimeMillis();
        this.b = SystemClock.elapsedRealtime();
        a("app_start", com.iflytek.cloud.a.f.c.a(this.c), false);
        String strD = aVar.d(UtilityConfig.KEY_CALLER_APPID);
        if (TextUtils.isEmpty(strD)) {
            return;
        }
        a("app_caller_appid", strD, false);
    }

    public synchronized void a(String str) {
        a(str, SystemClock.elapsedRealtime() - this.b, false);
    }

    public synchronized void a(String str, long j, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            try {
                if (z) {
                    JSONArray jSONArrayOptJSONArray = this.a.optJSONArray(str);
                    if (jSONArrayOptJSONArray == null) {
                        jSONArrayOptJSONArray = new JSONArray();
                        this.a.put(str, jSONArrayOptJSONArray);
                    }
                    if (jSONArrayOptJSONArray != null) {
                        jSONArrayOptJSONArray.put(j);
                    }
                } else {
                    this.a.put(str, j);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void a(String str, String str2, boolean z) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (z) {
                    JSONArray jSONArrayOptJSONArray = this.a.optJSONArray(str);
                    if (jSONArrayOptJSONArray == null) {
                        jSONArrayOptJSONArray = new JSONArray();
                        this.a.put(str, jSONArrayOptJSONArray);
                    }
                    if (jSONArrayOptJSONArray != null) {
                        jSONArrayOptJSONArray.put(str2);
                    }
                } else {
                    this.a.put(str, str2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
