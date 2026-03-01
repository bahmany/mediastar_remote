package com.google.android.gms.internal;

import com.google.android.gms.internal.pg;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class pg<M extends pg<M>> extends pm {
    protected pi awy;

    public final <T> T a(ph<M, T> phVar) {
        pj pjVarGD;
        if (this.awy == null || (pjVarGD = this.awy.gD(pp.gH(phVar.tag))) == null) {
            return null;
        }
        return (T) pjVarGD.b(phVar);
    }

    @Override // com.google.android.gms.internal.pm
    public void a(pf pfVar) throws IOException {
        if (this.awy == null) {
            return;
        }
        for (int i = 0; i < this.awy.size(); i++) {
            this.awy.gE(i).a(pfVar);
        }
    }

    protected final boolean a(pe peVar, int i) throws IOException {
        int position = peVar.getPosition();
        if (!peVar.gm(i)) {
            return false;
        }
        int iGH = pp.gH(i);
        po poVar = new po(i, peVar.r(position, peVar.getPosition() - position));
        pj pjVarGD = null;
        if (this.awy == null) {
            this.awy = new pi();
        } else {
            pjVarGD = this.awy.gD(iGH);
        }
        if (pjVarGD == null) {
            pjVarGD = new pj();
            this.awy.a(iGH, pjVarGD);
        }
        pjVarGD.a(poVar);
        return true;
    }

    protected final boolean a(M m) {
        return (this.awy == null || this.awy.isEmpty()) ? m.awy == null || m.awy.isEmpty() : this.awy.equals(m.awy);
    }

    @Override // com.google.android.gms.internal.pm
    protected int c() {
        if (this.awy == null) {
            return 0;
        }
        int iC = 0;
        for (int i = 0; i < this.awy.size(); i++) {
            iC += this.awy.gE(i).c();
        }
        return iC;
    }

    protected final int qx() {
        if (this.awy == null || this.awy.isEmpty()) {
            return 0;
        }
        return this.awy.hashCode();
    }
}
