package com.google.android.gms.internal;

import android.util.Base64OutputStream;
import com.google.android.gms.internal.as;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/* loaded from: classes.dex */
public class ap {
    private final int nK;
    private Base64OutputStream nM;
    private ByteArrayOutputStream nN;
    private final ao nL = new ar();
    private final int nJ = 6;

    /* renamed from: com.google.android.gms.internal.ap$1 */
    class AnonymousClass1 implements Comparator<String> {
        AnonymousClass1() {
        }

        @Override // java.util.Comparator
        public int compare(String s1, String s2) {
            return s2.length() - s1.length();
        }
    }

    /* renamed from: com.google.android.gms.internal.ap$2 */
    class AnonymousClass2 implements Comparator<as.a> {
        AnonymousClass2() {
        }

        @Override // java.util.Comparator
        /* renamed from: a */
        public int compare(as.a aVar, as.a aVar2) {
            return (int) (aVar.value - aVar2.value);
        }
    }

    public ap(int i) {
        this.nK = i;
    }

    private String m(String str) throws IOException {
        String[] strArrSplit = str.split("\n");
        if (strArrSplit == null || strArrSplit.length == 0) {
            return "";
        }
        this.nN = new ByteArrayOutputStream();
        this.nM = new Base64OutputStream(this.nN, 10);
        Arrays.sort(strArrSplit, new Comparator<String>() { // from class: com.google.android.gms.internal.ap.1
            AnonymousClass1() {
            }

            @Override // java.util.Comparator
            public int compare(String s1, String s2) {
                return s2.length() - s1.length();
            }
        });
        for (int i = 0; i < strArrSplit.length && i < this.nK; i++) {
            if (strArrSplit[i].trim().length() != 0) {
                try {
                    this.nM.write(this.nL.l(strArrSplit[i]));
                } catch (IOException e) {
                    gs.b("Error while writing hash to byteStream", e);
                }
            }
        }
        try {
            this.nM.flush();
            this.nM.close();
            return this.nN.toString();
        } catch (IOException e2) {
            gs.b("HashManager: Unable to convert to base 64", e2);
            return "";
        }
    }

    public String a(ArrayList<String> arrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next().toLowerCase());
            stringBuffer.append('\n');
        }
        boolean z = false;
        switch (z) {
            case false:
                return n(stringBuffer.toString());
            case true:
                return m(stringBuffer.toString());
            default:
                return "";
        }
    }

    String n(String str) throws IOException {
        String[] strArrSplit = str.split("\n");
        if (strArrSplit == null || strArrSplit.length == 0) {
            return "";
        }
        this.nN = new ByteArrayOutputStream();
        this.nM = new Base64OutputStream(this.nN, 10);
        PriorityQueue priorityQueue = new PriorityQueue(this.nK, new Comparator<as.a>() { // from class: com.google.android.gms.internal.ap.2
            AnonymousClass2() {
            }

            @Override // java.util.Comparator
            /* renamed from: a */
            public int compare(as.a aVar, as.a aVar2) {
                return (int) (aVar.value - aVar2.value);
            }
        });
        for (String str2 : strArrSplit) {
            String[] strArrP = aq.p(str2);
            if (strArrP.length >= this.nJ) {
                as.a(strArrP, this.nK, this.nJ, priorityQueue);
            }
        }
        Iterator it = priorityQueue.iterator();
        while (it.hasNext()) {
            try {
                this.nM.write(this.nL.l(((as.a) it.next()).nQ));
            } catch (IOException e) {
                gs.b("Error while writing hash to byteStream", e);
            }
        }
        try {
            this.nM.flush();
            this.nM.close();
            return this.nN.toString();
        } catch (IOException e2) {
            gs.b("HashManager: unable to convert to base 64", e2);
            return "";
        }
    }
}
