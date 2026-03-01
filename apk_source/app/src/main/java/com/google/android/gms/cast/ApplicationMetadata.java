package com.google.android.gms.cast;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.images.WebImage;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ik;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class ApplicationMetadata implements SafeParcelable {
    public static final Parcelable.Creator<ApplicationMetadata> CREATOR = new a();
    private final int BR;
    List<WebImage> EA;
    List<String> EB;
    String EC;
    Uri ED;
    String Ez;
    String mName;

    private ApplicationMetadata() {
        this.BR = 1;
        this.EA = new ArrayList();
        this.EB = new ArrayList();
    }

    ApplicationMetadata(int versionCode, String applicationId, String name, List<WebImage> images, List<String> namespaces, String senderAppIdentifier, Uri senderAppLaunchUrl) {
        this.BR = versionCode;
        this.Ez = applicationId;
        this.mName = name;
        this.EA = images;
        this.EB = namespaces;
        this.EC = senderAppIdentifier;
        this.ED = senderAppLaunchUrl;
    }

    public boolean areNamespacesSupported(List<String> namespaces) {
        return this.EB != null && this.EB.containsAll(namespaces);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ApplicationMetadata)) {
            return false;
        }
        ApplicationMetadata applicationMetadata = (ApplicationMetadata) obj;
        return ik.a(this.Ez, applicationMetadata.Ez) && ik.a(this.EA, applicationMetadata.EA) && ik.a(this.mName, applicationMetadata.mName) && ik.a(this.EB, applicationMetadata.EB) && ik.a(this.EC, applicationMetadata.EC) && ik.a(this.ED, applicationMetadata.ED);
    }

    public Uri fv() {
        return this.ED;
    }

    public String getApplicationId() {
        return this.Ez;
    }

    public List<WebImage> getImages() {
        return this.EA;
    }

    public String getName() {
        return this.mName;
    }

    public String getSenderAppIdentifier() {
        return this.EC;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.BR), this.Ez, this.mName, this.EA, this.EB, this.EC, this.ED);
    }

    public boolean isNamespaceSupported(String namespace) {
        return this.EB != null && this.EB.contains(namespace);
    }

    public String toString() {
        return this.mName;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        a.a(this, out, flags);
    }
}
