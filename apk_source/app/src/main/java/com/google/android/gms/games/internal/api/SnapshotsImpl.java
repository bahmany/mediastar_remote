package com.google.android.gms.games.internal.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.internal.GamesClientImpl;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotContents;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataBuffer;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

/* loaded from: classes.dex */
public final class SnapshotsImpl implements Snapshots {

    /* renamed from: com.google.android.gms.games.internal.api.SnapshotsImpl$7, reason: invalid class name */
    class AnonymousClass7 extends LoadImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String XW;
        final /* synthetic */ String XX;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.c(this, this.XW, this.XX, this.XU);
        }
    }

    private static abstract class CommitImpl extends Games.BaseGamesApiMethodImpl<Snapshots.CommitSnapshotResult> {
        private CommitImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: ao, reason: merged with bridge method [inline-methods] */
        public Snapshots.CommitSnapshotResult c(final Status status) {
            return new Snapshots.CommitSnapshotResult() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.CommitImpl.1
                @Override // com.google.android.gms.games.snapshot.Snapshots.CommitSnapshotResult
                public SnapshotMetadata getSnapshotMetadata() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    private static abstract class DeleteImpl extends Games.BaseGamesApiMethodImpl<Snapshots.DeleteSnapshotResult> {
        private DeleteImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: ap, reason: merged with bridge method [inline-methods] */
        public Snapshots.DeleteSnapshotResult c(final Status status) {
            return new Snapshots.DeleteSnapshotResult() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.DeleteImpl.1
                @Override // com.google.android.gms.games.snapshot.Snapshots.DeleteSnapshotResult
                public String getSnapshotId() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    private static abstract class LoadImpl extends Games.BaseGamesApiMethodImpl<Snapshots.LoadSnapshotsResult> {
        private LoadImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aq, reason: merged with bridge method [inline-methods] */
        public Snapshots.LoadSnapshotsResult c(final Status status) {
            return new Snapshots.LoadSnapshotsResult() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.LoadImpl.1
                @Override // com.google.android.gms.games.snapshot.Snapshots.LoadSnapshotsResult
                public SnapshotMetadataBuffer getSnapshots() {
                    return new SnapshotMetadataBuffer(DataHolder.as(14));
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
    }

    private static abstract class OpenImpl extends Games.BaseGamesApiMethodImpl<Snapshots.OpenSnapshotResult> {
        private OpenImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: ar, reason: merged with bridge method [inline-methods] */
        public Snapshots.OpenSnapshotResult c(final Status status) {
            return new Snapshots.OpenSnapshotResult() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.OpenImpl.1
                @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
                public String getConflictId() {
                    return null;
                }

                @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
                public Snapshot getConflictingSnapshot() {
                    return null;
                }

                @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
                public Contents getResolutionContents() {
                    return null;
                }

                @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
                public SnapshotContents getResolutionSnapshotContents() {
                    return null;
                }

                @Override // com.google.android.gms.games.snapshot.Snapshots.OpenSnapshotResult
                public Snapshot getSnapshot() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(GoogleApiClient apiClient, final Snapshot snapshot, final SnapshotMetadataChange metadataChange) {
        return apiClient.b(new CommitImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.a(this, snapshot, metadataChange);
            }
        });
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.DeleteSnapshotResult> delete(GoogleApiClient apiClient, final SnapshotMetadata metadata) {
        return apiClient.b(new DeleteImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.j(this, metadata.getSnapshotId());
            }
        });
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public void discardAndClose(GoogleApiClient apiClient, Snapshot snapshot) {
        Games.c(apiClient).a(snapshot);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public int getMaxCoverImageSize(GoogleApiClient apiClient) {
        return Games.c(apiClient).ks();
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public int getMaxDataSize(GoogleApiClient apiClient) {
        return Games.c(apiClient).kr();
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public Intent getSelectSnapshotIntent(GoogleApiClient apiClient, String title, boolean allowAddButton, boolean allowDelete, int maxSnapshots) {
        return Games.c(apiClient).a(title, allowAddButton, allowDelete, maxSnapshots);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public SnapshotMetadata getSnapshotFromBundle(Bundle extras) {
        if (extras == null || !extras.containsKey(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
            return null;
        }
        return (SnapshotMetadata) extras.getParcelable(Snapshots.EXTRA_SNAPSHOT_METADATA);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.LoadSnapshotsResult> load(GoogleApiClient apiClient, final boolean forceReload) {
        return apiClient.a((GoogleApiClient) new LoadImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.e(this, forceReload);
            }
        });
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.OpenSnapshotResult> open(GoogleApiClient apiClient, SnapshotMetadata metadata) {
        return open(apiClient, metadata.getUniqueName(), false);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.OpenSnapshotResult> open(GoogleApiClient apiClient, final String fileName, final boolean createIfNotFound) {
        return apiClient.b(new OpenImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.b(this, fileName, createIfNotFound);
            }
        });
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(GoogleApiClient apiClient, String conflictId, Snapshot snapshot) {
        SnapshotMetadata metadata = snapshot.getMetadata();
        return resolveConflict(apiClient, conflictId, metadata.getSnapshotId(), new SnapshotMetadataChange.Builder().fromMetadata(metadata).build(), snapshot.getSnapshotContents());
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(GoogleApiClient apiClient, final String conflictId, final String snapshotId, final SnapshotMetadataChange metadataChange, Contents contents) {
        final SnapshotContents snapshotContents = new SnapshotContents(contents);
        return apiClient.b(new OpenImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.5
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) throws RemoteException {
                gamesClientImpl.a(this, conflictId, snapshotId, metadataChange, snapshotContents);
            }
        });
    }

    @Override // com.google.android.gms.games.snapshot.Snapshots
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(GoogleApiClient apiClient, final String conflictId, final String snapshotId, final SnapshotMetadataChange metadataChange, final SnapshotContents snapshotContents) {
        return apiClient.b(new OpenImpl() { // from class: com.google.android.gms.games.internal.api.SnapshotsImpl.6
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) throws RemoteException {
                gamesClientImpl.a(this, conflictId, snapshotId, metadataChange, snapshotContents);
            }
        });
    }
}
