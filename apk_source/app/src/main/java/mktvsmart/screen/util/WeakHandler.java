package mktvsmart.screen.util;

import android.os.Handler;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public WeakHandler(T owner) {
        this.mOwner = new WeakReference<>(owner);
    }

    public T getOwner() {
        return this.mOwner.get();
    }
}
