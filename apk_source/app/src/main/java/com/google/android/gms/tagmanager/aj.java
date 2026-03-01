package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
abstract class aj {
    private final Set<String> aoY;
    private final String aoZ;

    public aj(String str, String... strArr) {
        this.aoZ = str;
        this.aoY = new HashSet(strArr.length);
        for (String str2 : strArr) {
            this.aoY.add(str2);
        }
    }

    public abstract d.a C(Map<String, d.a> map);

    boolean a(Set<String> set) {
        return set.containsAll(this.aoY);
    }

    public abstract boolean nL();

    public String op() {
        return this.aoZ;
    }

    public Set<String> oq() {
        return this.aoY;
    }
}
