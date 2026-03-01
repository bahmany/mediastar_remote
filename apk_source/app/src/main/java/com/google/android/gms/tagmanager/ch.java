package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: classes.dex */
class ch extends aj {
    private static final String ID = com.google.android.gms.internal.a.REGEX_GROUP.toString();
    private static final String aqc = com.google.android.gms.internal.b.ARG0.toString();
    private static final String aqd = com.google.android.gms.internal.b.ARG1.toString();
    private static final String aqe = com.google.android.gms.internal.b.IGNORE_CASE.toString();
    private static final String aqf = com.google.android.gms.internal.b.GROUP.toString();

    public ch() {
        super(ID, aqc, aqd);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        int iIntValue;
        d.a aVar = map.get(aqc);
        d.a aVar2 = map.get(aqd);
        if (aVar == null || aVar == di.pI() || aVar2 == null || aVar2 == di.pI()) {
            return di.pI();
        }
        int i = di.n(map.get(aqe)).booleanValue() ? 66 : 64;
        d.a aVar3 = map.get(aqf);
        if (aVar3 != null) {
            Long l = di.l(aVar3);
            if (l == di.pD()) {
                return di.pI();
            }
            iIntValue = l.intValue();
            if (iIntValue < 0) {
                return di.pI();
            }
        } else {
            iIntValue = 1;
        }
        try {
            String strJ = di.j(aVar);
            String strGroup = null;
            Matcher matcher = Pattern.compile(di.j(aVar2), i).matcher(strJ);
            if (matcher.find() && matcher.groupCount() >= iIntValue) {
                strGroup = matcher.group(iIntValue);
            }
            return strGroup == null ? di.pI() : di.u(strGroup);
        } catch (PatternSyntaxException e) {
            return di.pI();
        }
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
