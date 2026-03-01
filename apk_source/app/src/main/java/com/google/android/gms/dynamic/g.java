package com.google.android.gms.dynamic;

import android.content.Context;
import android.os.IBinder;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.n;

/* loaded from: classes.dex */
public abstract class g<T> {
    private final String Sd;
    private T Se;

    public static class a extends Exception {
        public a(String str) {
            super(str);
        }

        public a(String str, Throwable th) {
            super(str, th);
        }
    }

    protected g(String str) {
        this.Sd = str;
    }

    protected final T L(Context context) throws a {
        if (this.Se == null) {
            n.i(context);
            Context remoteContext = GooglePlayServicesUtil.getRemoteContext(context);
            if (remoteContext == null) {
                throw new a("Could not get remote context.");
            }
            try {
                this.Se = d((IBinder) remoteContext.getClassLoader().loadClass(this.Sd).newInstance());
            } catch (ClassNotFoundException e) {
                throw new a("Could not load creator class.", e);
            } catch (IllegalAccessException e2) {
                throw new a("Could not access creator.", e2);
            } catch (InstantiationException e3) {
                throw new a("Could not instantiate creator.", e3);
            }
        }
        return this.Se;
    }

    protected abstract T d(IBinder iBinder);
}
