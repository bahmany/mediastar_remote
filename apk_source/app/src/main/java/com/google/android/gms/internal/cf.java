package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

@ez
/* loaded from: classes.dex */
public final class cf {

    public static final class a implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
        private final Object mw;
        private final b pN;
        private final cg pO;

        public a(Context context, b bVar) {
            this(context, bVar, false);
        }

        a(Context context, b bVar, boolean z) {
            this.mw = new Object();
            this.pN = bVar;
            this.pO = new cg(context, this, this, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            if (z) {
                return;
            }
            this.pO.connect();
        }

        /* JADX WARN: Removed duplicated region for block: B:11:0x0023 A[Catch: all -> 0x0080, DONT_GENERATE, TryCatch #0 {, blocks: (B:7:0x0013, B:9:0x001b, B:12:0x0028, B:11:0x0023, B:17:0x0035, B:19:0x003d, B:21:0x0045, B:25:0x0052, B:27:0x005a, B:29:0x0062, B:31:0x006a, B:33:0x0072, B:36:0x007f, B:35:0x007a, B:4:0x0007, B:6:0x000f, B:16:0x0030, B:24:0x004d), top: B:42:0x0007, inners: #1, #2, #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:40:0x0083  */
        @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void onConnected(android.os.Bundle r5) {
            /*
                r4 = this;
                android.os.Bundle r1 = com.google.android.gms.internal.bn.bs()
                java.lang.Object r2 = r4.mw
                monitor-enter(r2)
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.IllegalStateException -> L2f android.os.RemoteException -> L4c java.lang.Throwable -> L69
                com.google.android.gms.internal.ch r0 = r0.bC()     // Catch: java.lang.IllegalStateException -> L2f android.os.RemoteException -> L4c java.lang.Throwable -> L69
                if (r0 == 0) goto L85
                android.os.Bundle r0 = r0.bD()     // Catch: java.lang.IllegalStateException -> L2f android.os.RemoteException -> L4c java.lang.Throwable -> L69
            L13:
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r1 = r1.isConnected()     // Catch: java.lang.Throwable -> L80
                if (r1 != 0) goto L23
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r1 = r1.isConnecting()     // Catch: java.lang.Throwable -> L80
                if (r1 == 0) goto L28
            L23:
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                r1.disconnect()     // Catch: java.lang.Throwable -> L80
            L28:
                monitor-exit(r2)     // Catch: java.lang.Throwable -> L80
                com.google.android.gms.internal.cf$b r1 = r4.pN
                r1.a(r0)
                return
            L2f:
                r0 = move-exception
                java.lang.String r3 = "Error when get Gservice values"
                com.google.android.gms.internal.gs.d(r3, r0)     // Catch: java.lang.Throwable -> L69
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r0 = r0.isConnected()     // Catch: java.lang.Throwable -> L80
                if (r0 != 0) goto L45
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r0 = r0.isConnecting()     // Catch: java.lang.Throwable -> L80
                if (r0 == 0) goto L83
            L45:
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                r0.disconnect()     // Catch: java.lang.Throwable -> L80
                r0 = r1
                goto L28
            L4c:
                r0 = move-exception
                java.lang.String r3 = "Error when get Gservice values"
                com.google.android.gms.internal.gs.d(r3, r0)     // Catch: java.lang.Throwable -> L69
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r0 = r0.isConnected()     // Catch: java.lang.Throwable -> L80
                if (r0 != 0) goto L62
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r0 = r0.isConnecting()     // Catch: java.lang.Throwable -> L80
                if (r0 == 0) goto L83
            L62:
                com.google.android.gms.internal.cg r0 = r4.pO     // Catch: java.lang.Throwable -> L80
                r0.disconnect()     // Catch: java.lang.Throwable -> L80
                r0 = r1
                goto L28
            L69:
                r0 = move-exception
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r1 = r1.isConnected()     // Catch: java.lang.Throwable -> L80
                if (r1 != 0) goto L7a
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                boolean r1 = r1.isConnecting()     // Catch: java.lang.Throwable -> L80
                if (r1 == 0) goto L7f
            L7a:
                com.google.android.gms.internal.cg r1 = r4.pO     // Catch: java.lang.Throwable -> L80
                r1.disconnect()     // Catch: java.lang.Throwable -> L80
            L7f:
                throw r0     // Catch: java.lang.Throwable -> L80
            L80:
                r0 = move-exception
                monitor-exit(r2)     // Catch: java.lang.Throwable -> L80
                throw r0
            L83:
                r0 = r1
                goto L28
            L85:
                r0 = r1
                goto L13
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.cf.a.onConnected(android.os.Bundle):void");
        }

        @Override // com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            this.pN.a(bn.bs());
        }

        @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
        public void onDisconnected() {
            gs.S("Disconnected from remote ad request service.");
        }
    }

    public interface b {
        void a(Bundle bundle);
    }

    public static void a(Context context, b bVar) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) != 0) {
            bVar.a(bn.bs());
        } else {
            new a(context, bVar);
        }
    }
}
