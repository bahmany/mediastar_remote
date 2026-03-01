package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class CRLFOutputStream extends FilterOutputStream {
    private static final byte[] newline = {13, 10};
    protected boolean atBOL;
    protected int lastb;

    public CRLFOutputStream(OutputStream os) {
        super(os);
        this.lastb = -1;
        this.atBOL = true;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        if (b == 13) {
            writeln();
        } else if (b == 10) {
            if (this.lastb != 13) {
                writeln();
            }
        } else {
            this.out.write(b);
            this.atBOL = false;
        }
        this.lastb = b;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        int start = off;
        int len2 = len + off;
        for (int i = start; i < len2; i++) {
            if (b[i] == 13) {
                this.out.write(b, start, i - start);
                writeln();
                start = i + 1;
            } else if (b[i] == 10) {
                if (this.lastb != 13) {
                    this.out.write(b, start, i - start);
                    writeln();
                }
                start = i + 1;
            }
            this.lastb = b[i];
        }
        if (len2 - start > 0) {
            this.out.write(b, start, len2 - start);
            this.atBOL = false;
        }
    }

    public void writeln() throws IOException {
        this.out.write(newline);
        this.atBOL = true;
    }
}
