package com.google.android.gms.drive.metadata.internal;

import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.internal.kd;
import com.google.android.gms.internal.kf;
import com.google.android.gms.internal.kh;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class e {
    private static Map<String, MetadataField<?>> PC = new HashMap();

    static {
        b(kd.PE);
        b(kd.Qe);
        b(kd.PV);
        b(kd.Qc);
        b(kd.Qf);
        b(kd.PP);
        b(kd.PQ);
        b(kd.PN);
        b(kd.PS);
        b(kd.Qa);
        b(kd.PF);
        b(kd.PX);
        b(kd.PH);
        b(kd.PO);
        b(kd.PI);
        b(kd.PJ);
        b(kd.PK);
        b(kd.PU);
        b(kd.PR);
        b(kd.PW);
        b(kd.PY);
        b(kd.PZ);
        b(kd.Qb);
        b(kd.Qg);
        b(kd.Qh);
        b(kd.PM);
        b(kd.PL);
        b(kd.Qd);
        b(kd.PT);
        b(kd.PG);
        b(kd.Qi);
        b(kd.Qj);
        b(kd.Qk);
        b(kf.Ql);
        b(kf.Qn);
        b(kf.Qo);
        b(kf.Qp);
        b(kf.Qm);
        b(kh.Qr);
        b(kh.Qs);
    }

    private static void b(MetadataField<?> metadataField) {
        if (PC.containsKey(metadataField.getName())) {
            throw new IllegalArgumentException("Duplicate field name registered: " + metadataField.getName());
        }
        PC.put(metadataField.getName(), metadataField);
    }

    public static MetadataField<?> bj(String str) {
        return PC.get(str);
    }

    public static Collection<MetadataField<?>> in() {
        return Collections.unmodifiableCollection(PC.values());
    }
}
