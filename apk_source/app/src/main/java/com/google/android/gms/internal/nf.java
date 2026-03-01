package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/* loaded from: classes.dex */
public class nf {
    private final nn aku;
    private nl akv;

    public interface a {
        void b(PendingIntent pendingIntent);

        void mS();

        void mT();
    }

    public nf(Context context, int i, String str, String str2, a aVar, boolean z) {
        int i2 = 0;
        String packageName = context.getPackageName();
        try {
            i2 = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("PlayLogger", "This can't happen.");
        }
        this.akv = new nl(packageName, i2, i, str, str2, z);
        this.aku = new nn(context, new nk(aVar));
    }

    public void a(long j, String str, byte[] bArr, String... strArr) {
        this.aku.b(this.akv, new nh(j, str, bArr, strArr));
    }

    public void b(String str, byte[] bArr, String... strArr) {
        a(System.currentTimeMillis(), str, bArr, strArr);
    }

    public void start() {
        this.aku.start();
    }

    public void stop() {
        this.aku.stop();
    }
}
