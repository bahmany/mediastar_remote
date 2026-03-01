package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.internal.fz;
import master.flame.danmaku.danmaku.parser.IDataSource;

@ez
/* loaded from: classes.dex */
public class v {
    private a lZ;
    private boolean ma;
    private boolean mb;

    public interface a {
        void e(String str);
    }

    @ez
    public static class b implements a {
        private final fz.a mc;
        private final gv md;

        public b(fz.a aVar, gv gvVar) {
            this.mc = aVar;
            this.md = gvVar;
        }

        @Override // com.google.android.gms.internal.v.a
        public void e(String str) {
            gs.S("An auto-clicking creative is blocked");
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(IDataSource.SCHEME_HTTPS_TAG);
            builder.path("//pagead2.googlesyndication.com/pagead/gen_204");
            builder.appendQueryParameter("id", "gmob-apps-blocked-navigation");
            if (!TextUtils.isEmpty(str)) {
                builder.appendQueryParameter("navigationURL", str);
            }
            if (this.mc != null && this.mc.vw != null && !TextUtils.isEmpty(this.mc.vw.tN)) {
                builder.appendQueryParameter("debugDialog", this.mc.vw.tN);
            }
            gj.c(this.md.getContext(), this.md.dy().wD, builder.toString());
        }
    }

    public v() {
        boolean z = false;
        Bundle bundleBD = gb.bD();
        if (bundleBD != null && bundleBD.getBoolean("gads:block_autoclicks", false)) {
            z = true;
        }
        this.mb = z;
    }

    public v(boolean z) {
        this.mb = z;
    }

    public void a(a aVar) {
        this.lZ = aVar;
    }

    public void ar() {
        this.ma = true;
    }

    public boolean av() {
        return !this.mb || this.ma;
    }

    public void d(String str) {
        gs.S("Action was blocked because no click was detected.");
        if (this.lZ != null) {
            this.lZ.e(str);
        }
    }
}
