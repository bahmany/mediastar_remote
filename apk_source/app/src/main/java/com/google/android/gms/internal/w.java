package com.google.android.gms.internal;

import android.content.Context;
import android.view.MotionEvent;
import com.google.android.gms.internal.u;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@ez
/* loaded from: classes.dex */
class w implements g, Runnable {
    private u.b lr;
    private final List<Object[]> me = new Vector();
    private final AtomicReference<g> mf = new AtomicReference<>();
    CountDownLatch mg = new CountDownLatch(1);

    public w(u.b bVar) {
        this.lr = bVar;
        if (gr.dt()) {
            gi.a(this);
        } else {
            run();
        }
    }

    private void ax() {
        if (this.me.isEmpty()) {
            return;
        }
        for (Object[] objArr : this.me) {
            if (objArr.length == 1) {
                this.mf.get().a((MotionEvent) objArr[0]);
            } else if (objArr.length == 3) {
                this.mf.get().a(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
            }
        }
    }

    @Override // com.google.android.gms.internal.g
    public String a(Context context) throws InterruptedException {
        aw();
        g gVar = this.mf.get();
        if (gVar == null) {
            return "";
        }
        ax();
        return gVar.a(context);
    }

    @Override // com.google.android.gms.internal.g
    public String a(Context context, String str) throws InterruptedException {
        aw();
        g gVar = this.mf.get();
        if (gVar == null) {
            return "";
        }
        ax();
        return gVar.a(context, str);
    }

    @Override // com.google.android.gms.internal.g
    public void a(int i, int i2, int i3) {
        g gVar = this.mf.get();
        if (gVar == null) {
            this.me.add(new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
        } else {
            ax();
            gVar.a(i, i2, i3);
        }
    }

    @Override // com.google.android.gms.internal.g
    public void a(MotionEvent motionEvent) {
        g gVar = this.mf.get();
        if (gVar == null) {
            this.me.add(new Object[]{motionEvent});
        } else {
            ax();
            gVar.a(motionEvent);
        }
    }

    protected void a(g gVar) {
        this.mf.set(gVar);
    }

    protected void aw() throws InterruptedException {
        try {
            this.mg.await();
        } catch (InterruptedException e) {
            gs.d("Interrupted during GADSignals creation.", e);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            a(j.a(this.lr.lD.wD, this.lr.lB));
        } finally {
            this.mg.countDown();
            this.lr = null;
        }
    }
}
