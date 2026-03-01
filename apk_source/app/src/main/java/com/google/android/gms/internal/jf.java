package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ji;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class jf implements SafeParcelable, ji.b<String, Integer> {
    public static final jg CREATOR = new jg();
    private final int BR;
    private final HashMap<String, Integer> Ml;
    private final HashMap<Integer, String> Mm;
    private final ArrayList<a> Mn;

    public static final class a implements SafeParcelable {
        public static final jh CREATOR = new jh();
        final String Mo;
        final int Mp;
        final int versionCode;

        a(int i, String str, int i2) {
            this.versionCode = i;
            this.Mo = str;
            this.Mp = i2;
        }

        a(String str, int i) {
            this.versionCode = 1;
            this.Mo = str;
            this.Mp = i;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            jh jhVar = CREATOR;
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            jh jhVar = CREATOR;
            jh.a(this, out, flags);
        }
    }

    public jf() {
        this.BR = 1;
        this.Ml = new HashMap<>();
        this.Mm = new HashMap<>();
        this.Mn = null;
    }

    jf(int i, ArrayList<a> arrayList) {
        this.BR = i;
        this.Ml = new HashMap<>();
        this.Mm = new HashMap<>();
        this.Mn = null;
        b(arrayList);
    }

    private void b(ArrayList<a> arrayList) {
        Iterator<a> it = arrayList.iterator();
        while (it.hasNext()) {
            a next = it.next();
            h(next.Mo, next.Mp);
        }
    }

    @Override // com.google.android.gms.internal.ji.b
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public String convertBack(Integer num) {
        String str = this.Mm.get(num);
        return (str == null && this.Ml.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        jg jgVar = CREATOR;
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    public jf h(String str, int i) {
        this.Ml.put(str, Integer.valueOf(i));
        this.Mm.put(Integer.valueOf(i), str);
        return this;
    }

    ArrayList<a> hc() {
        ArrayList<a> arrayList = new ArrayList<>();
        for (String str : this.Ml.keySet()) {
            arrayList.add(new a(str, this.Ml.get(str).intValue()));
        }
        return arrayList;
    }

    @Override // com.google.android.gms.internal.ji.b
    public int hd() {
        return 7;
    }

    @Override // com.google.android.gms.internal.ji.b
    public int he() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        jg jgVar = CREATOR;
        jg.a(this, out, flags);
    }
}
