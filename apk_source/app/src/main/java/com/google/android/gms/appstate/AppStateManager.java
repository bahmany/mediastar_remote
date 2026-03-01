package com.google.android.gms.appstate;

import android.content.Context;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.ib;

/* loaded from: classes.dex */
public final class AppStateManager {
    static final Api.c<ib> CU = new Api.c<>();
    private static final Api.b<ib, Api.ApiOptions.NoOptions> CV = new Api.b<ib, Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.appstate.AppStateManager.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: b */
        public ib a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new ib(context, looper, connectionCallbacks, onConnectionFailedListener, clientSettings.getAccountNameOrDefault(), (String[]) clientSettings.getScopes().toArray(new String[0]));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Scope SCOPE_APP_STATE = new Scope(Scopes.APP_STATE);
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(CV, CU, SCOPE_APP_STATE);

    /* renamed from: com.google.android.gms.appstate.AppStateManager$1 */
    static class AnonymousClass1 implements Api.b<ib, Api.ApiOptions.NoOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        /* renamed from: b */
        public ib a(Context context, Looper looper, ClientSettings clientSettings, Api.ApiOptions.NoOptions noOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new ib(context, looper, connectionCallbacks, onConnectionFailedListener, clientSettings.getAccountNameOrDefault(), (String[]) clientSettings.getScopes().toArray(new String[0]));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$2 */
    static class AnonymousClass2 implements StateResult {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateResult
        public StateConflictResult getConflictResult() {
            return null;
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateResult
        public StateLoadedResult getLoadedResult() {
            return null;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return status;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() {
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$3 */
    static class AnonymousClass3 extends e {
        final /* synthetic */ int CX;
        final /* synthetic */ byte[] CY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(int i, byte[] bArr) {
            super();
            i = i;
            bArr = bArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.a((BaseImplementation.b<StateResult>) null, i, bArr);
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$4 */
    static class AnonymousClass4 extends e {
        final /* synthetic */ int CX;
        final /* synthetic */ byte[] CY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass4(int i, byte[] bArr) {
            super();
            i = i;
            bArr = bArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.a(this, i, bArr);
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$5 */
    static class AnonymousClass5 extends b {
        final /* synthetic */ int CX;

        /* renamed from: com.google.android.gms.appstate.AppStateManager$5$1 */
        class AnonymousClass1 implements StateDeletedResult {
            final /* synthetic */ Status CW;

            AnonymousClass1(Status status) {
                status = status;
            }

            @Override // com.google.android.gms.appstate.AppStateManager.StateDeletedResult
            public int getStateKey() {
                return i;
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass5(int i) {
            super();
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.a(this, i);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: g */
        public StateDeletedResult c(Status status) {
            return new StateDeletedResult() { // from class: com.google.android.gms.appstate.AppStateManager.5.1
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.appstate.AppStateManager.StateDeletedResult
                public int getStateKey() {
                    return i;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$6 */
    static class AnonymousClass6 extends e {
        final /* synthetic */ int CX;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass6(int i) {
            super();
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.b(this, i);
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$7 */
    static class AnonymousClass7 extends c {
        AnonymousClass7() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.a(this);
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$8 */
    static class AnonymousClass8 extends e {
        final /* synthetic */ int CX;
        final /* synthetic */ String Da;
        final /* synthetic */ byte[] Db;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass8(int i, String str, byte[] bArr) {
            super();
            i = i;
            str = str;
            bArr = bArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.a(this, i, str, bArr);
        }
    }

    /* renamed from: com.google.android.gms.appstate.AppStateManager$9 */
    static class AnonymousClass9 extends d {
        AnonymousClass9() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ib ibVar) {
            ibVar.b(this);
        }
    }

    public interface StateConflictResult extends Releasable, Result {
        byte[] getLocalData();

        String getResolvedVersion();

        byte[] getServerData();

        int getStateKey();
    }

    public interface StateDeletedResult extends Result {
        int getStateKey();
    }

    public interface StateListResult extends Result {
        AppStateBuffer getStateBuffer();
    }

    public interface StateLoadedResult extends Releasable, Result {
        byte[] getLocalData();

        int getStateKey();
    }

    public interface StateResult extends Releasable, Result {
        StateConflictResult getConflictResult();

        StateLoadedResult getLoadedResult();
    }

    public static abstract class a<R extends Result> extends BaseImplementation.a<R, ib> {
        public a() {
            super(AppStateManager.CU);
        }
    }

    private static abstract class b extends a<StateDeletedResult> {
        private b() {
        }

        /* synthetic */ b(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private static abstract class c extends a<StateListResult> {

        /* renamed from: com.google.android.gms.appstate.AppStateManager$c$1 */
        class AnonymousClass1 implements StateListResult {
            final /* synthetic */ Status CW;

            AnonymousClass1(Status status) {
                status = status;
            }

            @Override // com.google.android.gms.appstate.AppStateManager.StateListResult
            public AppStateBuffer getStateBuffer() {
                return new AppStateBuffer(null);
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
            }
        }

        private c() {
        }

        /* synthetic */ c(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: h */
        public StateListResult c(Status status) {
            return new StateListResult() { // from class: com.google.android.gms.appstate.AppStateManager.c.1
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.appstate.AppStateManager.StateListResult
                public AppStateBuffer getStateBuffer() {
                    return new AppStateBuffer(null);
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    private static abstract class d extends a<Status> {
        private d() {
        }

        /* synthetic */ d(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private static abstract class e extends a<StateResult> {
        private e() {
        }

        /* synthetic */ e(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: i */
        public StateResult c(Status status) {
            return AppStateManager.e(status);
        }
    }

    private AppStateManager() {
    }

    public static ib a(GoogleApiClient googleApiClient) {
        n.b(googleApiClient != null, "GoogleApiClient parameter is required.");
        n.a(googleApiClient.isConnected(), "GoogleApiClient must be connected.");
        ib ibVar = (ib) googleApiClient.a(CU);
        n.a(ibVar != null, "GoogleApiClient is not configured to use the AppState API. Pass AppStateManager.API into GoogleApiClient.Builder#addApi() to use this feature.");
        return ibVar;
    }

    public static PendingResult<StateDeletedResult> delete(GoogleApiClient googleApiClient, int stateKey) {
        return googleApiClient.b(new b() { // from class: com.google.android.gms.appstate.AppStateManager.5
            final /* synthetic */ int CX;

            /* renamed from: com.google.android.gms.appstate.AppStateManager$5$1 */
            class AnonymousClass1 implements StateDeletedResult {
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.appstate.AppStateManager.StateDeletedResult
                public int getStateKey() {
                    return i;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass5(int stateKey2) {
                super();
                i = stateKey2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.a(this, i);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: g */
            public StateDeletedResult c(Status status2) {
                return new StateDeletedResult() { // from class: com.google.android.gms.appstate.AppStateManager.5.1
                    final /* synthetic */ Status CW;

                    AnonymousClass1(Status status22) {
                        status = status22;
                    }

                    @Override // com.google.android.gms.appstate.AppStateManager.StateDeletedResult
                    public int getStateKey() {
                        return i;
                    }

                    @Override // com.google.android.gms.common.api.Result
                    public Status getStatus() {
                        return status;
                    }
                };
            }
        });
    }

    public static StateResult e(Status status) {
        return new StateResult() { // from class: com.google.android.gms.appstate.AppStateManager.2
            AnonymousClass2() {
            }

            @Override // com.google.android.gms.appstate.AppStateManager.StateResult
            public StateConflictResult getConflictResult() {
                return null;
            }

            @Override // com.google.android.gms.appstate.AppStateManager.StateResult
            public StateLoadedResult getLoadedResult() {
                return null;
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
            }

            @Override // com.google.android.gms.common.api.Releasable
            public void release() {
            }
        };
    }

    public static int getMaxNumKeys(GoogleApiClient googleApiClient) {
        return a(googleApiClient).fs();
    }

    public static int getMaxStateSize(GoogleApiClient googleApiClient) {
        return a(googleApiClient).fr();
    }

    public static PendingResult<StateListResult> list(GoogleApiClient googleApiClient) {
        return googleApiClient.a((GoogleApiClient) new c() { // from class: com.google.android.gms.appstate.AppStateManager.7
            AnonymousClass7() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.a(this);
            }
        });
    }

    public static PendingResult<StateResult> load(GoogleApiClient googleApiClient, int stateKey) {
        return googleApiClient.a((GoogleApiClient) new e() { // from class: com.google.android.gms.appstate.AppStateManager.6
            final /* synthetic */ int CX;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass6(int stateKey2) {
                super();
                i = stateKey2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.b(this, i);
            }
        });
    }

    public static PendingResult<StateResult> resolve(GoogleApiClient googleApiClient, int stateKey, String resolvedVersion, byte[] resolvedData) {
        return googleApiClient.b(new e() { // from class: com.google.android.gms.appstate.AppStateManager.8
            final /* synthetic */ int CX;
            final /* synthetic */ String Da;
            final /* synthetic */ byte[] Db;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass8(int stateKey2, String resolvedVersion2, byte[] resolvedData2) {
                super();
                i = stateKey2;
                str = resolvedVersion2;
                bArr = resolvedData2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.a(this, i, str, bArr);
            }
        });
    }

    public static PendingResult<Status> signOut(GoogleApiClient googleApiClient) {
        return googleApiClient.b(new d() { // from class: com.google.android.gms.appstate.AppStateManager.9
            AnonymousClass9() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.b(this);
            }
        });
    }

    public static void update(GoogleApiClient googleApiClient, int stateKey, byte[] data) {
        googleApiClient.b(new e() { // from class: com.google.android.gms.appstate.AppStateManager.3
            final /* synthetic */ int CX;
            final /* synthetic */ byte[] CY;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass3(int stateKey2, byte[] data2) {
                super();
                i = stateKey2;
                bArr = data2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.a((BaseImplementation.b<StateResult>) null, i, bArr);
            }
        });
    }

    public static PendingResult<StateResult> updateImmediate(GoogleApiClient googleApiClient, int stateKey, byte[] data) {
        return googleApiClient.b(new e() { // from class: com.google.android.gms.appstate.AppStateManager.4
            final /* synthetic */ int CX;
            final /* synthetic */ byte[] CY;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass4(int stateKey2, byte[] data2) {
                super();
                i = stateKey2;
                bArr = data2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ib ibVar) {
                ibVar.a(this, i, bArr);
            }
        });
    }
}
