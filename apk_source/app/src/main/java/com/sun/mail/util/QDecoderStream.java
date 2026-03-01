package com.sun.mail.util;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class QDecoderStream extends QPDecoderStream {
    public QDecoderStream(InputStream in) {
        super(in);
    }

    @Override // com.sun.mail.util.QPDecoderStream, java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int c = this.in.read();
        if (c == 95) {
            return 32;
        }
        if (c == 61) {
            this.ba[0] = (byte) this.in.read();
            this.ba[1] = (byte) this.in.read();
            try {
                return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
            } catch (NumberFormatException nex) {
                throw new IOException("Error in QP stream " + nex.getMessage());
            }
        }
        return c;
    }
}
