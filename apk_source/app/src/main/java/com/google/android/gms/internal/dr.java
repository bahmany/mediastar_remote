package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.dynamic.g;
import com.google.android.gms.internal.ds;
import com.google.android.gms.internal.dt;

@ez
/* loaded from: classes.dex */
public final class dr extends com.google.android.gms.dynamic.g<dt> {
    private static final dr sh = new dr();

    private static final class a extends Exception {
        public a(String str) {
            super(str);
        }
    }

    private dr() {
        super("com.google.android.gms.ads.AdOverlayCreatorImpl");
    }

    public static ds b(Activity activity) {
        ds dsVarD;
        try {
            if (c(activity)) {
                gs.S("Using AdOverlay from the client jar.");
                dsVarD = new dk(activity);
            } else {
                dsVarD = sh.d(activity);
            }
            return dsVarD;
        } catch (a e) {
            gs.W(e.getMessage());
            return null;
        }
    }

    private static boolean c(Activity activity) throws a {
        Intent intent = activity.getIntent();
        if (intent.hasExtra("com.google.android.gms.ads.internal.overlay.useClientJar")) {
            return intent.getBooleanExtra("com.google.android.gms.ads.internal.overlay.useClientJar", false);
        }
        throw new a("Ad overlay requires the useClientJar flag in intent extras.");
    }

    private ds d(Activity activity) {
        try {
            return ds.a.p(L(activity).a(com.google.android.gms.dynamic.e.k(activity)));
        } catch (RemoteException e) {
            gs.d("Could not create remote AdOverlay.", e);
            return null;
        } catch (g.a e2) {
            gs.d("Could not create remote AdOverlay.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.g
    /* renamed from: o, reason: merged with bridge method [inline-methods] */
    public dt d(IBinder iBinder) {
        return dt.a.q(iBinder);
    }
}
