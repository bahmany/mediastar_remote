package com.sun.mail.util;

import com.alibaba.fastjson.asm.Opcodes;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class UUDecoderStream extends FilterInputStream {
    private byte[] buffer;
    private int bufsize;
    private boolean gotEnd;
    private boolean gotPrefix;
    private int index;
    private LineInputStream lin;
    private int mode;
    private String name;

    public UUDecoderStream(InputStream in) {
        super(in);
        this.bufsize = 0;
        this.index = 0;
        this.gotPrefix = false;
        this.gotEnd = false;
        this.lin = new LineInputStream(in);
        this.buffer = new byte[45];
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.index >= this.bufsize) {
            readPrefix();
            if (!decode()) {
                return -1;
            }
            this.index = 0;
        }
        byte[] bArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buf, int off, int len) throws IOException {
        int i = 0;
        while (i < len) {
            int c = read();
            if (c == -1) {
                if (i == 0) {
                    return -1;
                }
                return i;
            }
            buf[off + i] = (byte) c;
            i++;
        }
        return i;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return ((this.in.available() * 3) / 4) + (this.bufsize - this.index);
    }

    public String getName() throws IOException {
        readPrefix();
        return this.name;
    }

    public int getMode() throws IOException {
        readPrefix();
        return this.mode;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0039, code lost:
    
        r6 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0052, code lost:
    
        throw new java.io.IOException("UUDecoder error: " + r6.toString());
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void readPrefix() throws java.io.IOException {
        /*
            r7 = this;
            r1 = 1
            r2 = 0
            boolean r3 = r7.gotPrefix
            if (r3 == 0) goto L7
        L6:
            return
        L7:
            com.sun.mail.util.LineInputStream r3 = r7.lin
            java.lang.String r0 = r3.readLine()
            if (r0 != 0) goto L17
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r2 = "UUDecoder error: No Begin"
            r1.<init>(r2)
            throw r1
        L17:
            java.lang.String r3 = "begin"
            r5 = 5
            r4 = r2
            boolean r3 = r0.regionMatches(r1, r2, r3, r4, r5)
            if (r3 == 0) goto L7
            r2 = 6
            r3 = 9
            java.lang.String r2 = r0.substring(r2, r3)     // Catch: java.lang.NumberFormatException -> L39
            int r2 = java.lang.Integer.parseInt(r2)     // Catch: java.lang.NumberFormatException -> L39
            r7.mode = r2     // Catch: java.lang.NumberFormatException -> L39
            r2 = 10
            java.lang.String r2 = r0.substring(r2)
            r7.name = r2
            r7.gotPrefix = r1
            goto L6
        L39:
            r6 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "UUDecoder error: "
            r2.<init>(r3)
            java.lang.String r3 = r6.toString()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.util.UUDecoderStream.readPrefix():void");
    }

    private boolean decode() throws IOException {
        String line;
        if (this.gotEnd) {
            return false;
        }
        this.bufsize = 0;
        do {
            line = this.lin.readLine();
            if (line == null) {
                throw new IOException("Missing End");
            }
            if (line.regionMatches(true, 0, "end", 0, 3)) {
                this.gotEnd = true;
                return false;
            }
        } while (line.length() == 0);
        int count = line.charAt(0);
        if (count < 32) {
            throw new IOException("Buffer format error");
        }
        int count2 = (count - 32) & 63;
        if (count2 == 0) {
            String line2 = this.lin.readLine();
            if (line2 == null || !line2.regionMatches(true, 0, "end", 0, 3)) {
                throw new IOException("Missing End");
            }
            this.gotEnd = true;
            return false;
        }
        int need = ((count2 * 8) + 5) / 6;
        if (line.length() < need + 1) {
            throw new IOException("Short buffer error");
        }
        int i = 1;
        while (this.bufsize < count2) {
            byte a = (byte) ((line.charAt(i) - ' ') & 63);
            i = i + 1 + 1;
            byte b = (byte) ((line.charAt(r10) - ' ') & 63);
            byte[] bArr = this.buffer;
            int i2 = this.bufsize;
            this.bufsize = i2 + 1;
            bArr[i2] = (byte) (((a << 2) & 252) | ((b >>> 4) & 3));
            if (this.bufsize < count2) {
                b = (byte) ((line.charAt(i) - ' ') & 63);
                byte[] bArr2 = this.buffer;
                int i3 = this.bufsize;
                this.bufsize = i3 + 1;
                bArr2[i3] = (byte) (((b << 4) & 240) | ((b >>> 2) & 15));
                i++;
            }
            if (this.bufsize < count2) {
                byte a2 = b;
                byte[] bArr3 = this.buffer;
                int i4 = this.bufsize;
                this.bufsize = i4 + 1;
                bArr3[i4] = (byte) (((a2 << 6) & Opcodes.CHECKCAST) | (((byte) ((line.charAt(i) - ' ') & 63)) & 63));
                i++;
            }
        }
        return true;
    }
}
