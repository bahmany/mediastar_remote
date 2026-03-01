package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class QPEncoderStream extends FilterOutputStream {
    private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private int bytesPerLine;
    private int count;
    private boolean gotCR;
    private boolean gotSpace;

    public QPEncoderStream(OutputStream out, int bytesPerLine) {
        super(out);
        this.count = 0;
        this.gotSpace = false;
        this.gotCR = false;
        this.bytesPerLine = bytesPerLine - 1;
    }

    public QPEncoderStream(OutputStream out) {
        this(out, 76);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int c) throws IOException {
        int c2 = c & 255;
        if (this.gotSpace) {
            if (c2 == 13 || c2 == 10) {
                output(32, true);
            } else {
                output(32, false);
            }
            this.gotSpace = false;
        }
        if (c2 == 13) {
            this.gotCR = true;
            outputCRLF();
            return;
        }
        if (c2 == 10) {
            if (!this.gotCR) {
                outputCRLF();
            }
        } else if (c2 == 32) {
            this.gotSpace = true;
        } else if (c2 < 32 || c2 >= 127 || c2 == 61) {
            output(c2, true);
        } else {
            output(c2, false);
        }
        this.gotCR = false;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.close();
    }

    private void outputCRLF() throws IOException {
        this.out.write(13);
        this.out.write(10);
        this.count = 0;
    }

    protected void output(int c, boolean encode) throws IOException {
        if (encode) {
            int i = this.count + 3;
            this.count = i;
            if (i > this.bytesPerLine) {
                this.out.write(61);
                this.out.write(13);
                this.out.write(10);
                this.count = 3;
            }
            this.out.write(61);
            this.out.write(hex[c >> 4]);
            this.out.write(hex[c & 15]);
            return;
        }
        int i2 = this.count + 1;
        this.count = i2;
        if (i2 > this.bytesPerLine) {
            this.out.write(61);
            this.out.write(13);
            this.out.write(10);
            this.count = 1;
        }
        this.out.write(c);
    }
}
