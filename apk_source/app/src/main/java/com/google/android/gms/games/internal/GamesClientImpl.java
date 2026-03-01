package com.google.android.gms.games.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.a;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.common.internal.k;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameBuffer;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesMetadata;
import com.google.android.gms.games.Notifications;
import com.google.android.gms.games.OnNearbyPlayerDetectedListener;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.PlayerEntity;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.games.event.Events;
import com.google.android.gms.games.internal.IGamesService;
import com.google.android.gms.games.internal.constants.RequestType;
import com.google.android.gms.games.internal.events.EventIncrementCache;
import com.google.android.gms.games.internal.events.EventIncrementManager;
import com.google.android.gms.games.internal.experience.ExperienceEventBuffer;
import com.google.android.gms.games.internal.game.Acls;
import com.google.android.gms.games.internal.game.ExtendedGameBuffer;
import com.google.android.gms.games.internal.game.GameInstanceBuffer;
import com.google.android.gms.games.internal.request.RequestUpdateOutcomes;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardEntity;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScoreEntity;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.ScoreSubmissionData;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.ParticipantUtils;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeSocket;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomBuffer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomEntity;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.multiplayer.turnbased.LoadMatchesResponse;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.quest.Milestone;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestBuffer;
import com.google.android.gms.games.quest.QuestEntity;
import com.google.android.gms.games.quest.QuestUpdateListener;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.games.request.GameRequest;
import com.google.android.gms.games.request.GameRequestBuffer;
import com.google.android.gms.games.request.OnRequestReceivedListener;
import com.google.android.gms.games.request.Requests;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotContents;
import com.google.android.gms.games.snapshot.SnapshotEntity;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataBuffer;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.SnapshotMetadataEntity;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.internal.kc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class GamesClientImpl extends d<IGamesService> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String Dd;
    EventIncrementManager Wh;
    private final String Wi;
    private final Map<String, RealTimeSocket> Wj;
    private PlayerEntity Wk;
    private GameEntity Wl;
    private final PopupManager Wm;
    private boolean Wn;
    private final Binder Wo;
    private final long Wp;
    private final Games.GamesOptions Wq;

    private abstract class AbstractPeerStatusCallback extends AbstractRoomStatusCallback {
        private final ArrayList<String> Ws;

        AbstractPeerStatusCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder);
            this.Ws = new ArrayList<>();
            for (String str : participantIds) {
                this.Ws.add(str);
            }
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room) {
            a(roomStatusUpdateListener, room, this.Ws);
        }

        protected abstract void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList);
    }

    private abstract class AbstractRoomCallback extends d<IGamesService>.AbstractC0005d<RoomUpdateListener> {
        AbstractRoomCallback(RoomUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.AbstractC0005d
        public void a(RoomUpdateListener roomUpdateListener, DataHolder dataHolder) {
            a(roomUpdateListener, GamesClientImpl.this.R(dataHolder), dataHolder.getStatusCode());
        }

        protected abstract void a(RoomUpdateListener roomUpdateListener, Room room, int i);
    }

    private abstract class AbstractRoomStatusCallback extends d<IGamesService>.AbstractC0005d<RoomStatusUpdateListener> {
        AbstractRoomStatusCallback(RoomStatusUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.AbstractC0005d
        public void a(RoomStatusUpdateListener roomStatusUpdateListener, DataHolder dataHolder) {
            a(roomStatusUpdateListener, GamesClientImpl.this.R(dataHolder));
        }

        protected abstract void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room);
    }

    private static final class AcceptQuestResultImpl extends a implements Quests.AcceptQuestResult {
        private final Quest Wt;

        AcceptQuestResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            QuestBuffer questBuffer = new QuestBuffer(dataHolder);
            try {
                if (questBuffer.getCount() > 0) {
                    this.Wt = new QuestEntity(questBuffer.get(0));
                } else {
                    this.Wt = null;
                }
            } finally {
                questBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.quest.Quests.AcceptQuestResult
        public Quest getQuest() {
            return this.Wt;
        }
    }

    private final class AchievementUpdatedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Achievements.UpdateAchievementResult> De;

        AchievementUpdatedBinderCallback(BaseImplementation.b<Achievements.UpdateAchievementResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void g(int i, String str) {
            this.De.b(new UpdateAchievementResultImpl(i, str));
        }
    }

    private final class AchievementsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Achievements.LoadAchievementsResult> De;

        AchievementsLoadedBinderCallback(BaseImplementation.b<Achievements.LoadAchievementsResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void c(DataHolder dataHolder) {
            this.De.b(new LoadAchievementsResultImpl(dataHolder));
        }
    }

    private static final class CancelMatchResultImpl implements TurnBasedMultiplayer.CancelMatchResult {
        private final Status CM;
        private final String Wu;

        CancelMatchResultImpl(Status status, String externalMatchId) {
            this.CM = status;
            this.Wu = externalMatchId;
        }

        @Override // com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.CancelMatchResult
        public String getMatchId() {
            return this.Wu;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private static final class ClaimMilestoneResultImpl extends a implements Quests.ClaimMilestoneResult {
        private final Quest Wt;
        private final Milestone Wv;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ClaimMilestoneResultImpl(DataHolder dataHolder, String milestoneId) {
            super(dataHolder);
            QuestBuffer questBuffer = new QuestBuffer(dataHolder);
            try {
                if (questBuffer.getCount() > 0) {
                    this.Wt = new QuestEntity(questBuffer.get(0));
                    List<Milestone> listLH = this.Wt.lH();
                    int size = listLH.size();
                    for (int i = 0; i < size; i++) {
                        if (listLH.get(i).getMilestoneId().equals(milestoneId)) {
                            this.Wv = listLH.get(i);
                            return;
                        }
                    }
                    this.Wv = null;
                } else {
                    this.Wv = null;
                    this.Wt = null;
                }
            } finally {
                questBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.quest.Quests.ClaimMilestoneResult
        public Milestone getMilestone() {
            return this.Wv;
        }

        @Override // com.google.android.gms.games.quest.Quests.ClaimMilestoneResult
        public Quest getQuest() {
            return this.Wt;
        }
    }

    private static final class CommitSnapshotResultImpl extends a implements Snapshots.CommitSnapshotResult {
        private final SnapshotMetadata Ww;

        CommitSnapshotResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            SnapshotMetadataBuffer snapshotMetadataBuffer = new SnapshotMetadataBuffer(dataHolder);
            try {
                if (snapshotMetadataBuffer.getCount() > 0) {
                    this.Ww = new SnapshotMetadataEntity(snapshotMetadataBuffer.get(0));
                } else {
                    this.Ww = null;
                }
            } finally {
                snapshotMetadataBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.CommitSnapshotResult
        public SnapshotMetadata getSnapshotMetadata() {
            return this.Ww;
        }
    }

    private final class ConnectedToRoomCallback extends AbstractRoomStatusCallback {
        ConnectedToRoomCallback(RoomStatusUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomStatusCallback
        public void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room) {
            roomStatusUpdateListener.onConnectedToRoom(room);
        }
    }

    private static final class ContactSettingLoadResultImpl extends a implements Notifications.ContactSettingLoadResult {
        ContactSettingLoadResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private final class ContactSettingsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Notifications.ContactSettingLoadResult> De;

        ContactSettingsLoadedBinderCallback(BaseImplementation.b<Notifications.ContactSettingLoadResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void D(DataHolder dataHolder) {
            this.De.b(new ContactSettingLoadResultImpl(dataHolder));
        }
    }

    private final class ContactSettingsUpdatedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Status> De;

        ContactSettingsUpdatedBinderCallback(BaseImplementation.b<Status> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void dy(int i) {
            this.De.b(new Status(i));
        }
    }

    private static final class DeleteSnapshotResultImpl implements Snapshots.DeleteSnapshotResult {
        private final Status CM;
        private final String Wx;

        DeleteSnapshotResultImpl(int statusCode, String snapshotId) {
            this.CM = new Status(statusCode);
            this.Wx = snapshotId;
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.DeleteSnapshotResult
        public String getSnapshotId() {
            return this.Wx;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private final class DisconnectedFromRoomCallback extends AbstractRoomStatusCallback {
        DisconnectedFromRoomCallback(RoomStatusUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomStatusCallback
        public void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room) {
            roomStatusUpdateListener.onDisconnectedFromRoom(room);
        }
    }

    private final class EventsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Events.LoadEventsResult> De;

        EventsLoadedBinderCallback(BaseImplementation.b<Events.LoadEventsResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void d(DataHolder dataHolder) {
            this.De.b(new LoadEventResultImpl(dataHolder));
        }
    }

    private final class ExtendedGamesLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> De;

        ExtendedGamesLoadedBinderCallback(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void j(DataHolder dataHolder) {
            this.De.b(new LoadExtendedGamesResultImpl(dataHolder));
        }
    }

    private class GameClientEventIncrementCache extends EventIncrementCache {
        public GameClientEventIncrementCache() {
            super(GamesClientImpl.this.getContext().getMainLooper(), 1000);
        }

        @Override // com.google.android.gms.games.internal.events.EventIncrementCache
        protected void q(String str, int i) {
            try {
                if (GamesClientImpl.this.isConnected()) {
                    GamesClientImpl.this.gS().n(str, i);
                } else {
                    GamesLog.q("GamesClientImpl", "Unable to increment event " + str + " by " + i + " because the games client is no longer connected");
                }
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
            }
        }
    }

    private final class GameInstancesLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<GamesMetadata.LoadGameInstancesResult> De;

        GameInstancesLoadedBinderCallback(BaseImplementation.b<GamesMetadata.LoadGameInstancesResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void k(DataHolder dataHolder) {
            this.De.b(new LoadGameInstancesResultImpl(dataHolder));
        }
    }

    private static final class GameMuteStatusChangeResultImpl implements Notifications.GameMuteStatusChangeResult {
        private final Status CM;
        private final String Wy;
        private final boolean Wz;

        public GameMuteStatusChangeResultImpl(int statusCode, String externalGameId, boolean isMuted) {
            this.CM = new Status(statusCode);
            this.Wy = externalGameId;
            this.Wz = isMuted;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private final class GameMuteStatusChangedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Notifications.GameMuteStatusChangeResult> De;

        GameMuteStatusChangedBinderCallback(BaseImplementation.b<Notifications.GameMuteStatusChangeResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void a(int i, String str, boolean z) {
            this.De.b(new GameMuteStatusChangeResultImpl(i, str, z));
        }
    }

    private static final class GameMuteStatusLoadResultImpl implements Notifications.GameMuteStatusLoadResult {
        private final Status CM;
        private final String Wy;
        private final boolean Wz;

        public GameMuteStatusLoadResultImpl(DataHolder dataHolder) {
            try {
                this.CM = new Status(dataHolder.getStatusCode());
                if (dataHolder.getCount() > 0) {
                    this.Wy = dataHolder.c("external_game_id", 0, 0);
                    this.Wz = dataHolder.d("muted", 0, 0);
                } else {
                    this.Wy = null;
                    this.Wz = false;
                }
            } finally {
                dataHolder.close();
            }
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private final class GameMuteStatusLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Notifications.GameMuteStatusLoadResult> De;

        GameMuteStatusLoadedBinderCallback(BaseImplementation.b<Notifications.GameMuteStatusLoadResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void B(DataHolder dataHolder) {
            this.De.b(new GameMuteStatusLoadResultImpl(dataHolder));
        }
    }

    private final class GameSearchSuggestionsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<GamesMetadata.LoadGameSearchSuggestionsResult> De;

        GameSearchSuggestionsLoadedBinderCallback(BaseImplementation.b<GamesMetadata.LoadGameSearchSuggestionsResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void l(DataHolder dataHolder) {
            this.De.b(new LoadGameSearchSuggestionsResultImpl(dataHolder));
        }
    }

    private final class GamesLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<GamesMetadata.LoadGamesResult> De;

        GamesLoadedBinderCallback(BaseImplementation.b<GamesMetadata.LoadGamesResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void i(DataHolder dataHolder) {
            this.De.b(new LoadGamesResultImpl(dataHolder));
        }
    }

    private static final class InboxCountResultImpl implements Notifications.InboxCountResult {
        private final Status CM;
        private final Bundle WA;

        InboxCountResultImpl(Status status, Bundle inboxCounts) {
            this.CM = status;
            this.WA = inboxCounts;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private final class InboxCountsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Notifications.InboxCountResult> De;

        InboxCountsLoadedBinderCallback(BaseImplementation.b<Notifications.InboxCountResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void f(int i, Bundle bundle) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.De.b(new InboxCountResultImpl(new Status(i), bundle));
        }
    }

    private static final class InitiateMatchResultImpl extends TurnBasedMatchResult implements TurnBasedMultiplayer.InitiateMatchResult {
        InitiateMatchResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private final class InvitationReceivedBinderCallback extends AbstractGamesCallbacks {
        private final OnInvitationReceivedListener WB;

        InvitationReceivedBinderCallback(OnInvitationReceivedListener listener) {
            this.WB = listener;
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void n(DataHolder dataHolder) {
            InvitationBuffer invitationBuffer = new InvitationBuffer(dataHolder);
            try {
                Invitation invitationFreeze = invitationBuffer.getCount() > 0 ? invitationBuffer.get(0).freeze() : null;
                if (invitationFreeze != null) {
                    GamesClientImpl.this.a(GamesClientImpl.this.new InvitationReceivedCallback(this.WB, invitationFreeze));
                }
            } finally {
                invitationBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onInvitationRemoved(String invitationId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new InvitationRemovedCallback(this.WB, invitationId));
        }
    }

    private final class InvitationReceivedCallback extends d<IGamesService>.b<OnInvitationReceivedListener> {
        private final Invitation WC;

        InvitationReceivedCallback(OnInvitationReceivedListener listener, Invitation invitation) {
            super(listener);
            this.WC = invitation;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnInvitationReceivedListener onInvitationReceivedListener) {
            onInvitationReceivedListener.onInvitationReceived(this.WC);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class InvitationRemovedCallback extends d<IGamesService>.b<OnInvitationReceivedListener> {
        private final String WD;

        InvitationRemovedCallback(OnInvitationReceivedListener listener, String invitationId) {
            super(listener);
            this.WD = invitationId;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnInvitationReceivedListener onInvitationReceivedListener) {
            onInvitationReceivedListener.onInvitationRemoved(this.WD);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class InvitationsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Invitations.LoadInvitationsResult> De;

        InvitationsLoadedBinderCallback(BaseImplementation.b<Invitations.LoadInvitationsResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void m(DataHolder dataHolder) {
            this.De.b(new LoadInvitationsResultImpl(dataHolder));
        }
    }

    private final class JoinedRoomCallback extends AbstractRoomCallback {
        public JoinedRoomCallback(RoomUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomCallback
        public void a(RoomUpdateListener roomUpdateListener, Room room, int i) {
            roomUpdateListener.onJoinedRoom(i, room);
        }
    }

    private static final class LeaderboardMetadataResultImpl extends a implements Leaderboards.LeaderboardMetadataResult {
        private final LeaderboardBuffer WE;

        LeaderboardMetadataResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WE = new LeaderboardBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.leaderboard.Leaderboards.LeaderboardMetadataResult
        public LeaderboardBuffer getLeaderboards() {
            return this.WE;
        }
    }

    private final class LeaderboardScoresLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Leaderboards.LoadScoresResult> De;

        LeaderboardScoresLoadedBinderCallback(BaseImplementation.b<Leaderboards.LoadScoresResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void a(DataHolder dataHolder, DataHolder dataHolder2) {
            this.De.b(new LoadScoresResultImpl(dataHolder, dataHolder2));
        }
    }

    private final class LeaderboardsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> De;

        LeaderboardsLoadedBinderCallback(BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void e(DataHolder dataHolder) {
            this.De.b(new LeaderboardMetadataResultImpl(dataHolder));
        }
    }

    private static final class LeaveMatchResultImpl extends TurnBasedMatchResult implements TurnBasedMultiplayer.LeaveMatchResult {
        LeaveMatchResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private final class LeftRoomCallback extends d<IGamesService>.b<RoomUpdateListener> {
        private final int HF;
        private final String WF;

        LeftRoomCallback(RoomUpdateListener listener, int statusCode, String roomId) {
            super(listener);
            this.HF = statusCode;
            this.WF = roomId;
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(RoomUpdateListener roomUpdateListener) {
            roomUpdateListener.onLeftRoom(this.HF, this.WF);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private static final class LoadAchievementsResultImpl extends a implements Achievements.LoadAchievementsResult {
        private final AchievementBuffer WG;

        LoadAchievementsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WG = new AchievementBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult
        public AchievementBuffer getAchievements() {
            return this.WG;
        }
    }

    private static final class LoadAclResultImpl extends a implements Acls.LoadAclResult {
        LoadAclResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private static final class LoadEventResultImpl extends a implements Events.LoadEventsResult {
        private final EventBuffer WH;

        LoadEventResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WH = new EventBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.event.Events.LoadEventsResult
        public EventBuffer getEvents() {
            return this.WH;
        }
    }

    private static final class LoadExtendedGamesResultImpl extends a implements GamesMetadata.LoadExtendedGamesResult {
        private final ExtendedGameBuffer WI;

        LoadExtendedGamesResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WI = new ExtendedGameBuffer(dataHolder);
        }
    }

    private static final class LoadGameInstancesResultImpl extends a implements GamesMetadata.LoadGameInstancesResult {
        private final GameInstanceBuffer WJ;

        LoadGameInstancesResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WJ = new GameInstanceBuffer(dataHolder);
        }
    }

    private static final class LoadGameSearchSuggestionsResultImpl extends a implements GamesMetadata.LoadGameSearchSuggestionsResult {
        LoadGameSearchSuggestionsResultImpl(DataHolder data) {
            super(data);
        }
    }

    private static final class LoadGamesResultImpl extends a implements GamesMetadata.LoadGamesResult {
        private final GameBuffer WK;

        LoadGamesResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WK = new GameBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.GamesMetadata.LoadGamesResult
        public GameBuffer getGames() {
            return this.WK;
        }
    }

    private static final class LoadInvitationsResultImpl extends a implements Invitations.LoadInvitationsResult {
        private final InvitationBuffer WL;

        LoadInvitationsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WL = new InvitationBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.multiplayer.Invitations.LoadInvitationsResult
        public InvitationBuffer getInvitations() {
            return this.WL;
        }
    }

    private static final class LoadMatchResultImpl extends TurnBasedMatchResult implements TurnBasedMultiplayer.LoadMatchResult {
        LoadMatchResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private static final class LoadMatchesResultImpl implements TurnBasedMultiplayer.LoadMatchesResult {
        private final Status CM;
        private final LoadMatchesResponse WM;

        LoadMatchesResultImpl(Status status, Bundle matchData) {
            this.CM = status;
            this.WM = new LoadMatchesResponse(matchData);
        }

        @Override // com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.LoadMatchesResult
        public LoadMatchesResponse getMatches() {
            return this.WM;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() {
            this.WM.close();
        }
    }

    private static final class LoadOwnerCoverPhotoUrisResultImpl implements Players.LoadOwnerCoverPhotoUrisResult {
        private final Status CM;
        private final Bundle MZ;

        LoadOwnerCoverPhotoUrisResultImpl(int statusCode, Bundle bundle) {
            this.CM = new Status(statusCode);
            this.MZ = bundle;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private static final class LoadPlayerScoreResultImpl extends a implements Leaderboards.LoadPlayerScoreResult {
        private final LeaderboardScoreEntity WN;

        LoadPlayerScoreResultImpl(DataHolder scoreHolder) {
            super(scoreHolder);
            LeaderboardScoreBuffer leaderboardScoreBuffer = new LeaderboardScoreBuffer(scoreHolder);
            try {
                if (leaderboardScoreBuffer.getCount() > 0) {
                    this.WN = (LeaderboardScoreEntity) leaderboardScoreBuffer.get(0).freeze();
                } else {
                    this.WN = null;
                }
            } finally {
                leaderboardScoreBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult
        public LeaderboardScore getScore() {
            return this.WN;
        }
    }

    private static final class LoadPlayersResultImpl extends a implements Players.LoadPlayersResult {
        private final PlayerBuffer WO;

        LoadPlayersResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WO = new PlayerBuffer(dataHolder);
        }

        @Override // com.google.android.gms.games.Players.LoadPlayersResult
        public PlayerBuffer getPlayers() {
            return this.WO;
        }
    }

    private static final class LoadProfileSettingsResultImpl extends a implements Players.LoadProfileSettingsResult {
        private final boolean WP;
        private final boolean We;

        LoadProfileSettingsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            try {
                if (dataHolder.getCount() > 0) {
                    int iAr = dataHolder.ar(0);
                    this.We = dataHolder.d("profile_visible", 0, iAr);
                    this.WP = dataHolder.d("profile_visibility_explicitly_set", 0, iAr);
                } else {
                    this.We = true;
                    this.WP = false;
                }
            } finally {
                dataHolder.close();
            }
        }

        @Override // com.google.android.gms.common.api.a, com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.games.Players.LoadProfileSettingsResult
        public boolean isProfileVisible() {
            return this.We;
        }

        @Override // com.google.android.gms.games.Players.LoadProfileSettingsResult
        public boolean isVisibilityExplicitlySet() {
            return this.WP;
        }
    }

    private static final class LoadQuestsResultImpl extends a implements Quests.LoadQuestsResult {
        private final DataHolder IC;

        LoadQuestsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.IC = dataHolder;
        }

        @Override // com.google.android.gms.games.quest.Quests.LoadQuestsResult
        public QuestBuffer getQuests() {
            return new QuestBuffer(this.IC);
        }
    }

    private static final class LoadRequestSummariesResultImpl extends a implements Requests.LoadRequestSummariesResult {
        LoadRequestSummariesResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private static final class LoadRequestsResultImpl implements Requests.LoadRequestsResult {
        private final Status CM;
        private final Bundle WQ;

        LoadRequestsResultImpl(Status status, Bundle requestData) {
            this.CM = status;
            this.WQ = requestData;
        }

        @Override // com.google.android.gms.games.request.Requests.LoadRequestsResult
        public GameRequestBuffer getRequests(int requestType) {
            String strDH = RequestType.dH(requestType);
            if (this.WQ.containsKey(strDH)) {
                return new GameRequestBuffer((DataHolder) this.WQ.get(strDH));
            }
            return null;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() {
            Iterator<String> it = this.WQ.keySet().iterator();
            while (it.hasNext()) {
                DataHolder dataHolder = (DataHolder) this.WQ.getParcelable(it.next());
                if (dataHolder != null) {
                    dataHolder.close();
                }
            }
        }
    }

    private static final class LoadScoresResultImpl extends a implements Leaderboards.LoadScoresResult {
        private final LeaderboardEntity WR;
        private final LeaderboardScoreBuffer WS;

        LoadScoresResultImpl(DataHolder leaderboard, DataHolder scores) {
            super(scores);
            LeaderboardBuffer leaderboardBuffer = new LeaderboardBuffer(leaderboard);
            try {
                if (leaderboardBuffer.getCount() > 0) {
                    this.WR = (LeaderboardEntity) leaderboardBuffer.get(0).freeze();
                } else {
                    this.WR = null;
                }
                leaderboardBuffer.release();
                this.WS = new LeaderboardScoreBuffer(scores);
            } catch (Throwable th) {
                leaderboardBuffer.release();
                throw th;
            }
        }

        @Override // com.google.android.gms.games.leaderboard.Leaderboards.LoadScoresResult
        public Leaderboard getLeaderboard() {
            return this.WR;
        }

        @Override // com.google.android.gms.games.leaderboard.Leaderboards.LoadScoresResult
        public LeaderboardScoreBuffer getScores() {
            return this.WS;
        }
    }

    private static final class LoadSnapshotsResultImpl extends a implements Snapshots.LoadSnapshotsResult {
        LoadSnapshotsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.LoadSnapshotsResult
        public SnapshotMetadataBuffer getSnapshots() {
            return new SnapshotMetadataBuffer(this.IC);
        }
    }

    private static final class LoadXpForGameCategoriesResultImpl implements Players.LoadXpForGameCategoriesResult {
        private final Status CM;
        private final List<String> WT;
        private final Bundle WU;

        LoadXpForGameCategoriesResultImpl(Status status, Bundle xpData) {
            this.CM = status;
            this.WT = xpData.getStringArrayList("game_category_list");
            this.WU = xpData;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private static final class LoadXpStreamResultImpl extends a implements Players.LoadXpStreamResult {
        private final ExperienceEventBuffer WV;

        LoadXpStreamResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.WV = new ExperienceEventBuffer(dataHolder);
        }
    }

    private final class MatchRemovedCallback extends d<IGamesService>.b<OnTurnBasedMatchUpdateReceivedListener> {
        private final String WW;

        MatchRemovedCallback(OnTurnBasedMatchUpdateReceivedListener listener, String matchId) {
            super(listener);
            this.WW = matchId;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnTurnBasedMatchUpdateReceivedListener onTurnBasedMatchUpdateReceivedListener) {
            onTurnBasedMatchUpdateReceivedListener.onTurnBasedMatchRemoved(this.WW);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class MatchUpdateReceivedBinderCallback extends AbstractGamesCallbacks {
        private final OnTurnBasedMatchUpdateReceivedListener WX;

        MatchUpdateReceivedBinderCallback(OnTurnBasedMatchUpdateReceivedListener listener) {
            this.WX = listener;
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onTurnBasedMatchRemoved(String matchId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new MatchRemovedCallback(this.WX, matchId));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void t(DataHolder dataHolder) {
            TurnBasedMatchBuffer turnBasedMatchBuffer = new TurnBasedMatchBuffer(dataHolder);
            try {
                TurnBasedMatch turnBasedMatchFreeze = turnBasedMatchBuffer.getCount() > 0 ? turnBasedMatchBuffer.get(0).freeze() : null;
                if (turnBasedMatchFreeze != null) {
                    GamesClientImpl.this.a(GamesClientImpl.this.new MatchUpdateReceivedCallback(this.WX, turnBasedMatchFreeze));
                }
            } finally {
                turnBasedMatchBuffer.release();
            }
        }
    }

    private final class MatchUpdateReceivedCallback extends d<IGamesService>.b<OnTurnBasedMatchUpdateReceivedListener> {
        private final TurnBasedMatch WY;

        MatchUpdateReceivedCallback(OnTurnBasedMatchUpdateReceivedListener listener, TurnBasedMatch match) {
            super(listener);
            this.WY = match;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnTurnBasedMatchUpdateReceivedListener onTurnBasedMatchUpdateReceivedListener) {
            onTurnBasedMatchUpdateReceivedListener.onTurnBasedMatchReceived(this.WY);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class MessageReceivedCallback extends d<IGamesService>.b<RealTimeMessageReceivedListener> {
        private final RealTimeMessage WZ;

        MessageReceivedCallback(RealTimeMessageReceivedListener listener, RealTimeMessage message) {
            super(listener);
            this.WZ = message;
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(RealTimeMessageReceivedListener realTimeMessageReceivedListener) {
            if (realTimeMessageReceivedListener != null) {
                realTimeMessageReceivedListener.onRealTimeMessageReceived(this.WZ);
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class NearbyPlayerDetectedCallback extends d<IGamesService>.b<OnNearbyPlayerDetectedListener> {
        private final Player Xa;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(OnNearbyPlayerDetectedListener onNearbyPlayerDetectedListener) {
            onNearbyPlayerDetectedListener.a(this.Xa);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class NotifyAclLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Acls.LoadAclResult> De;

        NotifyAclLoadedBinderCallback(BaseImplementation.b<Acls.LoadAclResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void C(DataHolder dataHolder) {
            this.De.b(new LoadAclResultImpl(dataHolder));
        }
    }

    private final class NotifyAclUpdatedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Status> De;

        NotifyAclUpdatedBinderCallback(BaseImplementation.b<Status> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void dx(int i) {
            this.De.b(new Status(i));
        }
    }

    private static final class OpenSnapshotResultImpl extends a implements Snapshots.OpenSnapshotResult {
        private final Snapshot Xb;
        private final String Xc;
        private final Snapshot Xd;
        private final Contents Xe;
        private final SnapshotContents Xf;

        OpenSnapshotResultImpl(DataHolder dataHolder, Contents currentContents) {
            this(dataHolder, null, currentContents, null, null);
        }

        OpenSnapshotResultImpl(DataHolder metadataHolder, String conflictId, Contents currentContents, Contents conflictContents, Contents resolutionContents) {
            super(metadataHolder);
            SnapshotMetadataBuffer snapshotMetadataBuffer = new SnapshotMetadataBuffer(metadataHolder);
            try {
                if (snapshotMetadataBuffer.getCount() == 0) {
                    this.Xb = null;
                    this.Xd = null;
                } else if (snapshotMetadataBuffer.getCount() == 1) {
                    com.google.android.gms.common.internal.a.I(metadataHolder.getStatusCode() != 4004);
                    this.Xb = new SnapshotEntity(new SnapshotMetadataEntity(snapshotMetadataBuffer.get(0)), new SnapshotContents(currentContents));
                    this.Xd = null;
                } else {
                    this.Xb = new SnapshotEntity(new SnapshotMetadataEntity(snapshotMetadataBuffer.get(0)), new SnapshotContents(currentContents));
                    this.Xd = new SnapshotEntity(new SnapshotMetadataEntity(snapshotMetadataBuffer.get(1)), new SnapshotContents(conflictContents));
                }
                snapshotMetadataBuffer.release();
                this.Xc = conflictId;
                this.Xe = resolutionContents;
                this.Xf = new SnapshotContents(resolutionContents);
            } catch (Throwable th) {
                snapshotMetadataBuffer.release();
                throw th;
            }
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
        public String getConflictId() {
            return this.Xc;
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
        public Snapshot getConflictingSnapshot() {
            return this.Xd;
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
        @Deprecated
        public Contents getResolutionContents() {
            return this.Xe;
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
        public SnapshotContents getResolutionSnapshotContents() {
            return this.Xf;
        }

        @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
        public Snapshot getSnapshot() {
            return this.Xb;
        }
    }

    private final class OwnerCoverPhotoUrisLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Players.LoadOwnerCoverPhotoUrisResult> De;

        OwnerCoverPhotoUrisLoadedBinderCallback(BaseImplementation.b<Players.LoadOwnerCoverPhotoUrisResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void d(int i, Bundle bundle) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.De.b(new LoadOwnerCoverPhotoUrisResultImpl(i, bundle));
        }
    }

    private final class P2PConnectedCallback extends d<IGamesService>.b<RoomStatusUpdateListener> {
        private final String Xg;

        P2PConnectedCallback(RoomStatusUpdateListener listener, String participantId) {
            super(listener);
            this.Xg = participantId;
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(RoomStatusUpdateListener roomStatusUpdateListener) {
            if (roomStatusUpdateListener != null) {
                roomStatusUpdateListener.onP2PConnected(this.Xg);
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class P2PDisconnectedCallback extends d<IGamesService>.b<RoomStatusUpdateListener> {
        private final String Xg;

        P2PDisconnectedCallback(RoomStatusUpdateListener listener, String participantId) {
            super(listener);
            this.Xg = participantId;
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(RoomStatusUpdateListener roomStatusUpdateListener) {
            if (roomStatusUpdateListener != null) {
                roomStatusUpdateListener.onP2PDisconnected(this.Xg);
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class PeerConnectedCallback extends AbstractPeerStatusCallback {
        PeerConnectedCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeersConnected(room, arrayList);
        }
    }

    private final class PeerDeclinedCallback extends AbstractPeerStatusCallback {
        PeerDeclinedCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeerDeclined(room, arrayList);
        }
    }

    private final class PeerDisconnectedCallback extends AbstractPeerStatusCallback {
        PeerDisconnectedCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeersDisconnected(room, arrayList);
        }
    }

    private final class PeerInvitedToRoomCallback extends AbstractPeerStatusCallback {
        PeerInvitedToRoomCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeerInvitedToRoom(room, arrayList);
        }
    }

    private final class PeerJoinedRoomCallback extends AbstractPeerStatusCallback {
        PeerJoinedRoomCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeerJoined(room, arrayList);
        }
    }

    private final class PeerLeftRoomCallback extends AbstractPeerStatusCallback {
        PeerLeftRoomCallback(RoomStatusUpdateListener listener, DataHolder dataHolder, String[] participantIds) {
            super(listener, dataHolder, participantIds);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractPeerStatusCallback
        protected void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room, ArrayList<String> arrayList) {
            roomStatusUpdateListener.onPeerLeft(room, arrayList);
        }
    }

    private final class PlayerLeaderboardScoreLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Leaderboards.LoadPlayerScoreResult> De;

        PlayerLeaderboardScoreLoadedBinderCallback(BaseImplementation.b<Leaderboards.LoadPlayerScoreResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void E(DataHolder dataHolder) {
            this.De.b(new LoadPlayerScoreResultImpl(dataHolder));
        }
    }

    private final class PlayerXpForGameCategoriesLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Players.LoadXpForGameCategoriesResult> De;

        PlayerXpForGameCategoriesLoadedBinderCallback(BaseImplementation.b<Players.LoadXpForGameCategoriesResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void e(int i, Bundle bundle) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.De.b(new LoadXpForGameCategoriesResultImpl(new Status(i), bundle));
        }
    }

    final class PlayerXpStreamLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Players.LoadXpStreamResult> De;

        PlayerXpStreamLoadedBinderCallback(BaseImplementation.b<Players.LoadXpStreamResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void P(DataHolder dataHolder) {
            this.De.b(new LoadXpStreamResultImpl(dataHolder));
        }
    }

    private final class PlayersLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Players.LoadPlayersResult> De;

        PlayersLoadedBinderCallback(BaseImplementation.b<Players.LoadPlayersResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void g(DataHolder dataHolder) {
            this.De.b(new LoadPlayersResultImpl(dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void h(DataHolder dataHolder) {
            this.De.b(new LoadPlayersResultImpl(dataHolder));
        }
    }

    final class ProfileSettingsLoadedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Players.LoadProfileSettingsResult> De;

        ProfileSettingsLoadedBinderCallback(BaseImplementation.b<Players.LoadProfileSettingsResult> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void Q(DataHolder dataHolder) {
            this.De.b(new LoadProfileSettingsResultImpl(dataHolder));
        }
    }

    private final class ProfileSettingsUpdatedBinderCallback extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Status> De;

        ProfileSettingsUpdatedBinderCallback(BaseImplementation.b<Status> holder) {
            this.De = (BaseImplementation.b) n.b(holder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void dz(int i) {
            this.De.b(new Status(i));
        }
    }

    private final class QuestAcceptedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Quests.AcceptQuestResult> Xh;

        public QuestAcceptedBinderCallbacks(BaseImplementation.b<Quests.AcceptQuestResult> resultHolder) {
            this.Xh = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void L(DataHolder dataHolder) {
            this.Xh.b(new AcceptQuestResultImpl(dataHolder));
        }
    }

    private final class QuestCompletedCallback extends d<IGamesService>.b<QuestUpdateListener> {
        private final Quest Wt;

        QuestCompletedCallback(QuestUpdateListener listener, Quest quest) {
            super(listener);
            this.Wt = quest;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(QuestUpdateListener questUpdateListener) {
            questUpdateListener.onQuestCompleted(this.Wt);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class QuestMilestoneClaimBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Quests.ClaimMilestoneResult> Xi;
        private final String Xj;

        public QuestMilestoneClaimBinderCallbacks(BaseImplementation.b<Quests.ClaimMilestoneResult> resultHolder, String milestoneId) {
            this.Xi = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
            this.Xj = (String) n.b(milestoneId, (Object) "MilestoneId must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void K(DataHolder dataHolder) {
            this.Xi.b(new ClaimMilestoneResultImpl(dataHolder, this.Xj));
        }
    }

    private final class QuestUpdateBinderCallback extends AbstractGamesCallbacks {
        private final QuestUpdateListener Xk;

        QuestUpdateBinderCallback(QuestUpdateListener listener) {
            this.Xk = listener;
        }

        private Quest S(DataHolder dataHolder) {
            QuestBuffer questBuffer = new QuestBuffer(dataHolder);
            try {
                return questBuffer.getCount() > 0 ? questBuffer.get(0).freeze() : null;
            } finally {
                questBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void M(DataHolder dataHolder) {
            Quest questS = S(dataHolder);
            if (questS != null) {
                GamesClientImpl.this.a(GamesClientImpl.this.new QuestCompletedCallback(this.Xk, questS));
            }
        }
    }

    private final class QuestsLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Quests.LoadQuestsResult> Xl;

        public QuestsLoadedBinderCallbacks(BaseImplementation.b<Quests.LoadQuestsResult> resultHolder) {
            this.Xl = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void O(DataHolder dataHolder) {
            this.Xl.b(new LoadQuestsResultImpl(dataHolder));
        }
    }

    private final class RealTimeMessageSentCallback extends d<IGamesService>.b<RealTimeMultiplayer.ReliableMessageSentCallback> {
        private final int HF;
        private final String Xm;
        private final int Xn;

        RealTimeMessageSentCallback(RealTimeMultiplayer.ReliableMessageSentCallback listener, int statusCode, int token, String recipientParticipantId) {
            super(listener);
            this.HF = statusCode;
            this.Xn = token;
            this.Xm = recipientParticipantId;
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(RealTimeMultiplayer.ReliableMessageSentCallback reliableMessageSentCallback) {
            if (reliableMessageSentCallback != null) {
                reliableMessageSentCallback.onRealTimeMessageSent(this.HF, this.Xn, this.Xm);
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class RealTimeReliableMessageBinderCallbacks extends AbstractGamesCallbacks {
        final RealTimeMultiplayer.ReliableMessageSentCallback Xo;

        public RealTimeReliableMessageBinderCallbacks(RealTimeMultiplayer.ReliableMessageSentCallback messageSentCallbacks) {
            this.Xo = messageSentCallbacks;
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void b(int i, int i2, String str) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RealTimeMessageSentCallback(this.Xo, i, i2, str));
        }
    }

    private final class RequestReceivedBinderCallback extends AbstractGamesCallbacks {
        private final OnRequestReceivedListener Xp;

        RequestReceivedBinderCallback(OnRequestReceivedListener listener) {
            this.Xp = listener;
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void o(DataHolder dataHolder) {
            GameRequestBuffer gameRequestBuffer = new GameRequestBuffer(dataHolder);
            try {
                GameRequest gameRequestFreeze = gameRequestBuffer.getCount() > 0 ? gameRequestBuffer.get(0).freeze() : null;
                if (gameRequestFreeze != null) {
                    GamesClientImpl.this.a(GamesClientImpl.this.new RequestReceivedCallback(this.Xp, gameRequestFreeze));
                }
            } finally {
                gameRequestBuffer.release();
            }
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onRequestRemoved(String requestId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RequestRemovedCallback(this.Xp, requestId));
        }
    }

    private final class RequestReceivedCallback extends d<IGamesService>.b<OnRequestReceivedListener> {
        private final GameRequest Xq;

        RequestReceivedCallback(OnRequestReceivedListener listener, GameRequest request) {
            super(listener);
            this.Xq = request;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnRequestReceivedListener onRequestReceivedListener) {
            onRequestReceivedListener.onRequestReceived(this.Xq);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class RequestRemovedCallback extends d<IGamesService>.b<OnRequestReceivedListener> {
        private final String Xr;

        RequestRemovedCallback(OnRequestReceivedListener listener, String requestId) {
            super(listener);
            this.Xr = requestId;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(OnRequestReceivedListener onRequestReceivedListener) {
            onRequestReceivedListener.onRequestRemoved(this.Xr);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private final class RequestSentBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Requests.SendRequestResult> Xs;

        public RequestSentBinderCallbacks(BaseImplementation.b<Requests.SendRequestResult> resultHolder) {
            this.Xs = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void G(DataHolder dataHolder) {
            this.Xs.b(new SendRequestResultImpl(dataHolder));
        }
    }

    private final class RequestSummariesLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Requests.LoadRequestSummariesResult> Xt;

        public RequestSummariesLoadedBinderCallbacks(BaseImplementation.b<Requests.LoadRequestSummariesResult> resultHolder) {
            this.Xt = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void H(DataHolder dataHolder) {
            this.Xt.b(new LoadRequestSummariesResultImpl(dataHolder));
        }
    }

    private final class RequestsLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Requests.LoadRequestsResult> Xu;

        public RequestsLoadedBinderCallbacks(BaseImplementation.b<Requests.LoadRequestsResult> resultHolder) {
            this.Xu = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void c(int i, Bundle bundle) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.Xu.b(new LoadRequestsResultImpl(new Status(i), bundle));
        }
    }

    private final class RequestsUpdatedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Requests.UpdateRequestsResult> Xv;

        public RequestsUpdatedBinderCallbacks(BaseImplementation.b<Requests.UpdateRequestsResult> resultHolder) {
            this.Xv = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void F(DataHolder dataHolder) {
            this.Xv.b(new UpdateRequestsResultImpl(dataHolder));
        }
    }

    private final class RoomAutoMatchingCallback extends AbstractRoomStatusCallback {
        RoomAutoMatchingCallback(RoomStatusUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomStatusCallback
        public void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room) {
            roomStatusUpdateListener.onRoomAutoMatching(room);
        }
    }

    private final class RoomBinderCallbacks extends AbstractGamesCallbacks {
        private final RoomUpdateListener Xw;
        private final RoomStatusUpdateListener Xx;
        private final RealTimeMessageReceivedListener Xy;

        public RoomBinderCallbacks(RoomUpdateListener roomCallbacks) {
            this.Xw = (RoomUpdateListener) n.b(roomCallbacks, "Callbacks must not be null");
            this.Xx = null;
            this.Xy = null;
        }

        public RoomBinderCallbacks(RoomUpdateListener roomCallbacks, RoomStatusUpdateListener roomStatusCallbacks, RealTimeMessageReceivedListener realTimeMessageReceivedCallbacks) {
            this.Xw = (RoomUpdateListener) n.b(roomCallbacks, "Callbacks must not be null");
            this.Xx = roomStatusCallbacks;
            this.Xy = realTimeMessageReceivedCallbacks;
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void A(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new DisconnectedFromRoomCallback(this.Xx, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void a(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerInvitedToRoomCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void b(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerJoinedRoomCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void c(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerLeftRoomCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void d(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerDeclinedCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void e(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerConnectedCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void f(DataHolder dataHolder, String[] strArr) {
            GamesClientImpl.this.a(GamesClientImpl.this.new PeerDisconnectedCallback(this.Xx, dataHolder, strArr));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onLeftRoom(int statusCode, String externalRoomId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new LeftRoomCallback(this.Xw, statusCode, externalRoomId));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onP2PConnected(String participantId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new P2PConnectedCallback(this.Xx, participantId));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onP2PDisconnected(String participantId) {
            GamesClientImpl.this.a(GamesClientImpl.this.new P2PDisconnectedCallback(this.Xx, participantId));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void onRealTimeMessageReceived(RealTimeMessage message) {
            GamesClientImpl.this.a(GamesClientImpl.this.new MessageReceivedCallback(this.Xy, message));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void u(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RoomCreatedCallback(this.Xw, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void v(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new JoinedRoomCallback(this.Xw, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void w(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RoomConnectingCallback(this.Xx, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void x(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RoomAutoMatchingCallback(this.Xx, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void y(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new RoomConnectedCallback(this.Xw, dataHolder));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void z(DataHolder dataHolder) {
            GamesClientImpl.this.a(GamesClientImpl.this.new ConnectedToRoomCallback(this.Xx, dataHolder));
        }
    }

    private final class RoomConnectedCallback extends AbstractRoomCallback {
        RoomConnectedCallback(RoomUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomCallback
        public void a(RoomUpdateListener roomUpdateListener, Room room, int i) {
            roomUpdateListener.onRoomConnected(i, room);
        }
    }

    private final class RoomConnectingCallback extends AbstractRoomStatusCallback {
        RoomConnectingCallback(RoomStatusUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomStatusCallback
        public void a(RoomStatusUpdateListener roomStatusUpdateListener, Room room) {
            roomStatusUpdateListener.onRoomConnecting(room);
        }
    }

    private final class RoomCreatedCallback extends AbstractRoomCallback {
        public RoomCreatedCallback(RoomUpdateListener listener, DataHolder dataHolder) {
            super(listener, dataHolder);
        }

        @Override // com.google.android.gms.games.internal.GamesClientImpl.AbstractRoomCallback
        public void a(RoomUpdateListener roomUpdateListener, Room room, int i) {
            roomUpdateListener.onRoomCreated(i, room);
        }
    }

    private static final class SendRequestResultImpl extends a implements Requests.SendRequestResult {
        private final GameRequest Xq;

        SendRequestResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            GameRequestBuffer gameRequestBuffer = new GameRequestBuffer(dataHolder);
            try {
                if (gameRequestBuffer.getCount() > 0) {
                    this.Xq = gameRequestBuffer.get(0).freeze();
                } else {
                    this.Xq = null;
                }
            } finally {
                gameRequestBuffer.release();
            }
        }
    }

    private final class SignOutCompleteBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Status> De;

        public SignOutCompleteBinderCallbacks(BaseImplementation.b<Status> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void fq() {
            this.De.b(new Status(0));
        }
    }

    private final class SnapshotCommittedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Snapshots.CommitSnapshotResult> Xz;

        public SnapshotCommittedBinderCallbacks(BaseImplementation.b<Snapshots.CommitSnapshotResult> resultHolder) {
            this.Xz = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void J(DataHolder dataHolder) {
            this.Xz.b(new CommitSnapshotResultImpl(dataHolder));
        }
    }

    final class SnapshotDeletedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Snapshots.DeleteSnapshotResult> De;

        public SnapshotDeletedBinderCallbacks(BaseImplementation.b<Snapshots.DeleteSnapshotResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void i(int i, String str) {
            this.De.b(new DeleteSnapshotResultImpl(i, str));
        }
    }

    private final class SnapshotOpenedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Snapshots.OpenSnapshotResult> XA;

        public SnapshotOpenedBinderCallbacks(BaseImplementation.b<Snapshots.OpenSnapshotResult> resultHolder) {
            this.XA = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void a(DataHolder dataHolder, Contents contents) {
            this.XA.b(new OpenSnapshotResultImpl(dataHolder, contents));
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void a(DataHolder dataHolder, String str, Contents contents, Contents contents2, Contents contents3) {
            this.XA.b(new OpenSnapshotResultImpl(dataHolder, str, contents, contents2, contents3));
        }
    }

    private final class SnapshotsLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Snapshots.LoadSnapshotsResult> XB;

        public SnapshotsLoadedBinderCallbacks(BaseImplementation.b<Snapshots.LoadSnapshotsResult> resultHolder) {
            this.XB = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void I(DataHolder dataHolder) {
            this.XB.b(new LoadSnapshotsResultImpl(dataHolder));
        }
    }

    private final class SubmitScoreBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<Leaderboards.SubmitScoreResult> De;

        public SubmitScoreBinderCallbacks(BaseImplementation.b<Leaderboards.SubmitScoreResult> resultHolder) {
            this.De = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void f(DataHolder dataHolder) {
            this.De.b(new SubmitScoreResultImpl(dataHolder));
        }
    }

    private static final class SubmitScoreResultImpl extends a implements Leaderboards.SubmitScoreResult {
        private final ScoreSubmissionData XC;

        public SubmitScoreResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            try {
                this.XC = new ScoreSubmissionData(dataHolder);
            } finally {
                dataHolder.close();
            }
        }

        @Override // com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult
        public ScoreSubmissionData getScoreData() {
            return this.XC;
        }
    }

    private final class TurnBasedMatchCanceledBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.CancelMatchResult> XD;

        public TurnBasedMatchCanceledBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.CancelMatchResult> resultHolder) {
            this.XD = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void h(int i, String str) {
            this.XD.b(new CancelMatchResultImpl(new Status(i), str));
        }
    }

    private final class TurnBasedMatchInitiatedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> XE;

        public TurnBasedMatchInitiatedBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> resultHolder) {
            this.XE = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void q(DataHolder dataHolder) {
            this.XE.b(new InitiateMatchResultImpl(dataHolder));
        }
    }

    private final class TurnBasedMatchLeftBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.LeaveMatchResult> XF;

        public TurnBasedMatchLeftBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.LeaveMatchResult> resultHolder) {
            this.XF = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void s(DataHolder dataHolder) {
            this.XF.b(new LeaveMatchResultImpl(dataHolder));
        }
    }

    private final class TurnBasedMatchLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.LoadMatchResult> XG;

        public TurnBasedMatchLoadedBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.LoadMatchResult> resultHolder) {
            this.XG = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void p(DataHolder dataHolder) {
            this.XG.b(new LoadMatchResultImpl(dataHolder));
        }
    }

    private static abstract class TurnBasedMatchResult extends a {
        final TurnBasedMatch WY;

        TurnBasedMatchResult(DataHolder dataHolder) {
            super(dataHolder);
            TurnBasedMatchBuffer turnBasedMatchBuffer = new TurnBasedMatchBuffer(dataHolder);
            try {
                if (turnBasedMatchBuffer.getCount() > 0) {
                    this.WY = turnBasedMatchBuffer.get(0).freeze();
                } else {
                    this.WY = null;
                }
            } finally {
                turnBasedMatchBuffer.release();
            }
        }

        public TurnBasedMatch getMatch() {
            return this.WY;
        }
    }

    private final class TurnBasedMatchUpdatedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.UpdateMatchResult> XH;

        public TurnBasedMatchUpdatedBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.UpdateMatchResult> resultHolder) {
            this.XH = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void r(DataHolder dataHolder) {
            this.XH.b(new UpdateMatchResultImpl(dataHolder));
        }
    }

    private final class TurnBasedMatchesLoadedBinderCallbacks extends AbstractGamesCallbacks {
        private final BaseImplementation.b<TurnBasedMultiplayer.LoadMatchesResult> XI;

        public TurnBasedMatchesLoadedBinderCallbacks(BaseImplementation.b<TurnBasedMultiplayer.LoadMatchesResult> resultHolder) {
            this.XI = (BaseImplementation.b) n.b(resultHolder, "Holder must not be null");
        }

        @Override // com.google.android.gms.games.internal.AbstractGamesCallbacks, com.google.android.gms.games.internal.IGamesCallbacks
        public void b(int i, Bundle bundle) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.XI.b(new LoadMatchesResultImpl(new Status(i), bundle));
        }
    }

    private static final class UpdateAchievementResultImpl implements Achievements.UpdateAchievementResult {
        private final Status CM;
        private final String VP;

        UpdateAchievementResultImpl(int statusCode, String achievementId) {
            this.CM = new Status(statusCode);
            this.VP = achievementId;
        }

        @Override // com.google.android.gms.games.achievement.Achievements.UpdateAchievementResult
        public String getAchievementId() {
            return this.VP;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private static final class UpdateMatchResultImpl extends TurnBasedMatchResult implements TurnBasedMultiplayer.UpdateMatchResult {
        UpdateMatchResultImpl(DataHolder dataHolder) {
            super(dataHolder);
        }
    }

    private static final class UpdateRequestsResultImpl extends a implements Requests.UpdateRequestsResult {
        private final RequestUpdateOutcomes XJ;

        UpdateRequestsResultImpl(DataHolder dataHolder) {
            super(dataHolder);
            this.XJ = RequestUpdateOutcomes.V(dataHolder);
        }

        @Override // com.google.android.gms.games.request.Requests.UpdateRequestsResult
        public Set<String> getRequestIds() {
            return this.XJ.getRequestIds();
        }

        @Override // com.google.android.gms.games.request.Requests.UpdateRequestsResult
        public int getRequestOutcome(String requestId) {
            return this.XJ.getRequestOutcome(requestId);
        }
    }

    public GamesClientImpl(Context context, Looper looper, String gamePackageName, String accountName, GoogleApiClient.ConnectionCallbacks connectedListener, GoogleApiClient.OnConnectionFailedListener connectionFailedListener, String[] scopes, int gravity, View gamesContentView, Games.GamesOptions options) {
        super(context, looper, connectedListener, connectionFailedListener, scopes);
        this.Wh = new EventIncrementManager() { // from class: com.google.android.gms.games.internal.GamesClientImpl.1
            @Override // com.google.android.gms.games.internal.events.EventIncrementManager
            public EventIncrementCache kv() {
                return GamesClientImpl.this.new GameClientEventIncrementCache();
            }
        };
        this.Wn = false;
        this.Wi = gamePackageName;
        this.Dd = (String) n.i(accountName);
        this.Wo = new Binder();
        this.Wj = new HashMap();
        this.Wm = PopupManager.a(this, gravity);
        k(gamesContentView);
        this.Wp = hashCode();
        this.Wq = options;
        registerConnectionCallbacks(this);
        registerConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Room R(DataHolder dataHolder) {
        RoomBuffer roomBuffer = new RoomBuffer(dataHolder);
        try {
            return roomBuffer.getCount() > 0 ? roomBuffer.get(0).freeze() : null;
        } finally {
            roomBuffer.release();
        }
    }

    private RealTimeSocket bw(String str) {
        RealTimeSocket realTimeSocketBy = kc.hD() ? by(str) : bx(str);
        if (realTimeSocketBy != null) {
            this.Wj.put(str, realTimeSocketBy);
        }
        return realTimeSocketBy;
    }

    private RealTimeSocket bx(String str) throws IOException {
        try {
            String strBC = gS().bC(str);
            if (strBC == null) {
                return null;
            }
            LocalSocket localSocket = new LocalSocket();
            localSocket.connect(new LocalSocketAddress(strBC));
            return new RealTimeSocketImpl(localSocket, str);
        } catch (RemoteException e) {
            GamesLog.q("GamesClientImpl", "Unable to create socket. Service died.");
            return null;
        } catch (IOException e2) {
            GamesLog.q("GamesClientImpl", "connect() call failed on socket: " + e2.getMessage());
            return null;
        }
    }

    private RealTimeSocket by(String str) {
        LibjingleNativeSocket libjingleNativeSocket;
        try {
            ParcelFileDescriptor parcelFileDescriptorBH = gS().bH(str);
            if (parcelFileDescriptorBH != null) {
                GamesLog.o("GamesClientImpl", "Created native libjingle socket.");
                libjingleNativeSocket = new LibjingleNativeSocket(parcelFileDescriptorBH);
            } else {
                GamesLog.q("GamesClientImpl", "Unable to create socket for " + str);
                libjingleNativeSocket = null;
            }
            return libjingleNativeSocket;
        } catch (RemoteException e) {
            GamesLog.q("GamesClientImpl", "Unable to create socket. Service died.");
            return null;
        }
    }

    private void jW() {
        this.Wk = null;
    }

    private void kt() {
        Iterator<RealTimeSocket> it = this.Wj.values().iterator();
        while (it.hasNext()) {
            try {
                it.next().close();
            } catch (IOException e) {
                GamesLog.c("GamesClientImpl", "IOException:", e);
            }
        }
        this.Wj.clear();
    }

    public int a(RealTimeMultiplayer.ReliableMessageSentCallback reliableMessageSentCallback, byte[] bArr, String str, String str2) {
        try {
            return gS().a(new RealTimeReliableMessageBinderCallbacks(reliableMessageSentCallback), bArr, str, str2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public int a(byte[] bArr, String str, String[] strArr) {
        n.b(strArr, "Participant IDs must not be null");
        try {
            return gS().b(bArr, str, strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public Intent a(int i, int i2, boolean z) {
        try {
            return gS().a(i, i2, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent a(int i, byte[] bArr, int i2, Bitmap bitmap, String str) {
        try {
            Intent intentA = gS().a(i, bArr, i2, str);
            n.b(bitmap, "Must provide a non null icon");
            intentA.putExtra("com.google.android.gms.games.REQUEST_ITEM_ICON", bitmap);
            return intentA;
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent a(Room room, int i) {
        try {
            return gS().a((RoomEntity) room.freeze(), i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent a(String str, boolean z, boolean z2, int i) {
        try {
            return gS().a(str, z, z2, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(int i, IBinder iBinder, Bundle bundle) {
        if (i == 0 && bundle != null) {
            this.Wn = bundle.getBoolean("show_welcome_popup");
        }
        super.a(i, iBinder, bundle);
    }

    public void a(IBinder iBinder, Bundle bundle) {
        if (isConnected()) {
            try {
                gS().a(iBinder, bundle);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
            }
        }
    }

    public void a(BaseImplementation.b<Requests.LoadRequestsResult> bVar, int i, int i2, int i3) {
        try {
            gS().a(new RequestsLoadedBinderCallbacks(bVar), i, i2, i3);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, int i, int i2, boolean z, boolean z2) {
        try {
            gS().a(new ExtendedGamesLoadedBinderCallback(bVar), i, i2, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, int i, boolean z, boolean z2) {
        try {
            gS().a(new PlayersLoadedBinderCallback(bVar), i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.LoadMatchesResult> bVar, int i, int[] iArr) {
        try {
            gS().a(new TurnBasedMatchesLoadedBinderCallbacks(bVar), i, iArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.LoadScoresResult> bVar, LeaderboardScoreBuffer leaderboardScoreBuffer, int i, int i2) {
        try {
            gS().a(new LeaderboardScoresLoadedBinderCallback(bVar), leaderboardScoreBuffer.ly().lz(), i, i2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> bVar, TurnBasedMatchConfig turnBasedMatchConfig) {
        try {
            gS().a(new TurnBasedMatchInitiatedBinderCallbacks(bVar), turnBasedMatchConfig.getVariant(), turnBasedMatchConfig.lF(), turnBasedMatchConfig.getInvitedPlayerIds(), turnBasedMatchConfig.getAutoMatchCriteria());
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Snapshots.CommitSnapshotResult> bVar, Snapshot snapshot, SnapshotMetadataChange snapshotMetadataChange) {
        SnapshotContents snapshotContents = snapshot.getSnapshotContents();
        n.a(!snapshotContents.isClosed(), "Snapshot already closed");
        com.google.android.gms.common.data.a aVarLK = snapshotMetadataChange.lK();
        if (aVarLK != null) {
            aVarLK.a(getContext().getCacheDir());
        }
        Contents contents = snapshotContents.getContents();
        snapshotContents.close();
        try {
            gS().a(new SnapshotCommittedBinderCallbacks(bVar), snapshot.getMetadata().getSnapshotId(), snapshotMetadataChange, contents);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str) {
        try {
            gS().a(new PlayersLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Achievements.UpdateAchievementResult> bVar, String str, int i) {
        AchievementUpdatedBinderCallback achievementUpdatedBinderCallback;
        if (bVar == null) {
            achievementUpdatedBinderCallback = null;
        } else {
            try {
                achievementUpdatedBinderCallback = new AchievementUpdatedBinderCallback(bVar);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
                return;
            }
        }
        gS().a(achievementUpdatedBinderCallback, str, i, this.Wm.kL(), this.Wm.kK());
    }

    public void a(BaseImplementation.b<Leaderboards.LoadScoresResult> bVar, String str, int i, int i2, int i3, boolean z) {
        try {
            gS().a(new LeaderboardScoresLoadedBinderCallback(bVar), str, i, i2, i3, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str, int i, boolean z) {
        try {
            gS().a(new PlayersLoadedBinderCallback(bVar), str, i, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str, int i, boolean z, boolean z2) {
        switch (str) {
            case "played_with":
                try {
                    gS().d(new PlayersLoadedBinderCallback(bVar), str, i, z, z2);
                    return;
                } catch (RemoteException e) {
                    GamesLog.p("GamesClientImpl", "service died");
                    return;
                }
            default:
                throw new IllegalArgumentException("Invalid player collection: " + str);
        }
    }

    public void a(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str, int i, boolean z, boolean z2, boolean z3, boolean z4) {
        try {
            gS().a(new ExtendedGamesLoadedBinderCallback(bVar), str, i, z, z2, z3, z4);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.LoadMatchesResult> bVar, String str, int i, int[] iArr) {
        try {
            gS().a(new TurnBasedMatchesLoadedBinderCallbacks(bVar), str, i, iArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.SubmitScoreResult> bVar, String str, long j, String str2) {
        SubmitScoreBinderCallbacks submitScoreBinderCallbacks;
        if (bVar == null) {
            submitScoreBinderCallbacks = null;
        } else {
            try {
                submitScoreBinderCallbacks = new SubmitScoreBinderCallbacks(bVar);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
                return;
            }
        }
        gS().a(submitScoreBinderCallbacks, str, j, str2);
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.LeaveMatchResult> bVar, String str, String str2) {
        try {
            gS().c(new TurnBasedMatchLeftBinderCallbacks(bVar), str, str2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.LoadPlayerScoreResult> bVar, String str, String str2, int i, int i2) {
        try {
            gS().a(new PlayerLeaderboardScoreLoadedBinderCallback(bVar), str, str2, i, i2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Requests.LoadRequestsResult> bVar, String str, String str2, int i, int i2, int i3) {
        try {
            gS().a(new RequestsLoadedBinderCallbacks(bVar), str, str2, i, i2, i3);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.LoadScoresResult> bVar, String str, String str2, int i, int i2, int i3, boolean z) {
        try {
            gS().a(new LeaderboardScoresLoadedBinderCallback(bVar), str, str2, i, i2, i3, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str, String str2, int i, boolean z, boolean z2) {
        switch (str) {
            case "circled":
            case "played_with":
            case "nearby":
                try {
                    gS().a(new PlayersLoadedBinderCallback(bVar), str, str2, i, z, z2);
                    return;
                } catch (RemoteException e) {
                    GamesLog.p("GamesClientImpl", "service died");
                    return;
                }
            default:
                throw new IllegalArgumentException("Invalid player collection: " + str);
        }
    }

    public void a(BaseImplementation.b<Snapshots.OpenSnapshotResult> bVar, String str, String str2, SnapshotMetadataChange snapshotMetadataChange, SnapshotContents snapshotContents) {
        n.a(!snapshotContents.isClosed(), "SnapshotContents already closed");
        com.google.android.gms.common.data.a aVarLK = snapshotMetadataChange.lK();
        if (aVarLK != null) {
            aVarLK.a(getContext().getCacheDir());
        }
        Contents contents = snapshotContents.getContents();
        snapshotContents.close();
        try {
            gS().a(new SnapshotOpenedBinderCallbacks(bVar), str, str2, snapshotMetadataChange, contents);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> bVar, String str, String str2, boolean z) {
        try {
            gS().b(new LeaderboardsLoadedBinderCallback(bVar), str, str2, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Quests.LoadQuestsResult> bVar, String str, String str2, boolean z, String[] strArr) {
        try {
            this.Wh.flush();
            gS().a(new QuestsLoadedBinderCallbacks(bVar), str, str2, strArr, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Quests.LoadQuestsResult> bVar, String str, String str2, int[] iArr, int i, boolean z) {
        try {
            this.Wh.flush();
            gS().a(new QuestsLoadedBinderCallbacks(bVar), str, str2, iArr, i, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Requests.UpdateRequestsResult> bVar, String str, String str2, String[] strArr) {
        try {
            gS().a(new RequestsUpdatedBinderCallbacks(bVar), str, str2, strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> bVar, String str, boolean z) {
        try {
            gS().c(new LeaderboardsLoadedBinderCallback(bVar), str, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.UpdateMatchResult> bVar, String str, byte[] bArr, String str2, ParticipantResult[] participantResultArr) {
        try {
            gS().a(new TurnBasedMatchUpdatedBinderCallbacks(bVar), str, bArr, str2, participantResultArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<TurnBasedMultiplayer.UpdateMatchResult> bVar, String str, byte[] bArr, ParticipantResult[] participantResultArr) {
        try {
            gS().a(new TurnBasedMatchUpdatedBinderCallbacks(bVar), str, bArr, participantResultArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Requests.SendRequestResult> bVar, String str, String[] strArr, int i, byte[] bArr, int i2) {
        try {
            gS().a(new RequestSentBinderCallbacks(bVar), str, strArr, i, bArr, i2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, boolean z) {
        try {
            gS().c(new PlayersLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Status> bVar, boolean z, Bundle bundle) {
        try {
            gS().a(new ContactSettingsUpdatedBinderCallback(bVar), z, bundle);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Events.LoadEventsResult> bVar, boolean z, String... strArr) {
        try {
            this.Wh.flush();
            gS().a(new EventsLoadedBinderCallback(bVar), z, strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Quests.LoadQuestsResult> bVar, int[] iArr, int i, boolean z) {
        try {
            this.Wh.flush();
            gS().a(new QuestsLoadedBinderCallbacks(bVar), iArr, i, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(BaseImplementation.b<Players.LoadPlayersResult> bVar, String[] strArr) {
        try {
            gS().c(new PlayersLoadedBinderCallback(bVar), strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(k kVar, d.e eVar) throws RemoteException {
        String string = getContext().getResources().getConfiguration().locale.toString();
        Bundle bundle = new Bundle();
        bundle.putBoolean("com.google.android.gms.games.key.isHeadless", this.Wq.Vs);
        bundle.putBoolean("com.google.android.gms.games.key.showConnectingPopup", this.Wq.Vt);
        bundle.putInt("com.google.android.gms.games.key.connectingPopupGravity", this.Wq.Vu);
        bundle.putBoolean("com.google.android.gms.games.key.retryingSignIn", this.Wq.Vv);
        bundle.putInt("com.google.android.gms.games.key.sdkVariant", this.Wq.Vw);
        bundle.putString("com.google.android.gms.games.key.forceResolveAccountKey", this.Wq.Vx);
        bundle.putStringArrayList("com.google.android.gms.games.key.proxyApis", this.Wq.Vy);
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), this.Dd, gR(), this.Wi, this.Wm.kL(), string, bundle);
    }

    public void a(OnInvitationReceivedListener onInvitationReceivedListener) {
        try {
            gS().a(new InvitationReceivedBinderCallback(onInvitationReceivedListener), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(RoomConfig roomConfig) {
        kt();
        try {
            gS().a(new RoomBinderCallbacks(roomConfig.getRoomUpdateListener(), roomConfig.getRoomStatusUpdateListener(), roomConfig.getMessageReceivedListener()), this.Wo, roomConfig.getVariant(), roomConfig.getInvitedPlayerIds(), roomConfig.getAutoMatchCriteria(), roomConfig.isSocketEnabled(), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(RoomUpdateListener roomUpdateListener, String str) {
        try {
            gS().c(new RoomBinderCallbacks(roomUpdateListener), str);
            kt();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(OnTurnBasedMatchUpdateReceivedListener onTurnBasedMatchUpdateReceivedListener) {
        try {
            gS().b(new MatchUpdateReceivedBinderCallback(onTurnBasedMatchUpdateReceivedListener), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(QuestUpdateListener questUpdateListener) {
        try {
            gS().d(new QuestUpdateBinderCallback(questUpdateListener), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(OnRequestReceivedListener onRequestReceivedListener) {
        try {
            gS().c(new RequestReceivedBinderCallback(onRequestReceivedListener), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void a(Snapshot snapshot) {
        SnapshotContents snapshotContents = snapshot.getSnapshotContents();
        n.a(!snapshotContents.isClosed(), "Snapshot already closed");
        Contents contents = snapshotContents.getContents();
        snapshotContents.close();
        try {
            gS().a(contents);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: az, reason: merged with bridge method [inline-methods] */
    public IGamesService j(IBinder iBinder) {
        return IGamesService.Stub.aB(iBinder);
    }

    public Intent b(int i, int i2, boolean z) {
        try {
            return gS().b(i, i2, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent b(int[] iArr) {
        try {
            return gS().b(iArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public void b(BaseImplementation.b<Status> bVar) {
        try {
            this.Wh.flush();
            gS().a(new SignOutCompleteBinderCallbacks(bVar));
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Players.LoadPlayersResult> bVar, int i, boolean z, boolean z2) {
        try {
            gS().b(new PlayersLoadedBinderCallback(bVar), i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Achievements.UpdateAchievementResult> bVar, String str) {
        AchievementUpdatedBinderCallback achievementUpdatedBinderCallback;
        if (bVar == null) {
            achievementUpdatedBinderCallback = null;
        } else {
            try {
                achievementUpdatedBinderCallback = new AchievementUpdatedBinderCallback(bVar);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
                return;
            }
        }
        gS().a(achievementUpdatedBinderCallback, str, this.Wm.kL(), this.Wm.kK());
    }

    public void b(BaseImplementation.b<Achievements.UpdateAchievementResult> bVar, String str, int i) {
        AchievementUpdatedBinderCallback achievementUpdatedBinderCallback;
        if (bVar == null) {
            achievementUpdatedBinderCallback = null;
        } else {
            try {
                achievementUpdatedBinderCallback = new AchievementUpdatedBinderCallback(bVar);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
                return;
            }
        }
        gS().b(achievementUpdatedBinderCallback, str, i, this.Wm.kL(), this.Wm.kK());
    }

    public void b(BaseImplementation.b<Leaderboards.LoadScoresResult> bVar, String str, int i, int i2, int i3, boolean z) {
        try {
            gS().b(new LeaderboardScoresLoadedBinderCallback(bVar), str, i, i2, i3, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().a(new ExtendedGamesLoadedBinderCallback(bVar), str, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Quests.ClaimMilestoneResult> bVar, String str, String str2) {
        try {
            this.Wh.flush();
            gS().f(new QuestMilestoneClaimBinderCallbacks(bVar, str2), str, str2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Leaderboards.LoadScoresResult> bVar, String str, String str2, int i, int i2, int i3, boolean z) {
        try {
            gS().b(new LeaderboardScoresLoadedBinderCallback(bVar), str, str2, i, i2, i3, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Achievements.LoadAchievementsResult> bVar, String str, String str2, boolean z) {
        try {
            gS().a(new AchievementsLoadedBinderCallback(bVar), str, str2, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Snapshots.OpenSnapshotResult> bVar, String str, boolean z) {
        try {
            gS().e(new SnapshotOpenedBinderCallbacks(bVar), str, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> bVar, boolean z) {
        try {
            gS().b(new LeaderboardsLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Quests.LoadQuestsResult> bVar, boolean z, String[] strArr) {
        try {
            this.Wh.flush();
            gS().a(new QuestsLoadedBinderCallbacks(bVar), strArr, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(BaseImplementation.b<Requests.UpdateRequestsResult> bVar, String[] strArr) {
        try {
            gS().a(new RequestsUpdatedBinderCallbacks(bVar), strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void b(RoomConfig roomConfig) {
        kt();
        try {
            gS().a(new RoomBinderCallbacks(roomConfig.getRoomUpdateListener(), roomConfig.getRoomStatusUpdateListener(), roomConfig.getMessageReceivedListener()), this.Wo, roomConfig.getInvitationId(), roomConfig.isSocketEnabled(), this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void bA(String str) {
        try {
            gS().a(str, this.Wm.kL(), this.Wm.kK());
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public Intent bu(String str) {
        try {
            return gS().bu(str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public void bv(String str) {
        try {
            gS().bG(str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public Intent bz(String str) {
        try {
            return gS().bz(str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public void c(BaseImplementation.b<Invitations.LoadInvitationsResult> bVar, int i) {
        try {
            gS().a((IGamesCallbacks) new InvitationsLoadedBinderCallback(bVar), i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Players.LoadPlayersResult> bVar, int i, boolean z, boolean z2) {
        try {
            gS().c(new PlayersLoadedBinderCallback(bVar), i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Achievements.UpdateAchievementResult> bVar, String str) {
        AchievementUpdatedBinderCallback achievementUpdatedBinderCallback;
        if (bVar == null) {
            achievementUpdatedBinderCallback = null;
        } else {
            try {
                achievementUpdatedBinderCallback = new AchievementUpdatedBinderCallback(bVar);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
                return;
            }
        }
        gS().b(achievementUpdatedBinderCallback, str, this.Wm.kL(), this.Wm.kK());
    }

    public void c(BaseImplementation.b<Players.LoadXpStreamResult> bVar, String str, int i) {
        try {
            gS().b(new PlayerXpStreamLoadedBinderCallback(bVar), str, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().e(new ExtendedGamesLoadedBinderCallback(bVar), str, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> bVar, String str, String str2) {
        try {
            gS().d(new TurnBasedMatchInitiatedBinderCallbacks(bVar), str, str2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Snapshots.LoadSnapshotsResult> bVar, String str, String str2, boolean z) {
        try {
            gS().c(new SnapshotsLoadedBinderCallbacks(bVar), str, str2, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Leaderboards.LeaderboardMetadataResult> bVar, String str, boolean z) {
        try {
            gS().d(new LeaderboardsLoadedBinderCallback(bVar), str, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Achievements.LoadAchievementsResult> bVar, boolean z) {
        try {
            gS().a(new AchievementsLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void c(BaseImplementation.b<Requests.UpdateRequestsResult> bVar, String[] strArr) {
        try {
            gS().b(new RequestsUpdatedBinderCallbacks(bVar), strArr);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void c(String... strArr) {
        boolean z = false;
        boolean z2 = false;
        for (String str : strArr) {
            if (str.equals(Scopes.GAMES)) {
                z2 = true;
            } else if (str.equals("https://www.googleapis.com/auth/games.firstparty")) {
                z = true;
            }
        }
        if (z) {
            n.a(!z2, "Cannot have both %s and %s!", Scopes.GAMES, "https://www.googleapis.com/auth/games.firstparty");
        } else {
            n.a(z2, "Games APIs requires %s to function.", Scopes.GAMES);
        }
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void connect() throws PackageManager.NameNotFoundException {
        jW();
        super.connect();
    }

    public int d(byte[] bArr, String str) {
        try {
            return gS().b(bArr, str, (String[]) null);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public void d(BaseImplementation.b<Players.LoadPlayersResult> bVar, int i, boolean z, boolean z2) {
        try {
            gS().e(new PlayersLoadedBinderCallback(bVar), i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> bVar, String str) {
        try {
            gS().l(new TurnBasedMatchInitiatedBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<Players.LoadXpStreamResult> bVar, String str, int i) {
        try {
            gS().c(new PlayerXpStreamLoadedBinderCallback(bVar), str, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().f(new ExtendedGamesLoadedBinderCallback(bVar), str, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> bVar, String str, String str2) {
        try {
            gS().e(new TurnBasedMatchInitiatedBinderCallbacks(bVar), str, str2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<Notifications.GameMuteStatusChangeResult> bVar, String str, boolean z) {
        try {
            gS().a(new GameMuteStatusChangedBinderCallback(bVar), str, z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void d(BaseImplementation.b<Events.LoadEventsResult> bVar, boolean z) {
        try {
            this.Wh.flush();
            gS().f(new EventsLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void dB(int i) {
        this.Wm.setGravity(i);
    }

    public void dC(int i) {
        try {
            gS().dC(i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        this.Wn = false;
        if (isConnected()) {
            try {
                IGamesService iGamesServiceGS = gS();
                iGamesServiceGS.ku();
                this.Wh.flush();
                iGamesServiceGS.q(this.Wp);
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "Failed to notify client disconnect.");
            }
        }
        kt();
        super.disconnect();
    }

    public void e(BaseImplementation.b<Players.LoadPlayersResult> bVar, int i, boolean z, boolean z2) {
        try {
            gS().d(new PlayersLoadedBinderCallback(bVar), i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void e(BaseImplementation.b<TurnBasedMultiplayer.InitiateMatchResult> bVar, String str) {
        try {
            gS().m(new TurnBasedMatchInitiatedBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void e(BaseImplementation.b<Invitations.LoadInvitationsResult> bVar, String str, int i) {
        try {
            gS().b((IGamesCallbacks) new InvitationsLoadedBinderCallback(bVar), str, i, false);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void e(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().c(new ExtendedGamesLoadedBinderCallback(bVar), str, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void e(BaseImplementation.b<Snapshots.LoadSnapshotsResult> bVar, boolean z) {
        try {
            gS().d(new SnapshotsLoadedBinderCallbacks(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void f(BaseImplementation.b<GamesMetadata.LoadGamesResult> bVar) {
        try {
            gS().d(new GamesLoadedBinderCallback(bVar));
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void f(BaseImplementation.b<TurnBasedMultiplayer.LeaveMatchResult> bVar, String str) {
        try {
            gS().o(new TurnBasedMatchLeftBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void f(BaseImplementation.b<Requests.LoadRequestSummariesResult> bVar, String str, int i) {
        try {
            gS().a((IGamesCallbacks) new RequestSummariesLoadedBinderCallbacks(bVar), str, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void f(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().b(new PlayersLoadedBinderCallback(bVar), str, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void f(BaseImplementation.b<Players.LoadProfileSettingsResult> bVar, boolean z) {
        try {
            gS().g(new ProfileSettingsLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.internal.e.b
    public Bundle fD() {
        try {
            Bundle bundleFD = gS().fD();
            if (bundleFD == null) {
                return bundleFD;
            }
            bundleFD.setClassLoader(GamesClientImpl.class.getClassLoader());
            return bundleFD;
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public void g(BaseImplementation.b<Players.LoadOwnerCoverPhotoUrisResult> bVar) {
        try {
            gS().j(new OwnerCoverPhotoUrisLoadedBinderCallback(bVar));
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void g(BaseImplementation.b<TurnBasedMultiplayer.CancelMatchResult> bVar, String str) {
        try {
            gS().n(new TurnBasedMatchCanceledBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void g(BaseImplementation.b<Players.LoadPlayersResult> bVar, String str, int i, boolean z, boolean z2) {
        try {
            gS().b(new PlayersLoadedBinderCallback(bVar), str, (String) null, i, z, z2);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void g(BaseImplementation.b<Status> bVar, boolean z) {
        try {
            gS().h(new ProfileSettingsUpdatedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.games.internal.IGamesService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.games.service.START";
    }

    public void h(BaseImplementation.b<Acls.LoadAclResult> bVar) {
        try {
            gS().h(new NotifyAclLoadedBinderCallback(bVar));
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void h(BaseImplementation.b<TurnBasedMultiplayer.LoadMatchResult> bVar, String str) {
        try {
            gS().p(new TurnBasedMatchLoadedBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void h(BaseImplementation.b<Notifications.ContactSettingLoadResult> bVar, boolean z) {
        try {
            gS().e(new ContactSettingsLoadedBinderCallback(bVar), z);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Deprecated
    public void i(BaseImplementation.b<Notifications.ContactSettingLoadResult> bVar) {
        try {
            gS().e((IGamesCallbacks) new ContactSettingsLoadedBinderCallback(bVar), false);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void i(BaseImplementation.b<Quests.AcceptQuestResult> bVar, String str) {
        try {
            this.Wh.flush();
            gS().u(new QuestAcceptedBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void j(BaseImplementation.b<Notifications.InboxCountResult> bVar) {
        try {
            gS().t(new InboxCountsLoadedBinderCallback(bVar), null);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void j(BaseImplementation.b<Snapshots.DeleteSnapshotResult> bVar, String str) {
        try {
            gS().r(new SnapshotDeletedBinderCallbacks(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public String jX() {
        try {
            return gS().jX();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public String jY() {
        try {
            return gS().jY();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Player jZ() {
        dK();
        synchronized (this) {
            if (this.Wk == null) {
                try {
                    PlayerBuffer playerBuffer = new PlayerBuffer(gS().kw());
                    try {
                        if (playerBuffer.getCount() > 0) {
                            this.Wk = (PlayerEntity) playerBuffer.get(0).freeze();
                        }
                    } finally {
                        playerBuffer.release();
                    }
                } catch (RemoteException e) {
                    GamesLog.p("GamesClientImpl", "service died");
                }
            }
        }
        return this.Wk;
    }

    public void k(View view) {
        this.Wm.l(view);
    }

    public void k(BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult> bVar, String str) {
        try {
            gS().e(new ExtendedGamesLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public Game ka() {
        dK();
        synchronized (this) {
            if (this.Wl == null) {
                try {
                    GameBuffer gameBuffer = new GameBuffer(gS().ky());
                    try {
                        if (gameBuffer.getCount() > 0) {
                            this.Wl = (GameEntity) gameBuffer.get(0).freeze();
                        }
                    } finally {
                        gameBuffer.release();
                    }
                } catch (RemoteException e) {
                    GamesLog.p("GamesClientImpl", "service died");
                }
            }
        }
        return this.Wl;
    }

    public Intent kb() {
        try {
            return gS().kb();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent kc() {
        try {
            return gS().kc();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent kd() {
        try {
            return gS().kd();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent ke() {
        try {
            return gS().ke();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public void kf() {
        try {
            gS().r(this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void kg() {
        try {
            gS().s(this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void kh() {
        try {
            gS().u(this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void ki() {
        try {
            gS().t(this.Wp);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public Intent kj() {
        try {
            return gS().kj();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public Intent kk() {
        try {
            return gS().kk();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public int kl() {
        try {
            return gS().kl();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return 4368;
        }
    }

    public String km() {
        try {
            return gS().km();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public int kn() {
        try {
            return gS().kn();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public Intent ko() {
        try {
            return gS().ko();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return null;
        }
    }

    public int kp() {
        try {
            return gS().kp();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public int kq() {
        try {
            return gS().kq();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public int kr() {
        try {
            return gS().kr();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public int ks() {
        try {
            return gS().ks();
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
            return -1;
        }
    }

    public void ku() {
        if (isConnected()) {
            try {
                gS().ku();
            } catch (RemoteException e) {
                GamesLog.p("GamesClientImpl", "service died");
            }
        }
    }

    public void l(BaseImplementation.b<GamesMetadata.LoadGameInstancesResult> bVar, String str) {
        try {
            gS().f(new GameInstancesLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void m(BaseImplementation.b<GamesMetadata.LoadGameSearchSuggestionsResult> bVar, String str) {
        try {
            gS().q(new GameSearchSuggestionsLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void n(BaseImplementation.b<Players.LoadXpForGameCategoriesResult> bVar, String str) {
        try {
            gS().s(new PlayerXpForGameCategoriesLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void n(String str, int i) {
        this.Wh.n(str, i);
    }

    public void o(BaseImplementation.b<Invitations.LoadInvitationsResult> bVar, String str) {
        try {
            gS().k(new InvitationsLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void o(String str, int i) {
        try {
            gS().o(str, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        if (this.Wn) {
            this.Wm.kJ();
            this.Wn = false;
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        this.Wn = false;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
    }

    public void p(BaseImplementation.b<Status> bVar, String str) {
        try {
            gS().j(new NotifyAclUpdatedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void p(String str, int i) {
        try {
            gS().p(str, i);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public void q(BaseImplementation.b<Notifications.GameMuteStatusLoadResult> bVar, String str) {
        try {
            gS().i(new GameMuteStatusLoadedBinderCallback(bVar), str);
        } catch (RemoteException e) {
            GamesLog.p("GamesClientImpl", "service died");
        }
    }

    public RealTimeSocket t(String str, String str2) {
        if (str2 == null || !ParticipantUtils.bS(str2)) {
            throw new IllegalArgumentException("Bad participant ID");
        }
        RealTimeSocket realTimeSocket = this.Wj.get(str2);
        return (realTimeSocket == null || realTimeSocket.isClosed()) ? bw(str2) : realTimeSocket;
    }
}
