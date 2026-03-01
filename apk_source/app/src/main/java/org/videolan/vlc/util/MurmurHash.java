package org.videolan.vlc.util;

/* loaded from: classes.dex */
public final class MurmurHash {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int hash32(byte[] data, int length, int seed) {
        int h = seed ^ length;
        int length4 = length / 4;
        for (int i = 0; i < length4; i++) {
            int i4 = i * 4;
            int k = ((data[i4 + 0] & 255) + ((data[i4 + 1] & 255) << 8) + ((data[i4 + 2] & 255) << 16) + ((data[i4 + 3] & 255) << 24)) * 1540483477;
            h = (h * 1540483477) ^ ((k ^ (k >>> 24)) * 1540483477);
        }
        switch (length % 4) {
            case 1:
                h = (h ^ (data[length & (-4)] & 255)) * 1540483477;
                break;
            case 2:
                h ^= (data[(length & (-4)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-4)] & 255)) * 1540483477;
                break;
            case 3:
                h ^= (data[(length & (-4)) + 2] & 255) << 16;
                h ^= (data[(length & (-4)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-4)] & 255)) * 1540483477;
                break;
        }
        int h2 = (h ^ (h >>> 13)) * 1540483477;
        return h2 ^ (h2 >>> 15);
    }

    public static int hash32(byte[] data, int length) {
        return hash32(data, length, -1756908916);
    }

    public static int hash32(String text) {
        byte[] bytes = text.getBytes();
        return hash32(bytes, bytes.length);
    }

    public static int hash32(String text, int from, int length) {
        return hash32(text.substring(from, from + length));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static long hash64(byte[] data, int length, int seed) {
        long h = (seed & 4294967295L) ^ (length * (-4132994306676758123L));
        int length8 = length / 8;
        for (int i = 0; i < length8; i++) {
            int i8 = i * 8;
            long k = ((data[i8 + 0] & 255) + ((data[i8 + 1] & 255) << 8) + ((data[i8 + 2] & 255) << 16) + ((data[i8 + 3] & 255) << 24) + ((data[i8 + 4] & 255) << 32) + ((data[i8 + 5] & 255) << 40) + ((data[i8 + 6] & 255) << 48) + ((data[i8 + 7] & 255) << 56)) * (-4132994306676758123L);
            h = (h ^ ((k ^ (k >>> 47)) * (-4132994306676758123L))) * (-4132994306676758123L);
        }
        switch (length % 8) {
            case 1:
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 2:
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 3:
                h ^= (data[(length & (-8)) + 2] & 255) << 16;
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 4:
                h ^= (data[(length & (-8)) + 3] & 255) << 24;
                h ^= (data[(length & (-8)) + 2] & 255) << 16;
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 5:
                h ^= (data[(length & (-8)) + 4] & 255) << 32;
                h ^= (data[(length & (-8)) + 3] & 255) << 24;
                h ^= (data[(length & (-8)) + 2] & 255) << 16;
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 6:
                h ^= (data[(length & (-8)) + 5] & 255) << 40;
                h ^= (data[(length & (-8)) + 4] & 255) << 32;
                h ^= (data[(length & (-8)) + 3] & 255) << 24;
                h ^= (data[(length & (-8)) + 2] & 255) << 16;
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
            case 7:
                h ^= (data[(length & (-8)) + 6] & 255) << 48;
                h ^= (data[(length & (-8)) + 5] & 255) << 40;
                h ^= (data[(length & (-8)) + 4] & 255) << 32;
                h ^= (data[(length & (-8)) + 3] & 255) << 24;
                h ^= (data[(length & (-8)) + 2] & 255) << 16;
                h ^= (data[(length & (-8)) + 1] & 255) << 8;
                h = (h ^ (data[length & (-8)] & 255)) * (-4132994306676758123L);
                break;
        }
        long h2 = (h ^ (h >>> 47)) * (-4132994306676758123L);
        return h2 ^ (h2 >>> 47);
    }

    public static long hash64(byte[] data, int length) {
        return hash64(data, length, -512093083);
    }

    public static long hash64(String text) {
        byte[] bytes = text.getBytes();
        return hash64(bytes, bytes.length);
    }

    public static long hash64(String text, int from, int length) {
        return hash64(text.substring(from, from + length));
    }
}
