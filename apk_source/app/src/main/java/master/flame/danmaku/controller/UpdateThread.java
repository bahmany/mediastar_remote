package master.flame.danmaku.controller;

/* loaded from: classes.dex */
public class UpdateThread extends Thread {
    volatile boolean mIsQuited;

    public UpdateThread(String name) {
        super(name);
    }

    public void quit() {
        this.mIsQuited = true;
    }

    public boolean isQuited() {
        return this.mIsQuited;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (this.mIsQuited) {
        }
    }
}
