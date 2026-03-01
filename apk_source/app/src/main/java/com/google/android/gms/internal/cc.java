package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;

@ez
/* loaded from: classes.dex */
public class cc implements by {
    static final Map<String, Integer> pK = new HashMap();

    static {
        pK.put("resize", 1);
        pK.put("playVideo", 2);
        pK.put("storePicture", 3);
        pK.put("createCalendarEvent", 4);
    }

    @Override // com.google.android.gms.internal.by
    public void a(gv gvVar, Map<String, String> map) throws JSONException {
        switch (pK.get(map.get("a")).intValue()) {
            case 1:
                new dd(gvVar, map).execute();
                break;
            case 2:
            default:
                gs.U("Unknown MRAID command called.");
                break;
            case 3:
                new de(gvVar, map).execute();
                break;
            case 4:
                new dc(gvVar, map).execute();
                break;
        }
    }
}
