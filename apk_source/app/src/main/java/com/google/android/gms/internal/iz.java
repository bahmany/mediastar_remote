package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public final class iz extends ja<a, Drawable> {

    public static final class a {
        public final int Li;
        public final int Lj;

        public a(int i, int i2) {
            this.Li = i;
            this.Lj = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof a)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            a aVar = (a) obj;
            return aVar.Li == this.Li && aVar.Lj == this.Lj;
        }

        public int hashCode() {
            return com.google.android.gms.common.internal.m.hashCode(Integer.valueOf(this.Li), Integer.valueOf(this.Lj));
        }
    }

    public iz() {
        super(10);
    }
}
