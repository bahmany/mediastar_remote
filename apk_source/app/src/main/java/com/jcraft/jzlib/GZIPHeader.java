package com.jcraft.jzlib;

import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class GZIPHeader implements Cloneable {
    public static final byte OS_AMIGA = 1;
    public static final byte OS_ATARI = 5;
    public static final byte OS_CPM = 9;
    public static final byte OS_MACOS = 7;
    public static final byte OS_MSDOS = 0;
    public static final byte OS_OS2 = 6;
    public static final byte OS_QDOS = 12;
    public static final byte OS_RISCOS = 13;
    public static final byte OS_TOPS20 = 10;
    public static final byte OS_UNIX = 3;
    public static final byte OS_UNKNOWN = -1;
    public static final byte OS_VMCMS = 4;
    public static final byte OS_VMS = 2;
    public static final byte OS_WIN32 = 11;
    public static final byte OS_ZSYSTEM = 8;
    byte[] comment;
    long crc;
    byte[] extra;
    int hcrc;
    byte[] name;
    long time;
    int xflags;
    boolean text = false;
    private boolean fhcrc = false;
    int os = 255;
    boolean done = false;
    long mtime = 0;

    public void setModifiedTime(long mtime) {
        this.mtime = mtime;
    }

    public long getModifiedTime() {
        return this.mtime;
    }

    public void setOS(int os) {
        if ((os >= 0 && os <= 13) || os == 255) {
            this.os = os;
            return;
        }
        throw new IllegalArgumentException("os: " + os);
    }

    public int getOS() {
        return this.os;
    }

    public void setName(String name) {
        try {
            this.name = name.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("name must be in ISO-8859-1 " + name);
        }
    }

    public String getName() {
        if (this.name == null) {
            return "";
        }
        try {
            return new String(this.name, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e.toString());
        }
    }

    public void setComment(String comment) {
        try {
            this.comment = comment.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("comment must be in ISO-8859-1 " + this.name);
        }
    }

    public String getComment() {
        if (this.comment == null) {
            return "";
        }
        try {
            return new String(this.comment, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e.toString());
        }
    }

    public void setCRC(long crc) {
        this.crc = crc;
    }

    public long getCRC() {
        return this.crc;
    }

    void put(Deflate d) {
        int flag = 0;
        if (this.text) {
            flag = 0 | 1;
        }
        if (this.fhcrc) {
            flag |= 2;
        }
        if (this.extra != null) {
            flag |= 4;
        }
        if (this.name != null) {
            flag |= 8;
        }
        if (this.comment != null) {
            flag |= 16;
        }
        int xfl = 0;
        if (d.level == 1) {
            xfl = 0 | 4;
        } else if (d.level == 9) {
            xfl = 0 | 2;
        }
        d.put_short(-29921);
        d.put_byte((byte) 8);
        d.put_byte((byte) flag);
        d.put_byte((byte) this.mtime);
        d.put_byte((byte) (this.mtime >> 8));
        d.put_byte((byte) (this.mtime >> 16));
        d.put_byte((byte) (this.mtime >> 24));
        d.put_byte((byte) xfl);
        d.put_byte((byte) this.os);
        if (this.extra != null) {
            d.put_byte((byte) this.extra.length);
            d.put_byte((byte) (this.extra.length >> 8));
            d.put_byte(this.extra, 0, this.extra.length);
        }
        if (this.name != null) {
            d.put_byte(this.name, 0, this.name.length);
            d.put_byte((byte) 0);
        }
        if (this.comment != null) {
            d.put_byte(this.comment, 0, this.comment.length);
            d.put_byte((byte) 0);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        GZIPHeader gheader = (GZIPHeader) super.clone();
        if (gheader.extra != null) {
            byte[] tmp = new byte[gheader.extra.length];
            System.arraycopy(gheader.extra, 0, tmp, 0, tmp.length);
            gheader.extra = tmp;
        }
        if (gheader.name != null) {
            byte[] tmp2 = new byte[gheader.name.length];
            System.arraycopy(gheader.name, 0, tmp2, 0, tmp2.length);
            gheader.name = tmp2;
        }
        if (gheader.comment != null) {
            byte[] tmp3 = new byte[gheader.comment.length];
            System.arraycopy(gheader.comment, 0, tmp3, 0, tmp3.length);
            gheader.comment = tmp3;
        }
        return gheader;
    }
}
