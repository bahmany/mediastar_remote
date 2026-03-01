package com.google.android.gms.plus;

import android.content.Context;
import android.net.Uri;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Moments;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.internal.e;
import com.google.android.gms.plus.internal.i;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.moments.MomentBuffer;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import java.util.Collection;

@Deprecated
/* loaded from: classes.dex */
public class PlusClient implements GooglePlayServicesClient {
    final e akS;

    @Deprecated
    public static class Builder {
        private final GooglePlayServicesClient.ConnectionCallbacks akX;
        private final GooglePlayServicesClient.OnConnectionFailedListener akY;
        private final i akZ;
        private final Context mContext;

        public Builder(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener connectionFailedListener) {
            this.mContext = context;
            this.akX = connectionCallbacks;
            this.akY = connectionFailedListener;
            this.akZ = new i(this.mContext);
        }

        public PlusClient build() {
            return new PlusClient(new e(this.mContext, this.akX, this.akY, this.akZ.no()));
        }

        public Builder clearScopes() {
            this.akZ.nn();
            return this;
        }

        public Builder setAccountName(String accountName) {
            this.akZ.ce(accountName);
            return this;
        }

        public Builder setActions(String... actions) {
            this.akZ.h(actions);
            return this;
        }

        public Builder setScopes(String... scopes) {
            this.akZ.g(scopes);
            return this;
        }
    }

    @Deprecated
    public interface OnAccessRevokedListener {
        void onAccessRevoked(ConnectionResult connectionResult);
    }

    @Deprecated
    public interface OnMomentsLoadedListener {
        @Deprecated
        void onMomentsLoaded(ConnectionResult connectionResult, MomentBuffer momentBuffer, String str, String str2);
    }

    @Deprecated
    public interface OnPeopleLoadedListener {
        void onPeopleLoaded(ConnectionResult connectionResult, PersonBuffer personBuffer, String str);
    }

    @Deprecated
    public interface OrderBy {

        @Deprecated
        public static final int ALPHABETICAL = 0;

        @Deprecated
        public static final int BEST = 1;
    }

    PlusClient(e plusClientImpl) {
        this.akS = plusClientImpl;
    }

    @Deprecated
    public void clearDefaultAccount() {
        this.akS.clearDefaultAccount();
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void connect() {
        this.akS.connect();
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void disconnect() {
        this.akS.disconnect();
    }

    @Deprecated
    public String getAccountName() {
        return this.akS.getAccountName();
    }

    @Deprecated
    public Person getCurrentPerson() {
        return this.akS.getCurrentPerson();
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public boolean isConnected() {
        return this.akS.isConnected();
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public boolean isConnecting() {
        return this.akS.isConnecting();
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public boolean isConnectionCallbacksRegistered(GooglePlayServicesClient.ConnectionCallbacks listener) {
        return this.akS.isConnectionCallbacksRegistered(listener);
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public boolean isConnectionFailedListenerRegistered(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        return this.akS.isConnectionFailedListenerRegistered(listener);
    }

    @Deprecated
    public void loadMoments(final OnMomentsLoadedListener listener) {
        this.akS.k(new BaseImplementation.b<Moments.LoadMomentsResult>() { // from class: com.google.android.gms.plus.PlusClient.1
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(Moments.LoadMomentsResult loadMomentsResult) {
                listener.onMomentsLoaded(loadMomentsResult.getStatus().gu(), loadMomentsResult.getMomentBuffer(), loadMomentsResult.getNextPageToken(), loadMomentsResult.getUpdated());
            }
        });
    }

    @Deprecated
    public void loadMoments(final OnMomentsLoadedListener listener, int maxResults, String pageToken, Uri targetUrl, String type, String userId) {
        this.akS.a(new BaseImplementation.b<Moments.LoadMomentsResult>() { // from class: com.google.android.gms.plus.PlusClient.2
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(Moments.LoadMomentsResult loadMomentsResult) {
                listener.onMomentsLoaded(loadMomentsResult.getStatus().gu(), loadMomentsResult.getMomentBuffer(), loadMomentsResult.getNextPageToken(), loadMomentsResult.getUpdated());
            }
        }, maxResults, pageToken, targetUrl, type, userId);
    }

    @Deprecated
    public void loadPeople(final OnPeopleLoadedListener listener, Collection<String> personIds) {
        this.akS.a(new BaseImplementation.b<People.LoadPeopleResult>() { // from class: com.google.android.gms.plus.PlusClient.5
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(People.LoadPeopleResult loadPeopleResult) {
                listener.onPeopleLoaded(loadPeopleResult.getStatus().gu(), loadPeopleResult.getPersonBuffer(), loadPeopleResult.getNextPageToken());
            }
        }, personIds);
    }

    @Deprecated
    public void loadPeople(final OnPeopleLoadedListener listener, String... personIds) {
        this.akS.d(new BaseImplementation.b<People.LoadPeopleResult>() { // from class: com.google.android.gms.plus.PlusClient.6
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(People.LoadPeopleResult loadPeopleResult) {
                listener.onPeopleLoaded(loadPeopleResult.getStatus().gu(), loadPeopleResult.getPersonBuffer(), loadPeopleResult.getNextPageToken());
            }
        }, personIds);
    }

    @Deprecated
    public void loadVisiblePeople(final OnPeopleLoadedListener listener, int orderBy, String pageToken) {
        this.akS.a(new BaseImplementation.b<People.LoadPeopleResult>() { // from class: com.google.android.gms.plus.PlusClient.3
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(People.LoadPeopleResult loadPeopleResult) {
                listener.onPeopleLoaded(loadPeopleResult.getStatus().gu(), loadPeopleResult.getPersonBuffer(), loadPeopleResult.getNextPageToken());
            }
        }, orderBy, pageToken);
    }

    @Deprecated
    public void loadVisiblePeople(final OnPeopleLoadedListener listener, String pageToken) {
        this.akS.r(new BaseImplementation.b<People.LoadPeopleResult>() { // from class: com.google.android.gms.plus.PlusClient.4
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void b(People.LoadPeopleResult loadPeopleResult) {
                listener.onPeopleLoaded(loadPeopleResult.getStatus().gu(), loadPeopleResult.getPersonBuffer(), loadPeopleResult.getNextPageToken());
            }
        }, pageToken);
    }

    e mX() {
        return this.akS;
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void registerConnectionCallbacks(GooglePlayServicesClient.ConnectionCallbacks listener) {
        this.akS.registerConnectionCallbacks(listener);
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void registerConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        this.akS.registerConnectionFailedListener(listener);
    }

    @Deprecated
    public void removeMoment(String momentId) {
        this.akS.removeMoment(momentId);
    }

    @Deprecated
    public void revokeAccessAndDisconnect(final OnAccessRevokedListener listener) {
        this.akS.m(new BaseImplementation.b<Status>() { // from class: com.google.android.gms.plus.PlusClient.7
            @Override // com.google.android.gms.common.api.BaseImplementation.b
            /* renamed from: aA, reason: merged with bridge method [inline-methods] */
            public void b(Status status) {
                listener.onAccessRevoked(status.getStatus().gu());
            }
        });
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void unregisterConnectionCallbacks(GooglePlayServicesClient.ConnectionCallbacks listener) {
        this.akS.unregisterConnectionCallbacks(listener);
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient
    @Deprecated
    public void unregisterConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        this.akS.unregisterConnectionFailedListener(listener);
    }

    @Deprecated
    public void writeMoment(Moment moment) {
        this.akS.a((BaseImplementation.b<Status>) null, moment);
    }
}
