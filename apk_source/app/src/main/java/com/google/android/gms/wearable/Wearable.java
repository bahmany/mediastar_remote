package com.google.android.gms.wearable;

import android.content.Context;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.wearable.internal.ag;
import com.google.android.gms.wearable.internal.aj;
import com.google.android.gms.wearable.internal.aw;
import com.google.android.gms.wearable.internal.f;

/* loaded from: classes.dex */
public class Wearable {
    public static final DataApi DataApi = new f();
    public static final MessageApi MessageApi = new ag();
    public static final NodeApi NodeApi = new aj();
    public static final b auQ = new com.google.android.gms.wearable.internal.e();
    public static final Api.c<aw> CU = new Api.c<>();
    private static final Api.b<aw, WearableOptions> CV = new Api.b<aw, WearableOptions>() { // from class: com.google.android.gms.wearable.Wearable.1
        @Override // com.google.android.gms.common.api.Api.b
        public aw a(Context context, Looper looper, ClientSettings clientSettings, WearableOptions wearableOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            if (wearableOptions == null) {
                new WearableOptions(new WearableOptions.Builder());
            }
            return new aw(context, looper, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<WearableOptions> API = new Api<>(CV, CU, new Scope[0]);

    public static final class WearableOptions implements Api.ApiOptions.Optional {

        public static class Builder {
            public WearableOptions build() {
                return new WearableOptions(this);
            }
        }

        private WearableOptions(Builder builder) {
        }
    }

    private Wearable() {
    }
}
