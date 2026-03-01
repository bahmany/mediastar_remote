package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.ah;
import java.util.concurrent.Future;

@ez
/* loaded from: classes.dex */
public class ai {
    protected ah a(Context context, gt gtVar, final gk<ah> gkVar) {
        final aj ajVar = new aj(context, gtVar);
        ajVar.a(new ah.a() { // from class: com.google.android.gms.internal.ai.2
            @Override // com.google.android.gms.internal.ah.a
            public void aM() {
                gkVar.a(ajVar);
            }
        });
        return ajVar;
    }

    public Future<ah> a(final Context context, final gt gtVar, final String str) {
        final gk gkVar = new gk();
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.ai.1
            @Override // java.lang.Runnable
            public void run() {
                ai.this.a(context, gtVar, gkVar).f(str);
            }
        });
        return gkVar;
    }
}
