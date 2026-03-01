package com.jcraft.jzlib;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Deprecated
/* loaded from: classes.dex */
public class ZOutputStream extends FilterOutputStream {
    protected byte[] buf;
    private byte[] buf1;
    protected int bufsize;
    protected boolean compress;
    private DeflaterOutputStream dos;
    private boolean end;
    protected int flush;
    private Inflater inflater;
    protected OutputStream out;

    public ZOutputStream(OutputStream out) throws IOException {
        super(out);
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.end = false;
        this.buf1 = new byte[1];
        this.out = out;
        this.inflater = new Inflater();
        this.inflater.init();
        this.compress = false;
    }

    public ZOutputStream(OutputStream out, int level) throws IOException {
        this(out, level, false);
    }

    public ZOutputStream(OutputStream out, int level, boolean nowrap) throws IOException {
        super(out);
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.end = false;
        this.buf1 = new byte[1];
        this.out = out;
        Deflater deflater = new Deflater(level, nowrap);
        this.dos = new DeflaterOutputStream(out, deflater);
        this.compress = true;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.buf1[0] = (byte) b;
        write(this.buf1, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (len != 0) {
            if (this.compress) {
                this.dos.write(b, off, len);
                return;
            }
            this.inflater.setInput(b, off, len, true);
            int err = 0;
            while (this.inflater.avail_in > 0) {
                this.inflater.setOutput(this.buf, 0, this.buf.length);
                err = this.inflater.inflate(this.flush);
                if (this.inflater.next_out_index > 0) {
                    this.out.write(this.buf, 0, this.inflater.next_out_index);
                }
                if (err != 0) {
                    break;
                }
            }
            if (err != 0) {
                throw new ZStreamException("inflating: " + this.inflater.msg);
            }
        }
    }

    public int getFlushMode() {
        return this.flush;
    }

    public void setFlushMode(int flush) {
        this.flush = flush;
    }

    public void finish() throws IOException {
        if (this.compress) {
            int i = this.flush;
            write("".getBytes(), 0, 0);
        } else {
            this.dos.finish();
        }
        flush();
    }

    public synchronized void end() {
        if (!this.end) {
            if (this.compress) {
                try {
                    this.dos.finish();
                } catch (Exception e) {
                }
            } else {
                this.inflater.end();
            }
            this.end = true;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            finish();
        } catch (IOException e) {
        } catch (Throwable th) {
            end();
            this.out.close();
            this.out = null;
            throw th;
        }
        end();
        this.out.close();
        this.out = null;
    }

    public long getTotalIn() {
        return this.compress ? this.dos.getTotalIn() : this.inflater.total_in;
    }

    public long getTotalOut() {
        return this.compress ? this.dos.getTotalOut() : this.inflater.total_out;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }
}
