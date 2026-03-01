package org.cybergarage.xml;

/* loaded from: classes.dex */
public class XML {
    public static final String CHARSET_UTF8 = "utf-8";
    public static final String CONTENT_TYPE = "text/xml; charset=\"utf-8\"";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static final java.lang.String escapeXMLChars(java.lang.String r7, boolean r8) {
        /*
            r6 = 0
            if (r7 != 0) goto L5
            r7 = 0
        L4:
            return r7
        L5:
            java.lang.StringBuffer r4 = new java.lang.StringBuffer
            r4.<init>()
            int r3 = r7.length()
            char[] r2 = new char[r3]
            r7.getChars(r6, r3, r2, r6)
            r5 = 0
            r0 = 0
            r1 = 0
        L16:
            if (r1 < r3) goto L24
            if (r5 == 0) goto L4
            int r6 = r3 - r5
            r4.append(r2, r5, r6)
            java.lang.String r7 = r4.toString()
            goto L4
        L24:
            char r6 = r2[r1]
            switch(r6) {
                case 34: goto L47;
                case 38: goto L39;
                case 39: goto L42;
                case 60: goto L3c;
                case 62: goto L3f;
                default: goto L29;
            }
        L29:
            if (r0 == 0) goto L36
            int r6 = r1 - r5
            r4.append(r2, r5, r6)
            r4.append(r0)
            int r5 = r1 + 1
            r0 = 0
        L36:
            int r1 = r1 + 1
            goto L16
        L39:
            java.lang.String r0 = "&amp;"
            goto L29
        L3c:
            java.lang.String r0 = "&lt;"
            goto L29
        L3f:
            java.lang.String r0 = "&gt;"
            goto L29
        L42:
            if (r8 == 0) goto L47
            java.lang.String r0 = "&apos;"
            goto L29
        L47:
            if (r8 == 0) goto L29
            java.lang.String r0 = "&quot;"
            goto L29
        */
        throw new UnsupportedOperationException("Method not decompiled: org.cybergarage.xml.XML.escapeXMLChars(java.lang.String, boolean):java.lang.String");
    }

    public static final String escapeXMLChars(String input) {
        return escapeXMLChars(input, true);
    }

    public static final String unescapeXMLChars(String input) {
        if (input == null) {
            return null;
        }
        String outStr = input.replace("&amp;", "&");
        return outStr.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'").replace("&quot;", "\"");
    }
}
