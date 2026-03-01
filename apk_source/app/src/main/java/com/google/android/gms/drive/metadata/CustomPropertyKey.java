package com.google.android.gms.drive.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class CustomPropertyKey implements SafeParcelable {
    public static final Parcelable.Creator<CustomPropertyKey> CREATOR = new c();
    private static final Pattern Px = Pattern.compile("[\\w.!@$%^&*()/-]+");
    final int BR;
    final String JH;
    final int mVisibility;

    CustomPropertyKey(int versionCode, String key, int visibility) {
        boolean z = true;
        n.b(key, (Object) "key");
        n.b(Px.matcher(key).matches(), "key name characters must be alphanumeric or one of .!@$%^&*()-_/");
        if (visibility != 0 && visibility != 1) {
            z = false;
        }
        n.b(z, "visibility must be either PUBLIC or PRIVATE");
        this.BR = versionCode;
        this.JH = key;
        this.mVisibility = visibility;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomPropertyKey)) {
            return false;
        }
        CustomPropertyKey customPropertyKey = (CustomPropertyKey) obj;
        return customPropertyKey.getKey().equals(this.JH) && customPropertyKey.getVisibility() == this.mVisibility;
    }

    public String getKey() {
        return this.JH;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public int hashCode() {
        return (this.JH + this.mVisibility).hashCode();
    }

    public String toString() {
        return "CustomPropertyKey(" + this.JH + ClientInfo.SEPARATOR_BETWEEN_VARS + this.mVisibility + ")";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        c.a(this, dest, flags);
    }
}
