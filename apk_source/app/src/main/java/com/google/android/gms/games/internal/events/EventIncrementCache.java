package com.google.android.gms.games.internal.events;

import android.os.Handler;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class EventIncrementCache {
    private Handler aag;
    private boolean aah;
    private int aaj;
    final Object aaf = new Object();
    private HashMap<String, AtomicInteger> aai = new HashMap<>();

    public EventIncrementCache(Looper looper, int flushIntervalMillis) {
        this.aag = new Handler(looper);
        this.aaj = flushIntervalMillis;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void kN() {
        synchronized (this.aaf) {
            this.aah = false;
            flush();
        }
    }

    public void flush() {
        synchronized (this.aaf) {
            for (Map.Entry<String, AtomicInteger> entry : this.aai.entrySet()) {
                q(entry.getKey(), entry.getValue().get());
            }
            this.aai.clear();
        }
    }

    protected abstract void q(String str, int i);

    public void w(String str, int i) {
        synchronized (this.aaf) {
            if (!this.aah) {
                this.aah = true;
                this.aag.postDelayed(new Runnable() { // from class: com.google.android.gms.games.internal.events.EventIncrementCache.1
                    @Override // java.lang.Runnable
                    public void run() {
                        EventIncrementCache.this.kN();
                    }
                }, this.aaj);
            }
            AtomicInteger atomicInteger = this.aai.get(str);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger();
                this.aai.put(str, atomicInteger);
            }
            atomicInteger.addAndGet(i);
        }
    }
}
