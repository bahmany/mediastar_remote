package com.google.android.gms.ads.purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.internal.ei;
import com.google.android.gms.internal.en;
import com.google.android.gms.internal.gs;

/* loaded from: classes.dex */
public final class InAppPurchaseActivity extends Activity {
    public static final String CLASS_NAME = "com.google.android.gms.ads.purchase.InAppPurchaseActivity";
    public static final String SIMPLE_CLASS_NAME = "InAppPurchaseActivity";
    private ei xk;

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (this.xk != null) {
                this.xk.onActivityResult(requestCode, resultCode, data);
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onActivityResult to in-app purchase manager:", e);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.xk = en.e(this);
        if (this.xk == null) {
            gs.W("Could not create in-app purchase manager.");
            finish();
            return;
        }
        try {
            this.xk.onCreate();
        } catch (RemoteException e) {
            gs.d("Could not forward onCreate to in-app purchase manager:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            if (this.xk != null) {
                this.xk.onDestroy();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onDestroy to in-app purchase manager:", e);
        }
        super.onDestroy();
    }
}
