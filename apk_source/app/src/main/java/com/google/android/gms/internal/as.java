package com.google.android.gms.internal;

import java.util.PriorityQueue;

/* loaded from: classes.dex */
public class as {

    public static class a {
        final String nQ;
        final long value;

        a(long j, String str) {
            this.value = j;
            this.nQ = str;
        }
    }

    static long a(int i, int i2, long j, long j2, long j3) {
        return ((((((j + 1073807359) - ((((i + 2147483647L) % 1073807359) * j2) % 1073807359)) % 1073807359) * j3) % 1073807359) + ((i2 + 2147483647L) % 1073807359)) % 1073807359;
    }

    static long a(long j, int i) {
        if (i == 0) {
            return 1L;
        }
        return i != 1 ? i % 2 == 0 ? a((j * j) % 1073807359, i / 2) % 1073807359 : ((a((j * j) % 1073807359, i / 2) % 1073807359) * j) % 1073807359 : j;
    }

    static String a(String[] strArr, int i, int i2) {
        if (strArr.length < i + i2) {
            gs.T("Unable to construct shingle");
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i3 = i; i3 < (i + i2) - 1; i3++) {
            stringBuffer.append(strArr[i3]);
            stringBuffer.append(' ');
        }
        stringBuffer.append(strArr[(i + i2) - 1]);
        return stringBuffer.toString();
    }

    private static void a(int i, long j, int i2, String[] strArr, int i3, PriorityQueue<a> priorityQueue) {
        priorityQueue.add(new a(j, a(strArr, i2, i3)));
        if (priorityQueue.size() > i) {
            priorityQueue.poll();
        }
    }

    public static void a(String[] strArr, int i, int i2, PriorityQueue<a> priorityQueue) {
        long jB = b(strArr, 0, i2);
        a(i, jB, 0, strArr, i2, priorityQueue);
        long jA = a(16785407L, i2 - 1);
        int i3 = 1;
        while (i3 < (strArr.length - i2) + 1) {
            long jA2 = a(aq.o(strArr[i3 - 1]), aq.o(strArr[(i3 + i2) - 1]), jB, jA, 16785407L);
            a(i, jA2, i3, strArr, i2, priorityQueue);
            i3++;
            jB = jA2;
        }
    }

    private static long b(String[] strArr, int i, int i2) {
        long jO = (aq.o(strArr[i]) + 2147483647L) % 1073807359;
        for (int i3 = i + 1; i3 < i + i2; i3++) {
            jO = (((jO * 16785407) % 1073807359) + ((aq.o(strArr[i3]) + 2147483647L) % 1073807359)) % 1073807359;
        }
        return jO;
    }
}
