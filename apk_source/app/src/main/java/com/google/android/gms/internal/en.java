package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.dynamic.g;
import com.google.android.gms.internal.ei;
import com.google.android.gms.internal.ej;

@ez
/* loaded from: classes.dex */
public final class en extends com.google.android.gms.dynamic.g<ej> {
    private static final en sK = new en();

    private static final class a extends Exception {
        public a(String str) {
            super(str);
        }
    }

    private en() {
        super("com.google.android.gms.ads.InAppPurchaseManagerCreatorImpl");
    }

    private static boolean c(Activity activity) throws a {
        Intent intent = activity.getIntent();
        if (intent.hasExtra("com.google.android.gms.ads.internal.purchase.useClientJar")) {
            return intent.getBooleanExtra("com.google.android.gms.ads.internal.purchase.useClientJar", false);
        }
        throw new a("InAppPurchaseManager requires the useClientJar flag in intent extras.");
    }

    public static ei e(Activity activity) {
        ei eiVarF;
        try {
            if (c(activity)) {
                gs.S("Using AdOverlay from the client jar.");
                eiVarF = new dz(activity);
            } else {
                eiVarF = sK.f(activity);
            }
            return eiVarF;
        } catch (a e) {
            gs.W(e.getMessage());
            return null;
        }
    }

    private ei f(Activity activity) {
        try {
            return ei.a.u(L(activity).b(com.google.android.gms.dynamic.e.k(activity)));
        } catch (RemoteException e) {
            gs.d("Could not create remote InAppPurchaseManager.", e);
            return null;
        } catch (g.a e2) {
            gs.d("Could not create remote InAppPurchaseManager.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.g
    /* renamed from: y, reason: merged with bridge method [inline-methods] */
    public ej d(IBinder iBinder) {
        return ej.a.v(iBinder);
    }
}
