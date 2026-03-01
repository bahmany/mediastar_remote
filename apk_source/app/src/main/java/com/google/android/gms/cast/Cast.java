package com.google.android.gms.cast;

import android.content.Context;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.internal.widget.ActivityChooserView;
import android.text.TextUtils;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.ij;
import java.io.IOException;

/* loaded from: classes.dex */
public final class Cast {
    public static final String EXTRA_APP_NO_LONGER_RUNNING = "com.google.android.gms.cast.EXTRA_APP_NO_LONGER_RUNNING";
    public static final int MAX_MESSAGE_LENGTH = 65536;
    public static final int MAX_NAMESPACE_LENGTH = 128;
    static final Api.c<ij> CU = new Api.c<>();
    private static final Api.b<ij, CastOptions> CV = new Api.b<ij, CastOptions>() { // from class: com.google.android.gms.cast.Cast.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        public ij a(Context context, Looper looper, ClientSettings clientSettings, CastOptions castOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            n.b(castOptions, "Setting the API options is required.");
            return new ij(context, looper, castOptions.EK, castOptions.EM, castOptions.EL, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    };
    public static final Api<CastOptions> API = new Api<>(CV, CU, new Scope[0]);
    public static final CastApi CastApi = new CastApi.a();

    /* renamed from: com.google.android.gms.cast.Cast$1 */
    static class AnonymousClass1 implements Api.b<ij, CastOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        public ij a(Context context, Looper looper, ClientSettings clientSettings, CastOptions castOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            n.b(castOptions, "Setting the API options is required.");
            return new ij(context, looper, castOptions.EK, castOptions.EM, castOptions.EL, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    public interface ApplicationConnectionResult extends Result {
        ApplicationMetadata getApplicationMetadata();

        String getApplicationStatus();

        String getSessionId();

        boolean getWasLaunched();
    }

    public interface CastApi {

        public static final class a implements CastApi {

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$1 */
            class AnonymousClass1 extends b {
                final /* synthetic */ String EE;
                final /* synthetic */ String EF;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass1(String str, String str2) {
                    super();
                    str = str;
                    str = str2;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.a(str, str, this);
                    } catch (IllegalArgumentException e) {
                        V(2001);
                    } catch (IllegalStateException e2) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$2 */
            class AnonymousClass2 extends c {
                final /* synthetic */ String EH;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass2(String str) {
                    super();
                    str = str;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.a(str, false, (BaseImplementation.b<ApplicationConnectionResult>) this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$3 */
            class AnonymousClass3 extends c {
                final /* synthetic */ String EH;
                final /* synthetic */ LaunchOptions EI;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass3(String str, LaunchOptions launchOptions) {
                    super();
                    str = str;
                    launchOptions = launchOptions;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.a(str, launchOptions, this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$4 */
            class AnonymousClass4 extends c {
                final /* synthetic */ String EH;
                final /* synthetic */ String EJ;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass4(String str, String str2) {
                    super();
                    str = str;
                    str = str2;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.b(str, str, this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$5 */
            class AnonymousClass5 extends c {
                final /* synthetic */ String EH;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass5(String str) {
                    super();
                    str = str;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.b(str, null, this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$6 */
            class AnonymousClass6 extends c {
                AnonymousClass6() {
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.b(null, null, this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$7 */
            class AnonymousClass7 extends b {
                AnonymousClass7() {
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.d(this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$8 */
            class AnonymousClass8 extends b {
                AnonymousClass8() {
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    try {
                        ijVar.a("", this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            /* renamed from: com.google.android.gms.cast.Cast$CastApi$a$9 */
            class AnonymousClass9 extends b {
                final /* synthetic */ String EJ;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass9(String str) {
                    super();
                    str = str;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(ij ijVar) throws RemoteException {
                    if (TextUtils.isEmpty(str)) {
                        e(2001, "IllegalArgument: sessionId cannot be null or empty");
                        return;
                    }
                    try {
                        ijVar.a(str, this);
                    } catch (IllegalStateException e) {
                        V(2001);
                    }
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public ApplicationMetadata getApplicationMetadata(GoogleApiClient client) throws IllegalStateException {
                return ((ij) client.a(Cast.CU)).getApplicationMetadata();
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public String getApplicationStatus(GoogleApiClient client) throws IllegalStateException {
                return ((ij) client.a(Cast.CU)).getApplicationStatus();
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public double getVolume(GoogleApiClient client) throws IllegalStateException {
                return ((ij) client.a(Cast.CU)).fF();
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public boolean isMute(GoogleApiClient client) throws IllegalStateException {
                return ((ij) client.a(Cast.CU)).isMute();
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient client) {
                return client.b(new c() { // from class: com.google.android.gms.cast.Cast.CastApi.a.6
                    AnonymousClass6() {
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.b(null, null, this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient client, String applicationId) {
                return client.b(new c() { // from class: com.google.android.gms.cast.Cast.CastApi.a.5
                    final /* synthetic */ String EH;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass5(String applicationId2) {
                        super();
                        str = applicationId2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.b(str, null, this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient client, String applicationId, String sessionId) {
                return client.b(new c() { // from class: com.google.android.gms.cast.Cast.CastApi.a.4
                    final /* synthetic */ String EH;
                    final /* synthetic */ String EJ;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass4(String applicationId2, String sessionId2) {
                        super();
                        str = applicationId2;
                        str = sessionId2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.b(str, str, this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient client, String applicationId) {
                return client.b(new c() { // from class: com.google.android.gms.cast.Cast.CastApi.a.2
                    final /* synthetic */ String EH;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass2(String applicationId2) {
                        super();
                        str = applicationId2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.a(str, false, (BaseImplementation.b<ApplicationConnectionResult>) this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient client, String applicationId, LaunchOptions options) {
                return client.b(new c() { // from class: com.google.android.gms.cast.Cast.CastApi.a.3
                    final /* synthetic */ String EH;
                    final /* synthetic */ LaunchOptions EI;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass3(String applicationId2, LaunchOptions options2) {
                        super();
                        str = applicationId2;
                        launchOptions = options2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.a(str, launchOptions, this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            @Deprecated
            public PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient client, String applicationId, boolean relaunchIfRunning) {
                return launchApplication(client, applicationId, new LaunchOptions.Builder().setRelaunchIfRunning(relaunchIfRunning).build());
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<Status> leaveApplication(GoogleApiClient client) {
                return client.b(new b() { // from class: com.google.android.gms.cast.Cast.CastApi.a.7
                    AnonymousClass7() {
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.d(this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public void removeMessageReceivedCallbacks(GoogleApiClient client, String namespace) throws IOException, IllegalArgumentException {
                try {
                    ((ij) client.a(Cast.CU)).aE(namespace);
                } catch (RemoteException e) {
                    throw new IOException("service error");
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public void requestStatus(GoogleApiClient client) throws IllegalStateException, IOException {
                try {
                    ((ij) client.a(Cast.CU)).fE();
                } catch (RemoteException e) {
                    throw new IOException("service error");
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<Status> sendMessage(GoogleApiClient client, String namespace, String message) {
                return client.b(new b() { // from class: com.google.android.gms.cast.Cast.CastApi.a.1
                    final /* synthetic */ String EE;
                    final /* synthetic */ String EF;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass1(String namespace2, String message2) {
                        super();
                        str = namespace2;
                        str = message2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.a(str, str, this);
                        } catch (IllegalArgumentException e) {
                            V(2001);
                        } catch (IllegalStateException e2) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public void setMessageReceivedCallbacks(GoogleApiClient client, String namespace, MessageReceivedCallback callbacks) throws IllegalStateException, IOException, IllegalArgumentException {
                try {
                    ((ij) client.a(Cast.CU)).a(namespace, callbacks);
                } catch (RemoteException e) {
                    throw new IOException("service error");
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public void setMute(GoogleApiClient client, boolean mute) throws IllegalStateException, IOException {
                try {
                    ((ij) client.a(Cast.CU)).G(mute);
                } catch (RemoteException e) {
                    throw new IOException("service error");
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public void setVolume(GoogleApiClient client, double volume) throws IllegalStateException, IOException, IllegalArgumentException {
                try {
                    ((ij) client.a(Cast.CU)).a(volume);
                } catch (RemoteException e) {
                    throw new IOException("service error");
                }
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<Status> stopApplication(GoogleApiClient client) {
                return client.b(new b() { // from class: com.google.android.gms.cast.Cast.CastApi.a.8
                    AnonymousClass8() {
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        try {
                            ijVar.a("", this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }

            @Override // com.google.android.gms.cast.Cast.CastApi
            public PendingResult<Status> stopApplication(GoogleApiClient client, String sessionId) {
                return client.b(new b() { // from class: com.google.android.gms.cast.Cast.CastApi.a.9
                    final /* synthetic */ String EJ;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    AnonymousClass9(String sessionId2) {
                        super();
                        str = sessionId2;
                    }

                    @Override // com.google.android.gms.common.api.BaseImplementation.a
                    public void a(ij ijVar) throws RemoteException {
                        if (TextUtils.isEmpty(str)) {
                            e(2001, "IllegalArgument: sessionId cannot be null or empty");
                            return;
                        }
                        try {
                            ijVar.a(str, this);
                        } catch (IllegalStateException e) {
                            V(2001);
                        }
                    }
                });
            }
        }

        ApplicationMetadata getApplicationMetadata(GoogleApiClient googleApiClient) throws IllegalStateException;

        String getApplicationStatus(GoogleApiClient googleApiClient) throws IllegalStateException;

        double getVolume(GoogleApiClient googleApiClient) throws IllegalStateException;

        boolean isMute(GoogleApiClient googleApiClient) throws IllegalStateException;

        PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient googleApiClient);

        PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient googleApiClient, String str);

        PendingResult<ApplicationConnectionResult> joinApplication(GoogleApiClient googleApiClient, String str, String str2);

        PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient googleApiClient, String str);

        PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient googleApiClient, String str, LaunchOptions launchOptions);

        @Deprecated
        PendingResult<ApplicationConnectionResult> launchApplication(GoogleApiClient googleApiClient, String str, boolean z);

        PendingResult<Status> leaveApplication(GoogleApiClient googleApiClient);

        void removeMessageReceivedCallbacks(GoogleApiClient googleApiClient, String str) throws IOException, IllegalArgumentException;

        void requestStatus(GoogleApiClient googleApiClient) throws IllegalStateException, IOException;

        PendingResult<Status> sendMessage(GoogleApiClient googleApiClient, String str, String str2);

        void setMessageReceivedCallbacks(GoogleApiClient googleApiClient, String str, MessageReceivedCallback messageReceivedCallback) throws IllegalStateException, IOException;

        void setMute(GoogleApiClient googleApiClient, boolean z) throws IllegalStateException, IOException;

        void setVolume(GoogleApiClient googleApiClient, double d) throws IllegalStateException, IOException, IllegalArgumentException;

        PendingResult<Status> stopApplication(GoogleApiClient googleApiClient);

        PendingResult<Status> stopApplication(GoogleApiClient googleApiClient, String str);
    }

    public static final class CastOptions implements Api.ApiOptions.HasOptions {
        final CastDevice EK;
        final Listener EL;
        private final int EM;

        public static final class Builder {
            CastDevice EN;
            Listener EO;
            private int EP;

            private Builder(CastDevice castDevice, Listener castListener) {
                n.b(castDevice, "CastDevice parameter cannot be null");
                n.b(castListener, "CastListener parameter cannot be null");
                this.EN = castDevice;
                this.EO = castListener;
                this.EP = 0;
            }

            /* synthetic */ Builder(CastDevice x0, Listener x1, AnonymousClass1 x2) {
                this(x0, x1);
            }

            public CastOptions build() {
                return new CastOptions(this);
            }

            public Builder setVerboseLoggingEnabled(boolean enabled) {
                if (enabled) {
                    this.EP |= 1;
                } else {
                    this.EP &= -2;
                }
                return this;
            }
        }

        private CastOptions(Builder builder) {
            this.EK = builder.EN;
            this.EL = builder.EO;
            this.EM = builder.EP;
        }

        /* synthetic */ CastOptions(Builder x0, AnonymousClass1 x1) {
            this(x0);
        }

        public static Builder builder(CastDevice castDevice, Listener castListener) {
            return new Builder(castDevice, castListener);
        }
    }

    public static class Listener {
        public void W(int i) {
        }

        public void X(int i) {
        }

        public void onApplicationDisconnected(int statusCode) {
        }

        public void onApplicationStatusChanged() {
        }

        public void onVolumeChanged() {
        }
    }

    public interface MessageReceivedCallback {
        void onMessageReceived(CastDevice castDevice, String str, String str2);
    }

    protected static abstract class a<R extends Result> extends BaseImplementation.a<R, ij> {
        public a() {
            super(Cast.CU);
        }

        public void V(int i) {
            b((a<R>) c(new Status(i)));
        }

        public void e(int i, String str) {
            b((a<R>) c(new Status(i, str, null)));
        }
    }

    private static abstract class b extends a<Status> {
        private b() {
        }

        /* synthetic */ b(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private static abstract class c extends a<ApplicationConnectionResult> {

        /* renamed from: com.google.android.gms.cast.Cast$c$1 */
        class AnonymousClass1 implements ApplicationConnectionResult {
            final /* synthetic */ Status CW;

            AnonymousClass1(Status status) {
                status = status;
            }

            @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
            public ApplicationMetadata getApplicationMetadata() {
                return null;
            }

            @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
            public String getApplicationStatus() {
                return null;
            }

            @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
            public String getSessionId() {
                return null;
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
            }

            @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
            public boolean getWasLaunched() {
                return false;
            }
        }

        private c() {
        }

        /* synthetic */ c(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: j */
        public ApplicationConnectionResult c(Status status) {
            return new ApplicationConnectionResult() { // from class: com.google.android.gms.cast.Cast.c.1
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
                public ApplicationMetadata getApplicationMetadata() {
                    return null;
                }

                @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
                public String getApplicationStatus() {
                    return null;
                }

                @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
                public String getSessionId() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }

                @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
                public boolean getWasLaunched() {
                    return false;
                }
            };
        }
    }

    private Cast() {
    }
}
