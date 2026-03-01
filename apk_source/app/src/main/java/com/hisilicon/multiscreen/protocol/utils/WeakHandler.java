package com.hisilicon.multiscreen.protocol.utils;

import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public WeakHandler(T owner) {
        this(Looper.myLooper(), null, owner);
    }

    public WeakHandler(Looper looper, T owner) {
        this(looper, null, owner);
    }

    public WeakHandler(Looper looper, Handler.Callback callback, T owner) {
        super(looper, callback);
        this.mOwner = new WeakReference<>(owner);
    }

    public T getOwner() {
        return this.mOwner.get();
    }
}
