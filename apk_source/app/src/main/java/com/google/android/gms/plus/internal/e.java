package com.google.android.gms.plus.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.common.internal.k;
import com.google.android.gms.internal.jp;
import com.google.android.gms.internal.nv;
import com.google.android.gms.internal.ny;
import com.google.android.gms.plus.Moments;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.internal.d;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.moments.MomentBuffer;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/* loaded from: classes.dex */
public class e extends com.google.android.gms.common.internal.d<com.google.android.gms.plus.internal.d> {
    private Person ali;
    private final com.google.android.gms.plus.internal.h alj;

    final class a extends com.google.android.gms.plus.internal.a {
        private final BaseImplementation.b<Status> alk;

        public a(BaseImplementation.b<Status> bVar) {
            this.alk = bVar;
        }

        @Override // com.google.android.gms.plus.internal.a, com.google.android.gms.plus.internal.b
        public void aB(Status status) {
            e.this.a(e.this.new d(this.alk, status));
        }
    }

    final class b extends com.google.android.gms.plus.internal.a {
        private final BaseImplementation.b<Moments.LoadMomentsResult> alk;

        public b(BaseImplementation.b<Moments.LoadMomentsResult> bVar) {
            this.alk = bVar;
        }

        @Override // com.google.android.gms.plus.internal.a, com.google.android.gms.plus.internal.b
        public void a(DataHolder dataHolder, String str, String str2) {
            DataHolder dataHolder2;
            Status status = new Status(dataHolder.getStatusCode(), null, dataHolder.gz() != null ? (PendingIntent) dataHolder.gz().getParcelable("pendingIntent") : null);
            if (status.isSuccess() || dataHolder == null) {
                dataHolder2 = dataHolder;
            } else {
                if (!dataHolder.isClosed()) {
                    dataHolder.close();
                }
                dataHolder2 = null;
            }
            e.this.a(e.this.new c(this.alk, status, dataHolder2, str, str2));
        }
    }

    final class c extends com.google.android.gms.common.internal.d<com.google.android.gms.plus.internal.d>.AbstractC0005d<BaseImplementation.b<Moments.LoadMomentsResult>> implements Moments.LoadMomentsResult {
        private final Status CM;
        private final String Ni;
        private final String alm;
        private MomentBuffer aln;

        public c(BaseImplementation.b<Moments.LoadMomentsResult> bVar, Status status, DataHolder dataHolder, String str, String str2) {
            super(bVar, dataHolder);
            this.CM = status;
            this.Ni = str;
            this.alm = str2;
        }

        @Override // com.google.android.gms.common.internal.d.AbstractC0005d
        public void a(BaseImplementation.b<Moments.LoadMomentsResult> bVar, DataHolder dataHolder) {
            this.aln = dataHolder != null ? new MomentBuffer(dataHolder) : null;
            bVar.b(this);
        }

        @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
        public MomentBuffer getMomentBuffer() {
            return this.aln;
        }

        @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
        public String getNextPageToken() {
            return this.Ni;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
        public String getUpdated() {
            return this.alm;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() {
            if (this.aln != null) {
                this.aln.close();
            }
        }
    }

    final class d extends com.google.android.gms.common.internal.d<com.google.android.gms.plus.internal.d>.b<BaseImplementation.b<Status>> {
        private final Status CM;

        public d(BaseImplementation.b<Status> bVar, Status status) {
            super(bVar);
            this.CM = status;
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: n */
        public void g(BaseImplementation.b<Status> bVar) {
            if (bVar != null) {
                bVar.b(this.CM);
            }
        }
    }

    /* renamed from: com.google.android.gms.plus.internal.e$e */
    final class BinderC0140e extends com.google.android.gms.plus.internal.a {
        private final BaseImplementation.b<People.LoadPeopleResult> alk;

        public BinderC0140e(BaseImplementation.b<People.LoadPeopleResult> bVar) {
            this.alk = bVar;
        }

        @Override // com.google.android.gms.plus.internal.a, com.google.android.gms.plus.internal.b
        public void a(DataHolder dataHolder, String str) {
            DataHolder dataHolder2;
            Status status = new Status(dataHolder.getStatusCode(), null, dataHolder.gz() != null ? (PendingIntent) dataHolder.gz().getParcelable("pendingIntent") : null);
            if (status.isSuccess() || dataHolder == null) {
                dataHolder2 = dataHolder;
            } else {
                if (!dataHolder.isClosed()) {
                    dataHolder.close();
                }
                dataHolder2 = null;
            }
            e.this.a(e.this.new f(this.alk, status, dataHolder2, str));
        }
    }

    final class f extends com.google.android.gms.common.internal.d<com.google.android.gms.plus.internal.d>.AbstractC0005d<BaseImplementation.b<People.LoadPeopleResult>> implements People.LoadPeopleResult {
        private final Status CM;
        private final String Ni;
        private PersonBuffer alo;

        public f(BaseImplementation.b<People.LoadPeopleResult> bVar, Status status, DataHolder dataHolder, String str) {
            super(bVar, dataHolder);
            this.CM = status;
            this.Ni = str;
        }

        @Override // com.google.android.gms.common.internal.d.AbstractC0005d
        public void a(BaseImplementation.b<People.LoadPeopleResult> bVar, DataHolder dataHolder) {
            this.alo = dataHolder != null ? new PersonBuffer(dataHolder) : null;
            bVar.b(this);
        }

        @Override // com.google.android.gms.plus.People.LoadPeopleResult
        public String getNextPageToken() {
            return this.Ni;
        }

        @Override // com.google.android.gms.plus.People.LoadPeopleResult
        public PersonBuffer getPersonBuffer() {
            return this.alo;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() {
            if (this.alo != null) {
                this.alo.close();
            }
        }
    }

    final class g extends com.google.android.gms.plus.internal.a {
        private final BaseImplementation.b<Status> alk;

        public g(BaseImplementation.b<Status> bVar) {
            this.alk = bVar;
        }

        @Override // com.google.android.gms.plus.internal.a, com.google.android.gms.plus.internal.b
        public void h(int i, Bundle bundle) {
            e.this.a(e.this.new h(this.alk, new Status(i, null, bundle != null ? (PendingIntent) bundle.getParcelable("pendingIntent") : null)));
        }
    }

    final class h extends com.google.android.gms.common.internal.d<com.google.android.gms.plus.internal.d>.b<BaseImplementation.b<Status>> {
        private final Status CM;

        public h(BaseImplementation.b<Status> bVar, Status status) {
            super(bVar);
            this.CM = status;
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }

        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: n */
        public void g(BaseImplementation.b<Status> bVar) {
            e.this.disconnect();
            if (bVar != null) {
                bVar.b(this.CM);
            }
        }
    }

    public e(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, com.google.android.gms.plus.internal.h hVar) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, hVar.ne());
        this.alj = hVar;
    }

    @Deprecated
    public e(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener, com.google.android.gms.plus.internal.h hVar) {
        this(context, context.getMainLooper(), new d.c(connectionCallbacks), new d.g(onConnectionFailedListener), hVar);
    }

    public com.google.android.gms.common.internal.i a(BaseImplementation.b<People.LoadPeopleResult> bVar, int i, String str) {
        dK();
        BinderC0140e binderC0140e = new BinderC0140e(bVar);
        try {
            return gS().a(binderC0140e, 1, i, -1, str);
        } catch (RemoteException e) {
            binderC0140e.a(DataHolder.as(8), (String) null);
            return null;
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(int i, IBinder iBinder, Bundle bundle) {
        if (i == 0 && bundle != null && bundle.containsKey("loaded_person")) {
            this.ali = ny.i(bundle.getByteArray("loaded_person"));
        }
        super.a(i, iBinder, bundle);
    }

    public void a(BaseImplementation.b<Moments.LoadMomentsResult> bVar, int i, String str, Uri uri, String str2, String str3) {
        dK();
        b bVar2 = bVar != null ? new b(bVar) : null;
        try {
            gS().a(bVar2, i, str, uri, str2, str3);
        } catch (RemoteException e) {
            bVar2.a(DataHolder.as(8), (String) null, (String) null);
        }
    }

    public void a(BaseImplementation.b<Status> bVar, Moment moment) {
        dK();
        a aVar = bVar != null ? new a(bVar) : null;
        try {
            gS().a(aVar, jp.a((nv) moment));
        } catch (RemoteException e) {
            if (aVar == null) {
                throw new IllegalStateException(e);
            }
            aVar.aB(new Status(8, null, null));
        }
    }

    public void a(BaseImplementation.b<People.LoadPeopleResult> bVar, Collection<String> collection) {
        dK();
        BinderC0140e binderC0140e = new BinderC0140e(bVar);
        try {
            gS().a(binderC0140e, new ArrayList(collection));
        } catch (RemoteException e) {
            binderC0140e.a(DataHolder.as(8), (String) null);
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(k kVar, d.e eVar) throws RemoteException {
        Bundle bundleNm = this.alj.nm();
        bundleNm.putStringArray("request_visible_actions", this.alj.nf());
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, this.alj.ni(), this.alj.nh(), gR(), this.alj.getAccountName(), bundleNm);
    }

    @Override // com.google.android.gms.common.internal.d
    /* renamed from: bH */
    public com.google.android.gms.plus.internal.d j(IBinder iBinder) {
        return d.a.bG(iBinder);
    }

    public boolean cd(String str) {
        return Arrays.asList(gR()).contains(str);
    }

    public void clearDefaultAccount() {
        dK();
        try {
            this.ali = null;
            gS().clearDefaultAccount();
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public void d(BaseImplementation.b<People.LoadPeopleResult> bVar, String[] strArr) {
        a(bVar, Arrays.asList(strArr));
    }

    public String getAccountName() {
        dK();
        try {
            return gS().getAccountName();
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public Person getCurrentPerson() {
        dK();
        return this.ali;
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.plus.internal.IPlusService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.plus.service.START";
    }

    public void k(BaseImplementation.b<Moments.LoadMomentsResult> bVar) {
        a(bVar, 20, null, null, null, "me");
    }

    public void l(BaseImplementation.b<People.LoadPeopleResult> bVar) {
        dK();
        BinderC0140e binderC0140e = new BinderC0140e(bVar);
        try {
            gS().a(binderC0140e, 2, 1, -1, null);
        } catch (RemoteException e) {
            binderC0140e.a(DataHolder.as(8), (String) null);
        }
    }

    public void m(BaseImplementation.b<Status> bVar) {
        dK();
        clearDefaultAccount();
        g gVar = new g(bVar);
        try {
            gS().b(gVar);
        } catch (RemoteException e) {
            gVar.h(8, null);
        }
    }

    public com.google.android.gms.common.internal.i r(BaseImplementation.b<People.LoadPeopleResult> bVar, String str) {
        return a(bVar, 0, str);
    }

    public void removeMoment(String momentId) {
        dK();
        try {
            gS().removeMoment(momentId);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }
}
