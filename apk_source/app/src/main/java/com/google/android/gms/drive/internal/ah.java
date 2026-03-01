package com.google.android.gms.drive.internal;

import com.google.android.gms.internal.pe;
import com.google.android.gms.internal.pf;
import com.google.android.gms.internal.pg;
import com.google.android.gms.internal.pl;
import com.google.android.gms.internal.pm;
import java.io.IOException;

/* loaded from: classes.dex */
public final class ah extends pg<ah> {
    public String Pd;
    public long Pe;
    public long Pf;
    public int versionCode;

    public ah() {
        ic();
    }

    public static ah g(byte[] bArr) throws pl {
        return (ah) pm.a(new ah(), bArr);
    }

    @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
    public void a(pf pfVar) throws IOException {
        pfVar.s(1, this.versionCode);
        pfVar.b(2, this.Pd);
        pfVar.c(3, this.Pe);
        pfVar.c(4, this.Pf);
        super.a(pfVar);
    }

    @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
    protected int c() {
        return super.c() + pf.u(1, this.versionCode) + pf.j(2, this.Pd) + pf.e(3, this.Pe) + pf.e(4, this.Pf);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ah)) {
            return false;
        }
        ah ahVar = (ah) o;
        if (this.versionCode != ahVar.versionCode) {
            return false;
        }
        if (this.Pd == null) {
            if (ahVar.Pd != null) {
                return false;
            }
        } else if (!this.Pd.equals(ahVar.Pd)) {
            return false;
        }
        if (this.Pe == ahVar.Pe && this.Pf == ahVar.Pf) {
            return a(ahVar);
        }
        return false;
    }

    public int hashCode() {
        return (((((((this.Pd == null ? 0 : this.Pd.hashCode()) + ((this.versionCode + 527) * 31)) * 31) + ((int) (this.Pe ^ (this.Pe >>> 32)))) * 31) + ((int) (this.Pf ^ (this.Pf >>> 32)))) * 31) + qx();
    }

    public ah ic() {
        this.versionCode = 1;
        this.Pd = "";
        this.Pe = -1L;
        this.Pf = -1L;
        this.awy = null;
        this.awJ = -1;
        return this;
    }

    @Override // com.google.android.gms.internal.pm
    /* renamed from: m, reason: merged with bridge method [inline-methods] */
    public ah b(pe peVar) throws IOException {
        while (true) {
            int iQg = peVar.qg();
            switch (iQg) {
                case 0:
                    break;
                case 8:
                    this.versionCode = peVar.qj();
                    break;
                case 18:
                    this.Pd = peVar.readString();
                    break;
                case 24:
                    this.Pe = peVar.qm();
                    break;
                case 32:
                    this.Pf = peVar.qm();
                    break;
                default:
                    if (!a(peVar, iQg)) {
                        break;
                    } else {
                        break;
                    }
            }
        }
        return this;
    }
}
