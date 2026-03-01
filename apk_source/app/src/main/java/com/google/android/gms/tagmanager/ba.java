package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import com.google.android.gms.tagmanager.cr;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class ba {
    public static cr.c cD(String str) throws JSONException {
        d.a aVarN = n(new JSONObject(str));
        cr.d dVarOV = cr.c.oV();
        for (int i = 0; i < aVarN.gx.length; i++) {
            dVarOV.a(cr.a.oR().b(com.google.android.gms.internal.b.INSTANCE_NAME.toString(), aVarN.gx[i]).b(com.google.android.gms.internal.b.FUNCTION.toString(), di.cU(m.nO())).b(m.nP(), aVarN.gy[i]).oU());
        }
        return dVarOV.oY();
    }

    private static d.a n(Object obj) throws JSONException {
        return di.u(o(obj));
    }

    static Object o(Object obj) throws JSONException {
        if (obj instanceof JSONArray) {
            throw new RuntimeException("JSONArrays are not supported");
        }
        if (JSONObject.NULL.equals(obj)) {
            throw new RuntimeException("JSON nulls are not supported");
        }
        if (!(obj instanceof JSONObject)) {
            return obj;
        }
        JSONObject jSONObject = (JSONObject) obj;
        HashMap map = new HashMap();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            map.put(next, o(jSONObject.get(next)));
        }
        return map;
    }
}
