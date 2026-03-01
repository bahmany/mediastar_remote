package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.internal.nf;

/* loaded from: classes.dex */
public class ne implements nf.a {
    private final nf aks;
    private boolean akt;

    public ne(Context context, int i) {
        this(context, i, null);
    }

    public ne(Context context, int i, String str) {
        this(context, i, str, null, true);
    }

    public ne(Context context, int i, String str, String str2, boolean z) {
        this.aks = new nf(context, i, str, str2, this, z);
        this.akt = true;
    }

    private void mR() {
        if (!this.akt) {
            throw new IllegalStateException("Cannot reuse one-time logger after sending.");
        }
    }

    public void a(String str, byte[] bArr, String... strArr) {
        mR();
        this.aks.b(str, bArr, strArr);
    }

    @Override // com.google.android.gms.internal.nf.a
    public void b(PendingIntent pendingIntent) {
        Log.w("OneTimePlayLogger", "logger connection failed: " + pendingIntent);
    }

    @Override // com.google.android.gms.internal.nf.a
    public void mS() {
        this.aks.stop();
    }

    @Override // com.google.android.gms.internal.nf.a
    public void mT() {
        Log.w("OneTimePlayLogger", "logger connection failed");
    }

    public void send() {
        mR();
        this.aks.start();
        this.akt = false;
    }
}
