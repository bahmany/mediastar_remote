package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.c;
import com.google.android.gms.internal.d;
import com.google.android.gms.tagmanager.cr;
import com.google.android.gms.tagmanager.l;
import com.google.android.gms.tagmanager.s;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class ct {
    private static final bz<d.a> aqH = new bz<>(di.pI(), true);
    private final DataLayer anS;
    private final cr.c aqI;
    private final ag aqJ;
    private final Map<String, aj> aqK;
    private final Map<String, aj> aqL;
    private final Map<String, aj> aqM;
    private final k<cr.a, bz<d.a>> aqN;
    private final k<String, b> aqO;
    private final Set<cr.e> aqP;
    private final Map<String, c> aqQ;
    private volatile String aqR;
    private int aqS;

    /* renamed from: com.google.android.gms.tagmanager.ct$1 */
    class AnonymousClass1 implements l.a<cr.a, bz<d.a>> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.tagmanager.l.a
        /* renamed from: a */
        public int sizeOf(cr.a aVar, bz<d.a> bzVar) {
            return bzVar.getObject().qF();
        }
    }

    /* renamed from: com.google.android.gms.tagmanager.ct$2 */
    class AnonymousClass2 implements l.a<String, b> {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.tagmanager.l.a
        /* renamed from: a */
        public int sizeOf(String str, b bVar) {
            return str.length() + bVar.getSize();
        }
    }

    /* renamed from: com.google.android.gms.tagmanager.ct$3 */
    class AnonymousClass3 implements a {
        final /* synthetic */ Map aqU;
        final /* synthetic */ Map aqV;
        final /* synthetic */ Map aqW;
        final /* synthetic */ Map aqX;

        AnonymousClass3(Map map, Map map2, Map map3, Map map4) {
            map = map;
            map = map2;
            map = map3;
            map = map4;
        }

        @Override // com.google.android.gms.tagmanager.ct.a
        public void a(cr.e eVar, Set<cr.a> set, Set<cr.a> set2, cn cnVar) {
            List<cr.a> list = (List) map.get(eVar);
            List<String> list2 = (List) map.get(eVar);
            if (list != null) {
                set.addAll(list);
                cnVar.oy().c(list, list2);
            }
            List<cr.a> list3 = (List) map.get(eVar);
            List<String> list4 = (List) map.get(eVar);
            if (list3 != null) {
                set2.addAll(list3);
                cnVar.oz().c(list3, list4);
            }
        }
    }

    /* renamed from: com.google.android.gms.tagmanager.ct$4 */
    class AnonymousClass4 implements a {
        AnonymousClass4() {
        }

        @Override // com.google.android.gms.tagmanager.ct.a
        public void a(cr.e eVar, Set<cr.a> set, Set<cr.a> set2, cn cnVar) {
            set.addAll(eVar.pc());
            set2.addAll(eVar.pd());
            cnVar.oA().c(eVar.pc(), eVar.ph());
            cnVar.oB().c(eVar.pd(), eVar.pi());
        }
    }

    interface a {
        void a(cr.e eVar, Set<cr.a> set, Set<cr.a> set2, cn cnVar);
    }

    private static class b {
        private bz<d.a> aqY;
        private d.a aqt;

        public b(bz<d.a> bzVar, d.a aVar) {
            this.aqY = bzVar;
            this.aqt = aVar;
        }

        public int getSize() {
            return (this.aqt == null ? 0 : this.aqt.qF()) + this.aqY.getObject().qF();
        }

        public d.a oT() {
            return this.aqt;
        }

        public bz<d.a> pn() {
            return this.aqY;
        }
    }

    private static class c {
        private cr.a ard;
        private final Set<cr.e> aqP = new HashSet();
        private final Map<cr.e, List<cr.a>> aqZ = new HashMap();
        private final Map<cr.e, List<String>> arb = new HashMap();
        private final Map<cr.e, List<cr.a>> ara = new HashMap();
        private final Map<cr.e, List<String>> arc = new HashMap();

        public void a(cr.e eVar, cr.a aVar) {
            List<cr.a> arrayList = this.aqZ.get(eVar);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.aqZ.put(eVar, arrayList);
            }
            arrayList.add(aVar);
        }

        public void a(cr.e eVar, String str) {
            List<String> arrayList = this.arb.get(eVar);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.arb.put(eVar, arrayList);
            }
            arrayList.add(str);
        }

        public void b(cr.e eVar) {
            this.aqP.add(eVar);
        }

        public void b(cr.e eVar, cr.a aVar) {
            List<cr.a> arrayList = this.ara.get(eVar);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.ara.put(eVar, arrayList);
            }
            arrayList.add(aVar);
        }

        public void b(cr.e eVar, String str) {
            List<String> arrayList = this.arc.get(eVar);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.arc.put(eVar, arrayList);
            }
            arrayList.add(str);
        }

        public void i(cr.a aVar) {
            this.ard = aVar;
        }

        public Set<cr.e> po() {
            return this.aqP;
        }

        public Map<cr.e, List<cr.a>> pp() {
            return this.aqZ;
        }

        public Map<cr.e, List<String>> pq() {
            return this.arb;
        }

        public Map<cr.e, List<String>> pr() {
            return this.arc;
        }

        public Map<cr.e, List<cr.a>> ps() {
            return this.ara;
        }

        public cr.a pt() {
            return this.ard;
        }
    }

    public ct(Context context, cr.c cVar, DataLayer dataLayer, s.a aVar, s.a aVar2, ag agVar) {
        if (cVar == null) {
            throw new NullPointerException("resource cannot be null");
        }
        this.aqI = cVar;
        this.aqP = new HashSet(cVar.oW());
        this.anS = dataLayer;
        this.aqJ = agVar;
        this.aqN = new l().a(1048576, new l.a<cr.a, bz<d.a>>() { // from class: com.google.android.gms.tagmanager.ct.1
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.tagmanager.l.a
            /* renamed from: a */
            public int sizeOf(cr.a aVar3, bz<d.a> bzVar) {
                return bzVar.getObject().qF();
            }
        });
        this.aqO = new l().a(1048576, new l.a<String, b>() { // from class: com.google.android.gms.tagmanager.ct.2
            AnonymousClass2() {
            }

            @Override // com.google.android.gms.tagmanager.l.a
            /* renamed from: a */
            public int sizeOf(String str, b bVar) {
                return str.length() + bVar.getSize();
            }
        });
        this.aqK = new HashMap();
        b(new i(context));
        b(new s(aVar2));
        b(new w(dataLayer));
        b(new dj(context, dataLayer));
        this.aqL = new HashMap();
        c(new q());
        c(new ad());
        c(new ae());
        c(new al());
        c(new am());
        c(new bd());
        c(new be());
        c(new ci());
        c(new dc());
        this.aqM = new HashMap();
        a(new com.google.android.gms.tagmanager.b(context));
        a(new com.google.android.gms.tagmanager.c(context));
        a(new e(context));
        a(new f(context));
        a(new g(context));
        a(new h(context));
        a(new m());
        a(new p(this.aqI.getVersion()));
        a(new s(aVar));
        a(new u(dataLayer));
        a(new z(context));
        a(new aa());
        a(new ac());
        a(new ah(this));
        a(new an());
        a(new ao());
        a(new ax(context));
        a(new az());
        a(new bc());
        a(new bj());
        a(new bl(context));
        a(new ca());
        a(new cc());
        a(new cf());
        a(new ch());
        a(new cj(context));
        a(new cu());
        a(new cv());
        a(new de());
        a(new dk());
        this.aqQ = new HashMap();
        for (cr.e eVar : this.aqP) {
            if (agVar.oo()) {
                a(eVar.pe(), eVar.pf(), "add macro");
                a(eVar.pj(), eVar.pg(), "remove macro");
                a(eVar.pc(), eVar.ph(), "add tag");
                a(eVar.pd(), eVar.pi(), "remove tag");
            }
            for (int i = 0; i < eVar.pe().size(); i++) {
                cr.a aVar3 = eVar.pe().get(i);
                String str = "Unknown";
                if (agVar.oo() && i < eVar.pf().size()) {
                    str = eVar.pf().get(i);
                }
                c cVarE = e(this.aqQ, h(aVar3));
                cVarE.b(eVar);
                cVarE.a(eVar, aVar3);
                cVarE.a(eVar, str);
            }
            for (int i2 = 0; i2 < eVar.pj().size(); i2++) {
                cr.a aVar4 = eVar.pj().get(i2);
                String str2 = "Unknown";
                if (agVar.oo() && i2 < eVar.pg().size()) {
                    str2 = eVar.pg().get(i2);
                }
                c cVarE2 = e(this.aqQ, h(aVar4));
                cVarE2.b(eVar);
                cVarE2.b(eVar, aVar4);
                cVarE2.b(eVar, str2);
            }
        }
        for (Map.Entry<String, List<cr.a>> entry : this.aqI.oX().entrySet()) {
            for (cr.a aVar5 : entry.getValue()) {
                if (!di.n(aVar5.oS().get(com.google.android.gms.internal.b.NOT_DEFAULT_MACRO.toString())).booleanValue()) {
                    e(this.aqQ, entry.getKey()).i(aVar5);
                }
            }
        }
    }

    private bz<d.a> a(d.a aVar, Set<String> set, dl dlVar) {
        if (!aVar.gF) {
            return new bz<>(aVar, true);
        }
        switch (aVar.type) {
            case 2:
                d.a aVarG = cr.g(aVar);
                aVarG.gw = new d.a[aVar.gw.length];
                for (int i = 0; i < aVar.gw.length; i++) {
                    bz<d.a> bzVarA = a(aVar.gw[i], set, dlVar.fh(i));
                    if (bzVarA == aqH) {
                        return aqH;
                    }
                    aVarG.gw[i] = bzVarA.getObject();
                }
                return new bz<>(aVarG, false);
            case 3:
                d.a aVarG2 = cr.g(aVar);
                if (aVar.gx.length != aVar.gy.length) {
                    bh.T("Invalid serving value: " + aVar.toString());
                    return aqH;
                }
                aVarG2.gx = new d.a[aVar.gx.length];
                aVarG2.gy = new d.a[aVar.gx.length];
                for (int i2 = 0; i2 < aVar.gx.length; i2++) {
                    bz<d.a> bzVarA2 = a(aVar.gx[i2], set, dlVar.fi(i2));
                    bz<d.a> bzVarA3 = a(aVar.gy[i2], set, dlVar.fj(i2));
                    if (bzVarA2 == aqH || bzVarA3 == aqH) {
                        return aqH;
                    }
                    aVarG2.gx[i2] = bzVarA2.getObject();
                    aVarG2.gy[i2] = bzVarA3.getObject();
                }
                return new bz<>(aVarG2, false);
            case 4:
                if (set.contains(aVar.gz)) {
                    bh.T("Macro cycle detected.  Current macro reference: " + aVar.gz + ".  Previous macro references: " + set.toString() + ".");
                    return aqH;
                }
                set.add(aVar.gz);
                bz<d.a> bzVarA4 = dm.a(a(aVar.gz, set, dlVar.oD()), aVar.gE);
                set.remove(aVar.gz);
                return bzVarA4;
            case 5:
            case 6:
            default:
                bh.T("Unknown type: " + aVar.type);
                return aqH;
            case 7:
                d.a aVarG3 = cr.g(aVar);
                aVarG3.gD = new d.a[aVar.gD.length];
                for (int i3 = 0; i3 < aVar.gD.length; i3++) {
                    bz<d.a> bzVarA5 = a(aVar.gD[i3], set, dlVar.fk(i3));
                    if (bzVarA5 == aqH) {
                        return aqH;
                    }
                    aVarG3.gD[i3] = bzVarA5.getObject();
                }
                return new bz<>(aVarG3, false);
        }
    }

    private bz<d.a> a(String str, Set<String> set, bk bkVar) throws InterruptedException {
        cr.a next;
        this.aqS++;
        b bVar = this.aqO.get(str);
        if (bVar != null && !this.aqJ.oo()) {
            a(bVar.oT(), set);
            this.aqS--;
            return bVar.pn();
        }
        c cVar = this.aqQ.get(str);
        if (cVar == null) {
            bh.T(pm() + "Invalid macro: " + str);
            this.aqS--;
            return aqH;
        }
        bz<Set<cr.a>> bzVarA = a(str, cVar.po(), cVar.pp(), cVar.pq(), cVar.ps(), cVar.pr(), set, bkVar.of());
        if (bzVarA.getObject().isEmpty()) {
            next = cVar.pt();
        } else {
            if (bzVarA.getObject().size() > 1) {
                bh.W(pm() + "Multiple macros active for macroName " + str);
            }
            next = bzVarA.getObject().iterator().next();
        }
        if (next == null) {
            this.aqS--;
            return aqH;
        }
        bz<d.a> bzVarA2 = a(this.aqM, next, set, bkVar.ou());
        bz<d.a> bzVar = bzVarA2 == aqH ? aqH : new bz<>(bzVarA2.getObject(), bzVarA.oE() && bzVarA2.oE());
        d.a aVarOT = next.oT();
        if (bzVar.oE()) {
            this.aqO.e(str, new b(bzVar, aVarOT));
        }
        a(aVarOT, set);
        this.aqS--;
        return bzVar;
    }

    private bz<d.a> a(Map<String, aj> map, cr.a aVar, Set<String> set, ck ckVar) {
        boolean z;
        d.a aVar2 = aVar.oS().get(com.google.android.gms.internal.b.FUNCTION.toString());
        if (aVar2 == null) {
            bh.T("No function id in properties");
            return aqH;
        }
        String str = aVar2.gA;
        aj ajVar = map.get(str);
        if (ajVar == null) {
            bh.T(str + " has no backing implementation.");
            return aqH;
        }
        bz<d.a> bzVar = this.aqN.get(aVar);
        if (bzVar != null && !this.aqJ.oo()) {
            return bzVar;
        }
        HashMap map2 = new HashMap();
        boolean z2 = true;
        for (Map.Entry<String, d.a> entry : aVar.oS().entrySet()) {
            bz<d.a> bzVarA = a(entry.getValue(), set, ckVar.cE(entry.getKey()).e(entry.getValue()));
            if (bzVarA == aqH) {
                return aqH;
            }
            if (bzVarA.oE()) {
                aVar.a(entry.getKey(), bzVarA.getObject());
                z = z2;
            } else {
                z = false;
            }
            map2.put(entry.getKey(), bzVarA.getObject());
            z2 = z;
        }
        if (!ajVar.a(map2.keySet())) {
            bh.T("Incorrect keys for function " + str + " required " + ajVar.oq() + " had " + map2.keySet());
            return aqH;
        }
        boolean z3 = z2 && ajVar.nL();
        bz<d.a> bzVar2 = new bz<>(ajVar.C(map2), z3);
        if (z3) {
            this.aqN.e(aVar, bzVar2);
        }
        ckVar.d(bzVar2.getObject());
        return bzVar2;
    }

    private bz<Set<cr.a>> a(Set<cr.e> set, Set<String> set2, a aVar, cs csVar) {
        Set<cr.a> hashSet = new HashSet<>();
        Set<cr.a> hashSet2 = new HashSet<>();
        boolean z = true;
        for (cr.e eVar : set) {
            cn cnVarOC = csVar.oC();
            bz<Boolean> bzVarA = a(eVar, set2, cnVarOC);
            if (bzVarA.getObject().booleanValue()) {
                aVar.a(eVar, hashSet, hashSet2, cnVarOC);
            }
            z = z && bzVarA.oE();
        }
        hashSet.removeAll(hashSet2);
        csVar.b(hashSet);
        return new bz<>(hashSet, z);
    }

    private void a(d.a aVar, Set<String> set) throws InterruptedException {
        bz<d.a> bzVarA;
        if (aVar == null || (bzVarA = a(aVar, set, new bx())) == aqH) {
            return;
        }
        Object objO = di.o(bzVarA.getObject());
        if (objO instanceof Map) {
            this.anS.push((Map) objO);
            return;
        }
        if (!(objO instanceof List)) {
            bh.W("pushAfterEvaluate: value not a Map or List");
            return;
        }
        for (Object obj : (List) objO) {
            if (obj instanceof Map) {
                this.anS.push((Map) obj);
            } else {
                bh.W("pushAfterEvaluate: value not a Map");
            }
        }
    }

    private static void a(List<cr.a> list, List<String> list2, String str) {
        if (list.size() != list2.size()) {
            bh.U("Invalid resource: imbalance of rule names of functions for " + str + " operation. Using default rule name instead");
        }
    }

    private static void a(Map<String, aj> map, aj ajVar) {
        if (map.containsKey(ajVar.op())) {
            throw new IllegalArgumentException("Duplicate function type name: " + ajVar.op());
        }
        map.put(ajVar.op(), ajVar);
    }

    private static c e(Map<String, c> map, String str) {
        c cVar = map.get(str);
        if (cVar != null) {
            return cVar;
        }
        c cVar2 = new c();
        map.put(str, cVar2);
        return cVar2;
    }

    private static String h(cr.a aVar) {
        return di.j(aVar.oS().get(com.google.android.gms.internal.b.INSTANCE_NAME.toString()));
    }

    private String pm() {
        if (this.aqS <= 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(this.aqS));
        for (int i = 2; i < this.aqS; i++) {
            sb.append(' ');
        }
        sb.append(": ");
        return sb.toString();
    }

    bz<Boolean> a(cr.a aVar, Set<String> set, ck ckVar) {
        bz<d.a> bzVarA = a(this.aqL, aVar, set, ckVar);
        Boolean boolN = di.n(bzVarA.getObject());
        ckVar.d(di.u(boolN));
        return new bz<>(boolN, bzVarA.oE());
    }

    bz<Boolean> a(cr.e eVar, Set<String> set, cn cnVar) {
        Iterator<cr.a> it = eVar.pb().iterator();
        boolean z = true;
        while (it.hasNext()) {
            bz<Boolean> bzVarA = a(it.next(), set, cnVar.ow());
            if (bzVarA.getObject().booleanValue()) {
                cnVar.f(di.u(false));
                return new bz<>(false, bzVarA.oE());
            }
            z = z && bzVarA.oE();
        }
        Iterator<cr.a> it2 = eVar.pa().iterator();
        while (it2.hasNext()) {
            bz<Boolean> bzVarA2 = a(it2.next(), set, cnVar.ox());
            if (!bzVarA2.getObject().booleanValue()) {
                cnVar.f(di.u(false));
                return new bz<>(false, bzVarA2.oE());
            }
            z = z && bzVarA2.oE();
        }
        cnVar.f(di.u(true));
        return new bz<>(true, z);
    }

    bz<Set<cr.a>> a(String str, Set<cr.e> set, Map<cr.e, List<cr.a>> map, Map<cr.e, List<String>> map2, Map<cr.e, List<cr.a>> map3, Map<cr.e, List<String>> map4, Set<String> set2, cs csVar) {
        return a(set, set2, new a() { // from class: com.google.android.gms.tagmanager.ct.3
            final /* synthetic */ Map aqU;
            final /* synthetic */ Map aqV;
            final /* synthetic */ Map aqW;
            final /* synthetic */ Map aqX;

            AnonymousClass3(Map map5, Map map22, Map map32, Map map42) {
                map = map5;
                map = map22;
                map = map32;
                map = map42;
            }

            @Override // com.google.android.gms.tagmanager.ct.a
            public void a(cr.e eVar, Set<cr.a> set3, Set<cr.a> set22, cn cnVar) {
                List<cr.a> list = (List) map.get(eVar);
                List<String> list2 = (List) map.get(eVar);
                if (list != null) {
                    set3.addAll(list);
                    cnVar.oy().c(list, list2);
                }
                List<cr.a> list3 = (List) map.get(eVar);
                List<String> list4 = (List) map.get(eVar);
                if (list3 != null) {
                    set22.addAll(list3);
                    cnVar.oz().c(list3, list4);
                }
            }
        }, csVar);
    }

    bz<Set<cr.a>> a(Set<cr.e> set, cs csVar) {
        return a(set, new HashSet(), new a() { // from class: com.google.android.gms.tagmanager.ct.4
            AnonymousClass4() {
            }

            @Override // com.google.android.gms.tagmanager.ct.a
            public void a(cr.e eVar, Set<cr.a> set2, Set<cr.a> set22, cn cnVar) {
                set2.addAll(eVar.pc());
                set22.addAll(eVar.pd());
                cnVar.oA().c(eVar.pc(), eVar.ph());
                cnVar.oB().c(eVar.pd(), eVar.pi());
            }
        }, csVar);
    }

    void a(aj ajVar) {
        a(this.aqM, ajVar);
    }

    void b(aj ajVar) {
        a(this.aqK, ajVar);
    }

    void c(aj ajVar) {
        a(this.aqL, ajVar);
    }

    public bz<d.a> cO(String str) throws InterruptedException {
        this.aqS = 0;
        af afVarCx = this.aqJ.cx(str);
        bz<d.a> bzVarA = a(str, new HashSet(), afVarCx.ol());
        afVarCx.on();
        return bzVarA;
    }

    synchronized void cP(String str) {
        this.aqR = str;
    }

    public synchronized void cm(String str) {
        cP(str);
        af afVarCy = this.aqJ.cy(str);
        t tVarOm = afVarCy.om();
        Iterator<cr.a> it = a(this.aqP, tVarOm.of()).getObject().iterator();
        while (it.hasNext()) {
            a(this.aqK, it.next(), new HashSet(), tVarOm.oe());
        }
        afVarCy.on();
        cP(null);
    }

    public synchronized void k(List<c.i> list) {
        for (c.i iVar : list) {
            if (iVar.name == null || !iVar.name.startsWith("gaExperiment:")) {
                bh.V("Ignored supplemental: " + iVar);
            } else {
                ai.a(this.anS, iVar);
            }
        }
    }

    synchronized String pl() {
        return this.aqR;
    }
}
