package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.MediationBannerAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.internal.cu;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class cx extends cu.a {
    private final MediationAdapter qE;

    public cx(MediationAdapter mediationAdapter) {
        this.qE = mediationAdapter;
    }

    private Bundle a(String str, int i, String str2) throws RemoteException {
        gs.W("Server parameters: " + str);
        try {
            Bundle bundle = new Bundle();
            if (str != null) {
                JSONObject jSONObject = new JSONObject(str);
                Bundle bundle2 = new Bundle();
                Iterator<String> itKeys = jSONObject.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    bundle2.putString(next, jSONObject.getString(next));
                }
                bundle = bundle2;
            }
            if (this.qE instanceof AdMobAdapter) {
                bundle.putString("adJson", str2);
                bundle.putInt("tagForChildDirectedTreatment", i);
            }
            return bundle;
        } catch (Throwable th) {
            gs.d("Could not get Server Parameters Bundle.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, av avVar, String str, cv cvVar) throws RemoteException {
        a(dVar, avVar, str, (String) null, cvVar);
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, av avVar, String str, String str2, cv cvVar) throws RemoteException {
        if (!(this.qE instanceof MediationInterstitialAdapter)) {
            gs.W("MediationAdapter is not a MediationInterstitialAdapter: " + this.qE.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Requesting interstitial ad from adapter.");
        try {
            MediationInterstitialAdapter mediationInterstitialAdapter = (MediationInterstitialAdapter) this.qE;
            mediationInterstitialAdapter.requestInterstitialAd((Context) com.google.android.gms.dynamic.e.f(dVar), new cy(cvVar), a(str, avVar.nX, str2), new cw(new Date(avVar.nT), avVar.nU, avVar.nV != null ? new HashSet(avVar.nV) : null, avVar.ob, avVar.nW, avVar.nX), avVar.od != null ? avVar.od.getBundle(mediationInterstitialAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            gs.d("Could not request interstitial ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, ay ayVar, av avVar, String str, cv cvVar) throws RemoteException {
        a(dVar, ayVar, avVar, str, null, cvVar);
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, ay ayVar, av avVar, String str, String str2, cv cvVar) throws RemoteException {
        if (!(this.qE instanceof MediationBannerAdapter)) {
            gs.W("MediationAdapter is not a MediationBannerAdapter: " + this.qE.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Requesting banner ad from adapter.");
        try {
            MediationBannerAdapter mediationBannerAdapter = (MediationBannerAdapter) this.qE;
            mediationBannerAdapter.requestBannerAd((Context) com.google.android.gms.dynamic.e.f(dVar), new cy(cvVar), a(str, avVar.nX, str2), com.google.android.gms.ads.a.a(ayVar.width, ayVar.height, ayVar.of), new cw(new Date(avVar.nT), avVar.nU, avVar.nV != null ? new HashSet(avVar.nV) : null, avVar.ob, avVar.nW, avVar.nX), avVar.od != null ? avVar.od.getBundle(mediationBannerAdapter.getClass().getName()) : null);
        } catch (Throwable th) {
            gs.d("Could not request banner ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void destroy() throws RemoteException {
        try {
            this.qE.onDestroy();
        } catch (Throwable th) {
            gs.d("Could not destroy adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public com.google.android.gms.dynamic.d getView() throws RemoteException {
        if (!(this.qE instanceof MediationBannerAdapter)) {
            gs.W("MediationAdapter is not a MediationBannerAdapter: " + this.qE.getClass().getCanonicalName());
            throw new RemoteException();
        }
        try {
            return com.google.android.gms.dynamic.e.k(((MediationBannerAdapter) this.qE).getBannerView());
        } catch (Throwable th) {
            gs.d("Could not get banner view from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void pause() throws RemoteException {
        try {
            this.qE.onPause();
        } catch (Throwable th) {
            gs.d("Could not pause adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void resume() throws RemoteException {
        try {
            this.qE.onResume();
        } catch (Throwable th) {
            gs.d("Could not resume adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void showInterstitial() throws RemoteException {
        if (!(this.qE instanceof MediationInterstitialAdapter)) {
            gs.W("MediationAdapter is not a MediationInterstitialAdapter: " + this.qE.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Showing interstitial from adapter.");
        try {
            ((MediationInterstitialAdapter) this.qE).showInterstitial();
        } catch (Throwable th) {
            gs.d("Could not show interstitial from adapter.", th);
            throw new RemoteException();
        }
    }
}
