package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;
import com.google.android.gms.internal.bq;
import com.google.android.gms.internal.br;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;

@ez
/* loaded from: classes.dex */
public class bo extends br.a implements bq.a {
    private final Object mw = new Object();
    private final String pl;
    private final Drawable pm;
    private final String pn;
    private final Drawable po;
    private final String pp;
    private final double pq;
    private final String pr;
    private final String ps;
    private bq pt;

    public bo(String str, Drawable drawable, String str2, Drawable drawable2, String str3, double d, String str4, String str5) {
        this.pl = str;
        this.pm = drawable;
        this.pn = str2;
        this.po = drawable2;
        this.pp = str3;
        this.pq = d;
        this.pr = str4;
        this.ps = str5;
    }

    @Override // com.google.android.gms.internal.bq.a
    public void a(bq bqVar) {
        synchronized (this.mw) {
            this.pt = bqVar;
        }
    }

    @Override // com.google.android.gms.internal.br
    public void as() {
        synchronized (this.mw) {
            if (this.pt == null) {
                gs.T("Attempt to record impression before app install ad initialized.");
            } else {
                this.pt.as();
            }
        }
    }

    @Override // com.google.android.gms.internal.br
    public String bt() {
        return this.pl;
    }

    @Override // com.google.android.gms.internal.br
    public com.google.android.gms.dynamic.d bu() {
        return com.google.android.gms.dynamic.e.k(this.pm);
    }

    @Override // com.google.android.gms.internal.br
    public com.google.android.gms.dynamic.d bv() {
        return com.google.android.gms.dynamic.e.k(this.po);
    }

    @Override // com.google.android.gms.internal.br
    public String bw() {
        return this.pp;
    }

    @Override // com.google.android.gms.internal.br
    public double bx() {
        return this.pq;
    }

    @Override // com.google.android.gms.internal.br
    public String by() {
        return this.pr;
    }

    @Override // com.google.android.gms.internal.br
    public String bz() {
        return this.ps;
    }

    @Override // com.google.android.gms.internal.br
    public String getBody() {
        return this.pn;
    }

    @Override // com.google.android.gms.internal.br
    public void i(int i) {
        synchronized (this.mw) {
            if (this.pt == null) {
                gs.T("Attempt to perform click before app install ad initialized.");
            } else {
                this.pt.b(ContentTree.AUDIO_ID, i);
            }
        }
    }
}
