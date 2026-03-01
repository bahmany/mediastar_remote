package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class BASE64EncoderStream extends FilterOutputStream {
    private static byte[] newline = {13, 10};
    private static final char[] pem_array = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private byte[] buffer;
    private int bufsize;
    private int bytesPerLine;
    private int count;
    private int lineLimit;
    private boolean noCRLF;
    private byte[] outbuf;

    public BASE64EncoderStream(OutputStream out, int bytesPerLine) {
        super(out);
        this.bufsize = 0;
        this.count = 0;
        this.noCRLF = false;
        this.buffer = new byte[3];
        if (bytesPerLine == Integer.MAX_VALUE || bytesPerLine < 4) {
            this.noCRLF = true;
            bytesPerLine = 76;
        }
        int bytesPerLine2 = (bytesPerLine / 4) * 4;
        this.bytesPerLine = bytesPerLine2;
        this.lineLimit = (bytesPerLine2 / 4) * 3;
        if (this.noCRLF) {
            this.outbuf = new byte[bytesPerLine2];
            return;
        }
        this.outbuf = new byte[bytesPerLine2 + 2];
        this.outbuf[bytesPerLine2] = 13;
        this.outbuf[bytesPerLine2 + 1] = 10;
    }

    public BASE64EncoderStream(OutputStream out) {
        this(out, 76);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(byte[] b, int off, int len) throws Throwable {
        int off2;
        int end = off + len;
        int off3 = off;
        while (this.bufsize != 0 && off3 < end) {
            try {
                int off4 = off3 + 1;
                try {
                    write(b[off3]);
                    off3 = off4;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        int blen = ((this.bytesPerLine - this.count) / 4) * 3;
        if (off3 + blen < end) {
            int outlen = encodedSize(blen);
            if (!this.noCRLF) {
                int outlen2 = outlen + 1;
                this.outbuf[outlen] = 13;
                outlen = outlen2 + 1;
                this.outbuf[outlen2] = 10;
            }
            this.out.write(encode(b, off3, blen, this.outbuf), 0, outlen);
            off2 = off3 + blen;
            this.count = 0;
        } else {
            off2 = off3;
        }
        while (this.lineLimit + off2 < end) {
            this.out.write(encode(b, off2, this.lineLimit, this.outbuf));
            off2 += this.lineLimit;
        }
        if (off2 + 3 < end) {
            int blen2 = ((end - off2) / 3) * 3;
            int outlen3 = encodedSize(blen2);
            this.out.write(encode(b, off2, blen2, this.outbuf), 0, outlen3);
            off2 += blen2;
            this.count += outlen3;
        }
        while (off2 < end) {
            write(b[off2]);
            off2++;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws Throwable {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(int c) throws IOException {
        byte[] bArr = this.buffer;
        int i = this.bufsize;
        this.bufsize = i + 1;
        bArr[i] = (byte) c;
        if (this.bufsize == 3) {
            encode();
            this.bufsize = 0;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public synchronized void flush() throws IOException {
        if (this.bufsize > 0) {
            encode();
            this.bufsize = 0;
        }
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        flush();
        if (this.count > 0 && !this.noCRLF) {
            this.out.write(newline);
            this.out.flush();
        }
        this.out.close();
    }

    private void encode() throws IOException {
        int osize = encodedSize(this.bufsize);
        this.out.write(encode(this.buffer, 0, this.bufsize, this.outbuf), 0, osize);
        this.count += osize;
        if (this.count >= this.bytesPerLine) {
            if (!this.noCRLF) {
                this.out.write(newline);
            }
            this.count = 0;
        }
    }

    public static byte[] encode(byte[] inbuf) {
        return inbuf.length == 0 ? inbuf : encode(inbuf, 0, inbuf.length, null);
    }

    private static byte[] encode(byte[] inbuf, int off, int size, byte[] outbuf) {
        int inpos;
        if (outbuf == null) {
            outbuf = new byte[encodedSize(size)];
        }
        int inpos2 = off;
        int outpos = 0;
        while (true) {
            inpos = inpos2;
            if (size < 3) {
                break;
            }
            int inpos3 = inpos + 1;
            int val = inbuf[inpos] & 255;
            int inpos4 = inpos3 + 1;
            int val2 = ((val << 8) | (inbuf[inpos3] & 255)) << 8;
            inpos2 = inpos4 + 1;
            int val3 = val2 | (inbuf[inpos4] & 255);
            outbuf[outpos + 3] = (byte) pem_array[val3 & 63];
            int val4 = val3 >> 6;
            outbuf[outpos + 2] = (byte) pem_array[val4 & 63];
            int val5 = val4 >> 6;
            outbuf[outpos + 1] = (byte) pem_array[val5 & 63];
            outbuf[outpos + 0] = (byte) pem_array[(val5 >> 6) & 63];
            size -= 3;
            outpos += 4;
        }
        if (size == 1) {
            int i = inpos + 1;
            int val6 = inbuf[inpos] & 255;
            int val7 = val6 << 4;
            outbuf[outpos + 3] = 61;
            outbuf[outpos + 2] = 61;
            outbuf[outpos + 1] = (byte) pem_array[val7 & 63];
            outbuf[outpos + 0] = (byte) pem_array[(val7 >> 6) & 63];
        } else if (size == 2) {
            int inpos5 = inpos + 1;
            int val8 = inbuf[inpos] & 255;
            inpos = inpos5 + 1;
            int val9 = ((val8 << 8) | (inbuf[inpos5] & 255)) << 2;
            outbuf[outpos + 3] = 61;
            outbuf[outpos + 2] = (byte) pem_array[val9 & 63];
            int val10 = val9 >> 6;
            outbuf[outpos + 1] = (byte) pem_array[val10 & 63];
            outbuf[outpos + 0] = (byte) pem_array[(val10 >> 6) & 63];
        }
        return outbuf;
    }

    private static int encodedSize(int size) {
        return ((size + 2) / 3) * 4;
    }
}
