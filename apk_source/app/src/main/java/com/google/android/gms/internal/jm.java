package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ji;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes.dex */
public class jm implements SafeParcelable {
    public static final jn CREATOR = new jn();
    private final int BR;
    private final HashMap<String, HashMap<String, ji.a<?, ?>>> MA;
    private final ArrayList<a> MB;
    private final String MC;

    public static class a implements SafeParcelable {
        public static final jo CREATOR = new jo();
        final ArrayList<b> MD;
        final String className;
        final int versionCode;

        a(int i, String str, ArrayList<b> arrayList) {
            this.versionCode = i;
            this.className = str;
            this.MD = arrayList;
        }

        a(String str, HashMap<String, ji.a<?, ?>> map) {
            this.versionCode = 1;
            this.className = str;
            this.MD = a(map);
        }

        private static ArrayList<b> a(HashMap<String, ji.a<?, ?>> map) {
            if (map == null) {
                return null;
            }
            ArrayList<b> arrayList = new ArrayList<>();
            for (String str : map.keySet()) {
                arrayList.add(new b(str, map.get(str)));
            }
            return arrayList;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            jo joVar = CREATOR;
            return 0;
        }

        HashMap<String, ji.a<?, ?>> hw() {
            HashMap<String, ji.a<?, ?>> map = new HashMap<>();
            int size = this.MD.size();
            for (int i = 0; i < size; i++) {
                b bVar = this.MD.get(i);
                map.put(bVar.fv, bVar.ME);
            }
            return map;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            jo joVar = CREATOR;
            jo.a(this, out, flags);
        }
    }

    public static class b implements SafeParcelable {
        public static final jl CREATOR = new jl();
        final ji.a<?, ?> ME;
        final String fv;
        final int versionCode;

        b(int i, String str, ji.a<?, ?> aVar) {
            this.versionCode = i;
            this.fv = str;
            this.ME = aVar;
        }

        b(String str, ji.a<?, ?> aVar) {
            this.versionCode = 1;
            this.fv = str;
            this.ME = aVar;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            jl jlVar = CREATOR;
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            jl jlVar = CREATOR;
            jl.a(this, out, flags);
        }
    }

    jm(int i, ArrayList<a> arrayList, String str) {
        this.BR = i;
        this.MB = null;
        this.MA = c(arrayList);
        this.MC = (String) com.google.android.gms.common.internal.n.i(str);
        hs();
    }

    public jm(Class<? extends ji> cls) {
        this.BR = 1;
        this.MB = null;
        this.MA = new HashMap<>();
        this.MC = cls.getCanonicalName();
    }

    private static HashMap<String, HashMap<String, ji.a<?, ?>>> c(ArrayList<a> arrayList) {
        HashMap<String, HashMap<String, ji.a<?, ?>>> map = new HashMap<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            a aVar = arrayList.get(i);
            map.put(aVar.className, aVar.hw());
        }
        return map;
    }

    public void a(Class<? extends ji> cls, HashMap<String, ji.a<?, ?>> map) {
        this.MA.put(cls.getCanonicalName(), map);
    }

    public boolean b(Class<? extends ji> cls) {
        return this.MA.containsKey(cls.getCanonicalName());
    }

    public HashMap<String, ji.a<?, ?>> be(String str) {
        return this.MA.get(str);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        jn jnVar = CREATOR;
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    public void hs() {
        Iterator<String> it = this.MA.keySet().iterator();
        while (it.hasNext()) {
            HashMap<String, ji.a<?, ?>> map = this.MA.get(it.next());
            Iterator<String> it2 = map.keySet().iterator();
            while (it2.hasNext()) {
                map.get(it2.next()).a(this);
            }
        }
    }

    public void ht() {
        for (String str : this.MA.keySet()) {
            HashMap<String, ji.a<?, ?>> map = this.MA.get(str);
            HashMap<String, ji.a<?, ?>> map2 = new HashMap<>();
            for (String str2 : map.keySet()) {
                map2.put(str2, map.get(str2).hi());
            }
            this.MA.put(str, map2);
        }
    }

    ArrayList<a> hu() {
        ArrayList<a> arrayList = new ArrayList<>();
        for (String str : this.MA.keySet()) {
            arrayList.add(new a(str, this.MA.get(str)));
        }
        return arrayList;
    }

    public String hv() {
        return this.MC;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String str : this.MA.keySet()) {
            sb.append(str).append(":\n");
            HashMap<String, ji.a<?, ?>> map = this.MA.get(str);
            for (String str2 : map.keySet()) {
                sb.append("  ").append(str2).append(": ");
                sb.append(map.get(str2));
            }
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        jn jnVar = CREATOR;
        jn.a(this, out, flags);
    }
}
