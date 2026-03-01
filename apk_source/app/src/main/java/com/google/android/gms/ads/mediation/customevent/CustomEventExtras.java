package com.google.android.gms.ads.mediation.customevent;

import com.google.ads.mediation.NetworkExtras;
import java.util.HashMap;

@Deprecated
/* loaded from: classes.dex */
public final class CustomEventExtras implements NetworkExtras {
    private final HashMap<String, Object> xj = new HashMap<>();

    public Object getExtra(String label) {
        return this.xj.get(label);
    }

    public void setExtra(String label, Object value) {
        this.xj.put(label, value);
    }
}
