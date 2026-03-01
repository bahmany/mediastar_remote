package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/* loaded from: classes.dex */
public class QPDecoderStream extends FilterInputStream {
    protected byte[] ba;
    protected int spaces;

    public QPDecoderStream(InputStream in) {
        super(new PushbackInputStream(in, 2));
        this.ba = new byte[2];
        this.spaces = 0;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int c;
        if (this.spaces > 0) {
            this.spaces--;
            return 32;
        }
        int c2 = this.in.read();
        if (c2 == 32) {
            while (true) {
                c = this.in.read();
                if (c != 32) {
                    break;
                }
                this.spaces++;
            }
            if (c == 13 || c == 10 || c == -1) {
                this.spaces = 0;
                return c;
            }
            ((PushbackInputStream) this.in).unread(c);
            return 32;
        }
        if (c2 == 61) {
            int a = this.in.read();
            if (a == 10) {
                return read();
            }
            if (a == 13) {
                int b = this.in.read();
                if (b != 10) {
                    ((PushbackInputStream) this.in).unread(b);
                }
                return read();
            }
            if (a == -1) {
                return -1;
            }
            this.ba[0] = (byte) a;
            this.ba[1] = (byte) this.in.read();
            try {
                return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
            } catch (NumberFormatException e) {
                ((PushbackInputStream) this.in).unread(this.ba);
                return c2;
            }
        }
        return c2;
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
        return this.in.available();
    }
}
