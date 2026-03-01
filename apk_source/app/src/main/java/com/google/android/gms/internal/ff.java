package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.fg;

@ez
/* loaded from: classes.dex */
public final class ff {

    public interface a {
        void a(fk fkVar);
    }

    public static gg a(Context context, fi fiVar, a aVar) {
        return fiVar.lD.wG ? b(context, fiVar, aVar) : c(context, fiVar, aVar);
    }

    private static gg b(Context context, fi fiVar, a aVar) {
        gs.S("Fetching ad response from local ad request service.");
        fg.a aVar2 = new fg.a(context, fiVar, aVar);
        aVar2.start();
        return aVar2;
    }

    private static gg c(Context context, fi fiVar, a aVar) {
        gs.S("Fetching ad response from remote ad request service.");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
            return new fg.b(context, fiVar, aVar);
        }
        gs.W("Failed to connect to remote ad request service.");
        return null;
    }
}
