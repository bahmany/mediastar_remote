package com.google.android.gms.panorama;

import android.content.Context;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.internal.nb;
import com.google.android.gms.internal.nc;

/* loaded from: classes.dex */
public final class Panorama {
    public static final Api.c<nc> CU = new Api.c<>();
    static final Api.b<nc, Api.ApiOptions.NoOptions> CV = new Api.b<nc, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.panorama.Panorama.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: e */
        public nc a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new nc(context, looper, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static final PanoramaApi PanoramaApi = new nb();

    /* renamed from: com.google.android.gms.panorama.Panorama$1 */
    static class AnonymousClass1 implements Api.b<nc, Api.ApiOptions.NoOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: e */
        public nc a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new nc(context, looper, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    private Panorama() {
    }
}
