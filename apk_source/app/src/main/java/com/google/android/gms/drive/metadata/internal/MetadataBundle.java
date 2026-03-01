package com.google.android.gms.drive.metadata.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.internal.v;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.internal.kd;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public final class MetadataBundle implements SafeParcelable {
    public static final Parcelable.Creator<MetadataBundle> CREATOR = new h();
    final int BR;
    final Bundle PD;

    MetadataBundle(int versionCode, Bundle valueBundle) {
        this.BR = versionCode;
        this.PD = (Bundle) n.i(valueBundle);
        this.PD.setClassLoader(getClass().getClassLoader());
        ArrayList arrayList = new ArrayList();
        for (String str : this.PD.keySet()) {
            if (e.bj(str) == null) {
                arrayList.add(str);
                v.p("MetadataBundle", "Ignored unknown metadata field in bundle: " + str);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            this.PD.remove((String) it.next());
        }
    }

    private MetadataBundle(Bundle valueBundle) {
        this(1, valueBundle);
    }

    public static <T> MetadataBundle a(MetadataField<T> metadataField, T t) {
        MetadataBundle metadataBundleIo = io();
        metadataBundleIo.b(metadataField, t);
        return metadataBundleIo;
    }

    public static MetadataBundle a(MetadataBundle metadataBundle) {
        return new MetadataBundle(new Bundle(metadataBundle.PD));
    }

    public static MetadataBundle io() {
        return new MetadataBundle(new Bundle());
    }

    public <T> T a(MetadataField<T> metadataField) {
        return metadataField.f(this.PD);
    }

    public <T> void b(MetadataField<T> metadataField, T t) {
        if (e.bj(metadataField.getName()) == null) {
            throw new IllegalArgumentException("Unregistered field: " + metadataField.getName());
        }
        metadataField.a(t, this.PD);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MetadataBundle)) {
            return false;
        }
        MetadataBundle metadataBundle = (MetadataBundle) obj;
        Set<String> setKeySet = this.PD.keySet();
        if (!setKeySet.equals(metadataBundle.PD.keySet())) {
            return false;
        }
        for (String str : setKeySet) {
            if (!com.google.android.gms.common.internal.m.equal(this.PD.get(str), metadataBundle.PD.get(str))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int iHashCode = 1;
        Iterator<String> it = this.PD.keySet().iterator();
        while (true) {
            int i = iHashCode;
            if (!it.hasNext()) {
                return i;
            }
            iHashCode = this.PD.get(it.next()).hashCode() + (i * 31);
        }
    }

    public Set<MetadataField<?>> ip() {
        HashSet hashSet = new HashSet();
        Iterator<String> it = this.PD.keySet().iterator();
        while (it.hasNext()) {
            hashSet.add(e.bj(it.next()));
        }
        return hashSet;
    }

    public void setContext(Context context) {
        com.google.android.gms.common.data.a aVar = (com.google.android.gms.common.data.a) a(kd.Qd);
        if (aVar != null) {
            aVar.a(context.getCacheDir());
        }
    }

    public String toString() {
        return "MetadataBundle [values=" + this.PD + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        h.a(this, dest, flags);
    }
}
