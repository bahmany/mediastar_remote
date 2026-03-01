package com.google.android.gms.internal;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

@ez
/* loaded from: classes.dex */
public final class fi implements SafeParcelable {
    public static final fj CREATOR = new fj();
    public final ApplicationInfo applicationInfo;
    public final String lA;
    public final gt lD;
    public final ay lH;
    public final List<String> lS;
    public final String tA;
    public final String tB;
    public final Bundle tC;
    public final int tD;
    public final Bundle tE;
    public final boolean tF;
    public final Bundle tw;
    public final av tx;
    public final PackageInfo ty;
    public final String tz;
    public final int versionCode;

    @ez
    public static final class a {
        public final ApplicationInfo applicationInfo;
        public final String lA;
        public final gt lD;
        public final ay lH;
        public final List<String> lS;
        public final String tA;
        public final String tB;
        public final Bundle tC;
        public final int tD;
        public final Bundle tE;
        public final boolean tF;
        public final Bundle tw;
        public final av tx;
        public final PackageInfo ty;

        public a(Bundle bundle, av avVar, ay ayVar, String str, ApplicationInfo applicationInfo, PackageInfo packageInfo, String str2, String str3, gt gtVar, Bundle bundle2, List<String> list, Bundle bundle3, boolean z) {
            this.tw = bundle;
            this.tx = avVar;
            this.lH = ayVar;
            this.lA = str;
            this.applicationInfo = applicationInfo;
            this.ty = packageInfo;
            this.tA = str2;
            this.tB = str3;
            this.lD = gtVar;
            this.tC = bundle2;
            this.tF = z;
            if (list == null || list.size() <= 0) {
                this.tD = 0;
                this.lS = null;
            } else {
                this.tD = 2;
                this.lS = list;
            }
            this.tE = bundle3;
        }
    }

    fi(int i, Bundle bundle, av avVar, ay ayVar, String str, ApplicationInfo applicationInfo, PackageInfo packageInfo, String str2, String str3, String str4, gt gtVar, Bundle bundle2, int i2, List<String> list, Bundle bundle3, boolean z) {
        this.versionCode = i;
        this.tw = bundle;
        this.tx = avVar;
        this.lH = ayVar;
        this.lA = str;
        this.applicationInfo = applicationInfo;
        this.ty = packageInfo;
        this.tz = str2;
        this.tA = str3;
        this.tB = str4;
        this.lD = gtVar;
        this.tC = bundle2;
        this.tD = i2;
        this.lS = list;
        this.tE = bundle3;
        this.tF = z;
    }

    public fi(Bundle bundle, av avVar, ay ayVar, String str, ApplicationInfo applicationInfo, PackageInfo packageInfo, String str2, String str3, String str4, gt gtVar, Bundle bundle2, int i, List<String> list, Bundle bundle3, boolean z) {
        this(4, bundle, avVar, ayVar, str, applicationInfo, packageInfo, str2, str3, str4, gtVar, bundle2, i, list, bundle3, z);
    }

    public fi(a aVar, String str) {
        this(aVar.tw, aVar.tx, aVar.lH, aVar.lA, aVar.applicationInfo, aVar.ty, str, aVar.tA, aVar.tB, aVar.lD, aVar.tC, aVar.tD, aVar.lS, aVar.tE, aVar.tF);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        fj.a(this, out, flags);
    }
}
