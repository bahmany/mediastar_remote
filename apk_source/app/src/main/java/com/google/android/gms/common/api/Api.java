package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public final class Api<O extends ApiOptions> {
    private final b<?, O> Ij;
    private final c<?> Ik;
    private final ArrayList<Scope> Il;

    public interface ApiOptions {

        public interface HasOptions extends ApiOptions {
        }

        public static final class NoOptions implements NotRequiredOptions {
            private NoOptions() {
            }
        }

        public interface NotRequiredOptions extends ApiOptions {
        }

        public interface Optional extends HasOptions, NotRequiredOptions {
        }
    }

    public interface a {
        void connect();

        void disconnect();

        Looper getLooper();

        boolean isConnected();
    }

    public interface b<T extends a, O> {
        T a(Context context, Looper looper, ClientSettings clientSettings, O o, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener);

        int getPriority();
    }

    public static final class c<C extends a> {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <C extends a> Api(b<C, O> bVar, c<C> cVar, Scope... impliedScopes) {
        this.Ij = bVar;
        this.Ik = cVar;
        this.Il = new ArrayList<>(Arrays.asList(impliedScopes));
    }

    public b<?, O> gd() {
        return this.Ij;
    }

    public List<Scope> ge() {
        return this.Il;
    }

    public c<?> gf() {
        return this.Ik;
    }
}
