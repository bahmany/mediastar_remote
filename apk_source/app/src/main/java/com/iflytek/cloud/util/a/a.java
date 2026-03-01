package com.iflytek.cloud.util.a;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.cybergarage.soap.SOAP;

/* loaded from: classes.dex */
public class a {
    protected Context a;
    private com.iflytek.cloud.util.a.c.a b;
    private String[] c = null;
    private String[] d = null;
    private HashMap<String, String> e = new HashMap<>();
    private HashMap<String, String> f = new HashMap<>();
    private List<com.iflytek.cloud.util.a.a.a> g = new ArrayList();
    private HashMap<String, String> h = new HashMap<>();
    private HashMap<String, String> i = new HashMap<>();

    public a(Context context, com.iflytek.cloud.util.a.c.a aVar) {
        this.b = aVar;
        this.a = context;
    }

    private void b() {
        if (this.f.size() > 0) {
            this.f = null;
            this.f = new HashMap<>();
        }
        if (this.i.size() > 0) {
            this.i = null;
            this.i = new HashMap<>();
        }
        if (this.e.size() > 0) {
            this.e = null;
            this.e = new HashMap<>();
        }
        if (this.c == null || this.c.length <= 0) {
            return;
        }
        this.c = null;
    }

    public void a(int i) {
        if (this.h != null && this.h.size() > 0) {
            this.h.clear();
        }
        if (this.b != null) {
            this.h = this.b.a(i);
        }
    }

    public String[] a() throws Throwable {
        b();
        ArrayList arrayList = new ArrayList();
        HashMap<String, String> mapD = this.b.d();
        List<com.iflytek.cloud.util.a.a.a> listE = this.b.e();
        for (String str : mapD.keySet()) {
            String str2 = mapD.get(str);
            this.e.put(str + "p", str2);
            arrayList.add(str2);
            if (str2.contains("\u0000")) {
                this.i.put(str2.replace("\u0000", " "), str2);
            }
        }
        for (com.iflytek.cloud.util.a.a.a aVar : listE) {
            String strA = aVar.a();
            String strC = aVar.c();
            String strB = aVar.b();
            this.f.put(strA + SOAP.XMLNS, strC);
            this.e.put(strA + SOAP.XMLNS, strB);
            arrayList.add(strB);
            if (strB.contains("\u0000")) {
                this.i.put(strB.replace("\u0000", " "), strB);
            }
            this.g.add(aVar);
        }
        HashSet hashSet = new HashSet(arrayList);
        this.c = (String[]) hashSet.toArray(new String[hashSet.size()]);
        return this.c;
    }
}
