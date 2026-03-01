package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public interface GoogleApiClient {

    public static final class Builder {
        private String Dd;
        private Looper IB;
        private final Set<String> IE;
        private int IF;
        private View IG;
        private String IH;
        private final Map<Api<?>, Api.ApiOptions> II;
        private FragmentActivity IJ;
        private int IK;
        private OnConnectionFailedListener IL;
        private final Set<ConnectionCallbacks> IM;
        private final Set<OnConnectionFailedListener> IN;
        private final Context mContext;

        public Builder(Context context) {
            this.IE = new HashSet();
            this.II = new HashMap();
            this.IK = -1;
            this.IM = new HashSet();
            this.IN = new HashSet();
            this.mContext = context;
            this.IB = context.getMainLooper();
            this.IH = context.getPackageName();
        }

        public Builder(Context context, ConnectionCallbacks connectedListener, OnConnectionFailedListener connectionFailedListener) {
            this(context);
            n.b(connectedListener, "Must provide a connected listener");
            this.IM.add(connectedListener);
            n.b(connectionFailedListener, "Must provide a connection failed listener");
            this.IN.add(connectionFailedListener);
        }

        private GoogleApiClient gm() {
            d dVarA = d.a(this.IJ);
            GoogleApiClient googleApiClientAk = dVarA.ak(this.IK);
            if (googleApiClientAk == null) {
                googleApiClientAk = new b(this.mContext.getApplicationContext(), this.IB, gl(), this.II, this.IM, this.IN, this.IK);
            }
            dVarA.a(this.IK, googleApiClientAk, this.IL);
            return googleApiClientAk;
        }

        public Builder addApi(Api<? extends Api.ApiOptions.NotRequiredOptions> api) {
            this.II.put(api, null);
            List<Scope> listGe = api.ge();
            int size = listGe.size();
            for (int i = 0; i < size; i++) {
                this.IE.add(listGe.get(i).gt());
            }
            return this;
        }

        public <O extends Api.ApiOptions.HasOptions> Builder addApi(Api<O> api, O options) {
            n.b(options, "Null options are not permitted for this Api");
            this.II.put(api, options);
            List<Scope> listGe = api.ge();
            int size = listGe.size();
            for (int i = 0; i < size; i++) {
                this.IE.add(listGe.get(i).gt());
            }
            return this;
        }

        public Builder addConnectionCallbacks(ConnectionCallbacks listener) {
            this.IM.add(listener);
            return this;
        }

        public Builder addOnConnectionFailedListener(OnConnectionFailedListener listener) {
            this.IN.add(listener);
            return this;
        }

        public Builder addScope(Scope scope) {
            this.IE.add(scope.gt());
            return this;
        }

        public GoogleApiClient build() {
            n.b(!this.II.isEmpty(), "must call addApi() to add at least one API");
            return this.IK >= 0 ? gm() : new b(this.mContext, this.IB, gl(), this.II, this.IM, this.IN, -1);
        }

        public Builder enableAutoManage(FragmentActivity fragmentActivity, int clientId, OnConnectionFailedListener unresolvedConnectionFailedListener) {
            n.b(clientId >= 0, "clientId must be non-negative");
            this.IK = clientId;
            this.IJ = (FragmentActivity) n.b(fragmentActivity, "Null activity is not permitted.");
            this.IL = unresolvedConnectionFailedListener;
            return this;
        }

        public ClientSettings gl() {
            return new ClientSettings(this.Dd, this.IE, this.IF, this.IG, this.IH);
        }

        public Builder setAccountName(String accountName) {
            this.Dd = accountName;
            return this;
        }

        public Builder setGravityForPopups(int gravityForPopups) {
            this.IF = gravityForPopups;
            return this;
        }

        public Builder setHandler(Handler handler) {
            n.b(handler, "Handler must not be null");
            this.IB = handler.getLooper();
            return this;
        }

        public Builder setViewForPopups(View viewForPopups) {
            this.IG = viewForPopups;
            return this;
        }

        public Builder useDefaultAccount() {
            return setAccountName("<<default account>>");
        }
    }

    public interface ConnectionCallbacks {
        public static final int CAUSE_NETWORK_LOST = 2;
        public static final int CAUSE_SERVICE_DISCONNECTED = 1;

        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface OnConnectionFailedListener extends GooglePlayServicesClient.OnConnectionFailedListener {
        @Override // com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    <C extends Api.a> C a(Api.c<C> cVar);

    <A extends Api.a, R extends Result, T extends BaseImplementation.a<R, A>> T a(T t);

    boolean a(Scope scope);

    <A extends Api.a, T extends BaseImplementation.a<? extends Result, A>> T b(T t);

    ConnectionResult blockingConnect();

    ConnectionResult blockingConnect(long j, TimeUnit timeUnit);

    <L> c<L> c(L l);

    void connect();

    void disconnect();

    Looper getLooper();

    boolean isConnected();

    boolean isConnecting();

    boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks);

    boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener);

    void reconnect();

    void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks);

    void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    void stopAutoManage(FragmentActivity fragmentActivity);

    void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks);

    void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);
}
