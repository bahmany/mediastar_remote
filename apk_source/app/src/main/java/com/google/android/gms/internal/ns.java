package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import java.util.Collection;

/* loaded from: classes.dex */
public final class ns implements People {

    /* renamed from: com.google.android.gms.internal.ns$1 */
    class AnonymousClass1 extends a {
        final /* synthetic */ String alE;
        final /* synthetic */ int alL;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(int i, String str) {
            super();
            i = i;
            str = str;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            a(eVar.a(this, i, str));
        }
    }

    /* renamed from: com.google.android.gms.internal.ns$2 */
    class AnonymousClass2 extends a {
        final /* synthetic */ String alE;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(String str) {
            super();
            str = str;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            a(eVar.r(this, str));
        }
    }

    /* renamed from: com.google.android.gms.internal.ns$3 */
    class AnonymousClass3 extends a {
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.l(this);
        }
    }

    /* renamed from: com.google.android.gms.internal.ns$4 */
    class AnonymousClass4 extends a {
        final /* synthetic */ Collection alN;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass4(Collection collection) {
            super();
            collection = collection;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.a(this, collection);
        }
    }

    /* renamed from: com.google.android.gms.internal.ns$5 */
    class AnonymousClass5 extends a {
        final /* synthetic */ String[] alO;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass5(String[] strArr) {
            super();
            strArr = strArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(com.google.android.gms.plus.internal.e eVar) {
            eVar.d(this, strArr);
        }
    }

    private static abstract class a extends Plus.a<People.LoadPeopleResult> {

        /* renamed from: com.google.android.gms.internal.ns$a$1 */
        class AnonymousClass1 implements People.LoadPeopleResult {
            final /* synthetic */ Status CW;

            AnonymousClass1(Status status) {
                status = status;
            }

            @Override // com.google.android.gms.plus.People.LoadPeopleResult
            public String getNextPageToken() {
                return null;
            }

            @Override // com.google.android.gms.plus.People.LoadPeopleResult
            public PersonBuffer getPersonBuffer() {
                return null;
            }

            @Override // com.google.android.gms.common.api.Result
            public Status getStatus() {
                return status;
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
        /* renamed from: aD */
        public People.LoadPeopleResult c(Status status) {
            return new People.LoadPeopleResult() { // from class: com.google.android.gms.internal.ns.a.1
                final /* synthetic */ Status CW;

                AnonymousClass1(Status status2) {
                    status = status2;
                }

                @Override // com.google.android.gms.plus.People.LoadPeopleResult
                public String getNextPageToken() {
                    return null;
                }

                @Override // com.google.android.gms.plus.People.LoadPeopleResult
                public PersonBuffer getPersonBuffer() {
                    return null;
                }

                @Override // com.google.android.gms.common.api.Result
                public Status getStatus() {
                    return status;
                }

                @Override // com.google.android.gms.common.api.Releasable
                public void release() {
                }
            };
        }
    }

    @Override // com.google.android.gms.plus.People
    public Person getCurrentPerson(GoogleApiClient googleApiClient) {
        return Plus.a(googleApiClient, Plus.CU).getCurrentPerson();
    }

    @Override // com.google.android.gms.plus.People
    public PendingResult<People.LoadPeopleResult> load(GoogleApiClient googleApiClient, Collection<String> personIds) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.ns.4
            final /* synthetic */ Collection alN;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass4(Collection personIds2) {
                super();
                collection = personIds2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.a(this, collection);
            }
        });
    }

    @Override // com.google.android.gms.plus.People
    public PendingResult<People.LoadPeopleResult> load(GoogleApiClient googleApiClient, String... personIds) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.ns.5
            final /* synthetic */ String[] alO;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass5(String[] personIds2) {
                super();
                strArr = personIds2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.d(this, strArr);
            }
        });
    }

    @Override // com.google.android.gms.plus.People
    public PendingResult<People.LoadPeopleResult> loadConnected(GoogleApiClient googleApiClient) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.ns.3
            AnonymousClass3() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                eVar.l(this);
            }
        });
    }

    @Override // com.google.android.gms.plus.People
    public PendingResult<People.LoadPeopleResult> loadVisible(GoogleApiClient googleApiClient, int orderBy, String pageToken) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.ns.1
            final /* synthetic */ String alE;
            final /* synthetic */ int alL;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(int orderBy2, String pageToken2) {
                super();
                i = orderBy2;
                str = pageToken2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                a(eVar.a(this, i, str));
            }
        });
    }

    @Override // com.google.android.gms.plus.People
    public PendingResult<People.LoadPeopleResult> loadVisible(GoogleApiClient googleApiClient, String pageToken) {
        return googleApiClient.a((GoogleApiClient) new a() { // from class: com.google.android.gms.internal.ns.2
            final /* synthetic */ String alE;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(String pageToken2) {
                super();
                str = pageToken2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(com.google.android.gms.plus.internal.e eVar) {
                a(eVar.r(this, str));
            }
        });
    }
}
