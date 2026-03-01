package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.fi;
import com.google.android.gms.internal.fz;

@ez
/* loaded from: classes.dex */
public final class fa {

    public interface a {
        void a(fz.a aVar);
    }

    public static gg a(Context context, fi.a aVar, k kVar, a aVar2) {
        fb fbVar = new fb(context, aVar, kVar, aVar2);
        fbVar.start();
        return fbVar;
    }
}
