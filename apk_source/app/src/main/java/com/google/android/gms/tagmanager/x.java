package com.google.android.gms.tagmanager;

import android.util.Log;

/* loaded from: classes.dex */
class x implements bi {
    private int xW = 5;

    x() {
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void S(String str) {
        if (this.xW <= 3) {
            Log.d("GoogleTagManager", str);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void T(String str) {
        if (this.xW <= 6) {
            Log.e("GoogleTagManager", str);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void U(String str) {
        if (this.xW <= 4) {
            Log.i("GoogleTagManager", str);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void V(String str) {
        if (this.xW <= 2) {
            Log.v("GoogleTagManager", str);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void W(String str) {
        if (this.xW <= 5) {
            Log.w("GoogleTagManager", str);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void b(String str, Throwable th) {
        if (this.xW <= 6) {
            Log.e("GoogleTagManager", str, th);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void d(String str, Throwable th) {
        if (this.xW <= 5) {
            Log.w("GoogleTagManager", str, th);
        }
    }

    @Override // com.google.android.gms.tagmanager.bi
    public void setLogLevel(int logLevel) {
        this.xW = logLevel;
    }
}
