package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class s extends aj {
    private final a aou;
    private static final String ID = com.google.android.gms.internal.a.FUNCTION_CALL.toString();
    private static final String aot = com.google.android.gms.internal.b.FUNCTION_CALL_NAME.toString();
    private static final String anK = com.google.android.gms.internal.b.ADDITIONAL_PARAMS.toString();

    public interface a {
        Object b(String str, Map<String, Object> map);
    }

    public s(a aVar) {
        super(ID, aot);
        this.aou = aVar;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String strJ = di.j(map.get(aot));
        HashMap map2 = new HashMap();
        d.a aVar = map.get(anK);
        if (aVar != null) {
            Object objO = di.o(aVar);
            if (!(objO instanceof Map)) {
                bh.W("FunctionCallMacro: expected ADDITIONAL_PARAMS to be a map.");
                return di.pI();
            }
            for (Map.Entry entry : ((Map) objO).entrySet()) {
                map2.put(entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            return di.u(this.aou.b(strJ, map2));
        } catch (Exception e) {
            bh.W("Custom macro/tag " + strJ + " threw exception " + e.getMessage());
            return di.pI();
        }
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
