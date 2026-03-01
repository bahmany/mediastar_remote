package com.google.android.gms.analytics;

import android.util.Log;

/* loaded from: classes.dex */
class k implements Logger {
    private int xW = 2;

    k() {
    }

    private String ae(String str) {
        return Thread.currentThread().toString() + ": " + str;
    }

    @Override // com.google.android.gms.analytics.Logger
    public void error(Exception exception) {
        if (this.xW <= 3) {
            Log.e("GAV4", null, exception);
        }
    }

    @Override // com.google.android.gms.analytics.Logger
    public void error(String msg) {
        if (this.xW <= 3) {
            Log.e("GAV4", ae(msg));
        }
    }

    @Override // com.google.android.gms.analytics.Logger
    public int getLogLevel() {
        return this.xW;
    }

    @Override // com.google.android.gms.analytics.Logger
    public void info(String msg) {
        if (this.xW <= 1) {
            Log.i("GAV4", ae(msg));
        }
    }

    @Override // com.google.android.gms.analytics.Logger
    public void setLogLevel(int level) {
        this.xW = level;
    }

    @Override // com.google.android.gms.analytics.Logger
    public void verbose(String msg) {
        if (this.xW <= 0) {
            Log.v("GAV4", ae(msg));
        }
    }

    @Override // com.google.android.gms.analytics.Logger
    public void warn(String msg) {
        if (this.xW <= 2) {
            Log.w("GAV4", ae(msg));
        }
    }
}
