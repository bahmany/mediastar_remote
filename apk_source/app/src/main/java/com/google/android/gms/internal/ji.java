package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class ji {

    public static class a<I, O> implements SafeParcelable {
        public static final jk CREATOR = new jk();
        private final int BR;
        protected final int Mq;
        protected final boolean Mr;
        protected final int Ms;
        protected final boolean Mt;
        protected final String Mu;
        protected final int Mv;
        protected final Class<? extends ji> Mw;
        protected final String Mx;
        private jm My;
        private b<I, O> Mz;

        a(int i, int i2, boolean z, int i3, boolean z2, String str, int i4, String str2, jd jdVar) {
            this.BR = i;
            this.Mq = i2;
            this.Mr = z;
            this.Ms = i3;
            this.Mt = z2;
            this.Mu = str;
            this.Mv = i4;
            if (str2 == null) {
                this.Mw = null;
                this.Mx = null;
            } else {
                this.Mw = jp.class;
                this.Mx = str2;
            }
            if (jdVar == null) {
                this.Mz = null;
            } else {
                this.Mz = (b<I, O>) jdVar.hb();
            }
        }

        protected a(int i, boolean z, int i2, boolean z2, String str, int i3, Class<? extends ji> cls, b<I, O> bVar) {
            this.BR = 1;
            this.Mq = i;
            this.Mr = z;
            this.Ms = i2;
            this.Mt = z2;
            this.Mu = str;
            this.Mv = i3;
            this.Mw = cls;
            if (cls == null) {
                this.Mx = null;
            } else {
                this.Mx = cls.getCanonicalName();
            }
            this.Mz = bVar;
        }

        public static a a(String str, int i, b<?, ?> bVar, boolean z) {
            return new a(bVar.hd(), z, bVar.he(), false, str, i, null, bVar);
        }

        public static <T extends ji> a<T, T> a(String str, int i, Class<T> cls) {
            return new a<>(11, false, 11, false, str, i, cls, null);
        }

        public static <T extends ji> a<ArrayList<T>, ArrayList<T>> b(String str, int i, Class<T> cls) {
            return new a<>(11, true, 11, true, str, i, cls, null);
        }

        public static a<Integer, Integer> i(String str, int i) {
            return new a<>(0, false, 0, false, str, i, null, null);
        }

        public static a<Double, Double> j(String str, int i) {
            return new a<>(4, false, 4, false, str, i, null, null);
        }

        public static a<Boolean, Boolean> k(String str, int i) {
            return new a<>(6, false, 6, false, str, i, null, null);
        }

        public static a<String, String> l(String str, int i) {
            return new a<>(7, false, 7, false, str, i, null, null);
        }

        public static a<ArrayList<String>, ArrayList<String>> m(String str, int i) {
            return new a<>(7, true, 7, true, str, i, null, null);
        }

        public void a(jm jmVar) {
            this.My = jmVar;
        }

        public I convertBack(O output) {
            return this.Mz.convertBack(output);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            jk jkVar = CREATOR;
            return 0;
        }

        public int getVersionCode() {
            return this.BR;
        }

        public int hd() {
            return this.Mq;
        }

        public int he() {
            return this.Ms;
        }

        public a<I, O> hi() {
            return new a<>(this.BR, this.Mq, this.Mr, this.Ms, this.Mt, this.Mu, this.Mv, this.Mx, hq());
        }

        public boolean hj() {
            return this.Mr;
        }

        public boolean hk() {
            return this.Mt;
        }

        public String hl() {
            return this.Mu;
        }

        public int hm() {
            return this.Mv;
        }

        public Class<? extends ji> hn() {
            return this.Mw;
        }

        String ho() {
            if (this.Mx == null) {
                return null;
            }
            return this.Mx;
        }

        public boolean hp() {
            return this.Mz != null;
        }

        jd hq() {
            if (this.Mz == null) {
                return null;
            }
            return jd.a(this.Mz);
        }

        public HashMap<String, a<?, ?>> hr() {
            com.google.android.gms.common.internal.n.i(this.Mx);
            com.google.android.gms.common.internal.n.i(this.My);
            return this.My.be(this.Mx);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Field\n");
            sb.append("            versionCode=").append(this.BR).append('\n');
            sb.append("                 typeIn=").append(this.Mq).append('\n');
            sb.append("            typeInArray=").append(this.Mr).append('\n');
            sb.append("                typeOut=").append(this.Ms).append('\n');
            sb.append("           typeOutArray=").append(this.Mt).append('\n');
            sb.append("        outputFieldName=").append(this.Mu).append('\n');
            sb.append("      safeParcelFieldId=").append(this.Mv).append('\n');
            sb.append("       concreteTypeName=").append(ho()).append('\n');
            if (hn() != null) {
                sb.append("     concreteType.class=").append(hn().getCanonicalName()).append('\n');
            }
            sb.append("          converterName=").append(this.Mz == null ? "null" : this.Mz.getClass().getCanonicalName()).append('\n');
            return sb.toString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            jk jkVar = CREATOR;
            jk.a(this, out, flags);
        }
    }

    public interface b<I, O> {
        I convertBack(O o);

        int hd();

        int he();
    }

    private void a(StringBuilder sb, a aVar, Object obj) {
        if (aVar.hd() == 11) {
            sb.append(aVar.hn().cast(obj).toString());
        } else {
            if (aVar.hd() != 7) {
                sb.append(obj);
                return;
            }
            sb.append("\"");
            sb.append(jz.bf((String) obj));
            sb.append("\"");
        }
    }

    private void a(StringBuilder sb, a aVar, ArrayList<Object> arrayList) {
        sb.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
            }
            Object obj = arrayList.get(i);
            if (obj != null) {
                a(sb, aVar, obj);
            }
        }
        sb.append("]");
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected <O, I> I a(a<I, O> aVar, Object obj) {
        return ((a) aVar).Mz != null ? aVar.convertBack(obj) : obj;
    }

    protected boolean a(a aVar) {
        return aVar.he() == 11 ? aVar.hk() ? bd(aVar.hl()) : bc(aVar.hl()) : bb(aVar.hl());
    }

    protected Object b(a aVar) {
        String strHl = aVar.hl();
        if (aVar.hn() == null) {
            return ba(aVar.hl());
        }
        com.google.android.gms.common.internal.n.a(ba(aVar.hl()) == null, "Concrete field shouldn't be value object: %s", aVar.hl());
        HashMap<String, Object> mapHh = aVar.hk() ? hh() : hg();
        if (mapHh != null) {
            return mapHh.get(strHl);
        }
        try {
            return getClass().getMethod("get" + Character.toUpperCase(strHl.charAt(0)) + strHl.substring(1), new Class[0]).invoke(this, new Object[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object ba(String str);

    protected abstract boolean bb(String str);

    protected boolean bc(String str) {
        throw new UnsupportedOperationException("Concrete types not supported");
    }

    protected boolean bd(String str) {
        throw new UnsupportedOperationException("Concrete type arrays not supported");
    }

    public abstract HashMap<String, a<?, ?>> hf();

    public HashMap<String, Object> hg() {
        return null;
    }

    public HashMap<String, Object> hh() {
        return null;
    }

    public String toString() {
        HashMap<String, a<?, ?>> mapHf = hf();
        StringBuilder sb = new StringBuilder(100);
        for (String str : mapHf.keySet()) {
            a<?, ?> aVar = mapHf.get(str);
            if (a(aVar)) {
                Object objA = a(aVar, b(aVar));
                if (sb.length() == 0) {
                    sb.append("{");
                } else {
                    sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
                }
                sb.append("\"").append(str).append("\":");
                if (objA != null) {
                    switch (aVar.he()) {
                        case 8:
                            sb.append("\"").append(js.d((byte[]) objA)).append("\"");
                            break;
                        case 9:
                            sb.append("\"").append(js.e((byte[]) objA)).append("\"");
                            break;
                        case 10:
                            ka.a(sb, (HashMap) objA);
                            break;
                        default:
                            if (aVar.hj()) {
                                a(sb, (a) aVar, (ArrayList<Object>) objA);
                                break;
                            } else {
                                a(sb, aVar, objA);
                                break;
                            }
                    }
                } else {
                    sb.append("null");
                }
            }
        }
        if (sb.length() > 0) {
            sb.append("}");
        } else {
            sb.append("{}");
        }
        return sb.toString();
    }
}
