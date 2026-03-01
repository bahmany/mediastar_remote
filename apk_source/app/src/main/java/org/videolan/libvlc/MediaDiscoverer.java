package org.videolan.libvlc;

import org.videolan.libvlc.VLCObject;

/* loaded from: classes.dex */
public final class MediaDiscoverer extends VLCObject {
    private static final String TAG = "LibVLC/MediaDiscoverer";
    private long mInstance = 0;
    private MediaList mMediaList;

    private native void nativeNew(LibVLC libVLC, String str);

    private native void nativeRelease();

    private native boolean nativeStart();

    private native void nativeStop();

    public MediaDiscoverer(LibVLC libVLC, String name) {
        nativeNew(libVLC, name);
    }

    public boolean start() {
        if (isReleased()) {
            return false;
        }
        return nativeStart();
    }

    public void stop() {
        if (!isReleased()) {
            nativeStop();
        }
    }

    @Override // org.videolan.libvlc.VLCObject
    protected VLCObject.Event onEventNative(int event, long arg1, long arg2) {
        return null;
    }

    public synchronized MediaList getMediaList() {
        if (this.mMediaList == null && !isReleased()) {
            this.mMediaList = new MediaList(this);
        }
        return this.mMediaList;
    }

    @Override // org.videolan.libvlc.VLCObject
    protected void onReleaseNative() {
        if (this.mMediaList != null) {
            this.mMediaList.release();
        }
        nativeRelease();
    }
}
