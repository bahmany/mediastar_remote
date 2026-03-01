package com.google.android.gms.identity.intents;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.ll;

/* loaded from: classes.dex */
public final class Address {
    static final Api.c<ll> CU = new Api.c<>();
    private static final Api.b<ll, AddressOptions> CV = new Api.b<ll, AddressOptions>() { // from class: com.google.android.gms.identity.intents.Address.1
        @Override // com.google.android.gms.common.api.Api.b
        public ll a(Context context, Looper looper, ClientSettings clientSettings, AddressOptions addressOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            n.b(context instanceof Activity, "An Activity must be used for Address APIs");
            if (addressOptions == null) {
                addressOptions = new AddressOptions();
            }
            return new ll((Activity) context, looper, connectionCallbacks, onConnectionFailedListener, clientSettings.getAccountName(), addressOptions.theme);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<AddressOptions> API = new Api<>(CV, CU, new Scope[0]);

    public static final class AddressOptions implements Api.ApiOptions.HasOptions {
        public final int theme;

        public AddressOptions() {
            this.theme = 0;
        }

        public AddressOptions(int theme) {
            this.theme = theme;
        }
    }

    private static abstract class a extends BaseImplementation.a<Status, ll> {
        public a() {
            super(Address.CU);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d, reason: merged with bridge method [inline-methods] */
        public Status c(Status status) {
            return status;
        }
    }

    public static void requestUserAddress(GoogleApiClient googleApiClient, final UserAddressRequest request, final int requestCode) {
        googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.identity.intents.Address.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ll llVar) throws PendingIntent.CanceledException, RemoteException {
                llVar.a(request, requestCode);
                b((AnonymousClass2) Status.Jo);
            }
        });
    }
}
