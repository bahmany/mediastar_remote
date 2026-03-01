package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.g;
import com.google.android.gms.internal.bd;
import com.google.android.gms.internal.be;

@ez
/* loaded from: classes.dex */
public final class au extends com.google.android.gms.dynamic.g<be> {
    private static final au nS = new au();

    private au() {
        super("com.google.android.gms.ads.AdManagerCreatorImpl");
    }

    public static bd a(Context context, ay ayVar, String str, cs csVar) {
        bd bdVarB;
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0 && (bdVarB = nS.b(context, ayVar, str, csVar)) != null) {
            return bdVarB;
        }
        gs.S("Using AdManager from the client jar.");
        return new u(context, ayVar, str, csVar, new gt(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, true));
    }

    private bd b(Context context, ay ayVar, String str, cs csVar) {
        try {
            return bd.a.f(L(context).a(com.google.android.gms.dynamic.e.k(context), ayVar, str, csVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE));
        } catch (RemoteException e) {
            gs.d("Could not create remote AdManager.", e);
            return null;
        } catch (g.a e2) {
            gs.d("Could not create remote AdManager.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.g
    /* renamed from: c, reason: merged with bridge method [inline-methods] */
    public be d(IBinder iBinder) {
        return be.a.g(iBinder);
    }
}
