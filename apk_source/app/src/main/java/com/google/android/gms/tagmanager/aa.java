package com.google.android.gms.tagmanager;

import android.os.Build;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class aa extends aj {
    private static final String ID = com.google.android.gms.internal.a.DEVICE_NAME.toString();

    public aa() {
        super(ID, new String[0]);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String str = Build.MANUFACTURER;
        String str2 = Build.MODEL;
        if (!str2.startsWith(str) && !str.equals("unknown")) {
            str2 = str + " " + str2;
        }
        return di.u(str2);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
