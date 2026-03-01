package com.jcraft.jzlib;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Deprecated
/* loaded from: classes.dex */
public class ZInputStream extends FilterInputStream {
    private byte[] buf;
    private byte[] buf1;
    protected boolean compress;
    protected Deflater deflater;
    protected int flush;
    protected InflaterInputStream iis;
    protected InputStream in;

    public ZInputStream(InputStream in) throws IOException {
        this(in, false);
    }

    public ZInputStream(InputStream in, boolean nowrap) throws IOException {
        super(in);
        this.flush = 0;
        this.in = null;
        this.buf1 = new byte[1];
        this.buf = new byte[512];
        this.iis = new InflaterInputStream(in, nowrap);
        this.compress = false;
    }

    public ZInputStream(InputStream in, int level) throws IOException {
        super(in);
        this.flush = 0;
        this.in = null;
        this.buf1 = new byte[1];
        this.buf = new byte[512];
        this.in = in;
        this.deflater = new Deflater();
        this.deflater.init(level);
        this.compress = true;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (read(this.buf1, 0, 1) == -1) {
            return -1;
        }
        return this.buf1[0] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int err;
        if (this.compress) {
            this.deflater.setOutput(b, off, len);
            do {
                int datalen = this.in.read(this.buf, 0, this.buf.length);
                if (datalen != -1) {
                    this.deflater.setInput(this.buf, 0, datalen, true);
                    err = this.deflater.deflate(this.flush);
                    if (this.deflater.next_out_index > 0) {
                        return this.deflater.next_out_index;
                    }
                    if (err != 1) {
                        if (err == -2) {
                            break;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    return -1;
                }
            } while (err != -3);
            throw new ZStreamException("deflating: " + this.deflater.msg);
        }
        return this.iis.read(b, off, len);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        int len = 512;
        if (n < 512) {
            len = (int) n;
        }
        byte[] tmp = new byte[len];
        return read(tmp);
    }

    public int getFlushMode() {
        return this.flush;
    }

    public void setFlushMode(int flush) {
        this.flush = flush;
    }

    public long getTotalIn() {
        return this.compress ? this.deflater.total_in : this.iis.getTotalIn();
    }

    public long getTotalOut() {
        return this.compress ? this.deflater.total_out : this.iis.getTotalOut();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.compress) {
            this.iis.close();
        } else {
            this.deflater.end();
        }
    }
}
