package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.google.android.gms.appindexing.AppIndexApi;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.hu;
import java.util.List;

/* loaded from: classes.dex */
public final class hz implements AppIndexApi, hu {

    /* renamed from: com.google.android.gms.internal.hz$1 */
    class AnonymousClass1 extends c<hu.a> {

        /* renamed from: com.google.android.gms.internal.hz$1$1 */
        class BinderC00611 extends hx<hu.a> {
            BinderC00611(BaseImplementation.b bVar) {
                super(bVar);
            }

            @Override // com.google.android.gms.internal.hx, com.google.android.gms.internal.hw
            public void a(Status status, ParcelFileDescriptor parcelFileDescriptor) {
                this.CH.b(new b(status, parcelFileDescriptor));
            }
        }

        @Override // com.google.android.gms.internal.hz.c
        protected void a(hv hvVar) throws RemoteException {
            hvVar.a(new hx<hu.a>(this) { // from class: com.google.android.gms.internal.hz.1.1
                BinderC00611(BaseImplementation.b this) {
                    super(this);
                }

                @Override // com.google.android.gms.internal.hx, com.google.android.gms.internal.hw
                public void a(Status status, ParcelFileDescriptor parcelFileDescriptor) {
                    this.CH.b(new b(status, parcelFileDescriptor));
                }
            });
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: b */
        public hu.a c(Status status) {
            return new b(status, null);
        }
    }

    /* renamed from: com.google.android.gms.internal.hz$2 */
    class AnonymousClass2 extends d<Status> {
        final /* synthetic */ String CJ;
        final /* synthetic */ hs[] CK;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(String str, hs[] hsVarArr) {
            super(null);
            str = str;
            hsVarArr = hsVarArr;
        }

        @Override // com.google.android.gms.internal.hz.c
        protected void a(hv hvVar) throws RemoteException {
            hvVar.a(new e(this), str, hsVarArr);
        }
    }

    private static abstract class a<T> implements Result {
        private final Status CM;
        protected final T CN;

        public a(Status status, T t) {
            this.CM = status;
            this.CN = t;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static class b extends a<ParcelFileDescriptor> implements hu.a {
        public b(Status status, ParcelFileDescriptor parcelFileDescriptor) {
            super(status, parcelFileDescriptor);
        }
    }

    private static abstract class c<T extends Result> extends BaseImplementation.a<T, hy> {
        public c() {
            super(hd.BN);
        }

        protected abstract void a(hv hvVar) throws RemoteException;

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public final void a(hy hyVar) throws RemoteException {
            a(hyVar.fo());
        }
    }

    private static abstract class d<T extends Result> extends c<Status> {
        private d() {
        }

        /* synthetic */ d(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private static final class e extends hx<Status> {
        public e(BaseImplementation.b<Status> bVar) {
            super(bVar);
        }

        @Override // com.google.android.gms.internal.hx, com.google.android.gms.internal.hw
        public void a(Status status) {
            this.CH.b(status);
        }
    }

    public static Uri a(String str, Uri uri) {
        if (!"android-app".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Uri scheme must be android-app: " + uri);
        }
        if (!str.equals(uri.getHost())) {
            throw new IllegalArgumentException("Uri host must match package name: " + uri);
        }
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.isEmpty() || pathSegments.get(0).isEmpty()) {
            throw new IllegalArgumentException("Uri path must exist: " + uri);
        }
        String str2 = pathSegments.get(0);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(str2);
        if (pathSegments.size() > 1) {
            builder.authority(pathSegments.get(1));
            int i = 2;
            while (true) {
                int i2 = i;
                if (i2 >= pathSegments.size()) {
                    break;
                }
                builder.appendPath(pathSegments.get(i2));
                i = i2 + 1;
            }
        }
        builder.encodedQuery(uri.getEncodedQuery());
        builder.encodedFragment(uri.getEncodedFragment());
        return builder.build();
    }

    public PendingResult<Status> a(GoogleApiClient googleApiClient, hs... hsVarArr) {
        return googleApiClient.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.internal.hz.2
            final /* synthetic */ String CJ;
            final /* synthetic */ hs[] CK;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(String str, hs[] hsVarArr2) {
                super(null);
                str = str;
                hsVarArr = hsVarArr2;
            }

            @Override // com.google.android.gms.internal.hz.c
            protected void a(hv hvVar) throws RemoteException {
                hvVar.a(new e(this), str, hsVarArr);
            }
        });
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public PendingResult<Status> view(GoogleApiClient apiClient, Activity activity, Intent viewIntent, String title, Uri webUrl, List<AppIndexApi.AppIndexingLink> outLinks) {
        return a(apiClient, new hs(((hy) apiClient.a(hd.BN)).getContext().getPackageName(), viewIntent, title, webUrl, (String) null, outLinks));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public PendingResult<Status> view(GoogleApiClient apiClient, Activity activity, Uri appIndexingUrl, String title, Uri webUrl, List<AppIndexApi.AppIndexingLink> outLinks) {
        return view(apiClient, activity, new Intent("android.intent.action.VIEW", a(((hy) apiClient.a(hd.BN)).getContext().getPackageName(), appIndexingUrl)), title, webUrl, outLinks);
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public PendingResult<Status> viewEnd(GoogleApiClient apiClient, Activity activity, Intent viewIntent) {
        return a(apiClient, new hs(hs.a(((hy) apiClient.a(hd.BN)).getContext().getPackageName(), viewIntent), System.currentTimeMillis(), 3));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public PendingResult<Status> viewEnd(GoogleApiClient apiClient, Activity activity, Uri appIndexingUrl) {
        return viewEnd(apiClient, activity, new Intent("android.intent.action.VIEW", a(((hy) apiClient.a(hd.BN)).getContext().getPackageName(), appIndexingUrl)));
    }
}
