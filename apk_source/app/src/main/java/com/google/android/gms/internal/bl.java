package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

@ez
/* loaded from: classes.dex */
public class bl {
    private final Context mContext;

    public bl(Context context) {
        com.google.android.gms.common.internal.n.b(context, "Context can not be null");
        this.mContext = context;
    }

    public static boolean bn() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public boolean a(Intent intent) {
        com.google.android.gms.common.internal.n.b(intent, "Intent can not be null");
        return !this.mContext.getPackageManager().queryIntentActivities(intent, 0).isEmpty();
    }

    public boolean bj() {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:"));
        return a(intent);
    }

    public boolean bk() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("sms:"));
        return a(intent);
    }

    public boolean bl() {
        return bn() && this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    public boolean bm() {
        return false;
    }

    public boolean bo() {
        return Build.VERSION.SDK_INT >= 14 && a(new Intent("android.intent.action.INSERT").setType("vnd.android.cursor.dir/event"));
    }
}
