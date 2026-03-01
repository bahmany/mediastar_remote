package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.c;
import com.google.android.gms.internal.d;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class cr {

    public static class a {
        private final Map<String, d.a> aqs;
        private final d.a aqt;

        private a(Map<String, d.a> map, d.a aVar) {
            this.aqs = map;
            this.aqt = aVar;
        }

        /* synthetic */ a(Map map, d.a aVar, AnonymousClass1 anonymousClass1) {
            this(map, aVar);
        }

        public static b oR() {
            return new b();
        }

        public void a(String str, d.a aVar) {
            this.aqs.put(str, aVar);
        }

        public Map<String, d.a> oS() {
            return Collections.unmodifiableMap(this.aqs);
        }

        public d.a oT() {
            return this.aqt;
        }

        public String toString() {
            return "Properties: " + oS() + " pushAfterEvaluate: " + this.aqt;
        }
    }

    public static class b {
        private final Map<String, d.a> aqs;
        private d.a aqt;

        private b() {
            this.aqs = new HashMap();
        }

        /* synthetic */ b(AnonymousClass1 anonymousClass1) {
            this();
        }

        public b b(String str, d.a aVar) {
            this.aqs.put(str, aVar);
            return this;
        }

        public b i(d.a aVar) {
            this.aqt = aVar;
            return this;
        }

        public a oU() {
            return new a(this.aqs, this.aqt);
        }
    }

    public static class c {
        private final String Sq;
        private final List<e> aqu;
        private final Map<String, List<a>> aqv;
        private final int aqw;

        private c(List<e> list, Map<String, List<a>> map, String str, int i) {
            this.aqu = Collections.unmodifiableList(list);
            this.aqv = Collections.unmodifiableMap(map);
            this.Sq = str;
            this.aqw = i;
        }

        /* synthetic */ c(List list, Map map, String str, int i, AnonymousClass1 anonymousClass1) {
            this(list, map, str, i);
        }

        public static d oV() {
            return new d();
        }

        public String getVersion() {
            return this.Sq;
        }

        public List<e> oW() {
            return this.aqu;
        }

        public Map<String, List<a>> oX() {
            return this.aqv;
        }

        public String toString() {
            return "Rules: " + oW() + "  Macros: " + this.aqv;
        }
    }

    public static class d {
        private String Sq;
        private final List<e> aqu;
        private final Map<String, List<a>> aqv;
        private int aqw;

        private d() {
            this.aqu = new ArrayList();
            this.aqv = new HashMap();
            this.Sq = "";
            this.aqw = 0;
        }

        /* synthetic */ d(AnonymousClass1 anonymousClass1) {
            this();
        }

        public d a(a aVar) {
            String strJ = di.j(aVar.oS().get(com.google.android.gms.internal.b.INSTANCE_NAME.toString()));
            List<a> arrayList = this.aqv.get(strJ);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.aqv.put(strJ, arrayList);
            }
            arrayList.add(aVar);
            return this;
        }

        public d a(e eVar) {
            this.aqu.add(eVar);
            return this;
        }

        public d cJ(String str) {
            this.Sq = str;
            return this;
        }

        public d fl(int i) {
            this.aqw = i;
            return this;
        }

        public c oY() {
            return new c(this.aqu, this.aqv, this.Sq, this.aqw);
        }
    }

    public static class e {
        private final List<a> aqA;
        private final List<a> aqB;
        private final List<a> aqC;
        private final List<String> aqD;
        private final List<String> aqE;
        private final List<String> aqF;
        private final List<String> aqG;
        private final List<a> aqx;
        private final List<a> aqy;
        private final List<a> aqz;

        private e(List<a> list, List<a> list2, List<a> list3, List<a> list4, List<a> list5, List<a> list6, List<String> list7, List<String> list8, List<String> list9, List<String> list10) {
            this.aqx = Collections.unmodifiableList(list);
            this.aqy = Collections.unmodifiableList(list2);
            this.aqz = Collections.unmodifiableList(list3);
            this.aqA = Collections.unmodifiableList(list4);
            this.aqB = Collections.unmodifiableList(list5);
            this.aqC = Collections.unmodifiableList(list6);
            this.aqD = Collections.unmodifiableList(list7);
            this.aqE = Collections.unmodifiableList(list8);
            this.aqF = Collections.unmodifiableList(list9);
            this.aqG = Collections.unmodifiableList(list10);
        }

        /* synthetic */ e(List list, List list2, List list3, List list4, List list5, List list6, List list7, List list8, List list9, List list10, AnonymousClass1 anonymousClass1) {
            this(list, list2, list3, list4, list5, list6, list7, list8, list9, list10);
        }

        public static f oZ() {
            return new f();
        }

        public List<a> pa() {
            return this.aqx;
        }

        public List<a> pb() {
            return this.aqy;
        }

        public List<a> pc() {
            return this.aqz;
        }

        public List<a> pd() {
            return this.aqA;
        }

        public List<a> pe() {
            return this.aqB;
        }

        public List<String> pf() {
            return this.aqD;
        }

        public List<String> pg() {
            return this.aqE;
        }

        public List<String> ph() {
            return this.aqF;
        }

        public List<String> pi() {
            return this.aqG;
        }

        public List<a> pj() {
            return this.aqC;
        }

        public String toString() {
            return "Positive predicates: " + pa() + "  Negative predicates: " + pb() + "  Add tags: " + pc() + "  Remove tags: " + pd() + "  Add macros: " + pe() + "  Remove macros: " + pj();
        }
    }

    public static class f {
        private final List<a> aqA;
        private final List<a> aqB;
        private final List<a> aqC;
        private final List<String> aqD;
        private final List<String> aqE;
        private final List<String> aqF;
        private final List<String> aqG;
        private final List<a> aqx;
        private final List<a> aqy;
        private final List<a> aqz;

        private f() {
            this.aqx = new ArrayList();
            this.aqy = new ArrayList();
            this.aqz = new ArrayList();
            this.aqA = new ArrayList();
            this.aqB = new ArrayList();
            this.aqC = new ArrayList();
            this.aqD = new ArrayList();
            this.aqE = new ArrayList();
            this.aqF = new ArrayList();
            this.aqG = new ArrayList();
        }

        /* synthetic */ f(AnonymousClass1 anonymousClass1) {
            this();
        }

        public f b(a aVar) {
            this.aqx.add(aVar);
            return this;
        }

        public f c(a aVar) {
            this.aqy.add(aVar);
            return this;
        }

        public f cK(String str) {
            this.aqF.add(str);
            return this;
        }

        public f cL(String str) {
            this.aqG.add(str);
            return this;
        }

        public f cM(String str) {
            this.aqD.add(str);
            return this;
        }

        public f cN(String str) {
            this.aqE.add(str);
            return this;
        }

        public f d(a aVar) {
            this.aqz.add(aVar);
            return this;
        }

        public f e(a aVar) {
            this.aqA.add(aVar);
            return this;
        }

        public f f(a aVar) {
            this.aqB.add(aVar);
            return this;
        }

        public f g(a aVar) {
            this.aqC.add(aVar);
            return this;
        }

        public e pk() {
            return new e(this.aqx, this.aqy, this.aqz, this.aqA, this.aqB, this.aqC, this.aqD, this.aqE, this.aqF, this.aqG);
        }
    }

    public static class g extends Exception {
        public g(String str) {
            super(str);
        }
    }

    private static d.a a(int i, c.f fVar, d.a[] aVarArr, Set<Integer> set) throws g {
        int i2 = 0;
        if (set.contains(Integer.valueOf(i))) {
            cI("Value cycle detected.  Current value reference: " + i + ".  Previous value references: " + set + ".");
        }
        d.a aVar = (d.a) a(fVar.fG, i, "values");
        if (aVarArr[i] != null) {
            return aVarArr[i];
        }
        d.a aVarG = null;
        set.add(Integer.valueOf(i));
        switch (aVar.type) {
            case 1:
            case 5:
            case 6:
            case 8:
                aVarG = aVar;
                break;
            case 2:
                c.h hVarH = h(aVar);
                aVarG = g(aVar);
                aVarG.gw = new d.a[hVarH.gh.length];
                int[] iArr = hVarH.gh;
                int length = iArr.length;
                int i3 = 0;
                while (i2 < length) {
                    aVarG.gw[i3] = a(iArr[i2], fVar, aVarArr, set);
                    i2++;
                    i3++;
                }
                break;
            case 3:
                aVarG = g(aVar);
                c.h hVarH2 = h(aVar);
                if (hVarH2.gi.length != hVarH2.gj.length) {
                    cI("Uneven map keys (" + hVarH2.gi.length + ") and map values (" + hVarH2.gj.length + ")");
                }
                aVarG.gx = new d.a[hVarH2.gi.length];
                aVarG.gy = new d.a[hVarH2.gi.length];
                int[] iArr2 = hVarH2.gi;
                int length2 = iArr2.length;
                int i4 = 0;
                int i5 = 0;
                while (i4 < length2) {
                    aVarG.gx[i5] = a(iArr2[i4], fVar, aVarArr, set);
                    i4++;
                    i5++;
                }
                int[] iArr3 = hVarH2.gj;
                int length3 = iArr3.length;
                int i6 = 0;
                while (i2 < length3) {
                    aVarG.gy[i6] = a(iArr3[i2], fVar, aVarArr, set);
                    i2++;
                    i6++;
                }
                break;
            case 4:
                aVarG = g(aVar);
                aVarG.gz = di.j(a(h(aVar).gm, fVar, aVarArr, set));
                break;
            case 7:
                aVarG = g(aVar);
                c.h hVarH3 = h(aVar);
                aVarG.gD = new d.a[hVarH3.gl.length];
                int[] iArr4 = hVarH3.gl;
                int length4 = iArr4.length;
                int i7 = 0;
                while (i2 < length4) {
                    aVarG.gD[i7] = a(iArr4[i2], fVar, aVarArr, set);
                    i2++;
                    i7++;
                }
                break;
        }
        if (aVarG == null) {
            cI("Invalid value: " + aVar);
        }
        aVarArr[i] = aVarG;
        set.remove(Integer.valueOf(i));
        return aVarG;
    }

    private static a a(c.b bVar, c.f fVar, d.a[] aVarArr, int i) throws g {
        b bVarOR = a.oR();
        for (int i2 : bVar.fq) {
            c.e eVar = (c.e) a(fVar.fH, Integer.valueOf(i2).intValue(), "properties");
            String str = (String) a(fVar.fF, eVar.key, "keys");
            d.a aVar = (d.a) a(aVarArr, eVar.value, "values");
            if (com.google.android.gms.internal.b.PUSH_AFTER_EVALUATE.toString().equals(str)) {
                bVarOR.i(aVar);
            } else {
                bVarOR.b(str, aVar);
            }
        }
        return bVarOR.oU();
    }

    private static e a(c.g gVar, List<a> list, List<a> list2, List<a> list3, c.f fVar) {
        f fVarOZ = e.oZ();
        for (int i : gVar.fV) {
            fVarOZ.b(list3.get(Integer.valueOf(i).intValue()));
        }
        for (int i2 : gVar.fW) {
            fVarOZ.c(list3.get(Integer.valueOf(i2).intValue()));
        }
        for (int i3 : gVar.fX) {
            fVarOZ.d(list.get(Integer.valueOf(i3).intValue()));
        }
        for (int i4 : gVar.fZ) {
            fVarOZ.cK(fVar.fG[Integer.valueOf(i4).intValue()].gv);
        }
        for (int i5 : gVar.fY) {
            fVarOZ.e(list.get(Integer.valueOf(i5).intValue()));
        }
        for (int i6 : gVar.ga) {
            fVarOZ.cL(fVar.fG[Integer.valueOf(i6).intValue()].gv);
        }
        for (int i7 : gVar.gb) {
            fVarOZ.f(list2.get(Integer.valueOf(i7).intValue()));
        }
        for (int i8 : gVar.gd) {
            fVarOZ.cM(fVar.fG[Integer.valueOf(i8).intValue()].gv);
        }
        for (int i9 : gVar.gc) {
            fVarOZ.g(list2.get(Integer.valueOf(i9).intValue()));
        }
        for (int i10 : gVar.ge) {
            fVarOZ.cN(fVar.fG[Integer.valueOf(i10).intValue()].gv);
        }
        return fVarOZ.pk();
    }

    private static <T> T a(T[] tArr, int i, String str) throws g {
        if (i < 0 || i >= tArr.length) {
            cI("Index out of bounds detected: " + i + " in " + str);
        }
        return tArr[i];
    }

    public static c b(c.f fVar) throws g {
        d.a[] aVarArr = new d.a[fVar.fG.length];
        for (int i = 0; i < fVar.fG.length; i++) {
            a(i, fVar, aVarArr, new HashSet(0));
        }
        d dVarOV = c.oV();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < fVar.fJ.length; i2++) {
            arrayList.add(a(fVar.fJ[i2], fVar, aVarArr, i2));
        }
        ArrayList arrayList2 = new ArrayList();
        for (int i3 = 0; i3 < fVar.fK.length; i3++) {
            arrayList2.add(a(fVar.fK[i3], fVar, aVarArr, i3));
        }
        ArrayList arrayList3 = new ArrayList();
        for (int i4 = 0; i4 < fVar.fI.length; i4++) {
            a aVarA = a(fVar.fI[i4], fVar, aVarArr, i4);
            dVarOV.a(aVarA);
            arrayList3.add(aVarA);
        }
        for (c.g gVar : fVar.fL) {
            dVarOV.a(a(gVar, arrayList, arrayList3, arrayList2, fVar));
        }
        dVarOV.cJ(fVar.version);
        dVarOV.fl(fVar.fT);
        return dVarOV.oY();
    }

    public static void b(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int i = inputStream.read(bArr);
            if (i == -1) {
                return;
            } else {
                outputStream.write(bArr, 0, i);
            }
        }
    }

    private static void cI(String str) throws g {
        bh.T(str);
        throw new g(str);
    }

    public static d.a g(d.a aVar) {
        d.a aVar2 = new d.a();
        aVar2.type = aVar.type;
        aVar2.gE = (int[]) aVar.gE.clone();
        if (aVar.gF) {
            aVar2.gF = aVar.gF;
        }
        return aVar2;
    }

    private static c.h h(d.a aVar) throws g {
        if (((c.h) aVar.a(c.h.gf)) == null) {
            cI("Expected a ServingValue and didn't get one. Value is: " + aVar);
        }
        return (c.h) aVar.a(c.h.gf);
    }
}
