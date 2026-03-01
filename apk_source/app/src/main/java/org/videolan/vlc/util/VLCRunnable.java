package org.videolan.vlc.util;

/* loaded from: classes.dex */
public abstract class VLCRunnable implements Runnable {
    private final Object user;

    public abstract void run(Object obj);

    public VLCRunnable() {
        this.user = null;
    }

    public VLCRunnable(Object o) {
        this.user = o;
    }

    @Override // java.lang.Runnable
    public void run() {
        run(this.user);
    }
}
