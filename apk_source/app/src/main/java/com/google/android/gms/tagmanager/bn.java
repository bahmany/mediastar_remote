package com.google.android.gms.tagmanager;

import android.os.Build;

/* loaded from: classes.dex */
class bn {
    bn() {
    }

    int nN() {
        return Build.VERSION.SDK_INT;
    }

    public bm ov() {
        return nN() < 8 ? new av() : new aw();
    }
}
