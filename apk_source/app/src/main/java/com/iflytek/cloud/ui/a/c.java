package com.iflytek.cloud.ui.a;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

/* loaded from: classes.dex */
public class c extends Dialog {
    protected e a;
    private a b;

    public interface a {
        void a();
    }

    public c(Context context) {
        super(context);
        this.a = null;
        this.b = new d(this);
    }

    public boolean destroy() {
        if (isShowing()) {
            return false;
        }
        boolean zD = this.a.d();
        this.a = null;
        return zD;
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        this.a.c();
        super.dismiss();
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(this.a);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    @Override // android.app.Dialog
    public void show() {
        setCanceledOnTouchOutside(true);
        this.a.a(this.b);
        this.a.b();
        super.show();
    }
}
