package com.google.android.gms.analytics;

import android.content.Context;
import com.google.android.gms.analytics.j;

/* loaded from: classes.dex */
class ah extends j<ai> {

    private static class a implements j.a<ai> {
        private final ai BB = new ai();

        @Override // com.google.android.gms.analytics.j.a
        public void c(String str, int i) {
            if ("ga_sessionTimeout".equals(str)) {
                this.BB.BE = i;
            } else {
                z.W("int configuration name not recognized:  " + str);
            }
        }

        @Override // com.google.android.gms.analytics.j.a
        public void d(String str, boolean z) {
            if ("ga_autoActivityTracking".equals(str)) {
                this.BB.BF = z ? 1 : 0;
                return;
            }
            if ("ga_anonymizeIp".equals(str)) {
                this.BB.BG = z ? 1 : 0;
            } else if (!"ga_reportUncaughtExceptions".equals(str)) {
                z.W("bool configuration name not recognized:  " + str);
            } else {
                this.BB.BH = z ? 1 : 0;
            }
        }

        @Override // com.google.android.gms.analytics.j.a
        /* renamed from: eZ, reason: merged with bridge method [inline-methods] */
        public ai dX() {
            return this.BB;
        }

        @Override // com.google.android.gms.analytics.j.a
        public void f(String str, String str2) {
            this.BB.BI.put(str, str2);
        }

        @Override // com.google.android.gms.analytics.j.a
        public void g(String str, String str2) {
            if ("ga_trackingId".equals(str)) {
                this.BB.BC = str2;
                return;
            }
            if (!"ga_sampleFrequency".equals(str)) {
                z.W("string configuration name not recognized:  " + str);
                return;
            }
            try {
                this.BB.BD = Double.parseDouble(str2);
            } catch (NumberFormatException e) {
                z.T("Error parsing ga_sampleFrequency value: " + str2);
            }
        }
    }

    public ah(Context context) {
        super(context, new a());
    }
}
