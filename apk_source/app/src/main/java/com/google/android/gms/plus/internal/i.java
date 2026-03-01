package com.google.android.gms.plus.internal;

import android.content.Context;
import com.google.android.gms.common.Scopes;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class i {
    private String Dd;
    private String[] alB;
    private String[] alu;
    private String alv;
    private String alw;
    private String alx;
    private final ArrayList<String> alA = new ArrayList<>();
    private PlusCommonExtras alz = new PlusCommonExtras();

    public i(Context context) {
        this.alw = context.getPackageName();
        this.alv = context.getPackageName();
        this.alA.add(Scopes.PLUS_LOGIN);
    }

    public i ce(String str) {
        this.Dd = str;
        return this;
    }

    public i g(String... strArr) {
        this.alA.clear();
        this.alA.addAll(Arrays.asList(strArr));
        return this;
    }

    public i h(String... strArr) {
        this.alB = strArr;
        return this;
    }

    public i nn() {
        this.alA.clear();
        return this;
    }

    public h no() {
        if (this.Dd == null) {
            this.Dd = "<<default account>>";
        }
        return new h(this.Dd, (String[]) this.alA.toArray(new String[this.alA.size()]), this.alB, this.alu, this.alv, this.alw, this.alx, this.alz);
    }
}
