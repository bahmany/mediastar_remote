package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;

/* loaded from: classes.dex */
public final class no implements Account {

    /* renamed from: com.google.android.gms.internal.no$1 */
    class AnonymousClass1 extends a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.m(this);
        }
    }

    private static abstract class a extends Plus.a<Status> {
        private a() {
        }

        /* synthetic */ a(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private static com.google.android.gms.plus.internal.e a(GoogleApiClient googleApiClient, Api.c<com.google.android.gms.plus.internal.e> cVar) {
        com.google.android.gms.common.internal.n.b(googleApiClient != null, "GoogleApiClient parameter is required.");
        com.google.android.gms.common.internal.n.a(googleApiClient.isConnected(), "GoogleApiClient must be connected.");
        com.google.android.gms.plus.internal.e eVar = (com.google.android.gms.plus.internal.e) googleApiClient.a(cVar);
        com.google.android.gms.common.internal.n.a(eVar != null, "GoogleApiClient is not configured to use the Plus.API Api. Pass this into GoogleApiClient.Builder#addApi() to use this feature.");
        return eVar;
    }

    @Override // com.google.android.gms.plus.Account
    public void clearDefaultAccount(GoogleApiClient googleApiClient) {
        a(googleApiClient, Plus.CU).clearDefaultAccount();
    }

    @Override // com.google.android.gms.plus.Account
    public String getAccountName(GoogleApiClient googleApiClient) {
        return a(googleApiClient, Plus.CU).getAccountName();
    }

    @Override // com.google.android.gms.plus.Account
    public PendingResult<Status> revokeAccessAndDisconnect(GoogleApiClient googleApiClient) {
        return googleApiClient.b(new a() { // from class: com.google.android.gms.internal.no.1
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.m(this);
            }
        });
    }
}
