package com.jcraft.jzlib;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class InflaterInputStream extends FilterInputStream {
    protected static final int DEFAULT_BUFSIZE = 512;
    private byte[] b;
    protected byte[] buf;
    private byte[] byte1;
    private boolean close_in;
    private boolean closed;
    private boolean eof;
    protected final Inflater inflater;
    protected boolean myinflater;

    public InflaterInputStream(InputStream in) throws IOException {
        this(in, false);
    }

    public InflaterInputStream(InputStream in, boolean nowrap) throws IOException {
        this(in, new Inflater(nowrap));
        this.myinflater = true;
    }

    public InflaterInputStream(InputStream in, Inflater inflater) throws IOException {
        this(in, inflater, 512);
    }

    public InflaterInputStream(InputStream in, Inflater inflater, int size) throws IOException {
        this(in, inflater, size, true);
    }

    public InflaterInputStream(InputStream in, Inflater inflater, int size, boolean close_in) throws IOException {
        super(in);
        this.closed = false;
        this.eof = false;
        this.close_in = true;
        this.myinflater = false;
        this.byte1 = new byte[1];
        this.b = new byte[512];
        if (in == null || inflater == null) {
            throw new NullPointerException();
        }
        if (size <= 0) {
            throw new IllegalArgumentException("buffer size must be greater than 0");
        }
        this.inflater = inflater;
        this.buf = new byte[size];
        this.close_in = close_in;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (read(this.byte1, 0, 1) == -1) {
            return -1;
        }
        return this.byte1[0] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.eof) {
            return -1;
        }
        int n = 0;
        this.inflater.setOutput(b, off, len);
        while (!this.eof) {
            if (this.inflater.avail_in == 0) {
                fill();
            }
            int err = this.inflater.inflate(0);
            n += this.inflater.next_out_index - off;
            off = this.inflater.next_out_index;
            switch (err) {
                case -3:
                    throw new IOException(this.inflater.msg);
                case 1:
                case 2:
                    this.eof = true;
                    if (err == 2) {
                        return -1;
                    }
                    break;
            }
            if (this.inflater.avail_out == 0) {
                return n;
            }
        }
        return n;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        return this.eof ? 0 : 1;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("negative skip length");
        }
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        int max = (int) Math.min(n, 2147483647L);
        int total = 0;
        while (true) {
            if (total >= max) {
                break;
            }
            int len = max - total;
            if (len > this.b.length) {
                len = this.b.length;
            }
            int len2 = read(this.b, 0, len);
            if (len2 == -1) {
                this.eof = true;
                break;
            }
            total += len2;
        }
        return total;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            if (this.myinflater) {
                this.inflater.end();
            }
            if (this.close_in) {
                this.in.close();
            }
            this.closed = true;
        }
    }

    protected void fill() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        int len = this.in.read(this.buf, 0, this.buf.length);
        if (len == -1) {
            if (this.inflater.istate.wrap != 0 || this.inflater.finished()) {
                if (this.inflater.istate.was != -1) {
                    throw new IOException("footer is not found");
                }
                throw new EOFException("Unexpected end of ZLIB input stream");
            }
            this.buf[0] = 0;
            len = 1;
        }
        this.inflater.setInput(this.buf, 0, len, true);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void mark(int readlimit) {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public long getTotalIn() {
        return this.inflater.getTotalIn();
    }

    public long getTotalOut() {
        return this.inflater.getTotalOut();
    }

    public byte[] getAvailIn() {
        if (this.inflater.avail_in <= 0) {
            return null;
        }
        byte[] tmp = new byte[this.inflater.avail_in];
        System.arraycopy(this.inflater.next_in, this.inflater.next_in_index, tmp, 0, this.inflater.avail_in);
        return tmp;
    }

    /* JADX WARN: Incorrect condition in loop: B:29:0x002b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void readHeader() throws java.io.IOException {
        /*
            r6 = this;
            r5 = 0
            java.lang.String r4 = ""
            byte[] r1 = r4.getBytes()
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            r4.setInput(r1, r5, r5, r5)
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            r4.setOutput(r1, r5, r5)
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            int r2 = r4.inflate(r5)
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            com.jcraft.jzlib.Inflate r4 = r4.istate
            boolean r4 = r4.inParsingHeader()
            if (r4 != 0) goto L22
        L21:
            return
        L22:
            r4 = 1
            byte[] r0 = new byte[r4]
        L25:
            java.io.InputStream r4 = r6.in
            int r3 = r4.read(r0)
            if (r3 > 0) goto L35
            java.io.IOException r4 = new java.io.IOException
            java.lang.String r5 = "no input"
            r4.<init>(r5)
            throw r4
        L35:
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            r4.setInput(r0)
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            int r2 = r4.inflate(r5)
            if (r2 == 0) goto L4c
            java.io.IOException r4 = new java.io.IOException
            com.jcraft.jzlib.Inflater r5 = r6.inflater
            java.lang.String r5 = r5.msg
            r4.<init>(r5)
            throw r4
        L4c:
            com.jcraft.jzlib.Inflater r4 = r6.inflater
            com.jcraft.jzlib.Inflate r4 = r4.istate
            boolean r4 = r4.inParsingHeader()
            if (r4 != 0) goto L25
            goto L21
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.InflaterInputStream.readHeader():void");
    }

    public Inflater getInflater() {
        return this.inflater;
    }
}
