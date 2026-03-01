package com.google.android.gms.internal;

@ez
/* loaded from: classes.dex */
public abstract class gg {
    private final Runnable mk = new Runnable() { // from class: com.google.android.gms.internal.gg.1
        @Override // java.lang.Runnable
        public final void run() {
            gg.this.wf = Thread.currentThread();
            gg.this.cp();
        }
    };
    private volatile Thread wf;

    public final void cancel() {
        onStop();
        if (this.wf != null) {
            this.wf.interrupt();
        }
    }

    public abstract void cp();

    public abstract void onStop();

    public final void start() {
        gi.a(this.mk);
    }
}
