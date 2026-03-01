package com.google.android.gms.fitness;

import com.google.android.gms.common.api.Scope;
import java.util.HashSet;
import java.util.List;

/* loaded from: classes.dex */
public final class FitnessScopes {
    public static final Scope SCOPE_ACTIVITY_READ = new Scope("https://www.googleapis.com/auth/fitness.activity.read");
    public static final Scope SCOPE_ACTIVITY_READ_WRITE = new Scope("https://www.googleapis.com/auth/fitness.activity.write");
    public static final Scope SCOPE_LOCATION_READ = new Scope("https://www.googleapis.com/auth/fitness.location.read");
    public static final Scope SCOPE_LOCATION_READ_WRITE = new Scope("https://www.googleapis.com/auth/fitness.location.write");
    public static final Scope SCOPE_BODY_READ = new Scope("https://www.googleapis.com/auth/fitness.body.read");
    public static final Scope SCOPE_BODY_READ_WRITE = new Scope("https://www.googleapis.com/auth/fitness.body.write");

    private FitnessScopes() {
    }

    public static String bp(String str) {
        return str.equals("https://www.googleapis.com/auth/fitness.activity.read") ? "https://www.googleapis.com/auth/fitness.activity.write" : str.equals("https://www.googleapis.com/auth/fitness.location.read") ? "https://www.googleapis.com/auth/fitness.location.write" : str.equals("https://www.googleapis.com/auth/fitness.body.read") ? "https://www.googleapis.com/auth/fitness.body.write" : str;
    }

    public static String[] d(List<String> list) {
        HashSet hashSet = new HashSet(list.size());
        for (String str : list) {
            String strBp = bp(str);
            if (strBp.equals(str) || !list.contains(strBp)) {
                hashSet.add(str);
            }
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }
}
