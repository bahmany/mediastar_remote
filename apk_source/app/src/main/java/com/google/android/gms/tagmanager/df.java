package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/* loaded from: classes.dex */
class df {
    private GoogleAnalytics arF;
    private Context mContext;
    private Tracker xY;

    static class a implements Logger {
        a() {
        }

        private static int fm(int i) {
            switch (i) {
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                default:
                    return 3;
            }
        }

        @Override // com.google.android.gms.analytics.Logger
        public void error(Exception exception) {
            bh.b("", exception);
        }

        @Override // com.google.android.gms.analytics.Logger
        public void error(String message) {
            bh.T(message);
        }

        @Override // com.google.android.gms.analytics.Logger
        public int getLogLevel() {
            return fm(bh.getLogLevel());
        }

        @Override // com.google.android.gms.analytics.Logger
        public void info(String message) {
            bh.U(message);
        }

        @Override // com.google.android.gms.analytics.Logger
        public void setLogLevel(int logLevel) {
            bh.W("GA uses GTM logger. Please use TagManager.setLogLevel(int) instead.");
        }

        @Override // com.google.android.gms.analytics.Logger
        public void verbose(String message) {
            bh.V(message);
        }

        @Override // com.google.android.gms.analytics.Logger
        public void warn(String message) {
            bh.W(message);
        }
    }

    df(Context context) {
        this.mContext = context;
    }

    private synchronized void cS(String str) {
        if (this.arF == null) {
            this.arF = GoogleAnalytics.getInstance(this.mContext);
            this.arF.setLogger(new a());
            this.xY = this.arF.newTracker(str);
        }
    }

    public Tracker cR(String str) {
        cS(str);
        return this.xY;
    }
}
