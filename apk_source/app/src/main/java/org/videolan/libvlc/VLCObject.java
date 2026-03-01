package org.videolan.libvlc;

import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class VLCObject {
    private static final String TAG = "LibVLC/VlcObject";
    private EventListener mEventListener = null;
    private Handler mHandler = null;
    private int mNativeRefCount = 1;
    private long mInstance = 0;

    public interface EventListener {
        void onEvent(Event event);
    }

    public static class Events {
        public static final int MediaDiscovererEnded = 1281;
        public static final int MediaDiscovererStarted = 1280;
        public static final int MediaDurationChanged = 2;
        public static final int MediaListEndReached = 516;
        public static final int MediaListItemAdded = 512;
        public static final int MediaListItemDeleted = 514;
        public static final int MediaMetaChanged = 0;
        public static final int MediaParsedChanged = 3;
        public static final int MediaStateChanged = 5;
        public static final int MediaSubItemAdded = 1;
        public static final int MediaSubItemTreeAdded = 6;
    }

    private final native void nativeDetachEvents();

    protected abstract Event onEventNative(int i, long j, long j2);

    protected abstract void onReleaseNative();

    public static class Event {
        public final int type;

        protected Event(int type) {
            this.type = type;
        }
    }

    private static class EventRunnable implements Runnable {
        private final Event event;
        private final EventListener listener;

        private EventRunnable(EventListener listener, Event event) {
            this.listener = listener;
            this.event = event;
        }

        /* synthetic */ EventRunnable(EventListener eventListener, Event event, EventRunnable eventRunnable) {
            this(eventListener, event);
        }

        @Override // java.lang.Runnable
        public void run() {
            this.listener.onEvent(this.event);
        }
    }

    public synchronized boolean isReleased() {
        return this.mNativeRefCount == 0;
    }

    public final synchronized boolean retain() {
        boolean z;
        if (this.mNativeRefCount > 0) {
            this.mNativeRefCount++;
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public final void release() {
        int refCount = -1;
        synchronized (this) {
            if (this.mNativeRefCount != 0) {
                if (this.mNativeRefCount > 0) {
                    int refCount2 = this.mNativeRefCount - 1;
                    this.mNativeRefCount = refCount2;
                    refCount = refCount2;
                }
                if (refCount == 0) {
                    setEventListener(null);
                }
                if (refCount == 0) {
                    nativeDetachEvents();
                    synchronized (this) {
                        onReleaseNative();
                    }
                }
            }
        }
    }

    public final synchronized void setEventListener(EventListener listener) {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
        }
        this.mEventListener = listener;
        if (this.mEventListener != null && this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
    }

    private synchronized void dispatchEventFromNative(int eventType, long arg1, long arg2) {
        Event event;
        if (!isReleased() && (event = onEventNative(eventType, arg1, arg2)) != null && this.mEventListener != null && this.mHandler != null) {
            this.mHandler.post(new EventRunnable(this.mEventListener, event, null));
        }
    }

    private Object getWeakReference() {
        return new WeakReference(this);
    }

    private static void dispatchEventFromWeakNative(Object weak, int eventType, long arg1, long arg2) {
        VLCObject obj = (VLCObject) ((WeakReference) weak).get();
        if (obj != null) {
            obj.dispatchEventFromNative(eventType, arg1, arg2);
        }
    }
}
