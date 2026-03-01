package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public interface lk {

    public static final class a extends pg<a> {
        public C0075a[] adt;

        /* renamed from: com.google.android.gms.internal.lk$a$a, reason: collision with other inner class name */
        public static final class C0075a extends pg<C0075a> {
            private static volatile C0075a[] adu;
            public String adv;
            public String adw;
            public int viewId;

            public C0075a() {
                lP();
            }

            public static C0075a[] lO() {
                if (adu == null) {
                    synchronized (pk.awI) {
                        if (adu == null) {
                            adu = new C0075a[0];
                        }
                    }
                }
                return adu;
            }

            @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
            public void a(pf pfVar) throws IOException {
                if (!this.adv.equals("")) {
                    pfVar.b(1, this.adv);
                }
                if (!this.adw.equals("")) {
                    pfVar.b(2, this.adw);
                }
                if (this.viewId != 0) {
                    pfVar.s(3, this.viewId);
                }
                super.a(pfVar);
            }

            @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
            protected int c() {
                int iC = super.c();
                if (!this.adv.equals("")) {
                    iC += pf.j(1, this.adv);
                }
                if (!this.adw.equals("")) {
                    iC += pf.j(2, this.adw);
                }
                return this.viewId != 0 ? iC + pf.u(3, this.viewId) : iC;
            }

            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof C0075a)) {
                    return false;
                }
                C0075a c0075a = (C0075a) o;
                if (this.adv == null) {
                    if (c0075a.adv != null) {
                        return false;
                    }
                } else if (!this.adv.equals(c0075a.adv)) {
                    return false;
                }
                if (this.adw == null) {
                    if (c0075a.adw != null) {
                        return false;
                    }
                } else if (!this.adw.equals(c0075a.adw)) {
                    return false;
                }
                if (this.viewId == c0075a.viewId) {
                    return a(c0075a);
                }
                return false;
            }

            public int hashCode() {
                return (((((((this.adv == null ? 0 : this.adv.hashCode()) + 527) * 31) + (this.adw != null ? this.adw.hashCode() : 0)) * 31) + this.viewId) * 31) + qx();
            }

            public C0075a lP() {
                this.adv = "";
                this.adw = "";
                this.viewId = 0;
                this.awy = null;
                this.awJ = -1;
                return this;
            }

            @Override // com.google.android.gms.internal.pm
            /* renamed from: o, reason: merged with bridge method [inline-methods] */
            public C0075a b(pe peVar) throws IOException {
                while (true) {
                    int iQg = peVar.qg();
                    switch (iQg) {
                        case 0:
                            break;
                        case 10:
                            this.adv = peVar.readString();
                            break;
                        case 18:
                            this.adw = peVar.readString();
                            break;
                        case 24:
                            this.viewId = peVar.qj();
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

        public a() {
            lN();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.adt != null && this.adt.length > 0) {
                for (int i = 0; i < this.adt.length; i++) {
                    C0075a c0075a = this.adt[i];
                    if (c0075a != null) {
                        pfVar.a(1, c0075a);
                    }
                }
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.adt != null && this.adt.length > 0) {
                for (int i = 0; i < this.adt.length; i++) {
                    C0075a c0075a = this.adt[i];
                    if (c0075a != null) {
                        iC += pf.c(1, c0075a);
                    }
                }
            }
            return iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (pk.equals(this.adt, aVar.adt)) {
                return a(aVar);
            }
            return false;
        }

        public int hashCode() {
            return ((pk.hashCode(this.adt) + 527) * 31) + qx();
        }

        public a lN() {
            this.adt = C0075a.lO();
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: n, reason: merged with bridge method [inline-methods] */
        public a b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        int iB = pp.b(peVar, 10);
                        int length = this.adt == null ? 0 : this.adt.length;
                        C0075a[] c0075aArr = new C0075a[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.adt, 0, c0075aArr, 0, length);
                        }
                        while (length < c0075aArr.length - 1) {
                            c0075aArr[length] = new C0075a();
                            peVar.a(c0075aArr[length]);
                            peVar.qg();
                            length++;
                        }
                        c0075aArr[length] = new C0075a();
                        peVar.a(c0075aArr[length]);
                        this.adt = c0075aArr;
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
}
