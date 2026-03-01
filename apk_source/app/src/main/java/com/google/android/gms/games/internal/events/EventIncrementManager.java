package com.google.android.gms.games.internal.events;

import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public abstract class EventIncrementManager {
    private final AtomicReference<EventIncrementCache> aal = new AtomicReference<>();

    public void flush() {
        EventIncrementCache eventIncrementCache = this.aal.get();
        if (eventIncrementCache != null) {
            eventIncrementCache.flush();
        }
    }

    protected abstract EventIncrementCache kv();

    public void n(String str, int i) {
        EventIncrementCache eventIncrementCacheKv = this.aal.get();
        if (eventIncrementCacheKv == null) {
            eventIncrementCacheKv = kv();
            if (!this.aal.compareAndSet(null, eventIncrementCacheKv)) {
                eventIncrementCacheKv = this.aal.get();
            }
        }
        eventIncrementCacheKv.w(str, i);
    }
}
