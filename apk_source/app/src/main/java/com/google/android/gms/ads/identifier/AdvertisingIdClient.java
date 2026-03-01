package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.a;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.s;
import java.io.IOException;

/* loaded from: classes.dex */
public final class AdvertisingIdClient {
    a lk;
    s ll;
    boolean lm;
    final Context mContext;

    public static final class Info {
        private final String ln;
        private final boolean lo;

        public Info(String advertisingId, boolean limitAdTrackingEnabled) {
            this.ln = advertisingId;
            this.lo = limitAdTrackingEnabled;
        }

        public String getId() {
            return this.ln;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.lo;
        }

        public String toString() {
            return "{" + this.ln + "}" + this.lo;
        }
    }

    public AdvertisingIdClient(Context context) {
        n.i(context);
        this.mContext = context;
        this.lm = false;
    }

    static s a(Context context, a aVar) throws IOException {
        try {
            return s.a.b(aVar.fX());
        } catch (InterruptedException e) {
            throw new IOException("Interrupted exception");
        }
    }

    public static Info getAdvertisingIdInfo(Context context) throws GooglePlayServicesRepairableException, IllegalStateException, GooglePlayServicesNotAvailableException, IOException {
        AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(context);
        try {
            advertisingIdClient.start();
            return advertisingIdClient.W();
        } finally {
            advertisingIdClient.finish();
        }
    }

    static a i(Context context) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException, PackageManager.NameNotFoundException, IOException {
        try {
            context.getPackageManager().getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, 0);
            try {
                GooglePlayServicesUtil.D(context);
                a aVar = new a();
                Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
                if (context.bindService(intent, aVar, 1)) {
                    return aVar;
                }
                throw new IOException("Connection failure");
            } catch (GooglePlayServicesNotAvailableException e) {
                throw new IOException(e);
            }
        } catch (PackageManager.NameNotFoundException e2) {
            throw new GooglePlayServicesNotAvailableException(9);
        }
    }

    public Info W() throws IOException {
        n.aU("Calling this from your main thread can lead to deadlock");
        n.i(this.lk);
        n.i(this.ll);
        if (!this.lm) {
            throw new IOException("AdvertisingIdService is not connected.");
        }
        try {
            return new Info(this.ll.getId(), this.ll.a(true));
        } catch (RemoteException e) {
            Log.i("AdvertisingIdClient", "GMS remote exception ", e);
            throw new IOException("Remote exception");
        }
    }

    public void finish() {
        n.aU("Calling this from your main thread can lead to deadlock");
        if (this.mContext == null || this.lk == null) {
            return;
        }
        try {
            if (this.lm) {
                this.mContext.unbindService(this.lk);
            }
        } catch (IllegalArgumentException e) {
            Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e);
        }
        this.lm = false;
        this.ll = null;
        this.lk = null;
    }

    public void start() throws GooglePlayServicesRepairableException, IllegalStateException, GooglePlayServicesNotAvailableException, IOException {
        n.aU("Calling this from your main thread can lead to deadlock");
        if (this.lm) {
            finish();
        }
        this.lk = i(this.mContext);
        this.ll = a(this.mContext, this.lk);
        this.lm = true;
    }
}
