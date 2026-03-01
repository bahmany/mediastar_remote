package org.cybergarage.multiscreenutil;

/* loaded from: classes.dex */
public class ThreadCore implements Runnable {
    private Thread mThreadObject = null;

    public void setThreadObject(Thread obj) {
        this.mThreadObject = obj;
    }

    public Thread getThreadObject() {
        return this.mThreadObject;
    }

    public void start() {
        if (getThreadObject() == null) {
            Thread threadObject = new Thread(this);
            setThreadObject(threadObject);
            threadObject.start();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
    }

    public boolean isRunnable() {
        return Thread.currentThread() == getThreadObject();
    }

    public void stop() {
        Thread threadObject = getThreadObject();
        if (threadObject != null) {
            setThreadObject(null);
        }
    }

    public void restart() {
        stop();
        start();
    }
}
