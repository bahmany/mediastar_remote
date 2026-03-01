package com.google.android.gms.internal;

import android.content.Context;
import android.location.Location;
import com.google.android.gms.internal.fm;
import com.google.android.gms.internal.gw;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ez
/* loaded from: classes.dex */
public final class fr extends fm.a {
    private static final Object uf = new Object();
    private static fr ug;
    private final Context mContext;
    private final fx uh;
    private final ci ui;
    private final bm uj;

    /* renamed from: com.google.android.gms.internal.fr$1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ Context mV;
        final /* synthetic */ fi uk;
        final /* synthetic */ ft ul;
        final /* synthetic */ gw.a um;
        final /* synthetic */ String un;

        AnonymousClass1(Context context, fi fiVar, ft ftVar, gw.a aVar, String str) {
            context = context;
            fiVar = fiVar;
            ftVar = ftVar;
            aVar = aVar;
            str = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            gv gvVarA = gv.a(context, new ay(), false, false, null, fiVar.lD);
            gvVarA.setWillNotDraw(true);
            ftVar.b(gvVarA);
            gw gwVarDv = gvVarA.dv();
            gwVarDv.a("/invalidRequest", ftVar.us);
            gwVarDv.a("/loadAdURL", ftVar.ut);
            gwVarDv.a("/log", bx.pG);
            gwVarDv.a(aVar);
            gs.S("Loading the JS library.");
            gvVarA.loadUrl(str);
        }
    }

    /* renamed from: com.google.android.gms.internal.fr$2 */
    static class AnonymousClass2 implements gw.a {
        final /* synthetic */ String uo;

        AnonymousClass2(String str) {
            str = str;
        }

        @Override // com.google.android.gms.internal.gw.a
        public void a(gv gvVar) {
            String str = String.format("javascript:%s(%s);", "AFMA_buildAdURL", str);
            gs.V("About to execute: " + str);
            gvVar.loadUrl(str);
        }
    }

    fr(Context context, bm bmVar, ci ciVar, fx fxVar) {
        this.mContext = context;
        this.uh = fxVar;
        this.ui = ciVar;
        this.uj = bmVar;
    }

    private static gw.a I(String str) {
        return new gw.a() { // from class: com.google.android.gms.internal.fr.2
            final /* synthetic */ String uo;

            AnonymousClass2(String str2) {
                str = str2;
            }

            @Override // com.google.android.gms.internal.gw.a
            public void a(gv gvVar) {
                String str2 = String.format("javascript:%s(%s);", "AFMA_buildAdURL", str);
                gs.V("About to execute: " + str2);
                gvVar.loadUrl(str2);
            }
        };
    }

    private static fk a(Context context, bm bmVar, ci ciVar, fx fxVar, fi fiVar) throws ExecutionException, InterruptedException, TimeoutException {
        String string;
        gs.S("Starting ad request from service.");
        ciVar.init();
        fw fwVar = new fw(context);
        if (fwVar.vd == -1) {
            gs.S("Device is offline.");
            return new fk(2);
        }
        ft ftVar = new ft(fiVar.applicationInfo.packageName);
        if (fiVar.tx.extras != null && (string = fiVar.tx.extras.getString("_ad")) != null) {
            return fs.a(context, fiVar, string);
        }
        Location locationA = ciVar.a(250L);
        String strBp = bmVar.bp();
        String strA = fs.a(fiVar, fwVar, locationA, bmVar.bq(), bmVar.br());
        if (strA == null) {
            return new fk(0);
        }
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fr.1
            final /* synthetic */ Context mV;
            final /* synthetic */ fi uk;
            final /* synthetic */ ft ul;
            final /* synthetic */ gw.a um;
            final /* synthetic */ String un;

            AnonymousClass1(Context context2, fi fiVar2, ft ftVar2, gw.a aVar, String strBp2) {
                context = context2;
                fiVar = fiVar2;
                ftVar = ftVar2;
                aVar = aVar;
                str = strBp2;
            }

            @Override // java.lang.Runnable
            public void run() {
                gv gvVarA = gv.a(context, new ay(), false, false, null, fiVar.lD);
                gvVarA.setWillNotDraw(true);
                ftVar.b(gvVarA);
                gw gwVarDv = gvVarA.dv();
                gwVarDv.a("/invalidRequest", ftVar.us);
                gwVarDv.a("/loadAdURL", ftVar.ut);
                gwVarDv.a("/log", bx.pG);
                gwVarDv.a(aVar);
                gs.S("Loading the JS library.");
                gvVarA.loadUrl(str);
            }
        });
        try {
            fv fvVar = ftVar2.cL().get(10L, TimeUnit.SECONDS);
            if (fvVar == null) {
                return new fk(0);
            }
            if (fvVar.getErrorCode() != -2) {
                return new fk(fvVar.getErrorCode());
            }
            return a(context2, fiVar2.lD.wD, fvVar.getUrl(), fvVar.cO() ? fxVar.K(fiVar2.ty.packageName) : null, fvVar);
        } catch (Exception e) {
            return new fk(0);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:110:?, code lost:
    
        return new com.google.android.gms.internal.fk(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x00db, code lost:
    
        com.google.android.gms.internal.gs.W("Received error HTTP response code: " + r6);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.google.android.gms.internal.fk a(android.content.Context r10, java.lang.String r11, java.lang.String r12, java.lang.String r13, com.google.android.gms.internal.fv r14) {
        /*
            Method dump skipped, instructions count: 300
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.fr.a(android.content.Context, java.lang.String, java.lang.String, java.lang.String, com.google.android.gms.internal.fv):com.google.android.gms.internal.fk");
    }

    public static fr a(Context context, bm bmVar, ci ciVar, fx fxVar) {
        fr frVar;
        synchronized (uf) {
            if (ug == null) {
                ug = new fr(context.getApplicationContext(), bmVar, ciVar, fxVar);
            }
            frVar = ug;
        }
        return frVar;
    }

    private static void a(String str, Map<String, List<String>> map, String str2, int i) {
        if (gs.u(2)) {
            gs.V("Http Response: {\n  URL:\n    " + str + "\n  Headers:");
            if (map != null) {
                for (String str3 : map.keySet()) {
                    gs.V("    " + str3 + ":");
                    Iterator<String> it = map.get(str3).iterator();
                    while (it.hasNext()) {
                        gs.V("      " + it.next());
                    }
                }
            }
            gs.V("  Body:");
            if (str2 != null) {
                for (int i2 = 0; i2 < Math.min(str2.length(), 100000); i2 += 1000) {
                    gs.V(str2.substring(i2, Math.min(str2.length(), i2 + 1000)));
                }
            } else {
                gs.V("    null");
            }
            gs.V("  Response Code:\n    " + i + "\n}");
        }
    }

    @Override // com.google.android.gms.internal.fm
    public fk b(fi fiVar) {
        return a(this.mContext, this.uj, this.ui, this.uh, fiVar);
    }
}
