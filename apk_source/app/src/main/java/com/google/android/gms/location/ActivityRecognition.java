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
import com.google.android.gms.internal.lq;
import com.google.android.gms.internal.ly;

/* loaded from: classes.dex */
public class ActivityRecognition {
    public static final String CLIENT_NAME = "activity_recognition";
    private static final Api.c<ly> CU = new Api.c<>();
    private static final Api.b<ly, Api.ApiOptions.NoOptions> CV = new Api.b<ly, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.location.ActivityRecognition.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: d */
        public ly a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new ly(context, looper, context.getPackageName(), connectionCallbacks, onConnectionFailedListener, ActivityRecognition.CLIENT_NAME);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static ActivityRecognitionApi ActivityRecognitionApi = new lq();

    /* renamed from: com.google.android.gms.location.ActivityRecognition$1 */
    static class AnonymousClass1 implements Api.b<ly, Api.ApiOptions.NoOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: d */
        public ly a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new ly(context, looper, context.getPackageName(), connectionCallbacks, onConnectionFailedListener, ActivityRecognition.CLIENT_NAME);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    public static abstract class a<R extends Result> extends BaseImplementation.a<R, ly> {
        public a() {
            super(ActivityRecognition.CU);
        }
    }

    private ActivityRecognition() {
    }
}
