package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: classes.dex */
class ci extends dd {
    private static final String ID = com.google.android.gms.internal.a.REGEX.toString();
    private static final String aqe = com.google.android.gms.internal.b.IGNORE_CASE.toString();

    public ci() {
        super(ID);
    }

    @Override // com.google.android.gms.tagmanager.dd
    protected boolean a(String str, String str2, Map<String, d.a> map) {
        try {
            return Pattern.compile(str2, di.n(map.get(aqe)).booleanValue() ? 66 : 64).matcher(str).find();
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
