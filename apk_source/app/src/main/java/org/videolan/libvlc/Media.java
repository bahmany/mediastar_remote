package org.videolan.libvlc;

import org.videolan.libvlc.VLCObject;

/* loaded from: classes.dex */
public final class Media extends VLCObject {
    private static final int PARSE_STATUS_INIT = 0;
    private static final int PARSE_STATUS_PARSED = 2;
    private static final int PARSE_STATUS_PARSING = 1;
    private static final String TAG = "LibVLC/Media";
    private long mDuration;
    private String mMrl;
    private String[] mNativeMetas;
    private Track[] mNativeTracks;
    private int mParseStatus;
    private int mState;
    private MediaList mSubItems;
    private int mType;

    public static class Meta {
        public static final int Actors = 22;
        public static final int Album = 4;
        public static final int AlbumArtist = 23;
        public static final int Artist = 1;
        public static final int ArtworkURL = 15;
        public static final int Copyright = 3;
        public static final int Date = 8;
        public static final int Description = 6;
        public static final int Director = 18;
        public static final int DiscNumber = 24;
        public static final int EncodedBy = 14;
        public static final int Episode = 20;
        public static final int Genre = 2;
        public static final int Language = 11;
        public static final int MAX = 25;
        public static final int NowPlaying = 12;
        public static final int Publisher = 13;
        public static final int Rating = 7;
        public static final int Season = 19;
        public static final int Setting = 9;
        public static final int ShowName = 21;
        public static final int Title = 0;
        public static final int TrackID = 16;
        public static final int TrackNumber = 5;
        public static final int TrackTotal = 17;
        public static final int URL = 10;
    }

    public static class Parse {
        public static final int FetchLocal = 2;
        public static final int FetchNetwork = 4;
        public static final int ParseLocal = 0;
        public static final int ParseNetwork = 1;
    }

    public static class State {
        public static final int Buffering = 2;
        public static final int Ended = 6;
        public static final int Error = 7;
        public static final int MAX = 8;
        public static final int NothingSpecial = 0;
        public static final int Opening = 1;
        public static final int Paused = 4;
        public static final int Playing = 3;
        public static final int Stopped = 5;
    }

    public static class Type {
        public static final int Directory = 2;
        public static final int Disc = 3;
        public static final int File = 1;
        public static final int Playlist = 5;
        public static final int Stream = 4;
        public static final int Unknown = 0;
    }

    private native long nativeGetDuration();

    private native String nativeGetMeta(int i);

    private native String[] nativeGetMetas();

    private native String nativeGetMrl();

    private native int nativeGetState();

    private native Track[] nativeGetTracks();

    private native int nativeGetType();

    private native void nativeNewFromMediaList(MediaList mediaList, int i);

    private native void nativeNewFromMrl(LibVLC libVLC, String str);

    private native boolean nativeParse(int i);

    private native boolean nativeParseAsync(int i);

    private native void nativeRelease();

    public static abstract class Track {
        public final int bitrate;
        public final String codec;
        public final String description;
        public final int id;
        public final String language;
        public final int level;
        public final String originalCodec;
        public final int profile;
        public final int type;

        public static class Type {
            public static final int Audio = 0;
            public static final int Text = 2;
            public static final int Unknown = -1;
            public static final int Video = 1;
        }

        private Track(int type, String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description) {
            this.type = type;
            this.codec = codec;
            this.originalCodec = originalCodec;
            this.id = id;
            this.profile = profile;
            this.level = level;
            this.bitrate = bitrate;
            this.language = language;
            this.description = description;
        }

        /* synthetic */ Track(int i, String str, String str2, int i2, int i3, int i4, int i5, String str3, String str4, Track track) {
            this(i, str, str2, i2, i3, i4, i5, str3, str4);
        }
    }

    public static class AudioTrack extends Track {
        public final int channels;
        public final int rate;

        /* synthetic */ AudioTrack(String str, String str2, int i, int i2, int i3, int i4, String str3, String str4, int i5, int i6, AudioTrack audioTrack) {
            this(str, str2, i, i2, i3, i4, str3, str4, i5, i6);
        }

        private AudioTrack(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, int channels, int rate) {
            super(0, codec, originalCodec, id, profile, level, bitrate, language, description, null);
            this.channels = channels;
            this.rate = rate;
        }
    }

    private static Track createAudioTrackFromNative(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, int channels, int rate) {
        return new AudioTrack(codec, originalCodec, id, profile, level, bitrate, language, description, channels, rate, null);
    }

    public static class VideoTrack extends Track {
        public final int frameRateDen;
        public final int frameRateNum;
        public final int height;
        public final int sarDen;
        public final int sarNum;
        public final int width;

        /* synthetic */ VideoTrack(String str, String str2, int i, int i2, int i3, int i4, String str3, String str4, int i5, int i6, int i7, int i8, int i9, int i10, VideoTrack videoTrack) {
            this(str, str2, i, i2, i3, i4, str3, str4, i5, i6, i7, i8, i9, i10);
        }

        private VideoTrack(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, int height, int width, int sarNum, int sarDen, int frameRateNum, int frameRateDen) {
            super(1, codec, originalCodec, id, profile, level, bitrate, language, description, null);
            this.height = height;
            this.width = width;
            this.sarNum = sarNum;
            this.sarDen = sarDen;
            this.frameRateNum = frameRateNum;
            this.frameRateDen = frameRateDen;
        }
    }

    private static Track createVideoTrackFromNative(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, int height, int width, int sarNum, int sarDen, int frameRateNum, int frameRateDen) {
        return new VideoTrack(codec, originalCodec, id, profile, level, bitrate, language, description, height, width, sarNum, sarDen, frameRateNum, frameRateDen, null);
    }

    public static class SubtitleTrack extends Track {
        public final String encoding;

        /* synthetic */ SubtitleTrack(String str, String str2, int i, int i2, int i3, int i4, String str3, String str4, String str5, SubtitleTrack subtitleTrack) {
            this(str, str2, i, i2, i3, i4, str3, str4, str5);
        }

        private SubtitleTrack(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, String encoding) {
            super(2, codec, originalCodec, id, profile, level, bitrate, language, description, null);
            this.encoding = encoding;
        }
    }

    private static Track createSubtitleTrackFromNative(String codec, String originalCodec, int id, int profile, int level, int bitrate, String language, String description, String encoding) {
        return new SubtitleTrack(codec, originalCodec, id, profile, level, bitrate, language, description, encoding, null);
    }

    public Media(LibVLC libVLC, String mrl) {
        this.mMrl = null;
        this.mSubItems = null;
        this.mParseStatus = 0;
        this.mNativeMetas = null;
        this.mNativeTracks = null;
        this.mState = 0;
        this.mType = 0;
        nativeNewFromMrl(libVLC, mrl);
        this.mMrl = nativeGetMrl();
        this.mType = nativeGetType();
    }

    protected Media(MediaList ml, int index) {
        this.mMrl = null;
        this.mSubItems = null;
        this.mParseStatus = 0;
        this.mNativeMetas = null;
        this.mNativeTracks = null;
        this.mState = 0;
        this.mType = 0;
        if (ml.isReleased()) {
            throw new IllegalArgumentException("MediaList is not native");
        }
        nativeNewFromMediaList(ml, index);
        this.mMrl = nativeGetMrl();
        this.mNativeMetas = nativeGetMetas();
        this.mType = nativeGetType();
    }

    @Override // org.videolan.libvlc.VLCObject
    protected synchronized VLCObject.Event onEventNative(int eventType, long arg1, long arg2) {
        switch (eventType) {
            case 0:
                int id = (int) arg1;
                if (id >= 0 && id < 25) {
                    this.mNativeMetas[id] = nativeGetMeta(id);
                    break;
                }
                break;
            case 2:
                this.mDuration = nativeGetDuration();
                break;
            case 3:
                postParse();
                break;
            case 5:
                this.mState = nativeGetState();
                break;
        }
        return new VLCObject.Event(eventType);
    }

    public synchronized String getMrl() {
        return this.mMrl;
    }

    public synchronized long getDuration() {
        return this.mDuration;
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized MediaList subItems() {
        if (this.mSubItems == null && !isReleased()) {
            this.mSubItems = new MediaList(this);
        }
        return this.mSubItems;
    }

    private synchronized void postParse() {
        if (!isReleased() && (this.mParseStatus & 1) != 0 && (this.mParseStatus & 2) == 0) {
            this.mParseStatus &= -2;
            this.mParseStatus |= 2;
            this.mNativeTracks = nativeGetTracks();
            this.mNativeMetas = nativeGetMetas();
            if (this.mNativeMetas != null && this.mNativeMetas.length != 25) {
                throw new IllegalStateException("native metas size doesn't match");
            }
            this.mDuration = nativeGetDuration();
            this.mState = nativeGetState();
            this.mType = nativeGetType();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x001f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean parse(int r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.isReleased()     // Catch: java.lang.Throwable -> L21
            if (r0 != 0) goto L1f
            int r0 = r1.mParseStatus     // Catch: java.lang.Throwable -> L21
            r0 = r0 & 3
            if (r0 != 0) goto L1f
            int r0 = r1.mParseStatus     // Catch: java.lang.Throwable -> L21
            r0 = r0 | 1
            r1.mParseStatus = r0     // Catch: java.lang.Throwable -> L21
            boolean r0 = r1.nativeParse(r2)     // Catch: java.lang.Throwable -> L21
            if (r0 == 0) goto L1f
            r1.postParse()     // Catch: java.lang.Throwable -> L21
            r0 = 1
        L1d:
            monitor-exit(r1)
            return r0
        L1f:
            r0 = 0
            goto L1d
        L21:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.videolan.libvlc.Media.parse(int):boolean");
    }

    public synchronized boolean parse() {
        return parse(2);
    }

    public synchronized boolean parseAsync(int flags) {
        boolean zNativeParseAsync;
        if (isReleased() || (this.mParseStatus & 3) != 0) {
            zNativeParseAsync = false;
        } else {
            this.mParseStatus |= 1;
            zNativeParseAsync = nativeParseAsync(flags);
        }
        return zNativeParseAsync;
    }

    public synchronized boolean parseAsync() {
        return parseAsync(2);
    }

    public synchronized boolean isParsed() {
        return (this.mParseStatus & 2) != 0;
    }

    public synchronized int getType() {
        return this.mType;
    }

    public synchronized int getTrackCount() {
        return this.mNativeTracks != null ? this.mNativeTracks.length : 0;
    }

    public synchronized Track getTrack(int idx) {
        return (this.mNativeTracks == null || idx < 0 || idx >= this.mNativeTracks.length) ? null : this.mNativeTracks[idx];
    }

    public synchronized String getMeta(int id) {
        String str = null;
        synchronized (this) {
            if (id >= 0 && id < 25) {
                if (this.mNativeMetas != null) {
                    str = this.mNativeMetas[id];
                }
            }
        }
        return str;
    }

    @Override // org.videolan.libvlc.VLCObject
    protected void onReleaseNative() {
        if (this.mSubItems != null) {
            this.mSubItems.release();
        }
        nativeRelease();
    }
}
