package org.cybergarage.util;

/* loaded from: classes.dex */
public final class StringUtil {
    public static final boolean hasData(String value) {
        return value != null && value.length() > 0;
    }

    public static final int toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0;
        }
    }

    public static final long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0L;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:?, code lost:
    
        return -1;
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x001c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final int findOf(java.lang.String r7, java.lang.String r8, int r9, int r10, int r11, boolean r12) {
        /*
            r6 = -1
            if (r11 != 0) goto L5
            r2 = r6
        L4:
            return r2
        L5:
            int r0 = r8.length()
            r2 = r9
        La:
            if (r11 <= 0) goto L10
            if (r10 >= r2) goto L12
        Le:
            r2 = r6
            goto L4
        L10:
            if (r2 < r10) goto Le
        L12:
            char r5 = r7.charAt(r2)
            r4 = 0
            r3 = 0
        L18:
            if (r3 < r0) goto L1c
            int r2 = r2 + r11
            goto La
        L1c:
            char r1 = r8.charAt(r3)
            if (r12 == 0) goto L27
            if (r5 == r1) goto L4
        L24:
            int r3 = r3 + 1
            goto L18
        L27:
            if (r5 == r1) goto L2b
            int r4 = r4 + 1
        L2b:
            if (r4 != r0) goto L24
            goto L4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.cybergarage.util.StringUtil.findOf(java.lang.String, java.lang.String, int, int, int, boolean):int");
    }

    public static final int findFirstOf(String str, String chars) {
        return findOf(str, chars, 0, str.length() - 1, 1, true);
    }

    public static final int findFirstNotOf(String str, String chars) {
        return findOf(str, chars, 0, str.length() - 1, 1, false);
    }

    public static final int findLastOf(String str, String chars) {
        return findOf(str, chars, str.length() - 1, 0, -1, true);
    }

    public static final int findLastNotOf(String str, String chars) {
        return findOf(str, chars, str.length() - 1, 0, -1, false);
    }

    public static final String trim(String trimStr, String trimChars) {
        int spIdx = findFirstNotOf(trimStr, trimChars);
        if (spIdx < 0) {
            return trimStr;
        }
        String trimStr2 = trimStr.substring(spIdx, trimStr.length());
        int spIdx2 = findLastNotOf(trimStr2, trimChars);
        if (spIdx2 < 0) {
            return trimStr2;
        }
        String buf = trimStr2.substring(0, spIdx2 + 1);
        return buf;
    }
}
