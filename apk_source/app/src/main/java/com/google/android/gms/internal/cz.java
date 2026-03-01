package com.google.android.gms.internal;

import android.app.Activity;
import android.os.RemoteException;
import com.google.ads.mediation.MediationAdapter;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.NetworkExtras;
import com.google.android.gms.internal.cu;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class cz<NETWORK_EXTRAS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> extends cu.a {
    private final MediationAdapter<NETWORK_EXTRAS, SERVER_PARAMETERS> qG;
    private final NETWORK_EXTRAS qH;

    public cz(MediationAdapter<NETWORK_EXTRAS, SERVER_PARAMETERS> mediationAdapter, NETWORK_EXTRAS network_extras) {
        this.qG = mediationAdapter;
        this.qH = network_extras;
    }

    private SERVER_PARAMETERS b(String str, int i, String str2) throws RemoteException {
        HashMap map;
        try {
            if (str != null) {
                JSONObject jSONObject = new JSONObject(str);
                map = new HashMap(jSONObject.length());
                Iterator<String> itKeys = jSONObject.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    map.put(next, jSONObject.getString(next));
                }
            } else {
                map = new HashMap(0);
            }
            Class<SERVER_PARAMETERS> serverParametersType = this.qG.getServerParametersType();
            if (serverParametersType == null) {
                return null;
            }
            SERVER_PARAMETERS server_parametersNewInstance = serverParametersType.newInstance();
            server_parametersNewInstance.load(map);
            return server_parametersNewInstance;
        } catch (Throwable th) {
            gs.d("Could not get MediationServerParameters.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, av avVar, String str, cv cvVar) throws RemoteException {
        a(dVar, avVar, str, (String) null, cvVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, av avVar, String str, String str2, cv cvVar) throws RemoteException {
        if (!(this.qG instanceof MediationInterstitialAdapter)) {
            gs.W("MediationAdapter is not a MediationInterstitialAdapter: " + this.qG.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Requesting interstitial ad from adapter.");
        try {
            ((MediationInterstitialAdapter) this.qG).requestInterstitialAd(new da(cvVar), (Activity) com.google.android.gms.dynamic.e.f(dVar), b(str, avVar.nX, str2), db.d(avVar), this.qH);
        } catch (Throwable th) {
            gs.d("Could not request interstitial ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, ay ayVar, av avVar, String str, cv cvVar) throws RemoteException {
        a(dVar, ayVar, avVar, str, null, cvVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.cu
    public void a(com.google.android.gms.dynamic.d dVar, ay ayVar, av avVar, String str, String str2, cv cvVar) throws RemoteException {
        if (!(this.qG instanceof MediationBannerAdapter)) {
            gs.W("MediationAdapter is not a MediationBannerAdapter: " + this.qG.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Requesting banner ad from adapter.");
        try {
            ((MediationBannerAdapter) this.qG).requestBannerAd(new da(cvVar), (Activity) com.google.android.gms.dynamic.e.f(dVar), b(str, avVar.nX, str2), db.b(ayVar), db.d(avVar), this.qH);
        } catch (Throwable th) {
            gs.d("Could not request banner ad from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void destroy() throws RemoteException {
        try {
            this.qG.destroy();
        } catch (Throwable th) {
            gs.d("Could not destroy adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public com.google.android.gms.dynamic.d getView() throws RemoteException {
        if (!(this.qG instanceof MediationBannerAdapter)) {
            gs.W("MediationAdapter is not a MediationBannerAdapter: " + this.qG.getClass().getCanonicalName());
            throw new RemoteException();
        }
        try {
            return com.google.android.gms.dynamic.e.k(((MediationBannerAdapter) this.qG).getBannerView());
        } catch (Throwable th) {
            gs.d("Could not get banner view from adapter.", th);
            throw new RemoteException();
        }
    }

    @Override // com.google.android.gms.internal.cu
    public void pause() throws RemoteException {
        throw new RemoteException();
    }

    @Override // com.google.android.gms.internal.cu
    public void resume() throws RemoteException {
        throw new RemoteException();
    }

    @Override // com.google.android.gms.internal.cu
    public void showInterstitial() throws RemoteException {
        if (!(this.qG instanceof MediationInterstitialAdapter)) {
            gs.W("MediationAdapter is not a MediationInterstitialAdapter: " + this.qG.getClass().getCanonicalName());
            throw new RemoteException();
        }
        gs.S("Showing interstitial from adapter.");
        try {
            ((MediationInterstitialAdapter) this.qG).showInterstitial();
        } catch (Throwable th) {
            gs.d("Could not show interstitial from adapter.", th);
            throw new RemoteException();
        }
    }
}
