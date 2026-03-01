package com.google.android.gms.internal;

import com.google.android.gms.internal.fo;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class fp implements fo.a<bo> {
    @Override // com.google.android.gms.internal.fo.a
    /* renamed from: b */
    public bo a(fo foVar, JSONObject jSONObject) throws ExecutionException, JSONException, InterruptedException {
        return new bo(jSONObject.getString("headline"), foVar.a(jSONObject, "image", true).get(), jSONObject.getString("body"), foVar.a(jSONObject, "app_icon", true).get(), jSONObject.getString("call_to_action"), jSONObject.optDouble("rating", -1.0d), jSONObject.optString("store"), jSONObject.optString("price"));
    }
}
