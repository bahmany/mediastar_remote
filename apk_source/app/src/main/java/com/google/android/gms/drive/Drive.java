package com.google.android.gms.drive;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.drive.internal.o;
import com.google.android.gms.drive.internal.q;
import com.google.android.gms.drive.internal.t;
import com.google.android.gms.drive.internal.x;
import java.util.List;

/* loaded from: classes.dex */
public final class Drive {
    public static final Api.c<q> CU = new Api.c<>();
    public static final Scope SCOPE_FILE = new Scope(Scopes.DRIVE_FILE);
    public static final Scope SCOPE_APPFOLDER = new Scope(Scopes.DRIVE_APPFOLDER);
    public static final Scope MU = new Scope("https://www.googleapis.com/auth/drive");
    public static final Scope MV = new Scope("https://www.googleapis.com/auth/drive.apps");
    public static final Api<Api.ApiOptions.NoOptions> API = new Api<>(new a<Api.ApiOptions.NoOptions>() { // from class: com.google.android.gms.drive.Drive.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.drive.Drive.a
        public Bundle a(Api.ApiOptions.NoOptions noOptions) {
            return new Bundle();
        }
    }, CU, new Scope[0]);
    public static final Api<b> MW = new Api<>(new a<b>() { // from class: com.google.android.gms.drive.Drive.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.drive.Drive.a
        public Bundle a(b bVar) {
            return bVar == null ? new Bundle() : bVar.hM();
        }
    }, CU, new Scope[0]);
    public static final DriveApi DriveApi = new o();
    public static final com.google.android.gms.drive.b MX = new t();
    public static final e MY = new x();

    public static abstract class a<O extends Api.ApiOptions> implements Api.b<q, O> {
        protected abstract Bundle a(O o);

        @Override // com.google.android.gms.common.api.Api.b
        public q a(Context context, Looper looper, ClientSettings clientSettings, O o, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            List<String> scopes = clientSettings.getScopes();
            return new q(context, looper, clientSettings, connectionCallbacks, onConnectionFailedListener, (String[]) scopes.toArray(new String[scopes.size()]), a(o));
        }

        @Override // com.google.android.gms.common.api.Api.b
        public int getPriority() {
            return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    public static class b implements Api.ApiOptions.Optional {
        private final Bundle MZ;

        private b() {
            this(new Bundle());
        }

        private b(Bundle bundle) {
            this.MZ = bundle;
        }

        public Bundle hM() {
            return this.MZ;
        }
    }

    private Drive() {
    }
}
