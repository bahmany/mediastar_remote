package com.google.android.gms.games;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.event.Events;
import com.google.android.gms.games.internal.GamesClientImpl;
import com.google.android.gms.games.internal.api.AchievementsImpl;
import com.google.android.gms.games.internal.api.AclsImpl;
import com.google.android.gms.games.internal.api.EventsImpl;
import com.google.android.gms.games.internal.api.GamesMetadataImpl;
import com.google.android.gms.games.internal.api.InvitationsImpl;
import com.google.android.gms.games.internal.api.LeaderboardsImpl;
import com.google.android.gms.games.internal.api.MultiplayerImpl;
import com.google.android.gms.games.internal.api.NotificationsImpl;
import com.google.android.gms.games.internal.api.PlayersImpl;
import com.google.android.gms.games.internal.api.QuestsImpl;
import com.google.android.gms.games.internal.api.RealTimeMultiplayerImpl;
import com.google.android.gms.games.internal.api.RequestsImpl;
import com.google.android.gms.games.internal.api.SnapshotsImpl;
import com.google.android.gms.games.internal.api.TurnBasedMultiplayerImpl;
import com.google.android.gms.games.internal.game.Acls;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.games.request.Requests;
import com.google.android.gms.games.snapshot.Snapshots;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class Games {
    public static final String EXTRA_PLAYER_IDS = "players";
    static final Api.c<GamesClientImpl> CU = new Api.c<>();
    private static final Api.b<GamesClientImpl, GamesOptions> CV = new Api.b<GamesClientImpl, GamesOptions>() { // from class: com.google.android.gms.games.Games.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        public GamesClientImpl a(Context context, Looper looper, ClientSettings clientSettings, GamesOptions gamesOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new GamesClientImpl(context, looper, clientSettings.getRealClientPackageName(), clientSettings.getAccountNameOrDefault(), connectionCallbacks, onConnectionFailedListener, clientSettings.getScopesArray(), clientSettings.getGravityForPopups(), clientSettings.getViewForPopups(), gamesOptions == null ? new GamesOptions() : gamesOptions);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return 1;
        }
    };
    public static final Scope SCOPE_GAMES = new Scope(Scopes.GAMES);
    public static final Api<GamesOptions> API = new Api<>(CV, CU, SCOPE_GAMES);
    public static final Scope Vo = new Scope("https://www.googleapis.com/auth/games.firstparty");
    public static final Api<GamesOptions> Vp = new Api<>(CV, CU, Vo);
    public static final GamesMetadata GamesMetadata = new GamesMetadataImpl();
    public static final Achievements Achievements = new AchievementsImpl();
    public static final Events Events = new EventsImpl();
    public static final Leaderboards Leaderboards = new LeaderboardsImpl();
    public static final Invitations Invitations = new InvitationsImpl();
    public static final TurnBasedMultiplayer TurnBasedMultiplayer = new TurnBasedMultiplayerImpl();
    public static final RealTimeMultiplayer RealTimeMultiplayer = new RealTimeMultiplayerImpl();
    public static final Multiplayer Vq = new MultiplayerImpl();
    public static final Players Players = new PlayersImpl();
    public static final Notifications Notifications = new NotificationsImpl();
    public static final Quests Quests = new QuestsImpl();
    public static final Requests Requests = new RequestsImpl();
    public static final Snapshots Snapshots = new SnapshotsImpl();
    public static final Acls Vr = new AclsImpl();

    /* renamed from: com.google.android.gms.games.Games$1 */
    static class AnonymousClass1 implements Api.b<GamesClientImpl, GamesOptions> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.Api.b
        public GamesClientImpl a(Context context, Looper looper, ClientSettings clientSettings, GamesOptions gamesOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new GamesClientImpl(context, looper, clientSettings.getRealClientPackageName(), clientSettings.getAccountNameOrDefault(), connectionCallbacks, onConnectionFailedListener, clientSettings.getScopesArray(), clientSettings.getGravityForPopups(), clientSettings.getViewForPopups(), gamesOptions == null ? new GamesOptions() : gamesOptions);
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return 1;
        }
    }

    /* renamed from: com.google.android.gms.games.Games$2 */
    static class AnonymousClass2 extends SignOutImpl {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.b(this);
        }
    }

    public static abstract class BaseGamesApiMethodImpl<R extends Result> extends BaseImplementation.a<R, GamesClientImpl> {
        public BaseGamesApiMethodImpl() {
            super(Games.CU);
        }
    }

    public static final class GamesOptions implements Api.ApiOptions.Optional {
        public final boolean Vs;
        public final boolean Vt;
        public final int Vu;
        public final boolean Vv;
        public final int Vw;
        public final String Vx;
        public final ArrayList<String> Vy;

        public static final class Builder {
            boolean Vs;
            boolean Vt;
            int Vu;
            boolean Vv;
            int Vw;
            String Vx;
            ArrayList<String> Vy;

            private Builder() {
                this.Vs = false;
                this.Vt = true;
                this.Vu = 17;
                this.Vv = false;
                this.Vw = 4368;
                this.Vx = null;
                this.Vy = new ArrayList<>();
            }

            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            public GamesOptions build() {
                return new GamesOptions(this);
            }

            public Builder setSdkVariant(int variant) {
                this.Vw = variant;
                return this;
            }

            public Builder setShowConnectingPopup(boolean showConnectingPopup) {
                this.Vt = showConnectingPopup;
                this.Vu = 17;
                return this;
            }

            public Builder setShowConnectingPopup(boolean showConnectingPopup, int gravity) {
                this.Vt = showConnectingPopup;
                this.Vu = gravity;
                return this;
            }
        }

        private GamesOptions() {
            this.Vs = false;
            this.Vt = true;
            this.Vu = 17;
            this.Vv = false;
            this.Vw = 4368;
            this.Vx = null;
            this.Vy = new ArrayList<>();
        }

        /* synthetic */ GamesOptions(AnonymousClass1 x0) {
            this();
        }

        private GamesOptions(Builder builder) {
            this.Vs = builder.Vs;
            this.Vt = builder.Vt;
            this.Vu = builder.Vu;
            this.Vv = builder.Vv;
            this.Vw = builder.Vw;
            this.Vx = builder.Vx;
            this.Vy = builder.Vy;
        }

        /* synthetic */ GamesOptions(Builder x0, AnonymousClass1 x1) {
            this(x0);
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    private static abstract class SignOutImpl extends BaseGamesApiMethodImpl<Status> {
        private SignOutImpl() {
        }

        /* synthetic */ SignOutImpl(AnonymousClass1 x0) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private Games() {
    }

    public static GamesClientImpl c(GoogleApiClient googleApiClient) {
        n.b(googleApiClient != null, "GoogleApiClient parameter is required.");
        n.a(googleApiClient.isConnected(), "GoogleApiClient must be connected.");
        return d(googleApiClient);
    }

    public static GamesClientImpl d(GoogleApiClient googleApiClient) {
        GamesClientImpl gamesClientImpl = (GamesClientImpl) googleApiClient.a(CU);
        n.a(gamesClientImpl != null, "GoogleApiClient is not configured to use the Games Api. Pass Games.API into GoogleApiClient.Builder#addApi() to use this feature.");
        return gamesClientImpl;
    }

    public static String getAppId(GoogleApiClient apiClient) {
        return c(apiClient).km();
    }

    public static String getCurrentAccountName(GoogleApiClient apiClient) {
        return c(apiClient).jX();
    }

    public static int getSdkVariant(GoogleApiClient apiClient) {
        return c(apiClient).kl();
    }

    public static Intent getSettingsIntent(GoogleApiClient apiClient) {
        return c(apiClient).kk();
    }

    public static void setGravityForPopups(GoogleApiClient apiClient, int gravity) {
        c(apiClient).dB(gravity);
    }

    public static void setViewForPopups(GoogleApiClient apiClient, View gamesContentView) {
        n.i(gamesContentView);
        c(apiClient).k(gamesContentView);
    }

    public static PendingResult<Status> signOut(GoogleApiClient apiClient) {
        return apiClient.b(new SignOutImpl() { // from class: com.google.android.gms.games.Games.2
            AnonymousClass2() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.b(this);
            }
        });
    }
}
