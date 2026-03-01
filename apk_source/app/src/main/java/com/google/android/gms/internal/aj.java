package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.ah;
import com.google.android.gms.internal.gw;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class aj implements ah {
    private final gv md;

    /* renamed from: com.google.android.gms.internal.aj$1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ String nb;
        final /* synthetic */ JSONObject nc;

        AnonymousClass1(String str, JSONObject jSONObject) {
            str = str;
            jSONObject = jSONObject;
        }

        @Override // java.lang.Runnable
        public void run() {
            aj.this.md.a(str, jSONObject);
        }
    }

    /* renamed from: com.google.android.gms.internal.aj$2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ String mY;

        AnonymousClass2(String str) {
            str = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            aj.this.md.loadUrl(str);
        }
    }

    /* renamed from: com.google.android.gms.internal.aj$3 */
    class AnonymousClass3 implements gw.a {
        final /* synthetic */ ah.a ne;

        AnonymousClass3(ah.a aVar) {
            aVar = aVar;
        }

        @Override // com.google.android.gms.internal.gw.a
        public void a(gv gvVar) {
            aVar.aM();
        }
    }

    public aj(Context context, gt gtVar) {
        this.md = gv.a(context, new ay(), false, false, null, gtVar);
    }

    private void runOnUiThread(Runnable runnable) {
        if (gr.dt()) {
            runnable.run();
        } else {
            gr.wC.post(runnable);
        }
    }

    @Override // com.google.android.gms.internal.ah
    public void a(ah.a aVar) {
        this.md.dv().a(new gw.a() { // from class: com.google.android.gms.internal.aj.3
            final /* synthetic */ ah.a ne;

            AnonymousClass3(ah.a aVar2) {
                aVar = aVar2;
            }

            @Override // com.google.android.gms.internal.gw.a
            public void a(gv gvVar) {
                aVar.aM();
            }
        });
    }

    @Override // com.google.android.gms.internal.ah
    public void a(t tVar, dn dnVar, bw bwVar, dq dqVar, boolean z, bz bzVar) {
        this.md.dv().a(tVar, dnVar, bwVar, dqVar, z, bzVar, new v(false));
    }

    @Override // com.google.android.gms.internal.ah
    public void a(String str, by byVar) {
        this.md.dv().a(str, byVar);
    }

    @Override // com.google.android.gms.internal.ah
    public void a(String str, JSONObject jSONObject) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.aj.1
            final /* synthetic */ String nb;
            final /* synthetic */ JSONObject nc;

            AnonymousClass1(String str2, JSONObject jSONObject2) {
                str = str2;
                jSONObject = jSONObject2;
            }

            @Override // java.lang.Runnable
            public void run() {
                aj.this.md.a(str, jSONObject);
            }
        });
    }

    @Override // com.google.android.gms.internal.ah
    public void destroy() {
        this.md.destroy();
    }

    @Override // com.google.android.gms.internal.ah
    public void f(String str) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.aj.2
            final /* synthetic */ String mY;

            AnonymousClass2(String str2) {
                str = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                aj.this.md.loadUrl(str);
            }
        });
    }

    @Override // com.google.android.gms.internal.ah
    public void g(String str) {
        this.md.dv().a(str, (by) null);
    }

    @Override // com.google.android.gms.internal.ah
    public void pause() {
        gj.a(this.md);
    }

    @Override // com.google.android.gms.internal.ah
    public void resume() {
        gj.b(this.md);
    }
}
