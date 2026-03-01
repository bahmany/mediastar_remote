package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;

/* loaded from: classes.dex */
public final class hd {
    public static final Api.c<hy> BN = new Api.c<>();
    private static final Api.b<hy, Api.ApiOptions.NoOptions> BO = new Api.b<hy, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.internal.hd.1
        @Override // com.google.android.gms.common.api.Api.b
        public hy a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new hy(context, looper, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<Api.ApiOptions.NoOptions> BP = new Api<>(BO, BN, new Scope[0]);
    public static final hu BQ = new hz();
}
