package com.google.android.gms.internal;

import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Moments;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.moments.MomentBuffer;

/* loaded from: classes.dex */
public final class nr implements Moments {

    /* renamed from: com.google.android.gms.internal.nr$1 */
    class AnonymousClass1 extends a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.k(this);
        }
    }

    /* renamed from: com.google.android.gms.internal.nr$2 */
    class AnonymousClass2 extends a {
        final /* synthetic */ int YC;
        final /* synthetic */ String alE;
        final /* synthetic */ Uri alF;
        final /* synthetic */ String alG;
        final /* synthetic */ String alH;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(int i, String str, Uri uri, String str2, String str3) {
            super();
            i = i;
            str = str;
            uri = uri;
            str = str2;
            str = str3;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.a(this, i, str, uri, str, str);
        }
    }

    /* renamed from: com.google.android.gms.internal.nr$3 */
    class AnonymousClass3 extends c {
        final /* synthetic */ Moment alI;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(Moment moment) {
            super();
            moment = moment;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.a(this, moment);
        }
    }

    /* renamed from: com.google.android.gms.internal.nr$4 */
    class AnonymousClass4 extends b {
        final /* synthetic */ String alJ;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass4(String str) {
            super();
            str = str;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.removeMoment(str);
            b((AnonymousClass4) Status.Jo);
        }
    }

    private static abstract class a extends Plus.a<Moments.LoadMomentsResult> {

        /* renamed from: com.google.android.gms.internal.nr$a$1 */
        class AnonymousClass1 implements Moments.LoadMomentsResult {
            final /* synthetic */ Status CW;

            AnonymousClass1(Status status) {
                status = status;
            }

            @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
            public MomentBuffer getMomentBuffer() {
                return null;
            }

            @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
            public String getNextPageToken() {
                return null;
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
            }

            @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
            public String getUpdated() {
                return null;
            }

            @Override // com.google.android.gms.common.api.Releasable
            public void release() {
            }
        }

        private a() {
        }

        /* synthetic */ a(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aC */
        public Moments.LoadMomentsResult c(Status status) {
            return new Moments.LoadMomentsResult() { // from class: com.google.android.gms.internal.nr.a.1
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
                public MomentBuffer getMomentBuffer() {
                    return null;
                }

                @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
                public String getNextPageToken() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }

                @Override // com.google.android.gms.plus.Moments.LoadMomentsResult
                public String getUpdated() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Releasable
                public void release() {
                }
            };
        }
    }

    private static abstract class b extends Plus.a<Status> {
        private b() {
        }

        /* synthetic */ b(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    private static abstract class c extends Plus.a<Status> {
        private c() {
        }

        /* synthetic */ c(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    @Override // com.google.android.gms.plus.Moments
    public PendingResult<Moments.LoadMomentsResult> load(GoogleApiClient googleApiClient) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.nr.1
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.k(this);
            }
        });
    }

    @Override // com.google.android.gms.plus.Moments
    public PendingResult<Moments.LoadMomentsResult> load(GoogleApiClient googleApiClient, int maxResults, String pageToken, Uri targetUrl, String type, String userId) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.nr.2
            final /* synthetic */ int YC;
            final /* synthetic */ String alE;
            final /* synthetic */ Uri alF;
            final /* synthetic */ String alG;
            final /* synthetic */ String alH;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(int maxResults2, String pageToken2, Uri targetUrl2, String type2, String userId2) {
                super();
                i = maxResults2;
                str = pageToken2;
                uri = targetUrl2;
                str = type2;
                str = userId2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.a(this, i, str, uri, str, str);
            }
        });
    }

    @Override // com.google.android.gms.plus.Moments
    public PendingResult<Status> remove(GoogleApiClient googleApiClient, String momentId) {
        return googleApiClient.b(new b() { // from class: com.google.android.gms.internal.nr.4
            final /* synthetic */ String alJ;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass4(String momentId2) {
                super();
                str = momentId2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.removeMoment(str);
                b((AnonymousClass4) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.plus.Moments
    public PendingResult<Status> write(GoogleApiClient googleApiClient, Moment moment) {
        return googleApiClient.b(new c() { // from class: com.google.android.gms.internal.nr.3
            final /* synthetic */ Moment alI;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass3(Moment moment2) {
                super();
                moment = moment2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.a(this, moment);
            }
        });
    }
}
