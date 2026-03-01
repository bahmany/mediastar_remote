package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.ads.mediation.MediationAdapter;
import com.google.ads.mediation.MediationServerParameters;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.internal.ct;
import java.util.Map;

@ez
/* loaded from: classes.dex */
public final class cs extends ct.a {
    private Map<Class<? extends NetworkExtras>, NetworkExtras> qC;

    private <NETWORK_EXTRAS extends com.google.ads.mediation.NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> cu z(String str) throws RemoteException {
        try {
            Class<?> cls = Class.forName(str, false, cs.class.getClassLoader());
            if (MediationAdapter.class.isAssignableFrom(cls)) {
                MediationAdapter mediationAdapter = (MediationAdapter) cls.newInstance();
                return new cz(mediationAdapter, (com.google.ads.mediation.NetworkExtras) this.qC.get(mediationAdapter.getAdditionalParametersType()));
            }
            if (com.google.android.gms.ads.mediation.MediationAdapter.class.isAssignableFrom(cls)) {
                return new cx((com.google.android.gms.ads.mediation.MediationAdapter) cls.newInstance());
            }
            gs.W("Could not instantiate mediation adapter: " + str + " (not a valid adapter).");
            throw new RemoteException();
        } catch (Throwable th) {
            gs.W("Could not instantiate mediation adapter: " + str + ". " + th.getMessage());
            throw new RemoteException();
        }
    }

    public void d(Map<Class<? extends NetworkExtras>, NetworkExtras> map) {
        this.qC = map;
    }

    @Override // com.google.android.gms.internal.ct
    public cu x(String str) throws RemoteException {
        return z(str);
    }

    @Override // com.google.android.gms.internal.ct
    public boolean y(String str) throws RemoteException {
        try {
            return CustomEvent.class.isAssignableFrom(Class.forName(str, false, cs.class.getClassLoader()));
        } catch (Throwable th) {
            gs.W("Could not load custom event implementation class: " + str + ", assuming old implementation.");
            return false;
        }
    }
}
