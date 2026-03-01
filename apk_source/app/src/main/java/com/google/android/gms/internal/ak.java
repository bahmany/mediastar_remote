package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;

@ez
/* loaded from: classes.dex */
public class ak {
    private final int nf;
    private final int ng;
    private final int nh;
    private final ap ni;
    private int nn;
    private final Object mw = new Object();
    private ArrayList<String> nj = new ArrayList<>();
    private int nk = 0;
    private int nl = 0;
    private int nm = 0;
    private String no = "";

    public ak(int i, int i2, int i3, int i4) {
        this.nf = i;
        this.ng = i2;
        this.nh = i3;
        this.ni = new ap(i4);
    }

    private String a(ArrayList<String> arrayList, int i) {
        if (arrayList.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next());
            stringBuffer.append(' ');
            if (stringBuffer.length() > i) {
                break;
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        String string = stringBuffer.toString();
        return string.length() >= i ? string.substring(0, i) : string;
    }

    private void j(String str) {
        if (str == null || str.length() < this.nh) {
            return;
        }
        synchronized (this.mw) {
            this.nj.add(str);
            this.nk += str.length();
        }
    }

    int a(int i, int i2) {
        return (this.nf * i) + (this.ng * i2);
    }

    public boolean aN() {
        boolean z;
        synchronized (this.mw) {
            z = this.nm == 0;
        }
        return z;
    }

    public String aO() {
        return this.no;
    }

    public void aP() {
        synchronized (this.mw) {
            this.nn -= 100;
        }
    }

    public void aQ() {
        synchronized (this.mw) {
            this.nm--;
        }
    }

    public void aR() {
        synchronized (this.mw) {
            this.nm++;
        }
    }

    public void aS() {
        synchronized (this.mw) {
            int iA = a(this.nk, this.nl);
            if (iA > this.nn) {
                this.nn = iA;
                this.no = this.ni.a(this.nj);
            }
        }
    }

    int aT() {
        return this.nk;
    }

    public void c(int i) {
        this.nl = i;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ak)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        ak akVar = (ak) obj;
        return akVar.aO() != null && akVar.aO().equals(aO());
    }

    public int getScore() {
        return this.nn;
    }

    public void h(String str) {
        j(str);
        synchronized (this.mw) {
            if (this.nm < 0) {
                gs.S("ActivityContent: negative number of WebViews.");
            }
            aS();
        }
    }

    public int hashCode() {
        return aO().hashCode();
    }

    public void i(String str) {
        j(str);
    }

    public String toString() {
        return "ActivityContent fetchId: " + this.nl + " score:" + this.nn + " total_length:" + this.nk + "\n text: " + a(this.nj, 200) + "\n signture: " + this.no;
    }
}
