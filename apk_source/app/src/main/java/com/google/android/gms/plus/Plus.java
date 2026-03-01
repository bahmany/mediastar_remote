package com.google.android.gms.plus;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.no;
import com.google.android.gms.internal.np;
import com.google.android.gms.internal.nq;
import com.google.android.gms.internal.nr;
import com.google.android.gms.internal.ns;
import com.google.android.gms.plus.internal.PlusCommonExtras;
import com.google.android.gms.plus.internal.e;
import com.google.android.gms.plus.internal.h;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class Plus {
    public static final Api.c<e> CU = new Api.c<>();
    static final Api.b<e, PlusOptions> CV = new Api.b<e, PlusOptions>() { // from class: com.google.android.gms.plus.Plus.1
        @Override // com.google.android.gms.common.api.Api.b
        public e a(Context context, Looper looper, ClientSettings clientSettings, PlusOptions plusOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            if (plusOptions == null) {
                plusOptions = new PlusOptions();
            }
            return new e(context, looper, connectionCallbacks, onConnectionFailedListener, new h(clientSettings.getAccountNameOrDefault(), clientSettings.getScopesArray(), (String[]) plusOptions.akR.toArray(new String[0]), new String[0], context.getPackageName(), context.getPackageName(), null, new PlusCommonExtras()));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return 2;
        }
    };
    public static final Api<PlusOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static final Scope SCOPE_PLUS_LOGIN = new Scope(Scopes.PLUS_LOGIN);
    public static final Scope SCOPE_PLUS_PROFILE = new Scope(Scopes.PLUS_ME);
    public static final Moments MomentsApi = new nr();
    public static final People PeopleApi = new ns();
    public static final Account AccountApi = new no();
    public static final b akO = new nq();
    public static final com.google.android.gms.plus.a akP = new np();

    public static final class PlusOptions implements Api.ApiOptions.Optional {
        final String akQ;
        final Set<String> akR;

        public static final class Builder {
            String akQ;
            final Set<String> akR = new HashSet();

            public Builder addActivityTypes(String... activityTypes) {
                n.b(activityTypes, "activityTypes may not be null.");
                for (String str : activityTypes) {
                    this.akR.add(str);
                }
                return this;
            }

            public PlusOptions build() {
                return new PlusOptions(this);
            }

            public Builder setServerClientId(String clientId) {
                this.akQ = clientId;
                return this;
            }
        }

        private PlusOptions() {
            this.akQ = null;
            this.akR = new HashSet();
        }

        private PlusOptions(Builder builder) {
            this.akQ = builder.akQ;
            this.akR = builder.akR;
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    public static abstract class a<R extends Result> extends BaseImplementation.a<R, e> {
        public a() {
            super(Plus.CU);
        }
    }

    private Plus() {
    }

    public static e a(GoogleApiClient googleApiClient, Api.c<e> cVar) {
        n.b(googleApiClient != null, "GoogleApiClient parameter is required.");
        n.a(googleApiClient.isConnected(), "GoogleApiClient must be connected.");
        e eVar = (e) googleApiClient.a(cVar);
        n.a(eVar != null, "GoogleApiClient is not configured to use the Plus.API Api. Pass this into GoogleApiClient.Builder#addApi() to use this feature.");
        return eVar;
    }
}
