package com.google.android.gms.internal;

import android.text.TextUtils;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class ii {
    protected final ip Go;
    private final String Gp;
    private ir Gq;

    protected ii(String str, String str2, String str3) throws IllegalArgumentException {
        ik.aF(str);
        this.Gp = str;
        this.Go = new ip(str2);
        if (TextUtils.isEmpty(str3)) {
            return;
        }
        this.Go.aK(str3);
    }

    public final void a(ir irVar) {
        this.Gq = irVar;
        if (this.Gq == null) {
            fB();
        }
    }

    protected final void a(String str, long j, String str2) throws IOException {
        this.Go.a("Sending text message: %s to: %s", str, str2);
        this.Gq.a(this.Gp, str, j, str2);
    }

    public void aD(String str) {
    }

    public void b(long j, int i) {
    }

    protected final long fA() {
        return this.Gq.fy();
    }

    public void fB() {
    }

    public final String getNamespace() {
        return this.Gp;
    }
}
