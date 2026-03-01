package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;
import com.google.android.gms.internal.bq;
import com.google.android.gms.internal.bs;

@ez
/* loaded from: classes.dex */
public class bp extends bs.a implements bq.a {
    private final Object mw = new Object();
    private final String pl;
    private final Drawable pm;
    private final String pn;
    private final String pp;
    private bq pt;
    private final Drawable pu;
    private final String pv;

    public bp(String str, Drawable drawable, String str2, Drawable drawable2, String str3, String str4) {
        this.pl = str;
        this.pm = drawable;
        this.pn = str2;
        this.pu = drawable2;
        this.pp = str3;
        this.pv = str4;
    }

    @Override // com.google.android.gms.internal.bq.a
    public void a(bq bqVar) {
        synchronized (this.mw) {
            this.pt = bqVar;
        }
    }

    @Override // com.google.android.gms.internal.bs
    public void as() {
        synchronized (this.mw) {
            if (this.pt == null) {
                gs.T("Attempt to record impression before content ad initialized.");
            } else {
                this.pt.as();
            }
        }
    }

    @Override // com.google.android.gms.internal.bs
    public com.google.android.gms.dynamic.d bA() {
        return com.google.android.gms.dynamic.e.k(this.pu);
    }

    @Override // com.google.android.gms.internal.bs
    public String bB() {
        return this.pv;
    }

    @Override // com.google.android.gms.internal.bs
    public String bt() {
        return this.pl;
    }

    @Override // com.google.android.gms.internal.bs
    public com.google.android.gms.dynamic.d bu() {
        return com.google.android.gms.dynamic.e.k(this.pm);
    }

    @Override // com.google.android.gms.internal.bs
    public String bw() {
        return this.pp;
    }

    @Override // com.google.android.gms.internal.bs
    public String getBody() {
        return this.pn;
    }

    @Override // com.google.android.gms.internal.bs
    public void i(int i) {
        synchronized (this.mw) {
            if (this.pt == null) {
                gs.T("Attempt to perform click before content ad initialized.");
            } else {
                this.pt.b("1", i);
            }
        }
    }
}
