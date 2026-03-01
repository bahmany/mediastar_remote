package com.iflytek.cloud.b;

import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class a {
    HashMap<String, String> a = new HashMap<>();

    public a() {
    }

    public a(String str, String[][] strArr) {
        a(str);
        a(strArr);
    }

    public int a(String str, int i) {
        if (!this.a.containsKey(str)) {
            return i;
        }
        try {
            return Integer.parseInt(this.a.get(str));
        } catch (Exception e) {
            return i;
        }
    }

    public void a() {
        this.a.clear();
    }

    public void a(a aVar, String str) {
        if (aVar == null) {
            return;
        }
        a(str, aVar.d(str));
    }

    public void a(String str) {
        this.a.clear();
        b(str);
    }

    public void a(String str, String str2) {
        a(str, str2, true);
    }

    public void a(String str, String str2, boolean z) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        if (z || !this.a.containsKey(str)) {
            this.a.put(str, str2);
        }
    }

    public void a(String[][] strArr) {
        if (strArr == null) {
            return;
        }
        for (String[] strArr2 : strArr) {
            if (this.a.containsKey(strArr2[0])) {
                String str = this.a.get(strArr2[0]);
                this.a.remove(strArr2[0]);
                for (int i = 1; i < strArr2.length; i++) {
                    this.a.put(strArr2[i], str);
                }
            }
        }
    }

    public boolean a(String str, boolean z) {
        if (!this.a.containsKey(str)) {
            return z;
        }
        String str2 = this.a.get(str);
        if (str2.equals("true") || str2.equals("1")) {
            return true;
        }
        if (str2.equals("false") || str2.equals("0")) {
            return false;
        }
        return z;
    }

    /* renamed from: b, reason: merged with bridge method [inline-methods] */
    public a clone() {
        a aVar = new a();
        aVar.a = (HashMap) this.a.clone();
        return aVar;
    }

    public String b(String str, String str2) {
        return !this.a.containsKey(str) ? str2 : this.a.get(str);
    }

    public void b(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        for (String str2 : str.split(ClientInfo.SEPARATOR_BETWEEN_VARS)) {
            int iIndexOf = str2.indexOf("=");
            if (iIndexOf > 0 && iIndexOf < str2.length()) {
                this.a.put(str2.substring(0, iIndexOf), str2.substring(iIndexOf + 1));
            }
        }
    }

    public Boolean c(String str) {
        if (!TextUtils.isEmpty(str) && this.a.containsKey(str)) {
            this.a.remove(str);
            return true;
        }
        return false;
    }

    public HashMap<String, String> c() {
        return this.a;
    }

    public String d(String str) {
        if (this.a.containsKey(str)) {
            return this.a.get(str);
        }
        return null;
    }

    public void d() {
        for (Map.Entry<String, String> entry : this.a.entrySet()) {
            entry.setValue(entry.getValue().replaceAll("[,\n ]", "|"));
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : this.a.entrySet()) {
            stringBuffer.append(entry.getKey());
            stringBuffer.append("=");
            stringBuffer.append(entry.getValue());
            stringBuffer.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        String string = stringBuffer.toString();
        com.iflytek.cloud.a.f.a.a.c(string);
        return string;
    }
}
