package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.mz;
import com.google.android.gms.panorama.Panorama;
import com.google.android.gms.panorama.PanoramaApi;

/* loaded from: classes.dex */
public class nb implements PanoramaApi {

    /* renamed from: com.google.android.gms.internal.nb$1, reason: invalid class name */
    class AnonymousClass1 extends d<PanoramaApi.a> {
        final /* synthetic */ Uri akn;
        final /* synthetic */ Bundle ako;

        @Override // com.google.android.gms.internal.nb.d
        protected void a(Context context, na naVar) throws RemoteException {
            nb.a(context, naVar, new a(this), this.akn, this.ako);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: ay, reason: merged with bridge method [inline-methods] */
        public PanoramaApi.a c(Status status) {
            return new my(status, null, 0);
        }
    }

    private static final class a extends mz.a {
        private final BaseImplementation.b<PanoramaApi.a> De;

        public a(BaseImplementation.b<PanoramaApi.a> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.mz
        public void a(int i, Bundle bundle, int i2, Intent intent) {
            this.De.b(new my(new Status(i, null, bundle != null ? (PendingIntent) bundle.getParcelable("pendingIntent") : null), intent, i2));
        }
    }

    private static abstract class b extends d<PanoramaApi.PanoramaResult> {
        private b() {
        }

        /* synthetic */ b(AnonymousClass1 anonymousClass1) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: az, reason: merged with bridge method [inline-methods] */
        public PanoramaApi.PanoramaResult c(Status status) {
            return new nd(status, null);
        }
    }

    private static final class c extends mz.a {
        private final BaseImplementation.b<PanoramaApi.PanoramaResult> De;

        public c(BaseImplementation.b<PanoramaApi.PanoramaResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.mz
        public void a(int i, Bundle bundle, int i2, Intent intent) {
            this.De.b(new nd(new Status(i, null, bundle != null ? (PendingIntent) bundle.getParcelable("pendingIntent") : null), intent));
        }
    }

    private static abstract class d<R extends Result> extends BaseImplementation.a<R, nc> {
        protected d() {
            super(Panorama.CU);
        }

        protected abstract void a(Context context, na naVar) throws RemoteException;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public final void a(nc ncVar) throws RemoteException {
            a(ncVar.getContext(), ncVar.gS());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void a(Context context, Uri uri) {
        context.revokeUriPermission(uri, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void a(final Context context, na naVar, final mz mzVar, final Uri uri, Bundle bundle) throws RemoteException {
        context.grantUriPermission(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, uri, 1);
        try {
            naVar.a(new mz.a() { // from class: com.google.android.gms.internal.nb.4
                @Override // com.google.android.gms.internal.mz
                public void a(int i, Bundle bundle2, int i2, Intent intent) throws RemoteException {
                    nb.a(context, uri);
                    mzVar.a(i, bundle2, i2, intent);
                }
            }, uri, bundle, true);
        } catch (RemoteException e) {
            a(context, uri);
            throw e;
        } catch (RuntimeException e2) {
            a(context, uri);
            throw e2;
        }
    }

    @Override // com.google.android.gms.panorama.PanoramaApi
    public PendingResult<PanoramaApi.PanoramaResult> loadPanoramaInfo(GoogleApiClient client, final Uri uri) {
        return client.a((GoogleApiClient) new b() { // from class: com.google.android.gms.internal.nb.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(null);
            }

            @Override // com.google.android.gms.internal.nb.d
            protected void a(Context context, na naVar) throws RemoteException {
                naVar.a(new c(this), uri, null, false);
            }
        });
    }

    @Override // com.google.android.gms.panorama.PanoramaApi
    public PendingResult<PanoramaApi.PanoramaResult> loadPanoramaInfoAndGrantAccess(GoogleApiClient client, final Uri uri) {
        return client.a((GoogleApiClient) new b() { // from class: com.google.android.gms.internal.nb.3
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(null);
            }

            @Override // com.google.android.gms.internal.nb.d
            protected void a(Context context, na naVar) throws RemoteException {
                nb.a(context, naVar, new c(this), uri, null);
            }
        });
    }
}
