package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class az extends aj {
    private static final String ID = com.google.android.gms.internal.a.JOINER.toString();
    private static final String aoU = com.google.android.gms.internal.b.ARG0.toString();
    private static final String app = com.google.android.gms.internal.b.ITEM_SEPARATOR.toString();
    private static final String apq = com.google.android.gms.internal.b.KEY_VALUE_SEPARATOR.toString();
    private static final String apr = com.google.android.gms.internal.b.ESCAPE.toString();

    private enum a {
        NONE,
        URL,
        BACKSLASH
    }

    public az() {
        super(ID, aoU);
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0011, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0012, code lost:
    
        com.google.android.gms.tagmanager.bh.b("Joiner: unsupported encoding", r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String a(java.lang.String r6, com.google.android.gms.tagmanager.az.a r7, java.util.Set<java.lang.Character> r8) {
        /*
            r5 = this;
            int[] r0 = com.google.android.gms.tagmanager.az.AnonymousClass1.aps
            int r1 = r7.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto Lc;
                case 2: goto L18;
                default: goto Lb;
            }
        Lb:
            return r6
        Lc:
            java.lang.String r6 = com.google.android.gms.tagmanager.dm.db(r6)     // Catch: java.io.UnsupportedEncodingException -> L11
            goto Lb
        L11:
            r0 = move-exception
            java.lang.String r1 = "Joiner: unsupported encoding"
            com.google.android.gms.tagmanager.bh.b(r1, r0)
            goto Lb
        L18:
            java.lang.String r0 = "\\"
            java.lang.String r1 = "\\\\"
            java.lang.String r0 = r6.replace(r0, r1)
            java.util.Iterator r2 = r8.iterator()
            r1 = r0
        L25:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L4e
            java.lang.Object r0 = r2.next()
            java.lang.Character r0 = (java.lang.Character) r0
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "\\"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r3 = r3.append(r0)
            java.lang.String r3 = r3.toString()
            java.lang.String r0 = r1.replace(r0, r3)
            r1 = r0
            goto L25
        L4e:
            r6 = r1
            goto Lb
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.tagmanager.az.a(java.lang.String, com.google.android.gms.tagmanager.az$a, java.util.Set):java.lang.String");
    }

    private void a(StringBuilder sb, String str, a aVar, Set<Character> set) {
        sb.append(a(str, aVar, set));
    }

    private void a(Set<Character> set, String str) {
        for (int i = 0; i < str.length(); i++) {
            set.add(Character.valueOf(str.charAt(i)));
        }
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        HashSet hashSet;
        a aVar;
        d.a aVar2 = map.get(aoU);
        if (aVar2 == null) {
            return di.pI();
        }
        d.a aVar3 = map.get(app);
        String strJ = aVar3 != null ? di.j(aVar3) : "";
        d.a aVar4 = map.get(apq);
        String strJ2 = aVar4 != null ? di.j(aVar4) : "=";
        a aVar5 = a.NONE;
        d.a aVar6 = map.get(apr);
        if (aVar6 != null) {
            String strJ3 = di.j(aVar6);
            if ("url".equals(strJ3)) {
                aVar = a.URL;
                hashSet = null;
            } else {
                if (!"backslash".equals(strJ3)) {
                    bh.T("Joiner: unsupported escape type: " + strJ3);
                    return di.pI();
                }
                aVar = a.BACKSLASH;
                hashSet = new HashSet();
                a(hashSet, strJ);
                a(hashSet, strJ2);
                hashSet.remove('\\');
            }
        } else {
            hashSet = null;
            aVar = aVar5;
        }
        StringBuilder sb = new StringBuilder();
        switch (aVar2.type) {
            case 2:
                boolean z = true;
                d.a[] aVarArr = aVar2.gw;
                int length = aVarArr.length;
                int i = 0;
                while (i < length) {
                    d.a aVar7 = aVarArr[i];
                    if (!z) {
                        sb.append(strJ);
                    }
                    a(sb, di.j(aVar7), aVar, hashSet);
                    i++;
                    z = false;
                }
                break;
            case 3:
                for (int i2 = 0; i2 < aVar2.gx.length; i2++) {
                    if (i2 > 0) {
                        sb.append(strJ);
                    }
                    String strJ4 = di.j(aVar2.gx[i2]);
                    String strJ5 = di.j(aVar2.gy[i2]);
                    a(sb, strJ4, aVar, hashSet);
                    sb.append(strJ2);
                    a(sb, strJ5, aVar, hashSet);
                }
                break;
            default:
                a(sb, di.j(aVar2), aVar, hashSet);
                break;
        }
        return di.u(sb.toString());
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
