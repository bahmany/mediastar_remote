package com.google.android.gms.analytics;

import android.content.Context;
import com.google.android.gms.analytics.j;

/* loaded from: classes.dex */
class u extends j<v> {

    private static class a implements j.a<v> {
        private final v Ar = new v();

        @Override // com.google.android.gms.analytics.j.a
        public void c(String str, int i) {
            if ("ga_dispatchPeriod".equals(str)) {
                this.Ar.At = i;
            } else {
                z.W("int configuration name not recognized:  " + str);
            }
        }

        @Override // com.google.android.gms.analytics.j.a
        public void d(String str, boolean z) {
            if (!"ga_dryRun".equals(str)) {
                z.W("bool configuration name not recognized:  " + str);
            } else {
                this.Ar.Au = z ? 1 : 0;
            }
        }

        @Override // com.google.android.gms.analytics.j.a
        /* renamed from: et, reason: merged with bridge method [inline-methods] */
        public v dX() {
            return this.Ar;
        }

        @Override // com.google.android.gms.analytics.j.a
        public void f(String str, String str2) {
        }

        @Override // com.google.android.gms.analytics.j.a
        public void g(String str, String str2) {
            if ("ga_appName".equals(str)) {
                this.Ar.xL = str2;
                return;
            }
            if ("ga_appVersion".equals(str)) {
                this.Ar.xM = str2;
            } else if ("ga_logLevel".equals(str)) {
                this.Ar.As = str2;
            } else {
                z.W("string configuration name not recognized:  " + str);
            }
        }
    }

    public u(Context context) {
        super(context, new a());
    }
}
