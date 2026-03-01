package com.google.android.gms.internal;

import android.os.Process;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@ez
/* loaded from: classes.dex */
public final class gi {
    private static final ThreadFactory wh = new ThreadFactory() { // from class: com.google.android.gms.internal.gi.3
        private final AtomicInteger wl = new AtomicInteger(1);

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "AdWorker #" + this.wl.getAndIncrement());
        }
    };
    private static final ExecutorService wi = Executors.newFixedThreadPool(10, wh);

    public static Future<Void> a(final Runnable runnable) {
        return submit(new Callable<Void>() { // from class: com.google.android.gms.internal.gi.1
            @Override // java.util.concurrent.Callable
            /* renamed from: dk, reason: merged with bridge method [inline-methods] */
            public Void call() {
                runnable.run();
                return null;
            }
        });
    }

    public static <T> Future<T> submit(final Callable<T> callable) {
        try {
            return wi.submit(new Callable<T>() { // from class: com.google.android.gms.internal.gi.2
                @Override // java.util.concurrent.Callable
                public T call() throws Exception {
                    try {
                        Process.setThreadPriority(10);
                        return (T) callable.call();
                    } catch (Exception e) {
                        gb.e(e);
                        return null;
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            gs.d("Thread execution is rejected.", e);
            return new gl(null);
        }
    }
}
