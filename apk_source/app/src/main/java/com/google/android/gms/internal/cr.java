package com.google.android.gms.internal;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class cr {
    public static List<String> a(JSONObject jSONObject, String str) throws JSONException {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray(str);
        if (jSONArrayOptJSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(jSONArrayOptJSONArray.length());
        for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
            arrayList.add(jSONArrayOptJSONArray.getString(i));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static void a(Context context, String str, fz fzVar, String str2, boolean z, List<String> list) {
        String str3 = z ? "1" : "0";
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String strReplaceAll = it.next().replaceAll("@gw_adlocid@", str2).replaceAll("@gw_adnetrefresh@", str3).replaceAll("@gw_qdata@", fzVar.vq.qi).replaceAll("@gw_sdkver@", str).replaceAll("@gw_sessid@", gb.vK).replaceAll("@gw_seqnum@", fzVar.tA);
            if (fzVar.qy != null) {
                strReplaceAll = strReplaceAll.replaceAll("@gw_adnetid@", fzVar.qy.pX).replaceAll("@gw_allocid@", fzVar.qy.pZ);
            }
            new gq(context, str, strReplaceAll).start();
        }
    }
}
