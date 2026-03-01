package com.google.android.gms.drive.metadata.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class AppVisibleCustomProperties implements SafeParcelable, Iterable<CustomProperty> {
    public static final Parcelable.Creator<AppVisibleCustomProperties> CREATOR = new com.google.android.gms.drive.metadata.internal.a();
    public static final AppVisibleCustomProperties Py = new a().im();
    final int BR;
    final List<CustomProperty> Pz;

    public static class a {
        private final Map<CustomPropertyKey, CustomProperty> PA = new HashMap();

        public AppVisibleCustomProperties im() {
            return new AppVisibleCustomProperties(this.PA.values());
        }
    }

    AppVisibleCustomProperties(int versionCode, Collection<CustomProperty> properties) {
        this.BR = versionCode;
        n.i(properties);
        this.Pz = new ArrayList(properties);
    }

    private AppVisibleCustomProperties(Collection<CustomProperty> properties) {
        this(1, properties);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // java.lang.Iterable
    public Iterator<CustomProperty> iterator() {
        return this.Pz.iterator();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        com.google.android.gms.drive.metadata.internal.a.a(this, dest, flags);
    }
}
