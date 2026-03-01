package com.jcraft.jzlib;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class DeflaterOutputStream extends FilterOutputStream {
    protected static final int DEFAULT_BUFSIZE = 512;
    private final byte[] buf1;
    protected byte[] buffer;
    private boolean close_out;
    private boolean closed;
    protected final Deflater deflater;
    protected boolean mydeflater;
    private boolean syncFlush;

    public DeflaterOutputStream(OutputStream out) throws IOException {
        this(out, new Deflater(-1), 512, true);
        this.mydeflater = true;
    }

    public DeflaterOutputStream(OutputStream out, Deflater def) throws IOException {
        this(out, def, 512, true);
    }

    public DeflaterOutputStream(OutputStream out, Deflater deflater, int size) throws IOException {
        this(out, deflater, size, true);
    }

    public DeflaterOutputStream(OutputStream out, Deflater deflater, int size, boolean close_out) throws IOException {
        super(out);
        this.closed = false;
        this.syncFlush = false;
        this.buf1 = new byte[1];
        this.mydeflater = false;
        this.close_out = true;
        if (out == null || deflater == null) {
            throw new NullPointerException();
        }
        if (size <= 0) {
            throw new IllegalArgumentException("buffer size must be greater than 0");
        }
        this.deflater = deflater;
        this.buffer = new byte[size];
        this.close_out = close_out;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.buf1[0] = (byte) (b & 255);
        write(this.buf1, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.deflater.finished()) {
            throw new IOException("finished");
        }
        if ((off + len > b.length) | (off < 0) | (len < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (len != 0) {
            int flush = this.syncFlush ? 2 : 0;
            this.deflater.setInput(b, off, len, true);
            while (this.deflater.avail_in > 0) {
                int err = deflate(flush);
                if (err == 1) {
                    return;
                }
            }
        }
    }

    public void finish() throws IOException {
        while (!this.deflater.finished()) {
            deflate(4);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            finish();
            if (this.mydeflater) {
                this.deflater.end();
            }
            if (this.close_out) {
                this.out.close();
            }
            this.closed = true;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0023, code lost:
    
        if (r7 != 4) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int deflate(int r7) throws java.io.IOException {
        /*
            r6 = this;
            r5 = 0
            com.jcraft.jzlib.Deflater r2 = r6.deflater
            byte[] r3 = r6.buffer
            byte[] r4 = r6.buffer
            int r4 = r4.length
            r2.setOutput(r3, r5, r4)
            com.jcraft.jzlib.Deflater r2 = r6.deflater
            int r0 = r2.deflate(r7)
            switch(r0) {
                case -5: goto L1c;
                case 0: goto L25;
                case 1: goto L25;
                default: goto L14;
            }
        L14:
            java.io.IOException r2 = new java.io.IOException
            java.lang.String r3 = "failed to deflate"
            r2.<init>(r3)
            throw r2
        L1c:
            com.jcraft.jzlib.Deflater r2 = r6.deflater
            int r2 = r2.avail_in
            if (r2 > 0) goto L14
            r2 = 4
            if (r7 == r2) goto L14
        L25:
            com.jcraft.jzlib.Deflater r2 = r6.deflater
            int r1 = r2.next_out_index
            if (r1 <= 0) goto L32
            java.io.OutputStream r2 = r6.out
            byte[] r3 = r6.buffer
            r2.write(r3, r5, r1)
        L32:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.DeflaterOutputStream.deflate(int):int");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        int err;
        if (this.syncFlush && !this.deflater.finished()) {
            do {
                err = deflate(2);
                if (this.deflater.next_out_index < this.buffer.length) {
                    break;
                }
            } while (err != 1);
        }
        this.out.flush();
    }

    public long getTotalIn() {
        return this.deflater.getTotalIn();
    }

    public long getTotalOut() {
        return this.deflater.getTotalOut();
    }

    public void setSyncFlush(boolean syncFlush) {
        this.syncFlush = syncFlush;
    }

    public boolean getSyncFlush() {
        return this.syncFlush;
    }

    public Deflater getDeflater() {
        return this.deflater;
    }
}
