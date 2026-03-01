package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.location.Location;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/* loaded from: classes.dex */
public class lt implements FusedLocationProviderApi {

    /* renamed from: com.google.android.gms.internal.lt$1 */
    class AnonymousClass1 extends a {
        final /* synthetic */ LocationRequest aet;
        final /* synthetic */ LocationListener aeu;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(LocationRequest locationRequest, LocationListener locationListener) {
            super();
            locationRequest = locationRequest;
            locationListener = locationListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.requestLocationUpdates(locationRequest, locationListener, null);
            b((AnonymousClass1) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$2 */
    class AnonymousClass2 extends a {
        final /* synthetic */ Location aew;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(Location location) {
            super();
            location = location;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.setMockLocation(location);
            b((AnonymousClass2) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$3 */
    class AnonymousClass3 extends a {
        final /* synthetic */ LocationRequest aet;
        final /* synthetic */ LocationListener aeu;
        final /* synthetic */ Looper aex;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(LocationRequest locationRequest, LocationListener locationListener, Looper looper) {
            super();
            locationRequest = locationRequest;
            locationListener = locationListener;
            looper = looper;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.requestLocationUpdates(locationRequest, locationListener, looper);
            b((AnonymousClass3) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$4 */
    class AnonymousClass4 extends a {
        final /* synthetic */ PendingIntent aer;
        final /* synthetic */ LocationRequest aet;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass4(LocationRequest locationRequest, PendingIntent pendingIntent) {
            super();
            locationRequest = locationRequest;
            pendingIntent = pendingIntent;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.requestLocationUpdates(locationRequest, pendingIntent);
            b((AnonymousClass4) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$5 */
    class AnonymousClass5 extends a {
        final /* synthetic */ LocationListener aeu;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass5(LocationListener locationListener) {
            super();
            locationListener = locationListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.removeLocationUpdates(locationListener);
            b((AnonymousClass5) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$6 */
    class AnonymousClass6 extends a {
        final /* synthetic */ PendingIntent aer;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass6(PendingIntent pendingIntent) {
            super();
            pendingIntent = pendingIntent;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.removeLocationUpdates(pendingIntent);
            b((AnonymousClass6) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.lt$7 */
    class AnonymousClass7 extends a {
        final /* synthetic */ boolean aey;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass7(boolean z) {
            super();
            z = z;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ly lyVar) throws RemoteException {
            lyVar.setMockMode(z);
            b((AnonymousClass7) Status.Jo);
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

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public Location getLastLocation(GoogleApiClient client) {
        try {
            return LocationServices.e(client).getLastLocation();
        } catch (Exception e) {
            return null;
        }
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> removeLocationUpdates(GoogleApiClient client, PendingIntent callbackIntent) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.6
            final /* synthetic */ PendingIntent aer;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass6(PendingIntent callbackIntent2) {
                super();
                pendingIntent = callbackIntent2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.removeLocationUpdates(pendingIntent);
                b((AnonymousClass6) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> removeLocationUpdates(GoogleApiClient client, LocationListener listener) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.5
            final /* synthetic */ LocationListener aeu;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass5(LocationListener listener2) {
                super();
                locationListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.removeLocationUpdates(locationListener);
                b((AnonymousClass5) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient client, LocationRequest request, PendingIntent callbackIntent) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.4
            final /* synthetic */ PendingIntent aer;
            final /* synthetic */ LocationRequest aet;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass4(LocationRequest request2, PendingIntent callbackIntent2) {
                super();
                locationRequest = request2;
                pendingIntent = callbackIntent2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.requestLocationUpdates(locationRequest, pendingIntent);
                b((AnonymousClass4) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient client, LocationRequest request, LocationListener listener) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.1
            final /* synthetic */ LocationRequest aet;
            final /* synthetic */ LocationListener aeu;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(LocationRequest request2, LocationListener listener2) {
                super();
                locationRequest = request2;
                locationListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.requestLocationUpdates(locationRequest, locationListener, null);
                b((AnonymousClass1) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient client, LocationRequest request, LocationListener listener, Looper looper) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.3
            final /* synthetic */ LocationRequest aet;
            final /* synthetic */ LocationListener aeu;
            final /* synthetic */ Looper aex;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass3(LocationRequest request2, LocationListener listener2, Looper looper2) {
                super();
                locationRequest = request2;
                locationListener = listener2;
                looper = looper2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.requestLocationUpdates(locationRequest, locationListener, looper);
                b((AnonymousClass3) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> setMockLocation(GoogleApiClient client, Location mockLocation) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.2
            final /* synthetic */ Location aew;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(Location mockLocation2) {
                super();
                location = mockLocation2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.setMockLocation(location);
                b((AnonymousClass2) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.location.FusedLocationProviderApi
    public PendingResult<Status> setMockMode(GoogleApiClient client, boolean isMockMode) {
        return client.b(new a() { // from class: com.google.android.gms.internal.lt.7
            final /* synthetic */ boolean aey;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass7(boolean isMockMode2) {
                super();
                z = isMockMode2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ly lyVar) throws RemoteException {
                lyVar.setMockMode(z);
                b((AnonymousClass7) Status.Jo);
            }
        });
    }
}
