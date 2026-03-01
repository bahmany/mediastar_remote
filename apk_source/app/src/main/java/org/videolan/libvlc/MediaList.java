package org.videolan.libvlc;

import android.util.SparseArray;
import org.videolan.libvlc.VLCObject;

/* loaded from: classes.dex */
public final class MediaList extends VLCObject {
    private static final String TAG = "LibVLC/MediaList";
    private int mCount = 0;
    private SparseArray<Media> mMediaArray = new SparseArray<>();

    private native int nativeGetCount();

    private native void nativeNewFromLibVlc(LibVLC libVLC);

    private native void nativeNewFromMedia(Media media);

    private native void nativeNewFromMediaDiscoverer(MediaDiscoverer mediaDiscoverer);

    private native void nativeRelease();

    public static class Event extends VLCObject.Event {
        public final int index;
        public final Media media;

        protected Event(int type, Media media, int index) {
            super(type);
            this.media = media;
            this.index = index;
        }
    }

    private void init() {
        this.mCount = nativeGetCount();
    }

    public MediaList(LibVLC libVLC) {
        nativeNewFromLibVlc(libVLC);
        init();
    }

    protected MediaList(MediaDiscoverer md) {
        if (md.isReleased()) {
            throw new IllegalArgumentException("MediaDiscoverer is not native");
        }
        nativeNewFromMediaDiscoverer(md);
        init();
    }

    protected MediaList(Media m) {
        if (m.isReleased()) {
            throw new IllegalArgumentException("Media is not native");
        }
        nativeNewFromMedia(m);
        init();
    }

    private synchronized Media insertMedia(int index) {
        Media media;
        this.mCount++;
        for (int i = this.mCount - 1; i >= index; i--) {
            this.mMediaArray.put(i + 1, this.mMediaArray.valueAt(i));
        }
        media = new Media(this, index);
        this.mMediaArray.put(index, media);
        return media;
    }

    private synchronized Media removeMedia(int index) {
        Media media;
        this.mCount--;
        media = this.mMediaArray.get(index);
        if (media != null) {
            media.release();
        }
        for (int i = index; i < this.mCount; i++) {
            this.mMediaArray.put(i, this.mMediaArray.valueAt(i + 1));
        }
        return media;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // org.videolan.libvlc.VLCObject
    public synchronized Event onEventNative(int eventType, long arg1, long arg2) {
        Event event = null;
        synchronized (this) {
            switch (eventType) {
                case 512:
                    int index = (int) arg1;
                    if (index != -1) {
                        Media media = insertMedia(index);
                        event = new Event(eventType, media, index);
                    }
                    break;
                case VLCObject.Events.MediaListItemDeleted /* 514 */:
                    int index2 = (int) arg1;
                    if (index2 != -1) {
                        Media media2 = removeMedia(index2);
                        event = new Event(eventType, media2, index2);
                    }
                    break;
                case VLCObject.Events.MediaListEndReached /* 516 */:
                    event = new Event(eventType, null, -1);
                    break;
            }
        }
        return event;
    }

    public synchronized int getCount() {
        return this.mCount;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0009  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized org.videolan.libvlc.Media getMediaAt(int r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            if (r2 < 0) goto L9
            int r0 = r1.getCount()     // Catch: java.lang.Throwable -> L15
            if (r2 <= r0) goto Lc
        L9:
            r0 = 0
        La:
            monitor-exit(r1)
            return r0
        Lc:
            android.util.SparseArray<org.videolan.libvlc.Media> r0 = r1.mMediaArray     // Catch: java.lang.Throwable -> L15
            java.lang.Object r0 = r0.get(r2)     // Catch: java.lang.Throwable -> L15
            org.videolan.libvlc.Media r0 = (org.videolan.libvlc.Media) r0     // Catch: java.lang.Throwable -> L15
            goto La
        L15:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.videolan.libvlc.MediaList.getMediaAt(int):org.videolan.libvlc.Media");
    }

    @Override // org.videolan.libvlc.VLCObject
    public void onReleaseNative() {
        for (int i = 0; i < this.mMediaArray.size(); i++) {
            Media media = this.mMediaArray.get(i);
            if (media != null) {
                media.release();
            }
        }
        nativeRelease();
    }
}
