package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
class bc extends aj {
    private static final String ID = com.google.android.gms.internal.a.LANGUAGE.toString();

    public bc() {
        super(ID, new String[0]);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String language;
        Locale locale = Locale.getDefault();
        if (locale != null && (language = locale.getLanguage()) != null) {
            return di.u(language.toLowerCase());
        }
        return di.pI();
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
