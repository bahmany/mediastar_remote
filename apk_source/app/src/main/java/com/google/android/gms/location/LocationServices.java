package com.google.android.gms.location;

import android.content.Context;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.lt;
import com.google.android.gms.internal.lu;
import com.google.android.gms.internal.ly;

/* loaded from: classes.dex */
public class LocationServices {
    private static final Api.c<ly> CU = new Api.c<>();
    private static final Api.b<ly, Api.ApiOptions.NoOptions> CV = new Api.b<ly, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.location.LocationServices.1
        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: d, reason: merged with bridge method [inline-methods] */
        public ly a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new ly(context, looper, context.getPackageName(), connectionCallbacks, onConnectionFailedListener, "locationServices", clientSettings.getAccountName());
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static FusedLocationProviderApi FusedLocationApi = new lt();
    public static GeofencingApi GeofencingApi = new lu();

    public static abstract class a<R extends Result> extends BaseImplementation.a<R, ly> {
        public a() {
            super(LocationServices.CU);
        }
    }

    private LocationServices() {
    }

    public static ly e(GoogleApiClient googleApiClient) {
        n.b(googleApiClient != null, "GoogleApiClient parameter is required.");
        ly lyVar = (ly) googleApiClient.a(CU);
        n.a(lyVar != null, "GoogleApiClient is not configured to use the LocationServices.API Api. Pass thisinto GoogleApiClient.Builder#addApi() to use this feature.");
        return lyVar;
    }
}
