package com.google.android.gms.internal;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.ads.search.SearchAdRequest;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ez
/* loaded from: classes.dex */
public final class bg {
    public static final String DEVICE_ID_EMULATOR = gr.R("emulator");
    private final Date d;
    private final Set<String> f;
    private final Location h;
    private final String ol;
    private final int om;
    private final boolean on;
    private final Bundle oo;
    private final Map<Class<? extends NetworkExtras>, NetworkExtras> op;
    private final String oq;
    private final SearchAdRequest or;
    private final int os;
    private final Set<String> ot;

    public static final class a {
        private Date d;
        private Location h;
        private String ol;
        private String oq;
        private final HashSet<String> ou = new HashSet<>();
        private final Bundle oo = new Bundle();
        private final HashMap<Class<? extends NetworkExtras>, NetworkExtras> ov = new HashMap<>();
        private final HashSet<String> ow = new HashSet<>();
        private int om = -1;
        private boolean on = false;
        private int os = -1;

        public void a(Location location) {
            this.h = location;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Deprecated
        public void a(NetworkExtras networkExtras) {
            if (networkExtras instanceof AdMobExtras) {
                a(AdMobAdapter.class, ((AdMobExtras) networkExtras).getExtras());
            } else {
                this.ov.put(networkExtras.getClass(), networkExtras);
            }
        }

        public void a(Class<? extends MediationAdapter> cls, Bundle bundle) {
            this.oo.putBundle(cls.getName(), bundle);
        }

        public void a(Date date) {
            this.d = date;
        }

        public void b(Class<? extends CustomEvent> cls, Bundle bundle) {
            if (this.oo.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter") == null) {
                this.oo.putBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter", new Bundle());
            }
            this.oo.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter").putBundle(cls.getName(), bundle);
        }

        public void g(int i) {
            this.om = i;
        }

        public void g(boolean z) {
            this.on = z;
        }

        public void h(boolean z) {
            this.os = z ? 1 : 0;
        }

        public void r(String str) {
            this.ou.add(str);
        }

        public void s(String str) {
            this.ow.add(str);
        }

        public void t(String str) {
            this.ol = str;
        }

        public void u(String str) {
            this.oq = str;
        }
    }

    public bg(a aVar) {
        this(aVar, null);
    }

    public bg(a aVar, SearchAdRequest searchAdRequest) {
        this.d = aVar.d;
        this.ol = aVar.ol;
        this.om = aVar.om;
        this.f = Collections.unmodifiableSet(aVar.ou);
        this.h = aVar.h;
        this.on = aVar.on;
        this.oo = aVar.oo;
        this.op = Collections.unmodifiableMap(aVar.ov);
        this.oq = aVar.oq;
        this.or = searchAdRequest;
        this.os = aVar.os;
        this.ot = Collections.unmodifiableSet(aVar.ow);
    }

    public SearchAdRequest bd() {
        return this.or;
    }

    public Map<Class<? extends NetworkExtras>, NetworkExtras> be() {
        return this.op;
    }

    public Bundle bf() {
        return this.oo;
    }

    public int bg() {
        return this.os;
    }

    public Date getBirthday() {
        return this.d;
    }

    public String getContentUrl() {
        return this.ol;
    }

    public Bundle getCustomEventExtrasBundle(Class<? extends CustomEvent> adapterClass) {
        Bundle bundle = this.oo.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter");
        if (bundle != null) {
            return bundle.getBundle(adapterClass.getClass().getName());
        }
        return null;
    }

    public int getGender() {
        return this.om;
    }

    public Set<String> getKeywords() {
        return this.f;
    }

    public Location getLocation() {
        return this.h;
    }

    public boolean getManualImpressionsEnabled() {
        return this.on;
    }

    @Deprecated
    public <T extends NetworkExtras> T getNetworkExtras(Class<T> networkExtrasClass) {
        return (T) this.op.get(networkExtrasClass);
    }

    public Bundle getNetworkExtrasBundle(Class<? extends MediationAdapter> adapterClass) {
        return this.oo.getBundle(adapterClass.getName());
    }

    public String getPublisherProvidedId() {
        return this.oq;
    }

    public boolean isTestDevice(Context context) {
        return this.ot.contains(gr.v(context));
    }
}
