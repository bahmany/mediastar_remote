package com.google.android.gms.internal;

import com.google.android.gms.internal.cq;
import com.google.android.gms.internal.cv;

@ez
/* loaded from: classes.dex */
public final class co extends cv.a {
    private final Object mw = new Object();
    private cq.a qm;
    private cn qn;

    public void a(cn cnVar) {
        synchronized (this.mw) {
            this.qn = cnVar;
        }
    }

    public void a(cq.a aVar) {
        synchronized (this.mw) {
            this.qm = aVar;
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdClicked() {
        synchronized (this.mw) {
            if (this.qn != null) {
                this.qn.ae();
            }
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdClosed() {
        synchronized (this.mw) {
            if (this.qn != null) {
                this.qn.af();
            }
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdFailedToLoad(int error) {
        synchronized (this.mw) {
            if (this.qm != null) {
                this.qm.j(error == 3 ? 1 : 2);
                this.qm = null;
            }
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdLeftApplication() {
        synchronized (this.mw) {
            if (this.qn != null) {
                this.qn.ag();
            }
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdLoaded() {
        synchronized (this.mw) {
            if (this.qm != null) {
                this.qm.j(0);
                this.qm = null;
            } else {
                if (this.qn != null) {
                    this.qn.ai();
                }
            }
        }
    }

    @Override // com.google.android.gms.internal.cv
    public void onAdOpened() {
        synchronized (this.mw) {
            if (this.qn != null) {
                this.qn.ah();
            }
        }
    }
}
