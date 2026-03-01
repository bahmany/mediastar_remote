package com.sun.mail.iap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ResponseInputStream {
    private static final int incrementSlop = 16;
    private static final int maxIncrement = 262144;
    private static final int minIncrement = 256;
    private BufferedInputStream bin;

    public ResponseInputStream(InputStream in) {
        this.bin = new BufferedInputStream(in, 2048);
    }

    public ByteArray readResponse() throws IOException {
        return readResponse(null);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0024 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.sun.mail.iap.ByteArray readResponse(com.sun.mail.iap.ByteArray r15) throws java.io.IOException {
        /*
            r14 = this;
            if (r15 != 0) goto Le
            com.sun.mail.iap.ByteArray r15 = new com.sun.mail.iap.ByteArray
            r11 = 128(0x80, float:1.8E-43)
            byte[] r11 = new byte[r11]
            r12 = 0
            r13 = 128(0x80, float:1.8E-43)
            r15.<init>(r11, r12, r13)
        Le:
            byte[] r3 = r15.getBytes()
            r8 = 0
        L13:
            r2 = 0
            r6 = 0
            r9 = r8
        L16:
            if (r6 != 0) goto L21
            java.io.BufferedInputStream r11 = r14.bin
            int r2 = r11.read()
            r11 = -1
            if (r2 != r11) goto L2a
        L21:
            r11 = -1
            if (r2 != r11) goto L51
            java.io.IOException r11 = new java.io.IOException
            r11.<init>()
            throw r11
        L2a:
            switch(r2) {
                case 10: goto L45;
                default: goto L2d;
            }
        L2d:
            int r11 = r3.length
            if (r9 < r11) goto L3e
            int r10 = r3.length
            r11 = 262144(0x40000, float:3.67342E-40)
            if (r10 <= r11) goto L37
            r10 = 262144(0x40000, float:3.67342E-40)
        L37:
            r15.grow(r10)
            byte[] r3 = r15.getBytes()
        L3e:
            int r8 = r9 + 1
            byte r11 = (byte) r2
            r3[r9] = r11
            r9 = r8
            goto L16
        L45:
            if (r9 <= 0) goto L2d
            int r11 = r9 + (-1)
            r11 = r3[r11]
            r12 = 13
            if (r11 != r12) goto L2d
            r6 = 1
            goto L2d
        L51:
            r11 = 5
            if (r9 < r11) goto L5c
            int r11 = r9 + (-3)
            r11 = r3[r11]
            r12 = 125(0x7d, float:1.75E-43)
            if (r11 == r12) goto L60
        L5c:
            r15.setCount(r9)
            return r15
        L60:
            int r7 = r9 + (-4)
        L62:
            if (r7 >= 0) goto L94
        L64:
            if (r7 < 0) goto L5c
            r4 = 0
            int r11 = r7 + 1
            int r12 = r9 + (-3)
            int r4 = com.sun.mail.util.ASCIIUtility.parseInt(r3, r11, r12)     // Catch: java.lang.NumberFormatException -> L9d
            if (r4 <= 0) goto La5
            int r11 = r3.length
            int r1 = r11 - r9
            int r11 = r4 + 16
            if (r11 <= r1) goto La3
            r11 = 256(0x100, float:3.59E-43)
            int r12 = r4 + 16
            int r12 = r12 - r1
            if (r11 <= r12) goto L9f
            r11 = 256(0x100, float:3.59E-43)
        L81:
            r15.grow(r11)
            byte[] r3 = r15.getBytes()
            r8 = r9
        L89:
            if (r4 <= 0) goto L13
            java.io.BufferedInputStream r11 = r14.bin
            int r0 = r11.read(r3, r8, r4)
            int r4 = r4 - r0
            int r8 = r8 + r0
            goto L89
        L94:
            r11 = r3[r7]
            r12 = 123(0x7b, float:1.72E-43)
            if (r11 == r12) goto L64
            int r7 = r7 + (-1)
            goto L62
        L9d:
            r5 = move-exception
            goto L5c
        L9f:
            int r11 = r4 + 16
            int r11 = r11 - r1
            goto L81
        La3:
            r8 = r9
            goto L89
        La5:
            r8 = r9
            goto L13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.iap.ResponseInputStream.readResponse(com.sun.mail.iap.ByteArray):com.sun.mail.iap.ByteArray");
    }
}
