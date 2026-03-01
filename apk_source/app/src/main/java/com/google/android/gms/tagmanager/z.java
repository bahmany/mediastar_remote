package com.google.android.gms.tagmanager;

import android.content.Context;
import android.provider.Settings;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class z extends aj {
    private static final String ID = com.google.android.gms.internal.a.DEVICE_ID.toString();
    private final Context mContext;

    public z(Context context) {
        super(ID, new String[0]);
        this.mContext = context;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String strX = X(this.mContext);
        return strX == null ? di.pI() : di.u(strX);
    }

    protected String X(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
