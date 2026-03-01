package com.google.android.gms.games.internal.api;

import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameBuffer;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesMetadata;
import com.google.android.gms.games.internal.GamesClientImpl;

/* loaded from: classes.dex */
public final class GamesMetadataImpl implements GamesMetadata {

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$10, reason: invalid class name */
    class AnonymousClass10 extends LoadExtendedGamesImpl {
        final /* synthetic */ String Yk;
        final /* synthetic */ int Yl;
        final /* synthetic */ boolean Ym;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.a((BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult>) this, this.Yk, this.Yl, false, true, false, this.Ym);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$11, reason: invalid class name */
    class AnonymousClass11 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.c(this, this.XW, this.Yl, false, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$12, reason: invalid class name */
    class AnonymousClass12 extends LoadExtendedGamesImpl {
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.c(this, this.XW, this.Yl, true, false);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$13, reason: invalid class name */
    class AnonymousClass13 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.d(this, this.XW, this.Yl, false, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$14, reason: invalid class name */
    class AnonymousClass14 extends LoadExtendedGamesImpl {
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.d(this, this.XW, this.Yl, true, false);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$15, reason: invalid class name */
    class AnonymousClass15 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String Yk;
        final /* synthetic */ int Yl;
        final /* synthetic */ boolean Ym;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.a((BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult>) this, this.Yk, this.Yl, true, false, this.XU, this.Ym);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$16, reason: invalid class name */
    class AnonymousClass16 extends LoadExtendedGamesImpl {
        final /* synthetic */ String Yk;
        final /* synthetic */ int Yl;
        final /* synthetic */ boolean Ym;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.a((BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult>) this, this.Yk, this.Yl, true, true, false, this.Ym);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$17, reason: invalid class name */
    class AnonymousClass17 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ int Yl;
        final /* synthetic */ String Yn;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.e(this, this.Yn, this.Yl, false, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$18, reason: invalid class name */
    class AnonymousClass18 extends LoadExtendedGamesImpl {
        final /* synthetic */ int Yl;
        final /* synthetic */ String Yn;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.e(this, this.Yn, this.Yl, true, false);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$19, reason: invalid class name */
    class AnonymousClass19 extends LoadGameSearchSuggestionsImpl {
        final /* synthetic */ String Yn;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.m(this, this.Yn);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$2, reason: invalid class name */
    class AnonymousClass2 extends LoadExtendedGamesImpl {
        final /* synthetic */ String XX;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.k(this, this.XX);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$3, reason: invalid class name */
    class AnonymousClass3 extends LoadGameInstancesImpl {
        final /* synthetic */ String XX;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.l(this, this.XX);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$4, reason: invalid class name */
    class AnonymousClass4 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.b(this, null, this.Yl, false, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$5, reason: invalid class name */
    class AnonymousClass5 extends LoadExtendedGamesImpl {
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.b(this, null, this.Yl, true, false);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$6, reason: invalid class name */
    class AnonymousClass6 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.b(this, this.XW, this.Yl, false, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$7, reason: invalid class name */
    class AnonymousClass7 extends LoadExtendedGamesImpl {
        final /* synthetic */ String XW;
        final /* synthetic */ int Yl;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.b(this, this.XW, this.Yl, true, false);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$8, reason: invalid class name */
    class AnonymousClass8 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ int Yl;
        final /* synthetic */ int Yo;
        final /* synthetic */ boolean Yp;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.a(this, this.Yl, this.Yo, this.Yp, this.XU);
        }
    }

    /* renamed from: com.google.android.gms.games.internal.api.GamesMetadataImpl$9, reason: invalid class name */
    class AnonymousClass9 extends LoadExtendedGamesImpl {
        final /* synthetic */ boolean XU;
        final /* synthetic */ String Yk;
        final /* synthetic */ int Yl;
        final /* synthetic */ boolean Ym;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(GamesClientImpl gamesClientImpl) {
            gamesClientImpl.a((BaseImplementation.b<GamesMetadata.LoadExtendedGamesResult>) this, this.Yk, this.Yl, false, false, this.XU, this.Ym);
        }
    }

    private static abstract class LoadExtendedGamesImpl extends Games.BaseGamesApiMethodImpl<GamesMetadata.LoadExtendedGamesResult> {
        private LoadExtendedGamesImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: P, reason: merged with bridge method [inline-methods] */
        public GamesMetadata.LoadExtendedGamesResult c(final Status status) {
            return new GamesMetadata.LoadExtendedGamesResult() { // from class: com.google.android.gms.games.internal.api.GamesMetadataImpl.LoadExtendedGamesImpl.1
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

    private static abstract class LoadGameInstancesImpl extends Games.BaseGamesApiMethodImpl<GamesMetadata.LoadGameInstancesResult> {
        private LoadGameInstancesImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: Q, reason: merged with bridge method [inline-methods] */
        public GamesMetadata.LoadGameInstancesResult c(final Status status) {
            return new GamesMetadata.LoadGameInstancesResult() { // from class: com.google.android.gms.games.internal.api.GamesMetadataImpl.LoadGameInstancesImpl.1
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

    private static abstract class LoadGameSearchSuggestionsImpl extends Games.BaseGamesApiMethodImpl<GamesMetadata.LoadGameSearchSuggestionsResult> {
        private LoadGameSearchSuggestionsImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: R, reason: merged with bridge method [inline-methods] */
        public GamesMetadata.LoadGameSearchSuggestionsResult c(final Status status) {
            return new GamesMetadata.LoadGameSearchSuggestionsResult() { // from class: com.google.android.gms.games.internal.api.GamesMetadataImpl.LoadGameSearchSuggestionsImpl.1
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

    private static abstract class LoadGamesImpl extends Games.BaseGamesApiMethodImpl<GamesMetadata.LoadGamesResult> {
        private LoadGamesImpl() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: S, reason: merged with bridge method [inline-methods] */
        public GamesMetadata.LoadGamesResult c(final Status status) {
            return new GamesMetadata.LoadGamesResult() { // from class: com.google.android.gms.games.internal.api.GamesMetadataImpl.LoadGamesImpl.1
                @Override // com.google.android.gms.games.GamesMetadata.LoadGamesResult
                public GameBuffer getGames() {
                    return new GameBuffer(DataHolder.as(14));
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

    @Override // com.google.android.gms.games.GamesMetadata
    public Game getCurrentGame(GoogleApiClient apiClient) {
        return Games.c(apiClient).ka();
    }

    @Override // com.google.android.gms.games.GamesMetadata
    public PendingResult<GamesMetadata.LoadGamesResult> loadGame(GoogleApiClient apiClient) {
        return apiClient.a((GoogleApiClient) new LoadGamesImpl() { // from class: com.google.android.gms.games.internal.api.GamesMetadataImpl.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(GamesClientImpl gamesClientImpl) {
                gamesClientImpl.f(this);
            }
        });
    }
}
