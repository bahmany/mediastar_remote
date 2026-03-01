package com.google.android.gms.fitness;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.kk;
import com.google.android.gms.internal.ku;
import com.google.android.gms.internal.kw;
import com.google.android.gms.internal.kx;
import com.google.android.gms.internal.ky;
import com.google.android.gms.internal.kz;
import com.google.android.gms.internal.la;
import com.google.android.gms.internal.lb;
import com.google.android.gms.internal.lc;
import com.google.android.gms.internal.ld;

/* loaded from: classes.dex */
public class Fitness {
    public static final Api.c<kj> CU = new Api.c<>();
    private static final Api.b<kj, Api.ApiOptions.NoOptions> CV = new Api.b<kj, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.fitness.Fitness.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: c */
        public kj a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new kk(context, looper, connectionCallbacks, onConnectionFailedListener, clientSettings.getAccountNameOrDefault(), FitnessScopes.d(clientSettings.getScopes()));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static final SensorsApi SensorsApi = new lb();
    public static final RecordingApi RecordingApi = new la();
    public static final SessionsApi SessionsApi = new lc();
    public static final HistoryApi HistoryApi = new ky();
    public static final ConfigApi ConfigApi = new kx();
    public static final BleApi BleApi = iy();
    public static final ku Sf = new kz();

    /* renamed from: com.google.android.gms.fitness.Fitness$1 */
    static class AnonymousClass1 implements Api.b<kj, Api.ApiOptions.NoOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: c */
        public kj a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new kk(context, looper, connectionCallbacks, onConnectionFailedListener, clientSettings.getAccountNameOrDefault(), FitnessScopes.d(clientSettings.getScopes()));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    private Fitness() {
    }

    private static BleApi iy() {
        return Build.VERSION.SDK_INT >= 18 ? new kw() : new ld();
    }
}
