package com.google.android.gms.internal;

import com.google.android.gms.internal.c;
import java.io.IOException;

/* loaded from: classes.dex */
public interface ok {

    public static final class a extends pg<a> {
        public long asg;
        public c.j ash;
        public c.f gs;

        public a() {
            pJ();
        }

        public static a l(byte[] bArr) throws pl {
            return (a) pm.a(new a(), bArr);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            pfVar.b(1, this.asg);
            if (this.gs != null) {
                pfVar.a(2, this.gs);
            }
            if (this.ash != null) {
                pfVar.a(3, this.ash);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c() + pf.d(1, this.asg);
            if (this.gs != null) {
                iC += pf.c(2, this.gs);
            }
            return this.ash != null ? iC + pf.c(3, this.ash) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (this.asg != aVar.asg) {
                return false;
            }
            if (this.gs == null) {
                if (aVar.gs != null) {
                    return false;
                }
            } else if (!this.gs.equals(aVar.gs)) {
                return false;
            }
            if (this.ash == null) {
                if (aVar.ash != null) {
                    return false;
                }
            } else if (!this.ash.equals(aVar.ash)) {
                return false;
            }
            return a(aVar);
        }

        public int hashCode() {
            return (((((this.gs == null ? 0 : this.gs.hashCode()) + ((((int) (this.asg ^ (this.asg >>> 32))) + 527) * 31)) * 31) + (this.ash != null ? this.ash.hashCode() : 0)) * 31) + qx();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: p, reason: merged with bridge method [inline-methods] */
        public a b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        this.asg = peVar.qi();
                        break;
                    case 18:
                        if (this.gs == null) {
                            this.gs = new c.f();
                        }
                        peVar.a(this.gs);
                        break;
                    case 26:
                        if (this.ash == null) {
                            this.ash = new c.j();
                        }
                        peVar.a(this.ash);
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

        public a pJ() {
            this.asg = 0L;
            this.gs = null;
            this.ash = null;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }
}
