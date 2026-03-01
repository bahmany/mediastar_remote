package com.google.android.gms.internal;

import android.location.Location;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import java.util.Date;
import java.util.Set;

@ez
/* loaded from: classes.dex */
public final class cw implements MediationAdRequest {
    private final Date d;
    private final Set<String> f;
    private final boolean g;
    private final Location h;
    private final int om;
    private final int qD;

    public cw(Date date, int i, Set<String> set, Location location, boolean z, int i2) {
        this.d = date;
        this.om = i;
        this.f = set;
        this.h = location;
        this.g = z;
        this.qD = i2;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public Date getBirthday() {
        return this.d;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public int getGender() {
        return this.om;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public Set<String> getKeywords() {
        return this.f;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public Location getLocation() {
        return this.h;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public boolean isTesting() {
        return this.g;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public int taggedForChildDirectedTreatment() {
        return this.qD;
    }
}
