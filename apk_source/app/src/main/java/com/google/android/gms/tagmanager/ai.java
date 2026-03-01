package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.c;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ai {
    private static void a(DataLayer dataLayer, c.d dVar) throws InterruptedException {
        for (d.a aVar : dVar.fB) {
            dataLayer.cs(di.j(aVar));
        }
    }

    public static void a(DataLayer dataLayer, c.i iVar) throws InterruptedException {
        if (iVar.gq == null) {
            bh.W("supplemental missing experimentSupplemental");
            return;
        }
        a(dataLayer, iVar.gq);
        b(dataLayer, iVar.gq);
        c(dataLayer, iVar.gq);
    }

    private static void b(DataLayer dataLayer, c.d dVar) throws InterruptedException {
        for (d.a aVar : dVar.fA) {
            Map<String, Object> mapC = c(aVar);
            if (mapC != null) {
                dataLayer.push(mapC);
            }
        }
    }

    private static Map<String, Object> c(d.a aVar) {
        Object objO = di.o(aVar);
        if (objO instanceof Map) {
            return (Map) objO;
        }
        bh.W("value: " + objO + " is not a map value, ignored.");
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0063  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void c(com.google.android.gms.tagmanager.DataLayer r14, com.google.android.gms.internal.c.d r15) throws java.lang.InterruptedException {
        /*
            r3 = 0
            com.google.android.gms.internal.c$c[] r4 = r15.fC
            int r5 = r4.length
            r2 = r3
        L5:
            if (r2 >= r5) goto Lb9
            r6 = r4[r2]
            java.lang.String r0 = r6.fv
            if (r0 != 0) goto L16
            java.lang.String r0 = "GaExperimentRandom: No key"
            com.google.android.gms.tagmanager.bh.W(r0)
        L12:
            int r0 = r2 + 1
            r2 = r0
            goto L5
        L16:
            java.lang.String r0 = r6.fv
            java.lang.Object r1 = r14.get(r0)
            boolean r0 = r1 instanceof java.lang.Number
            if (r0 != 0) goto L88
            r0 = 0
        L21:
            long r8 = r6.fw
            long r10 = r6.fx
            boolean r7 = r6.fy
            if (r7 == 0) goto L3b
            if (r0 == 0) goto L3b
            long r12 = r0.longValue()
            int r7 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r7 < 0) goto L3b
            long r12 = r0.longValue()
            int r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r0 <= 0) goto L50
        L3b:
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 > 0) goto L94
            double r0 = java.lang.Math.random()
            long r10 = r10 - r8
            double r10 = (double) r10
            double r0 = r0 * r10
            double r8 = (double) r8
            double r0 = r0 + r8
            long r0 = java.lang.Math.round(r0)
            java.lang.Long r1 = java.lang.Long.valueOf(r0)
        L50:
            java.lang.String r0 = r6.fv
            r14.cs(r0)
            java.lang.String r0 = r6.fv
            java.util.Map r1 = r14.c(r0, r1)
            long r8 = r6.fz
            r10 = 0
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 <= 0) goto L84
            java.lang.String r0 = "gtm"
            boolean r0 = r1.containsKey(r0)
            if (r0 != 0) goto L9b
            java.lang.String r0 = "gtm"
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r8 = "lifetime"
            r7[r3] = r8
            r8 = 1
            long r10 = r6.fz
            java.lang.Long r6 = java.lang.Long.valueOf(r10)
            r7[r8] = r6
            java.util.Map r6 = com.google.android.gms.tagmanager.DataLayer.mapOf(r7)
            r1.put(r0, r6)
        L84:
            r14.push(r1)
            goto L12
        L88:
            r0 = r1
            java.lang.Number r0 = (java.lang.Number) r0
            long r8 = r0.longValue()
            java.lang.Long r0 = java.lang.Long.valueOf(r8)
            goto L21
        L94:
            java.lang.String r0 = "GaExperimentRandom: random range invalid"
            com.google.android.gms.tagmanager.bh.W(r0)
            goto L12
        L9b:
            java.lang.String r0 = "gtm"
            java.lang.Object r0 = r1.get(r0)
            boolean r7 = r0 instanceof java.util.Map
            if (r7 == 0) goto Lb3
            java.util.Map r0 = (java.util.Map) r0
            java.lang.String r7 = "lifetime"
            long r8 = r6.fz
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            r0.put(r7, r6)
            goto L84
        Lb3:
            java.lang.String r0 = "GaExperimentRandom: gtm not a map"
            com.google.android.gms.tagmanager.bh.W(r0)
            goto L84
        Lb9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.tagmanager.ai.c(com.google.android.gms.tagmanager.DataLayer, com.google.android.gms.internal.c$d):void");
    }
}
