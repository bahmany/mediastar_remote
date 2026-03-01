package com.google.android.gms.internal;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.g;
import com.google.android.gms.internal.ot;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

/* loaded from: classes.dex */
public class oy extends com.google.android.gms.dynamic.g<ot> {
    private static oy aum;

    protected oy() {
        super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
    }

    public static oq a(Activity activity, com.google.android.gms.dynamic.c cVar, WalletFragmentOptions walletFragmentOptions, or orVar) throws GooglePlayServicesNotAvailableException, PackageManager.NameNotFoundException {
        int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (iIsGooglePlayServicesAvailable != 0) {
            throw new GooglePlayServicesNotAvailableException(iIsGooglePlayServicesAvailable);
        }
        try {
            return pN().L(activity).a(com.google.android.gms.dynamic.e.k(activity), cVar, walletFragmentOptions, orVar);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (g.a e2) {
            throw new RuntimeException(e2);
        }
    }

    private static oy pN() {
        if (aum == null) {
            aum = new oy();
        }
        return aum;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.g
    /* renamed from: bQ, reason: merged with bridge method [inline-methods] */
    public ot d(IBinder iBinder) {
        return ot.a.bM(iBinder);
    }
}
