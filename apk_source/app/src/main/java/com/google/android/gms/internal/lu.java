package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class lu implements GeofencingApi {

    /* renamed from: com.google.android.gms.internal.lu$1 */
    class AnonymousClass1 extends a {
        final /* synthetic */ PendingIntent aeA;
        final /* synthetic */ List aez;

        /* renamed from: com.google.android.gms.internal.lu$1$1 */
        class C00781 implements LocationClient.OnAddGeofencesResultListener {
            C00781() {
            }

            @Override // com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener
            public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
                AnonymousClass1.this.b((AnonymousClass1) LocationStatusCodes.ef(statusCode));
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(List list, PendingIntent pendingIntent) {
            super();
            list = list;
            pendingIntent = pendingIntent;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.addGeofences(list, pendingIntent, new LocationClient.OnAddGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.1.1
                C00781() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener
                public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
                    AnonymousClass1.this.b((AnonymousClass1) LocationStatusCodes.ef(statusCode));
                }
            });
        }
    }

    /* renamed from: com.google.android.gms.internal.lu$2 */
    class AnonymousClass2 extends a {
        final /* synthetic */ PendingIntent aeA;

        /* renamed from: com.google.android.gms.internal.lu$2$1 */
        class AnonymousClass1 implements LocationClient.OnRemoveGeofencesResultListener {
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
            public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                AnonymousClass2.this.b((AnonymousClass2) LocationStatusCodes.ef(statusCode));
            }

            @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
            public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                Log.wtf("GeofencingImpl", "Request ID callback shouldn't have been called");
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(PendingIntent pendingIntent) {
            super();
            pendingIntent = pendingIntent;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.removeGeofences(pendingIntent, new LocationClient.OnRemoveGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.2.1
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                    AnonymousClass2.this.b((AnonymousClass2) LocationStatusCodes.ef(statusCode));
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                    Log.wtf("GeofencingImpl", "Request ID callback shouldn't have been called");
                }
            });
        }
    }

    /* renamed from: com.google.android.gms.internal.lu$3 */
    class AnonymousClass3 extends a {
        final /* synthetic */ List aeE;

        /* renamed from: com.google.android.gms.internal.lu$3$1 */
        class AnonymousClass1 implements LocationClient.OnRemoveGeofencesResultListener {
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
            public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                Log.wtf("GeofencingImpl", "PendingIntent callback shouldn't have been called");
            }

            @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
            public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                AnonymousClass3.this.b((AnonymousClass3) LocationStatusCodes.ef(statusCode));
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(List list) {
            super();
            list = list;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.removeGeofences(list, new LocationClient.OnRemoveGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.3.1
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                    Log.wtf("GeofencingImpl", "PendingIntent callback shouldn't have been called");
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                    AnonymousClass3.this.b((AnonymousClass3) LocationStatusCodes.ef(statusCode));
                }
            });
        }
    }

    private static abstract class a extends LocationServices.a<Status> {
        private a() {
        }

        /* synthetic */ a(AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return status;
        }
    }

    @Override // com.google.android.gms.location.GeofencingApi
    public PendingResult<Status> addGeofences(GoogleApiClient client, List<Geofence> geofences, PendingIntent pendingIntent) {
        ArrayList arrayList;
        if (geofences != null) {
            ArrayList arrayList2 = new ArrayList(geofences.size());
            for (Geofence geofence : geofences) {
                com.google.android.gms.common.internal.n.b(geofence instanceof mb, "Geofence must be created using Geofence.Builder.");
                arrayList2.add((mb) geofence);
            }
            arrayList = arrayList2;
        } else {
            arrayList = null;
        }
        return client.b(new a() { // from class: com.google.android.gms.internal.lu.1
            final /* synthetic */ PendingIntent aeA;
            final /* synthetic */ List aez;

            /* renamed from: com.google.android.gms.internal.lu$1$1 */
            class C00781 implements LocationClient.OnAddGeofencesResultListener {
                C00781() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener
                public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
                    AnonymousClass1.this.b((AnonymousClass1) LocationStatusCodes.ef(statusCode));
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(List arrayList3, PendingIntent pendingIntent2) {
                super();
                list = arrayList3;
                pendingIntent = pendingIntent2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.addGeofences(list, pendingIntent, new LocationClient.OnAddGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.1.1
                    C00781() {
                    }

                    @Override // com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener
                    public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
                        AnonymousClass1.this.b((AnonymousClass1) LocationStatusCodes.ef(statusCode));
                    }
                });
            }
        });
    }

    @Override // com.google.android.gms.location.GeofencingApi
    public PendingResult<Status> removeGeofences(GoogleApiClient client, PendingIntent pendingIntent) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lu.2
            final /* synthetic */ PendingIntent aeA;

            /* renamed from: com.google.android.gms.internal.lu$2$1 */
            class AnonymousClass1 implements LocationClient.OnRemoveGeofencesResultListener {
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                    AnonymousClass2.this.b((AnonymousClass2) LocationStatusCodes.ef(statusCode));
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                    Log.wtf("GeofencingImpl", "Request ID callback shouldn't have been called");
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(PendingIntent pendingIntent2) {
                super();
                pendingIntent = pendingIntent2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.removeGeofences(pendingIntent, new LocationClient.OnRemoveGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.2.1
                    AnonymousClass1() {
                    }

                    @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent2) {
                        AnonymousClass2.this.b((AnonymousClass2) LocationStatusCodes.ef(statusCode));
                    }

                    @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                        Log.wtf("GeofencingImpl", "Request ID callback shouldn't have been called");
                    }
                });
            }
        });
    }

    @Override // com.google.android.gms.location.GeofencingApi
    public PendingResult<Status> removeGeofences(GoogleApiClient client, List<String> geofenceRequestIds) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lu.3
            final /* synthetic */ List aeE;

            /* renamed from: com.google.android.gms.internal.lu$3$1 */
            class AnonymousClass1 implements LocationClient.OnRemoveGeofencesResultListener {
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                    Log.wtf("GeofencingImpl", "PendingIntent callback shouldn't have been called");
                }

                @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
                    AnonymousClass3.this.b((AnonymousClass3) LocationStatusCodes.ef(statusCode));
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass3(List geofenceRequestIds2) {
                super();
                list = geofenceRequestIds2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.removeGeofences(list, new LocationClient.OnRemoveGeofencesResultListener() { // from class: com.google.android.gms.internal.lu.3.1
                    AnonymousClass1() {
                    }

                    @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
                        Log.wtf("GeofencingImpl", "PendingIntent callback shouldn't have been called");
                    }

                    @Override // com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener
                    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds2) {
                        AnonymousClass3.this.b((AnonymousClass3) LocationStatusCodes.ef(statusCode));
                    }
                });
            }
        });
    }
}
