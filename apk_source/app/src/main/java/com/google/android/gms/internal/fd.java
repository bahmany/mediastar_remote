package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.fz;

@ez
/* loaded from: classes.dex */
public final class fd {

    public interface a {
        void a(fz fzVar);
    }

    public static gg a(Context context, u uVar, fz.a aVar, gv gvVar, ct ctVar, a aVar2) {
        gg fnVar = aVar.vw.tS ? new fn(context, uVar, new ai(), aVar, aVar2) : new fe(context, aVar, gvVar, ctVar, aVar2);
        fnVar.start();
        return fnVar;
    }
}
