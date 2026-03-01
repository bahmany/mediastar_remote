package com.google.android.gms.internal;

import android.accounts.Account;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.os;
import com.google.android.gms.internal.ov;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.WalletConstants;

/* loaded from: classes.dex */
public class ox extends com.google.android.gms.common.internal.d<os> {
    private final String Dd;
    private final int atA;
    private final int mTheme;
    private final Activity nr;

    private static class a extends ov.a {
        private a() {
        }

        @Override // com.google.android.gms.internal.ov
        public void a(int i, FullWallet fullWallet, Bundle bundle) {
        }

        @Override // com.google.android.gms.internal.ov
        public void a(int i, MaskedWallet maskedWallet, Bundle bundle) {
        }

        @Override // com.google.android.gms.internal.ov
        public void a(int i, boolean z, Bundle bundle) {
        }

        @Override // com.google.android.gms.internal.ov
        public void a(Status status, oo ooVar, Bundle bundle) {
        }

        @Override // com.google.android.gms.internal.ov
        public void i(int i, Bundle bundle) {
        }
    }

    final class b extends a {
        private final int Lm;

        public b(int i) {
            super();
            this.Lm = i;
        }

        @Override // com.google.android.gms.internal.ox.a, com.google.android.gms.internal.ov
        public void a(int i, FullWallet fullWallet, Bundle bundle) throws PendingIntent.CanceledException {
            int i2;
            ConnectionResult connectionResult = new ConnectionResult(i, bundle != null ? (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT") : null);
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(ox.this.nr, this.Lm);
                    return;
                } catch (IntentSender.SendIntentException e) {
                    Log.w("WalletClientImpl", "Exception starting pending intent", e);
                    return;
                }
            }
            Intent intent = new Intent();
            if (connectionResult.isSuccess()) {
                i2 = -1;
                intent.putExtra(WalletConstants.EXTRA_FULL_WALLET, fullWallet);
            } else {
                i2 = i == 408 ? 0 : 1;
                intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, i);
            }
            PendingIntent pendingIntentCreatePendingResult = ox.this.nr.createPendingResult(this.Lm, intent, 1073741824);
            if (pendingIntentCreatePendingResult == null) {
                Log.w("WalletClientImpl", "Null pending result returned for onFullWalletLoaded");
                return;
            }
            try {
                pendingIntentCreatePendingResult.send(i2);
            } catch (PendingIntent.CanceledException e2) {
                Log.w("WalletClientImpl", "Exception setting pending result", e2);
            }
        }

        @Override // com.google.android.gms.internal.ox.a, com.google.android.gms.internal.ov
        public void a(int i, MaskedWallet maskedWallet, Bundle bundle) throws PendingIntent.CanceledException {
            int i2;
            ConnectionResult connectionResult = new ConnectionResult(i, bundle != null ? (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT") : null);
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(ox.this.nr, this.Lm);
                    return;
                } catch (IntentSender.SendIntentException e) {
                    Log.w("WalletClientImpl", "Exception starting pending intent", e);
                    return;
                }
            }
            Intent intent = new Intent();
            if (connectionResult.isSuccess()) {
                i2 = -1;
                intent.putExtra(WalletConstants.EXTRA_MASKED_WALLET, maskedWallet);
            } else {
                i2 = i == 408 ? 0 : 1;
                intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, i);
            }
            PendingIntent pendingIntentCreatePendingResult = ox.this.nr.createPendingResult(this.Lm, intent, 1073741824);
            if (pendingIntentCreatePendingResult == null) {
                Log.w("WalletClientImpl", "Null pending result returned for onMaskedWalletLoaded");
                return;
            }
            try {
                pendingIntentCreatePendingResult.send(i2);
            } catch (PendingIntent.CanceledException e2) {
                Log.w("WalletClientImpl", "Exception setting pending result", e2);
            }
        }

        @Override // com.google.android.gms.internal.ox.a, com.google.android.gms.internal.ov
        public void a(int i, boolean z, Bundle bundle) throws PendingIntent.CanceledException {
            Intent intent = new Intent();
            intent.putExtra(WalletConstants.EXTRA_IS_USER_PREAUTHORIZED, z);
            PendingIntent pendingIntentCreatePendingResult = ox.this.nr.createPendingResult(this.Lm, intent, 1073741824);
            if (pendingIntentCreatePendingResult == null) {
                Log.w("WalletClientImpl", "Null pending result returned for onPreAuthorizationDetermined");
                return;
            }
            try {
                pendingIntentCreatePendingResult.send(-1);
            } catch (PendingIntent.CanceledException e) {
                Log.w("WalletClientImpl", "Exception setting pending result", e);
            }
        }

        @Override // com.google.android.gms.internal.ox.a, com.google.android.gms.internal.ov
        public void i(int i, Bundle bundle) throws PendingIntent.CanceledException {
            com.google.android.gms.common.internal.n.b(bundle, "Bundle should not be null");
            ConnectionResult connectionResult = new ConnectionResult(i, (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT"));
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(ox.this.nr, this.Lm);
                    return;
                } catch (IntentSender.SendIntentException e) {
                    Log.w("WalletClientImpl", "Exception starting pending intent", e);
                    return;
                }
            }
            Log.e("WalletClientImpl", "Create Wallet Objects confirmation UI will not be shown connection result: " + connectionResult);
            Intent intent = new Intent();
            intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, 413);
            PendingIntent pendingIntentCreatePendingResult = ox.this.nr.createPendingResult(this.Lm, intent, 1073741824);
            if (pendingIntentCreatePendingResult == null) {
                Log.w("WalletClientImpl", "Null pending result returned for onWalletObjectsCreated");
                return;
            }
            try {
                pendingIntentCreatePendingResult.send(1);
            } catch (PendingIntent.CanceledException e2) {
                Log.w("WalletClientImpl", "Exception setting pending result", e2);
            }
        }
    }

    public ox(Activity activity, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, int i, String str, int i2) {
        super(activity, looper, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.nr = activity;
        this.atA = i;
        this.Dd = str;
        this.mTheme = i2;
    }

    public static Bundle a(int i, String str, String str2, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt("com.google.android.gms.wallet.EXTRA_ENVIRONMENT", i);
        bundle.putString("androidPackageName", str);
        if (!TextUtils.isEmpty(str2)) {
            bundle.putParcelable("com.google.android.gms.wallet.EXTRA_BUYER_ACCOUNT", new Account(str2, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE));
        }
        bundle.putInt("com.google.android.gms.wallet.EXTRA_THEME", i2);
        return bundle;
    }

    private Bundle pM() {
        return a(this.atA, this.nr.getPackageName(), this.Dd, this.mTheme);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.k(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName());
    }

    public void a(FullWalletRequest fullWalletRequest, int i) throws PendingIntent.CanceledException {
        b bVar = new b(i);
        try {
            gS().a(fullWalletRequest, pM(), bVar);
        } catch (RemoteException e) {
            Log.e("WalletClientImpl", "RemoteException getting full wallet", e);
            bVar.a(8, (FullWallet) null, Bundle.EMPTY);
        }
    }

    public void a(MaskedWalletRequest maskedWalletRequest, int i) throws PendingIntent.CanceledException {
        Bundle bundlePM = pM();
        b bVar = new b(i);
        try {
            gS().a(maskedWalletRequest, bundlePM, bVar);
        } catch (RemoteException e) {
            Log.e("WalletClientImpl", "RemoteException getting masked wallet", e);
            bVar.a(8, (MaskedWallet) null, Bundle.EMPTY);
        }
    }

    public void a(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        try {
            gS().a(notifyTransactionStatusRequest, pM());
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: bP, reason: merged with bridge method [inline-methods] */
    public os j(IBinder iBinder) {
        return os.a.bL(iBinder);
    }

    public void d(String str, String str2, int i) throws PendingIntent.CanceledException {
        Bundle bundlePM = pM();
        b bVar = new b(i);
        try {
            gS().a(str, str2, bundlePM, bVar);
        } catch (RemoteException e) {
            Log.e("WalletClientImpl", "RemoteException changing masked wallet", e);
            bVar.a(8, (MaskedWallet) null, Bundle.EMPTY);
        }
    }

    public void fH(int i) throws PendingIntent.CanceledException {
        Bundle bundlePM = pM();
        b bVar = new b(i);
        try {
            gS().a(bundlePM, bVar);
        } catch (RemoteException e) {
            Log.e("WalletClientImpl", "RemoteException during checkForPreAuthorization", e);
            bVar.a(8, false, Bundle.EMPTY);
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.wallet.internal.IOwService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.wallet.service.BIND";
    }
}
