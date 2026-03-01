package com.google.android.gms.internal;

import com.google.android.gms.internal.fo;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class fq implements fo.a<bp> {
    @Override // com.google.android.gms.internal.fo.a
    /* renamed from: c, reason: merged with bridge method [inline-methods] */
    public bp a(fo foVar, JSONObject jSONObject) throws ExecutionException, JSONException, InterruptedException {
        return new bp(jSONObject.getString("headline"), foVar.a(jSONObject, "image", true).get(), jSONObject.getString("body"), foVar.a(jSONObject, "secondary_image", false).get(), jSONObject.getString("call_to_action"), jSONObject.getString("attribution"));
    }
}
