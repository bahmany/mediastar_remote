package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.n;

/* loaded from: classes.dex */
public class d extends Fragment implements DialogInterface.OnCancelListener, LoaderManager.LoaderCallbacks<ConnectionResult> {
    private boolean Ju;
    private ConnectionResult Jw;
    private int Jv = -1;
    private final Handler Jx = new Handler(Looper.getMainLooper());
    private final SparseArray<b> Jy = new SparseArray<>();

    static class a extends Loader<ConnectionResult> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private boolean JA;
        private ConnectionResult JB;
        public final GoogleApiClient Jz;

        public a(Context context, GoogleApiClient googleApiClient) {
            super(context);
            this.Jz = googleApiClient;
        }

        private void a(ConnectionResult connectionResult) {
            this.JB = connectionResult;
            if (!isStarted() || isAbandoned()) {
                return;
            }
            deliverResult(connectionResult);
        }

        public void gw() {
            if (this.JA) {
                this.JA = false;
                if (!isStarted() || isAbandoned()) {
                    return;
                }
                this.Jz.connect();
            }
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            this.JA = false;
            a(ConnectionResult.HE);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            this.JA = true;
            a(result);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
        }

        @Override // android.support.v4.content.Loader
        protected void onReset() {
            this.JB = null;
            this.JA = false;
            this.Jz.unregisterConnectionCallbacks(this);
            this.Jz.unregisterConnectionFailedListener(this);
            this.Jz.disconnect();
        }

        @Override // android.support.v4.content.Loader
        protected void onStartLoading() {
            super.onStartLoading();
            this.Jz.registerConnectionCallbacks(this);
            this.Jz.registerConnectionFailedListener(this);
            if (this.JB != null) {
                deliverResult(this.JB);
            }
            if (this.Jz.isConnected() || this.Jz.isConnecting() || this.JA) {
                return;
            }
            this.Jz.connect();
        }

        @Override // android.support.v4.content.Loader
        protected void onStopLoading() {
            this.Jz.disconnect();
        }
    }

    private static class b {
        public final GoogleApiClient.OnConnectionFailedListener JC;
        public final GoogleApiClient Jz;

        private b(GoogleApiClient googleApiClient, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            this.Jz = googleApiClient;
            this.JC = onConnectionFailedListener;
        }
    }

    private class c implements Runnable {
        private final int JD;
        private final ConnectionResult JE;

        public c(int i, ConnectionResult connectionResult) {
            this.JD = i;
            this.JE = connectionResult;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.JE.hasResolution()) {
                try {
                    this.JE.startResolutionForResult(d.this.getActivity(), ((d.this.getActivity().getSupportFragmentManager().getFragments().indexOf(d.this) + 1) << 16) + 1);
                    return;
                } catch (IntentSender.SendIntentException e) {
                    d.this.gv();
                    return;
                }
            }
            if (GooglePlayServicesUtil.isUserRecoverableError(this.JE.getErrorCode())) {
                GooglePlayServicesUtil.showErrorDialogFragment(this.JE.getErrorCode(), d.this.getActivity(), d.this, 2, d.this);
            } else {
                d.this.b(this.JD, this.JE);
            }
        }
    }

    public static d a(FragmentActivity fragmentActivity) {
        n.aT("Must be called from main thread of process");
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        try {
            d dVar = (d) supportFragmentManager.findFragmentByTag("GmsSupportLifecycleFragment");
            if (dVar != null && !dVar.isRemoving()) {
                return dVar;
            }
            d dVar2 = new d();
            supportFragmentManager.beginTransaction().add(dVar2, "GmsSupportLifecycleFragment").commit();
            supportFragmentManager.executePendingTransactions();
            return dVar2;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Fragment with tag GmsSupportLifecycleFragment is not a SupportLifecycleFragment", e);
        }
    }

    private void a(int i, ConnectionResult connectionResult) {
        if (this.Ju) {
            return;
        }
        this.Ju = true;
        this.Jv = i;
        this.Jw = connectionResult;
        this.Jx.post(new c(i, connectionResult));
    }

    private void an(int i) {
        if (i == this.Jv) {
            gv();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(int i, ConnectionResult connectionResult) {
        Log.w("GmsSupportLifecycleFragment", "Unresolved error while connecting client. Stopping auto-manage.");
        b bVar = this.Jy.get(i);
        if (bVar != null) {
            al(i);
            GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = bVar.JC;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
        gv();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void gv() {
        this.Ju = false;
        this.Jv = -1;
        this.Jw = null;
        LoaderManager loaderManager = getLoaderManager();
        for (int i = 0; i < this.Jy.size(); i++) {
            int iKeyAt = this.Jy.keyAt(i);
            a aVarAm = am(iKeyAt);
            if (aVarAm != null) {
                aVarAm.gw();
            }
            loaderManager.initLoader(iKeyAt, null, this);
        }
    }

    public void a(int i, GoogleApiClient googleApiClient, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        n.b(googleApiClient, "GoogleApiClient instance cannot be null");
        n.a(this.Jy.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        this.Jy.put(i, new b(googleApiClient, onConnectionFailedListener));
        if (getActivity() != null) {
            getLoaderManager().initLoader(i, null, this);
        }
    }

    @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public void onLoadFinished(Loader<ConnectionResult> loader, ConnectionResult connectionResult) {
        if (connectionResult.isSuccess()) {
            an(loader.getId());
        } else {
            a(loader.getId(), connectionResult);
        }
    }

    public GoogleApiClient ak(int i) {
        a aVarAm;
        if (getActivity() == null || (aVarAm = am(i)) == null) {
            return null;
        }
        return aVarAm.Jz;
    }

    public void al(int i) {
        getLoaderManager().destroyLoader(i);
        this.Jy.remove(i);
    }

    a am(int i) {
        try {
            return (a) getLoaderManager().getLoader(i);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unknown loader in SupportLifecycleFragment", e);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:4:0x0005  */
    @Override // android.support.v4.app.Fragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onActivityResult(int r4, int r5, android.content.Intent r6) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            switch(r4) {
                case 1: goto L17;
                case 2: goto Lc;
                default: goto L5;
            }
        L5:
            r0 = r1
        L6:
            if (r0 == 0) goto L1b
            r3.gv()
        Lb:
            return
        Lc:
            android.support.v4.app.FragmentActivity r2 = r3.getActivity()
            int r2 = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(r2)
            if (r2 != 0) goto L5
            goto L6
        L17:
            r2 = -1
            if (r5 != r2) goto L5
            goto L6
        L1b:
            int r0 = r3.Jv
            com.google.android.gms.common.ConnectionResult r1 = r3.Jw
            r3.b(r0, r1)
            goto Lb
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.api.d.onActivityResult(int, int, android.content.Intent):void");
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.Jy.size()) {
                return;
            }
            int iKeyAt = this.Jy.keyAt(i2);
            a aVarAm = am(iKeyAt);
            if (aVarAm == null || this.Jy.valueAt(i2).Jz == aVarAm.Jz) {
                getLoaderManager().initLoader(iKeyAt, null, this);
            } else {
                getLoaderManager().restartLoader(iKeyAt, null, this);
            }
            i = i2 + 1;
        }
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        b(this.Jv, this.Jw);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.Ju = savedInstanceState.getBoolean("resolving_error", false);
            this.Jv = savedInstanceState.getInt("failed_client_id", -1);
            if (this.Jv >= 0) {
                this.Jw = new ConnectionResult(savedInstanceState.getInt("failed_status"), (PendingIntent) savedInstanceState.getParcelable("failed_resolution"));
            }
        }
    }

    @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
    public Loader<ConnectionResult> onCreateLoader(int id, Bundle args) {
        return new a(getActivity(), this.Jy.get(id).Jz);
    }

    @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<ConnectionResult> loader) {
        if (loader.getId() == this.Jv) {
            gv();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resolving_error", this.Ju);
        if (this.Jv >= 0) {
            outState.putInt("failed_client_id", this.Jv);
            outState.putInt("failed_status", this.Jw.getErrorCode());
            outState.putParcelable("failed_resolution", this.Jw.getResolution());
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.Ju) {
            return;
        }
        for (int i = 0; i < this.Jy.size(); i++) {
            getLoaderManager().initLoader(this.Jy.keyAt(i), null, this);
        }
    }
}
