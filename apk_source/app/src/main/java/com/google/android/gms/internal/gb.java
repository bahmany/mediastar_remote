package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.cf;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

@ez
/* loaded from: classes.dex */
public class gb implements cf.b {
    private static final gb vJ = new gb();
    public static final String vK = vJ.vL;
    private Context mContext;
    private gt qs;
    private String vW;
    private final Object mw = new Object();
    private BigInteger vN = BigInteger.ONE;
    private final HashSet<ga> vO = new HashSet<>();
    private final HashMap<String, ge> vP = new HashMap<>();
    private boolean vQ = false;
    private boolean uH = true;
    private boolean vR = false;
    private boolean uI = true;
    private am nu = null;
    private an vS = null;
    private al nv = null;
    private LinkedList<Thread> vT = new LinkedList<>();
    private boolean vU = false;
    private Bundle vV = bn.bs();
    private ey nw = null;
    public final String vL = gj.dp();
    private final gc vM = new gc(this.vL);

    private gb() {
    }

    public static Bundle a(Context context, gd gdVar, String str) {
        return vJ.b(context, gdVar, str);
    }

    public static void a(Context context, gt gtVar) {
        vJ.b(context, gtVar);
    }

    public static void a(Context context, boolean z) {
        vJ.b(context, z);
    }

    public static void b(HashSet<ga> hashSet) {
        vJ.c(hashSet);
    }

    public static Bundle bD() {
        return vJ.dh();
    }

    public static String c(int i, String str) {
        return vJ.d(i, str);
    }

    public static gb cV() {
        return vJ;
    }

    public static String cX() {
        return vJ.cY();
    }

    public static gc cZ() {
        return vJ.da();
    }

    public static boolean db() {
        return vJ.dc();
    }

    public static boolean dd() {
        return vJ.de();
    }

    public static String df() {
        return vJ.dg();
    }

    public static void e(Throwable th) {
        vJ.f(th);
    }

    @Override // com.google.android.gms.internal.cf.b
    public void a(Bundle bundle) {
        synchronized (this.mw) {
            this.vU = true;
            this.vV = bundle;
            while (!this.vT.isEmpty()) {
                ey.a(this.mContext, this.vT.remove(0), this.qs);
            }
        }
    }

    public void a(ga gaVar) {
        synchronized (this.mw) {
            this.vO.add(gaVar);
        }
    }

    public void a(String str, ge geVar) {
        synchronized (this.mw) {
            this.vP.put(str, geVar);
        }
    }

    public void a(Thread thread) {
        synchronized (this.mw) {
            if (this.vU) {
                ey.a(this.mContext, thread, this.qs);
            } else {
                this.vT.add(thread);
            }
        }
    }

    public Bundle b(Context context, gd gdVar, String str) {
        Bundle bundle;
        synchronized (this.mw) {
            bundle = new Bundle();
            bundle.putBundle("app", this.vM.b(context, str));
            Bundle bundle2 = new Bundle();
            for (String str2 : this.vP.keySet()) {
                bundle2.putBundle(str2, this.vP.get(str2).toBundle());
            }
            bundle.putBundle("slots", bundle2);
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            Iterator<ga> it = this.vO.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().toBundle());
            }
            bundle.putParcelableArrayList("ads", arrayList);
            gdVar.a(this.vO);
            this.vO.clear();
        }
        return bundle;
    }

    public void b(Context context, gt gtVar) {
        synchronized (this.mw) {
            if (!this.vR) {
                this.mContext = context.getApplicationContext();
                this.qs = gtVar;
                this.uH = gh.o(context);
                iv.H(context);
                cf.a(context, this);
                a(Thread.currentThread());
                this.vW = gj.c(context, gtVar.wD);
                this.vR = true;
            }
        }
    }

    public void b(Context context, boolean z) {
        synchronized (this.mw) {
            if (z != this.uH) {
                this.uH = z;
                gh.a(context, z);
            }
        }
    }

    public void c(HashSet<ga> hashSet) {
        synchronized (this.mw) {
            this.vO.addAll(hashSet);
        }
    }

    public boolean cW() {
        boolean z;
        synchronized (this.mw) {
            z = this.uI;
        }
        return z;
    }

    public String cY() {
        String string;
        synchronized (this.mw) {
            string = this.vN.toString();
            this.vN = this.vN.add(BigInteger.ONE);
        }
        return string;
    }

    public String d(int i, String str) {
        Resources resources = this.qs.wG ? this.mContext.getResources() : GooglePlayServicesUtil.getRemoteResource(this.mContext);
        return resources == null ? str : resources.getString(i);
    }

    public gc da() {
        gc gcVar;
        synchronized (this.mw) {
            gcVar = this.vM;
        }
        return gcVar;
    }

    public boolean dc() {
        boolean z;
        synchronized (this.mw) {
            z = this.vQ;
            this.vQ = true;
        }
        return z;
    }

    public boolean de() {
        boolean z;
        synchronized (this.mw) {
            z = this.uH;
        }
        return z;
    }

    public String dg() {
        String str;
        synchronized (this.mw) {
            str = this.vW;
        }
        return str;
    }

    public Bundle dh() {
        Bundle bundle;
        synchronized (this.mw) {
            bundle = this.vV;
        }
        return bundle;
    }

    public void f(Throwable th) {
        if (this.vR) {
            new ey(this.mContext, this.qs, null, null).b(th);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x0042 A[Catch: all -> 0x002c, TryCatch #0 {, blocks: (B:42:0x0022, B:44:0x0026, B:46:0x002a, B:51:0x002f, B:52:0x003e, B:54:0x0042, B:55:0x0049, B:57:0x004d, B:58:0x0065, B:59:0x006c), top: B:61:0x0022 }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x004d A[Catch: all -> 0x002c, TryCatch #0 {, blocks: (B:42:0x0022, B:44:0x0026, B:46:0x002a, B:51:0x002f, B:52:0x003e, B:54:0x0042, B:55:0x0049, B:57:0x004d, B:58:0x0065, B:59:0x006c), top: B:61:0x0022 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.internal.an l(android.content.Context r11) {
        /*
            r10 = this;
            r0 = 0
            android.os.Bundle r1 = bD()
            com.google.android.gms.internal.iv<java.lang.Boolean> r2 = com.google.android.gms.internal.bn.pd
            java.lang.String r2 = r2.getKey()
            r3 = 0
            boolean r1 = r1.getBoolean(r2, r3)
            if (r1 == 0) goto L1e
            boolean r1 = com.google.android.gms.internal.kc.hE()
            if (r1 == 0) goto L1e
            boolean r1 = r10.cW()
            if (r1 == 0) goto L1f
        L1e:
            return r0
        L1f:
            java.lang.Object r1 = r10.mw
            monitor-enter(r1)
            com.google.android.gms.internal.am r2 = r10.nu     // Catch: java.lang.Throwable -> L2c
            if (r2 != 0) goto L3e
            boolean r2 = r11 instanceof android.app.Activity     // Catch: java.lang.Throwable -> L2c
            if (r2 != 0) goto L2f
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L2c
            goto L1e
        L2c:
            r0 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L2c
            throw r0
        L2f:
            com.google.android.gms.internal.am r2 = new com.google.android.gms.internal.am     // Catch: java.lang.Throwable -> L2c
            android.content.Context r0 = r11.getApplicationContext()     // Catch: java.lang.Throwable -> L2c
            android.app.Application r0 = (android.app.Application) r0     // Catch: java.lang.Throwable -> L2c
            android.app.Activity r11 = (android.app.Activity) r11     // Catch: java.lang.Throwable -> L2c
            r2.<init>(r0, r11)     // Catch: java.lang.Throwable -> L2c
            r10.nu = r2     // Catch: java.lang.Throwable -> L2c
        L3e:
            com.google.android.gms.internal.al r0 = r10.nv     // Catch: java.lang.Throwable -> L2c
            if (r0 != 0) goto L49
            com.google.android.gms.internal.al r0 = new com.google.android.gms.internal.al     // Catch: java.lang.Throwable -> L2c
            r0.<init>()     // Catch: java.lang.Throwable -> L2c
            r10.nv = r0     // Catch: java.lang.Throwable -> L2c
        L49:
            com.google.android.gms.internal.an r0 = r10.vS     // Catch: java.lang.Throwable -> L2c
            if (r0 != 0) goto L65
            com.google.android.gms.internal.an r0 = new com.google.android.gms.internal.an     // Catch: java.lang.Throwable -> L2c
            com.google.android.gms.internal.am r2 = r10.nu     // Catch: java.lang.Throwable -> L2c
            com.google.android.gms.internal.al r3 = r10.nv     // Catch: java.lang.Throwable -> L2c
            android.os.Bundle r4 = r10.vV     // Catch: java.lang.Throwable -> L2c
            com.google.android.gms.internal.ey r5 = new com.google.android.gms.internal.ey     // Catch: java.lang.Throwable -> L2c
            android.content.Context r6 = r10.mContext     // Catch: java.lang.Throwable -> L2c
            com.google.android.gms.internal.gt r7 = r10.qs     // Catch: java.lang.Throwable -> L2c
            r8 = 0
            r9 = 0
            r5.<init>(r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L2c
            r0.<init>(r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L2c
            r10.vS = r0     // Catch: java.lang.Throwable -> L2c
        L65:
            com.google.android.gms.internal.an r0 = r10.vS     // Catch: java.lang.Throwable -> L2c
            r0.aV()     // Catch: java.lang.Throwable -> L2c
            com.google.android.gms.internal.an r0 = r10.vS     // Catch: java.lang.Throwable -> L2c
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L2c
            goto L1e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.gb.l(android.content.Context):com.google.android.gms.internal.an");
    }

    public void v(boolean z) {
        synchronized (this.mw) {
            this.uI = z;
        }
    }
}
