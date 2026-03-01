package com.google.android.gms.cast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.ij;
import com.google.android.gms.internal.iq;
import com.google.android.gms.internal.ir;
import com.google.android.gms.internal.is;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class RemoteMediaPlayer implements Cast.MessageReceivedCallback {
    public static final int RESUME_STATE_PAUSE = 2;
    public static final int RESUME_STATE_PLAY = 1;
    public static final int RESUME_STATE_UNCHANGED = 0;
    public static final int STATUS_CANCELED = 2101;
    public static final int STATUS_FAILED = 2100;
    public static final int STATUS_REPLACED = 2103;
    public static final int STATUS_SUCCEEDED = 0;
    public static final int STATUS_TIMED_OUT = 2102;
    private OnMetadataUpdatedListener FI;
    private OnStatusUpdatedListener FJ;
    private final Object mw = new Object();
    private final a FH = new a();
    private final iq FG = new iq() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.1
        @Override // com.google.android.gms.internal.iq
        protected void onMetadataUpdated() {
            RemoteMediaPlayer.this.onMetadataUpdated();
        }

        @Override // com.google.android.gms.internal.iq
        protected void onStatusUpdated() {
            RemoteMediaPlayer.this.onStatusUpdated();
        }
    };

    public interface MediaChannelResult extends Result {
        JSONObject getCustomData();
    }

    public interface OnMetadataUpdatedListener {
        void onMetadataUpdated();
    }

    public interface OnStatusUpdatedListener {
        void onStatusUpdated();
    }

    private class a implements ir {
        private GoogleApiClient FX;
        private long FY = 0;

        /* renamed from: com.google.android.gms.cast.RemoteMediaPlayer$a$a, reason: collision with other inner class name */
        private final class C0002a implements ResultCallback<Status> {
            private final long FZ;

            C0002a(long j) {
                this.FZ = j;
            }

            @Override // com.google.android.gms.common.api.ResultCallback
            /* renamed from: k, reason: merged with bridge method [inline-methods] */
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    return;
                }
                RemoteMediaPlayer.this.FG.b(this.FZ, status.getStatusCode());
            }
        }

        public a() {
        }

        @Override // com.google.android.gms.internal.ir
        public void a(String str, String str2, long j, String str3) throws IOException {
            if (this.FX == null) {
                throw new IOException("No GoogleApiClient available");
            }
            Cast.CastApi.sendMessage(this.FX, str, str2).setResultCallback(new C0002a(j));
        }

        public void b(GoogleApiClient googleApiClient) {
            this.FX = googleApiClient;
        }

        @Override // com.google.android.gms.internal.ir
        public long fy() {
            long j = this.FY + 1;
            this.FY = j;
            return j;
        }
    }

    private static abstract class b extends Cast.a<MediaChannelResult> {
        is Gb = new is() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.b.1
            @Override // com.google.android.gms.internal.is
            public void a(long j, int i, JSONObject jSONObject) {
                b.this.b((b) new c(new Status(i), jSONObject));
            }

            @Override // com.google.android.gms.internal.is
            public void n(long j) {
                b.this.b((b) b.this.c(new Status(2103)));
            }
        };

        b() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: l, reason: merged with bridge method [inline-methods] */
        public MediaChannelResult c(final Status status) {
            return new MediaChannelResult() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.b.2
                @Override // com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult
                public JSONObject getCustomData() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    private static final class c implements MediaChannelResult {
        private final Status CM;
        private final JSONObject Fl;

        c(Status status, JSONObject jSONObject) {
            this.CM = status;
            this.Fl = jSONObject;
        }

        @Override // com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult
        public JSONObject getCustomData() {
            return this.Fl;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    public RemoteMediaPlayer() {
        this.FG.a(this.FH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMetadataUpdated() {
        if (this.FI != null) {
            this.FI.onMetadataUpdated();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStatusUpdated() {
        if (this.FJ != null) {
            this.FJ.onStatusUpdated();
        }
    }

    public long getApproximateStreamPosition() {
        long approximateStreamPosition;
        synchronized (this.mw) {
            approximateStreamPosition = this.FG.getApproximateStreamPosition();
        }
        return approximateStreamPosition;
    }

    public MediaInfo getMediaInfo() {
        MediaInfo mediaInfo;
        synchronized (this.mw) {
            mediaInfo = this.FG.getMediaInfo();
        }
        return mediaInfo;
    }

    public MediaStatus getMediaStatus() {
        MediaStatus mediaStatus;
        synchronized (this.mw) {
            mediaStatus = this.FG.getMediaStatus();
        }
        return mediaStatus;
    }

    public String getNamespace() {
        return this.FG.getNamespace();
    }

    public long getStreamDuration() {
        long streamDuration;
        synchronized (this.mw) {
            streamDuration = this.FG.getStreamDuration();
        }
        return streamDuration;
    }

    public PendingResult<MediaChannelResult> load(GoogleApiClient apiClient, MediaInfo mediaInfo) {
        return load(apiClient, mediaInfo, true, 0L, null, null);
    }

    public PendingResult<MediaChannelResult> load(GoogleApiClient apiClient, MediaInfo mediaInfo, boolean autoplay) {
        return load(apiClient, mediaInfo, autoplay, 0L, null, null);
    }

    public PendingResult<MediaChannelResult> load(GoogleApiClient apiClient, MediaInfo mediaInfo, boolean autoplay, long playPosition) {
        return load(apiClient, mediaInfo, autoplay, playPosition, null, null);
    }

    public PendingResult<MediaChannelResult> load(GoogleApiClient apiClient, MediaInfo mediaInfo, boolean autoplay, long playPosition, JSONObject customData) {
        return load(apiClient, mediaInfo, autoplay, playPosition, null, customData);
    }

    public PendingResult<MediaChannelResult> load(final GoogleApiClient apiClient, final MediaInfo mediaInfo, final boolean autoplay, final long playPosition, final long[] activeTrackIds, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, mediaInfo, autoplay, playPosition, activeTrackIds, customData);
                        } catch (IOException e) {
                            b((AnonymousClass4) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                    }
                }
            }
        });
    }

    @Override // com.google.android.gms.cast.Cast.MessageReceivedCallback
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) throws JSONException {
        this.FG.aD(message);
    }

    public PendingResult<MediaChannelResult> pause(GoogleApiClient apiClient) {
        return pause(apiClient, null);
    }

    public PendingResult<MediaChannelResult> pause(final GoogleApiClient apiClient, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, customData);
                        } catch (IOException e) {
                            b((AnonymousClass5) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> play(GoogleApiClient apiClient) {
        return play(apiClient, null);
    }

    public PendingResult<MediaChannelResult> play(final GoogleApiClient apiClient, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.7
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.c(this.Gb, customData);
                        } catch (IOException e) {
                            b((AnonymousClass7) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> requestStatus(final GoogleApiClient apiClient) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.11
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb);
                        } finally {
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } catch (IOException e) {
                        b((AnonymousClass11) c(new Status(2100)));
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> seek(GoogleApiClient apiClient, long position) {
        return seek(apiClient, position, 0, null);
    }

    public PendingResult<MediaChannelResult> seek(GoogleApiClient apiClient, long position, int resumeState) {
        return seek(apiClient, position, resumeState, null);
    }

    public PendingResult<MediaChannelResult> seek(final GoogleApiClient apiClient, final long position, final int resumeState, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.8
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, position, resumeState, customData);
                        } finally {
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } catch (IOException e) {
                        b((AnonymousClass8) c(new Status(2100)));
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> setActiveMediaTracks(final GoogleApiClient apiClient, final long[] trackIds) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, trackIds);
                        } catch (IOException e) {
                            b((AnonymousClass2) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public void setOnMetadataUpdatedListener(OnMetadataUpdatedListener listener) {
        this.FI = listener;
    }

    public void setOnStatusUpdatedListener(OnStatusUpdatedListener listener) {
        this.FJ = listener;
    }

    public PendingResult<MediaChannelResult> setStreamMute(GoogleApiClient apiClient, boolean muteState) {
        return setStreamMute(apiClient, muteState, null);
    }

    public PendingResult<MediaChannelResult> setStreamMute(final GoogleApiClient apiClient, final boolean muteState, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.10
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, muteState, customData);
                        } catch (IOException e) {
                            b((AnonymousClass10) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        } catch (IllegalStateException e2) {
                            b((AnonymousClass10) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> setStreamVolume(GoogleApiClient apiClient, double volume) throws IllegalArgumentException {
        return setStreamVolume(apiClient, volume, null);
    }

    public PendingResult<MediaChannelResult> setStreamVolume(final GoogleApiClient apiClient, final double volume, final JSONObject customData) throws IllegalArgumentException {
        if (Double.isInfinite(volume) || Double.isNaN(volume)) {
            throw new IllegalArgumentException("Volume cannot be " + volume);
        }
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.9
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            try {
                                RemoteMediaPlayer.this.FG.a(this.Gb, volume, customData);
                            } catch (IOException e) {
                                b((AnonymousClass9) c(new Status(2100)));
                                RemoteMediaPlayer.this.FH.b(null);
                            } catch (IllegalArgumentException e2) {
                                b((AnonymousClass9) c(new Status(2100)));
                            }
                        } catch (IllegalStateException e3) {
                            b((AnonymousClass9) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> setTextTrackStyle(final GoogleApiClient apiClient, final TextTrackStyle trackStyle) {
        if (trackStyle == null) {
            throw new IllegalArgumentException("trackStyle cannot be null");
        }
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.a(this.Gb, trackStyle);
                        } catch (IOException e) {
                            b((AnonymousClass3) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }

    public PendingResult<MediaChannelResult> stop(GoogleApiClient apiClient) {
        return stop(apiClient, null);
    }

    public PendingResult<MediaChannelResult> stop(final GoogleApiClient apiClient, final JSONObject customData) {
        return apiClient.b(new b() { // from class: com.google.android.gms.cast.RemoteMediaPlayer.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ij ijVar) {
                synchronized (RemoteMediaPlayer.this.mw) {
                    RemoteMediaPlayer.this.FH.b(apiClient);
                    try {
                        try {
                            RemoteMediaPlayer.this.FG.b(this.Gb, customData);
                        } catch (IOException e) {
                            b((AnonymousClass6) c(new Status(2100)));
                            RemoteMediaPlayer.this.FH.b(null);
                        }
                    } finally {
                        RemoteMediaPlayer.this.FH.b(null);
                    }
                }
            }
        });
    }
}
