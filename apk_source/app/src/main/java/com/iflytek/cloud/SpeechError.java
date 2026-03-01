package com.iflytek.cloud;

import com.iflytek.cloud.resource.Resource;

/* loaded from: classes.dex */
public class SpeechError extends Exception {
    private static final long serialVersionUID = 4434424251478985596L;
    private int a;
    private String b;

    /* JADX WARN: Removed duplicated region for block: B:96:0x014f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public SpeechError(int r7) {
        /*
            Method dump skipped, instructions count: 338
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.SpeechError.<init>(int):void");
    }

    public SpeechError(int i, String str) {
        this.a = 0;
        this.b = "";
        this.a = i;
        this.b = str;
    }

    public SpeechError(Exception exc) {
        this.a = 0;
        this.b = "";
        this.a = ErrorCode.ERROR_UNKNOWN;
        this.b = exc.toString();
    }

    public int getErrorCode() {
        return this.a;
    }

    public String getErrorDescription() {
        return this.b;
    }

    public String getHtmlDescription(boolean z) {
        String str = this.b + "...";
        if (!z) {
            return str;
        }
        return ((str + "<br>(") + Resource.getErrorTag(0) + ":") + this.a + ")";
    }

    public String getPlainDescription(boolean z) {
        String str = this.b;
        if (!z) {
            return str;
        }
        return ((str + ".") + "(" + Resource.getErrorTag(0) + ":") + this.a + ")";
    }

    @Override // java.lang.Throwable
    public String toString() {
        return getPlainDescription(true);
    }
}
