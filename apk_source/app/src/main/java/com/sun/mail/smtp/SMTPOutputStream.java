package com.sun.mail.smtp;

import com.sun.mail.util.CRLFOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class SMTPOutputStream extends CRLFOutputStream {
    public SMTPOutputStream(OutputStream os) {
        super(os);
    }

    @Override // com.sun.mail.util.CRLFOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        if ((this.lastb == 10 || this.lastb == 13 || this.lastb == -1) && b == 46) {
            this.out.write(46);
        }
        super.write(b);
    }

    @Override // com.sun.mail.util.CRLFOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int off, int len) throws IOException {
        int i = this.lastb == -1 ? 10 : this.lastb;
        int start = off;
        int len2 = len + off;
        for (int i2 = off; i2 < len2; i2++) {
            if ((i == 10 || i == 13) && bArr[i2] == 46) {
                super.write(bArr, start, i2 - start);
                this.out.write(46);
                start = i2;
            }
            i = bArr[i2];
        }
        if (len2 - start > 0) {
            super.write(bArr, start, len2 - start);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() {
    }

    public void ensureAtBOL() throws IOException {
        if (!this.atBOL) {
            super.writeln();
        }
    }
}
