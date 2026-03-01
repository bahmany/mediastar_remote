package com.google.android.gms.tagmanager;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.c;
import com.google.android.gms.internal.ju;
import com.google.android.gms.internal.jw;
import com.google.android.gms.internal.ok;
import com.google.android.gms.tagmanager.bg;
import com.google.android.gms.tagmanager.ce;
import com.google.android.gms.tagmanager.cr;
import com.google.android.gms.tagmanager.n;

/* loaded from: classes.dex */
class o extends BaseImplementation.AbstractPendingResult<ContainerHolder> {
    private final Looper IB;
    private final String anR;
    private long anW;
    private final TagManager aod;
    private final d aog;
    private final cg aoh;
    private final int aoi;
    private f aoj;
    private volatile n aok;
    private volatile boolean aol;
    private c.j aom;
    private String aon;
    private e aoo;
    private a aop;
    private final Context mContext;
    private final ju yD;

    /* renamed from: com.google.android.gms.tagmanager.o$1 */
    class AnonymousClass1 implements n.a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public void co(String str) {
            o.this.co(str);
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public String nS() {
            return o.this.nS();
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public void nU() {
            bh.W("Refresh ignored: container loaded as default only.");
        }
    }

    /* renamed from: com.google.android.gms.tagmanager.o$2 */
    class AnonymousClass2 implements a {
        final /* synthetic */ boolean aor;

        AnonymousClass2(boolean z) {
            z = z;
        }

        @Override // com.google.android.gms.tagmanager.o.a
        public boolean b(Container container) {
            return z ? container.getLastRefreshTime() + 43200000 >= o.this.yD.currentTimeMillis() : !container.isDefault();
        }
    }

    interface a {
        boolean b(Container container);
    }

    private class b implements bg<ok.a> {
        private b() {
        }

        /* synthetic */ b(o oVar, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.tagmanager.bg
        /* renamed from: a */
        public void l(ok.a aVar) {
            c.j jVar;
            if (aVar.ash != null) {
                jVar = aVar.ash;
            } else {
                c.f fVar = aVar.gs;
                jVar = new c.j();
                jVar.gs = fVar;
                jVar.gr = null;
                jVar.gt = fVar.version;
            }
            o.this.a(jVar, aVar.asg, true);
        }

        @Override // com.google.android.gms.tagmanager.bg
        public void a(bg.a aVar) {
            if (o.this.aol) {
                return;
            }
            o.this.w(0L);
        }

        @Override // com.google.android.gms.tagmanager.bg
        public void nZ() {
        }
    }

    private class c implements bg<c.j> {
        private c() {
        }

        /* synthetic */ c(o oVar, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.tagmanager.bg
        public void a(bg.a aVar) {
            if (o.this.aok != null) {
                o.this.b((o) o.this.aok);
            } else {
                o.this.b((o) o.this.c(Status.Jr));
            }
            o.this.w(3600000L);
        }

        @Override // com.google.android.gms.tagmanager.bg
        /* renamed from: b */
        public void l(c.j jVar) {
            synchronized (o.this) {
                if (jVar.gs == null) {
                    if (o.this.aom.gs == null) {
                        bh.T("Current resource is null; network resource is also null");
                        o.this.w(3600000L);
                        return;
                    }
                    jVar.gs = o.this.aom.gs;
                }
                o.this.a(jVar, o.this.yD.currentTimeMillis(), false);
                bh.V("setting refresh time to current time: " + o.this.anW);
                if (!o.this.nY()) {
                    o.this.a(jVar);
                }
            }
        }

        @Override // com.google.android.gms.tagmanager.bg
        public void nZ() {
        }
    }

    private class d implements n.a {
        private d() {
        }

        /* synthetic */ d(o oVar, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public void co(String str) {
            o.this.co(str);
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public String nS() {
            return o.this.nS();
        }

        @Override // com.google.android.gms.tagmanager.n.a
        public void nU() {
            if (o.this.aoh.eK()) {
                o.this.w(0L);
            }
        }
    }

    interface e extends Releasable {
        void a(bg<c.j> bgVar);

        void cr(String str);

        void e(long j, String str);
    }

    interface f extends Releasable {
        void a(bg<ok.a> bgVar);

        void b(ok.a aVar);

        cr.c fe(int i);

        void oa();
    }

    o(Context context, TagManager tagManager, Looper looper, String str, int i, f fVar, e eVar, ju juVar, cg cgVar) {
        super(looper == null ? Looper.getMainLooper() : looper);
        this.mContext = context;
        this.aod = tagManager;
        this.IB = looper == null ? Looper.getMainLooper() : looper;
        this.anR = str;
        this.aoi = i;
        this.aoj = fVar;
        this.aoo = eVar;
        this.aog = new d();
        this.aom = new c.j();
        this.yD = juVar;
        this.aoh = cgVar;
        if (nY()) {
            co(ce.oH().oJ());
        }
    }

    public o(Context context, TagManager tagManager, Looper looper, String str, int i, r rVar) {
        this(context, tagManager, looper, str, i, new cq(context, str), new cp(context, str, rVar), jw.hA(), new bf(30, 900000L, 5000L, "refreshing", jw.hA()));
    }

    private void T(boolean z) {
        this.aoj.a(new b());
        this.aoo.a(new c());
        cr.c cVarFe = this.aoj.fe(this.aoi);
        if (cVarFe != null) {
            this.aok = new n(this.aod, this.IB, new Container(this.mContext, this.aod.getDataLayer(), this.anR, 0L, cVarFe), this.aog);
        }
        this.aop = new a() { // from class: com.google.android.gms.tagmanager.o.2
            final /* synthetic */ boolean aor;

            AnonymousClass2(boolean z2) {
                z = z2;
            }

            @Override // com.google.android.gms.tagmanager.o.a
            public boolean b(Container container) {
                return z ? container.getLastRefreshTime() + 43200000 >= o.this.yD.currentTimeMillis() : !container.isDefault();
            }
        };
        if (nY()) {
            this.aoo.e(0L, "");
        } else {
            this.aoj.oa();
        }
    }

    public synchronized void a(c.j jVar) {
        if (this.aoj != null) {
            ok.a aVar = new ok.a();
            aVar.asg = this.anW;
            aVar.gs = new c.f();
            aVar.ash = jVar;
            this.aoj.b(aVar);
        }
    }

    public synchronized void a(c.j jVar, long j, boolean z) {
        if (z) {
            if (!this.aol) {
            }
        }
        if (!isReady() || this.aok == null) {
        }
        this.aom = jVar;
        this.anW = j;
        w(Math.max(0L, Math.min(43200000L, (this.anW + 43200000) - this.yD.currentTimeMillis())));
        Container container = new Container(this.mContext, this.aod.getDataLayer(), this.anR, j, jVar);
        if (this.aok == null) {
            this.aok = new n(this.aod, this.IB, container, this.aog);
        } else {
            this.aok.a(container);
        }
        if (!isReady() && this.aop.b(container)) {
            b((o) this.aok);
        }
    }

    public boolean nY() {
        ce ceVarOH = ce.oH();
        return (ceVarOH.oI() == ce.a.CONTAINER || ceVarOH.oI() == ce.a.CONTAINER_DEBUG) && this.anR.equals(ceVarOH.getContainerId());
    }

    public synchronized void w(long j) {
        if (this.aoo == null) {
            bh.W("Refresh requested, but no network load scheduler.");
        } else {
            this.aoo.e(j, this.aom.gt);
        }
    }

    @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
    /* renamed from: aE */
    public ContainerHolder c(Status status) {
        if (this.aok != null) {
            return this.aok;
        }
        if (status == Status.Jr) {
            bh.T("timer expired: setting result to failure");
        }
        return new n(status);
    }

    synchronized void co(String str) {
        this.aon = str;
        if (this.aoo != null) {
            this.aoo.cr(str);
        }
    }

    synchronized String nS() {
        return this.aon;
    }

    public void nV() {
        cr.c cVarFe = this.aoj.fe(this.aoi);
        if (cVarFe != null) {
            b((o) new n(this.aod, this.IB, new Container(this.mContext, this.aod.getDataLayer(), this.anR, 0L, cVarFe), new n.a() { // from class: com.google.android.gms.tagmanager.o.1
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.tagmanager.n.a
                public void co(String str) {
                    o.this.co(str);
                }

                @Override // com.google.android.gms.tagmanager.n.a
                public String nS() {
                    return o.this.nS();
                }

                @Override // com.google.android.gms.tagmanager.n.a
                public void nU() {
                    bh.W("Refresh ignored: container loaded as default only.");
                }
            }));
        } else {
            bh.T("Default was requested, but no default container was found");
            b((o) c(new Status(10, "Default was requested, but no default container was found", null)));
        }
        this.aoo = null;
        this.aoj = null;
    }

    public void nW() {
        T(false);
    }

    public void nX() {
        T(true);
    }
}
