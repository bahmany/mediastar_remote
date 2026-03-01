package mktvsmart.screen.util;

/* loaded from: classes.dex */
public abstract class GRunnable implements Runnable {
    private final Object user;

    public abstract void run(Object obj);

    public GRunnable() {
        this.user = null;
    }

    public GRunnable(Object o) {
        this.user = o;
    }

    @Override // java.lang.Runnable
    public void run() {
        run(this.user);
    }
}
