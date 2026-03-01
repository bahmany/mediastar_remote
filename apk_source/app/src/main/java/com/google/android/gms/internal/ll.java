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
import com.google.android.gms.common.internal.d;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.identity.intents.UserAddressRequest;
import com.google.android.gms.internal.lm;
import com.google.android.gms.internal.ln;

/* loaded from: classes.dex */
public class ll extends com.google.android.gms.common.internal.d<ln> {
    private final String Dd;
    private a adB;
    private final int mTheme;
    private Activity nr;

    public static final class a extends lm.a {
        private final int Lm;
        private Activity nr;

        public a(int i, Activity activity) {
            this.Lm = i;
            this.nr = activity;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setActivity(Activity activity) {
            this.nr = activity;
        }

        @Override // com.google.android.gms.internal.lm
        public void g(int i, Bundle bundle) throws PendingIntent.CanceledException {
            if (i == 1) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                PendingIntent pendingIntentCreatePendingResult = this.nr.createPendingResult(this.Lm, intent, 1073741824);
                if (pendingIntentCreatePendingResult == null) {
                    return;
                }
                try {
                    pendingIntentCreatePendingResult.send(1);
                    return;
                } catch (PendingIntent.CanceledException e) {
                    Log.w("AddressClientImpl", "Exception settng pending result", e);
                    return;
                }
            }
            ConnectionResult connectionResult = new ConnectionResult(i, bundle != null ? (PendingIntent) bundle.getParcelable("com.google.android.gms.identity.intents.EXTRA_PENDING_INTENT") : null);
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this.nr, this.Lm);
                    return;
                } catch (IntentSender.SendIntentException e2) {
                    Log.w("AddressClientImpl", "Exception starting pending intent", e2);
                    return;
                }
            }
            try {
                PendingIntent pendingIntentCreatePendingResult2 = this.nr.createPendingResult(this.Lm, new Intent(), 1073741824);
                if (pendingIntentCreatePendingResult2 != null) {
                    pendingIntentCreatePendingResult2.send(1);
                }
            } catch (PendingIntent.CanceledException e3) {
                Log.w("AddressClientImpl", "Exception setting pending result", e3);
            }
        }
    }

    public ll(Activity activity, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str, int i) {
        super(activity, looper, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.Dd = str;
        this.nr = activity;
        this.mTheme = i;
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.d(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName());
    }

    public void a(UserAddressRequest userAddressRequest, int i) throws PendingIntent.CanceledException {
        lR();
        this.adB = new a(i, this.nr);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("com.google.android.gms.identity.intents.EXTRA_CALLING_PACKAGE_NAME", getContext().getPackageName());
            if (!TextUtils.isEmpty(this.Dd)) {
                bundle.putParcelable("com.google.android.gms.identity.intents.EXTRA_ACCOUNT", new Account(this.Dd, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE));
            }
            bundle.putInt("com.google.android.gms.identity.intents.EXTRA_THEME", this.mTheme);
            lQ().a(this.adB, userAddressRequest, bundle);
        } catch (RemoteException e) {
            Log.e("AddressClientImpl", "Exception requesting user address", e);
            Bundle bundle2 = new Bundle();
            bundle2.putInt(AddressConstants.Extras.EXTRA_ERROR_CODE, AddressConstants.ErrorCodes.ERROR_CODE_NO_APPLICABLE_ADDRESSES);
            this.adB.g(1, bundle2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: aF, reason: merged with bridge method [inline-methods] */
    public ln j(IBinder iBinder) {
        return ln.a.aH(iBinder);
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        super.disconnect();
        if (this.adB != null) {
            this.adB.setActivity(null);
            this.adB = null;
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.identity.intents.internal.IAddressService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.identity.service.BIND";
    }

    protected ln lQ() {
        return (ln) super.gS();
    }

    protected void lR() {
        super.dK();
    }
}
