package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.internal.ji;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public final class nv extends jj implements Moment {
    public static final nw CREATOR = new nw();
    private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
    String BL;
    final int BR;
    final Set<Integer> alR;
    String amE;
    nt amM;
    nt amN;
    String uO;

    static {
        alQ.put("id", ji.a.l("id", 2));
        alQ.put("result", ji.a.a("result", 4, nt.class));
        alQ.put("startDate", ji.a.l("startDate", 5));
        alQ.put("target", ji.a.a("target", 6, nt.class));
        alQ.put(PlaylistSQLiteHelper.COL_TYPE, ji.a.l(PlaylistSQLiteHelper.COL_TYPE, 7));
    }

    public nv() {
        this.BR = 1;
        this.alR = new HashSet();
    }

    nv(Set<Integer> set, int i, String str, nt ntVar, String str2, nt ntVar2, String str3) {
        this.alR = set;
        this.BR = i;
        this.BL = str;
        this.amM = ntVar;
        this.amE = str2;
        this.amN = ntVar2;
        this.uO = str3;
    }

    public nv(Set<Integer> set, String str, nt ntVar, String str2, nt ntVar2, String str3) {
        this.alR = set;
        this.BR = 1;
        this.BL = str;
        this.amM = ntVar;
        this.amE = str2;
        this.amN = ntVar2;
        this.uO = str3;
    }

    @Override // com.google.android.gms.internal.ji
    protected boolean a(ji.a aVar) {
        return this.alR.contains(Integer.valueOf(aVar.hm()));
    }

    @Override // com.google.android.gms.internal.ji
    protected Object b(ji.a aVar) {
        switch (aVar.hm()) {
            case 2:
                return this.BL;
            case 3:
            default:
                throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            case 4:
                return this.amM;
            case 5:
                return this.amE;
            case 6:
                return this.amN;
            case 7:
                return this.uO;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        nw nwVar = CREATOR;
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof nv)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        nv nvVar = (nv) obj;
        for (ji.a<?, ?> aVar : alQ.values()) {
            if (a(aVar)) {
                if (nvVar.a(aVar) && b(aVar).equals(nvVar.b(aVar))) {
                }
                return false;
            }
            if (nvVar.a(aVar)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public String getId() {
        return this.BL;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public ItemScope getResult() {
        return this.amM;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public String getStartDate() {
        return this.amE;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public ItemScope getTarget() {
        return this.amN;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public String getType() {
        return this.uO;
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public boolean hasId() {
        return this.alR.contains(2);
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public boolean hasResult() {
        return this.alR.contains(4);
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public boolean hasStartDate() {
        return this.alR.contains(5);
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public boolean hasTarget() {
        return this.alR.contains(6);
    }

    @Override // com.google.android.gms.plus.model.moments.Moment
    public boolean hasType() {
        return this.alR.contains(7);
    }

    public int hashCode() {
        int iHashCode = 0;
        Iterator<ji.a<?, ?>> it = alQ.values().iterator();
        while (true) {
            int i = iHashCode;
            if (!it.hasNext()) {
                return i;
            }
            ji.a<?, ?> next = it.next();
            if (a(next)) {
                iHashCode = b(next).hashCode() + i + next.hm();
            } else {
                iHashCode = i;
            }
        }
    }

    @Override // com.google.android.gms.internal.ji
    public HashMap<String, ji.a<?, ?>> hf() {
        return alQ;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: nq, reason: merged with bridge method [inline-methods] */
    public nv freeze() {
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        nw nwVar = CREATOR;
        nw.a(this, out, flags);
    }
}
