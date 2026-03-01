package com.google.android.gms.internal;

import android.content.pm.PackageManager;
import android.os.Handler;
import java.lang.ref.WeakReference;

@ez
/* loaded from: classes.dex */
public class ab {
    private final a mj;
    private final Runnable mk;
    private av ml;
    private boolean mm;
    private boolean mn;
    private long mo;

    public static class a {
        private final Handler mHandler;

        public a(Handler handler) {
            this.mHandler = handler;
        }

        public boolean postDelayed(Runnable runnable, long timeFromNowInMillis) {
            return this.mHandler.postDelayed(runnable, timeFromNowInMillis);
        }

        public void removeCallbacks(Runnable runnable) {
            this.mHandler.removeCallbacks(runnable);
        }
    }

    public ab(u uVar) {
        this(uVar, new a(gr.wC));
    }

    ab(final u uVar, a aVar) {
        this.mm = false;
        this.mn = false;
        this.mo = 0L;
        this.mj = aVar;
        this.mk = new Runnable() { // from class: com.google.android.gms.internal.ab.1
            private final WeakReference<u> mp;

            {
                this.mp = new WeakReference<>(uVar);
            }

            @Override // java.lang.Runnable
            public void run() throws PackageManager.NameNotFoundException {
                ab.this.mm = false;
                u uVar2 = this.mp.get();
                if (uVar2 != null) {
                    uVar2.b(ab.this.ml);
                }
            }
        };
    }

    public void a(av avVar, long j) {
        if (this.mm) {
            gs.W("An ad refresh is already scheduled.");
            return;
        }
        this.ml = avVar;
        this.mm = true;
        this.mo = j;
        if (this.mn) {
            return;
        }
        gs.U("Scheduling ad refresh " + j + " milliseconds from now.");
        this.mj.postDelayed(this.mk, j);
    }

    public boolean ay() {
        return this.mm;
    }

    public void c(av avVar) {
        a(avVar, 60000L);
    }

    public void cancel() {
        this.mm = false;
        this.mj.removeCallbacks(this.mk);
    }

    public void pause() {
        this.mn = true;
        if (this.mm) {
            this.mj.removeCallbacks(this.mk);
        }
    }

    public void resume() {
        this.mn = false;
        if (this.mm) {
            this.mm = false;
            a(this.ml, this.mo);
        }
    }
}
