package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.ji;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class jp extends ji implements SafeParcelable {
    public static final jq CREATOR = new jq();
    private final int BR;
    private final Parcel MF;
    private final int MG;
    private int MH;
    private int MI;
    private final jm My;
    private final String mClassName;

    jp(int i, Parcel parcel, jm jmVar) {
        this.BR = i;
        this.MF = (Parcel) com.google.android.gms.common.internal.n.i(parcel);
        this.MG = 2;
        this.My = jmVar;
        if (this.My == null) {
            this.mClassName = null;
        } else {
            this.mClassName = this.My.hv();
        }
        this.MH = 2;
    }

    private jp(SafeParcelable safeParcelable, jm jmVar, String str) {
        this.BR = 1;
        this.MF = Parcel.obtain();
        safeParcelable.writeToParcel(this.MF, 0);
        this.MG = 1;
        this.My = (jm) com.google.android.gms.common.internal.n.i(jmVar);
        this.mClassName = (String) com.google.android.gms.common.internal.n.i(str);
        this.MH = 2;
    }

    public static <T extends ji & SafeParcelable> jp a(T t) {
        String canonicalName = t.getClass().getCanonicalName();
        return new jp(t, b(t), canonicalName);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void a(jm jmVar, ji jiVar) {
        Class<?> cls = jiVar.getClass();
        if (jmVar.b(cls)) {
            return;
        }
        HashMap<String, ji.a<?, ?>> mapHf = jiVar.hf();
        jmVar.a(cls, jiVar.hf());
        Iterator<String> it = mapHf.keySet().iterator();
        while (it.hasNext()) {
            ji.a<?, ?> aVar = mapHf.get(it.next());
            Class<? extends ji> clsHn = aVar.hn();
            if (clsHn != null) {
                try {
                    a(jmVar, clsHn.newInstance());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not access object of type " + aVar.hn().getCanonicalName(), e);
                } catch (InstantiationException e2) {
                    throw new IllegalStateException("Could not instantiate an object of type " + aVar.hn().getCanonicalName(), e2);
                }
            }
        }
    }

    private void a(StringBuilder sb, int i, Object obj) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                sb.append(obj);
                return;
            case 7:
                sb.append("\"").append(jz.bf(obj.toString())).append("\"");
                return;
            case 8:
                sb.append("\"").append(js.d((byte[]) obj)).append("\"");
                return;
            case 9:
                sb.append("\"").append(js.e((byte[]) obj));
                sb.append("\"");
                return;
            case 10:
                ka.a(sb, (HashMap) obj);
                return;
            case 11:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown type = " + i);
        }
    }

    private void a(StringBuilder sb, ji.a<?, ?> aVar, Parcel parcel, int i) {
        switch (aVar.he()) {
            case 0:
                b(sb, aVar, a(aVar, Integer.valueOf(com.google.android.gms.common.internal.safeparcel.a.g(parcel, i))));
                return;
            case 1:
                b(sb, aVar, a(aVar, com.google.android.gms.common.internal.safeparcel.a.k(parcel, i)));
                return;
            case 2:
                b(sb, aVar, a(aVar, Long.valueOf(com.google.android.gms.common.internal.safeparcel.a.i(parcel, i))));
                return;
            case 3:
                b(sb, aVar, a(aVar, Float.valueOf(com.google.android.gms.common.internal.safeparcel.a.l(parcel, i))));
                return;
            case 4:
                b(sb, aVar, a(aVar, Double.valueOf(com.google.android.gms.common.internal.safeparcel.a.m(parcel, i))));
                return;
            case 5:
                b(sb, aVar, a(aVar, com.google.android.gms.common.internal.safeparcel.a.n(parcel, i)));
                return;
            case 6:
                b(sb, aVar, a(aVar, Boolean.valueOf(com.google.android.gms.common.internal.safeparcel.a.c(parcel, i))));
                return;
            case 7:
                b(sb, aVar, a(aVar, com.google.android.gms.common.internal.safeparcel.a.o(parcel, i)));
                return;
            case 8:
            case 9:
                b(sb, aVar, a(aVar, com.google.android.gms.common.internal.safeparcel.a.r(parcel, i)));
                return;
            case 10:
                b(sb, aVar, a(aVar, e(com.google.android.gms.common.internal.safeparcel.a.q(parcel, i))));
                return;
            case 11:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown field out type = " + aVar.he());
        }
    }

    private void a(StringBuilder sb, String str, ji.a<?, ?> aVar, Parcel parcel, int i) {
        sb.append("\"").append(str).append("\":");
        if (aVar.hp()) {
            a(sb, aVar, parcel, i);
        } else {
            b(sb, aVar, parcel, i);
        }
    }

    private void a(StringBuilder sb, HashMap<String, ji.a<?, ?>> map, Parcel parcel) {
        HashMap<Integer, Map.Entry<String, ji.a<?, ?>>> mapB = b(map);
        sb.append('{');
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        boolean z = false;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            Map.Entry<String, ji.a<?, ?>> entry = mapB.get(Integer.valueOf(com.google.android.gms.common.internal.safeparcel.a.aD(iB)));
            if (entry != null) {
                if (z) {
                    sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
                }
                a(sb, entry.getKey(), entry.getValue(), parcel, iB);
                z = true;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        sb.append('}');
    }

    private static jm b(ji jiVar) {
        jm jmVar = new jm(jiVar.getClass());
        a(jmVar, jiVar);
        jmVar.ht();
        jmVar.hs();
        return jmVar;
    }

    private static HashMap<Integer, Map.Entry<String, ji.a<?, ?>>> b(HashMap<String, ji.a<?, ?>> map) {
        HashMap<Integer, Map.Entry<String, ji.a<?, ?>>> map2 = new HashMap<>();
        for (Map.Entry<String, ji.a<?, ?>> entry : map.entrySet()) {
            map2.put(Integer.valueOf(entry.getValue().hm()), entry);
        }
        return map2;
    }

    private void b(StringBuilder sb, ji.a<?, ?> aVar, Parcel parcel, int i) {
        if (aVar.hk()) {
            sb.append("[");
            switch (aVar.he()) {
                case 0:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.u(parcel, i));
                    break;
                case 1:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.w(parcel, i));
                    break;
                case 2:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.v(parcel, i));
                    break;
                case 3:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.x(parcel, i));
                    break;
                case 4:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.y(parcel, i));
                    break;
                case 5:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.z(parcel, i));
                    break;
                case 6:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.t(parcel, i));
                    break;
                case 7:
                    jr.a(sb, com.google.android.gms.common.internal.safeparcel.a.A(parcel, i));
                    break;
                case 8:
                case 9:
                case 10:
                    throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
                case 11:
                    Parcel[] parcelArrE = com.google.android.gms.common.internal.safeparcel.a.E(parcel, i);
                    int length = parcelArrE.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        if (i2 > 0) {
                            sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
                        }
                        parcelArrE[i2].setDataPosition(0);
                        a(sb, aVar.hr(), parcelArrE[i2]);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown field type out.");
            }
            sb.append("]");
            return;
        }
        switch (aVar.he()) {
            case 0:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.g(parcel, i));
                return;
            case 1:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.k(parcel, i));
                return;
            case 2:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.i(parcel, i));
                return;
            case 3:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.l(parcel, i));
                return;
            case 4:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.m(parcel, i));
                return;
            case 5:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.n(parcel, i));
                return;
            case 6:
                sb.append(com.google.android.gms.common.internal.safeparcel.a.c(parcel, i));
                return;
            case 7:
                sb.append("\"").append(jz.bf(com.google.android.gms.common.internal.safeparcel.a.o(parcel, i))).append("\"");
                return;
            case 8:
                sb.append("\"").append(js.d(com.google.android.gms.common.internal.safeparcel.a.r(parcel, i))).append("\"");
                return;
            case 9:
                sb.append("\"").append(js.e(com.google.android.gms.common.internal.safeparcel.a.r(parcel, i)));
                sb.append("\"");
                return;
            case 10:
                Bundle bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, i);
                Set<String> setKeySet = bundleQ.keySet();
                setKeySet.size();
                sb.append("{");
                boolean z = true;
                for (String str : setKeySet) {
                    if (!z) {
                        sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
                    }
                    sb.append("\"").append(str).append("\"");
                    sb.append(":");
                    sb.append("\"").append(jz.bf(bundleQ.getString(str))).append("\"");
                    z = false;
                }
                sb.append("}");
                return;
            case 11:
                Parcel parcelD = com.google.android.gms.common.internal.safeparcel.a.D(parcel, i);
                parcelD.setDataPosition(0);
                a(sb, aVar.hr(), parcelD);
                return;
            default:
                throw new IllegalStateException("Unknown field type out");
        }
    }

    private void b(StringBuilder sb, ji.a<?, ?> aVar, Object obj) {
        if (aVar.hj()) {
            b(sb, aVar, (ArrayList<?>) obj);
        } else {
            a(sb, aVar.hd(), obj);
        }
    }

    private void b(StringBuilder sb, ji.a<?, ?> aVar, ArrayList<?> arrayList) {
        sb.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
            }
            a(sb, aVar.hd(), arrayList.get(i));
        }
        sb.append("]");
    }

    public static HashMap<String, String> e(Bundle bundle) {
        HashMap<String, String> map = new HashMap<>();
        for (String str : bundle.keySet()) {
            map.put(str, bundle.getString(str));
        }
        return map;
    }

    @Override // com.google.android.gms.internal.ji
    protected Object ba(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    @Override // com.google.android.gms.internal.ji
    protected boolean bb(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        jq jqVar = CREATOR;
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // com.google.android.gms.internal.ji
    public HashMap<String, ji.a<?, ?>> hf() {
        if (this.My == null) {
            return null;
        }
        return this.My.be(this.mClassName);
    }

    public Parcel hx() {
        switch (this.MH) {
            case 0:
                this.MI = com.google.android.gms.common.internal.safeparcel.b.D(this.MF);
                com.google.android.gms.common.internal.safeparcel.b.H(this.MF, this.MI);
                this.MH = 2;
                break;
            case 1:
                com.google.android.gms.common.internal.safeparcel.b.H(this.MF, this.MI);
                this.MH = 2;
                break;
        }
        return this.MF;
    }

    jm hy() {
        switch (this.MG) {
            case 0:
                return null;
            case 1:
                return this.My;
            case 2:
                return this.My;
            default:
                throw new IllegalStateException("Invalid creation type: " + this.MG);
        }
    }

    @Override // com.google.android.gms.internal.ji
    public String toString() {
        com.google.android.gms.common.internal.n.b(this.My, "Cannot convert to JSON on client side.");
        Parcel parcelHx = hx();
        parcelHx.setDataPosition(0);
        StringBuilder sb = new StringBuilder(100);
        a(sb, this.My.be(this.mClassName), parcelHx);
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        jq jqVar = CREATOR;
        jq.a(this, out, flags);
    }
}
