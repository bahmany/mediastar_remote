package com.jcraft.jzlib;

/* loaded from: classes.dex */
interface Checksum {
    Checksum copy();

    long getValue();

    void reset();

    void reset(long j);

    void update(byte[] bArr, int i, int i2);
}
