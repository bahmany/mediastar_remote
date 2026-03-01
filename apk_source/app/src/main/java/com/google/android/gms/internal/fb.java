package com.google.android.gms.internal;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.internal.fa;
import com.google.android.gms.internal.ff;
import com.google.android.gms.internal.fi;
import org.json.JSONException;

@ez
/* loaded from: classes.dex */
public class fb extends gg implements ff.a {
    private final Context mContext;
    private cm pR;
    private final fa.a sU;
    private final fi.a sW;
    private final k sX;
    private gg sY;
    private fk sZ;
    private final Object sV = new Object();
    private final Object mw = new Object();

    @ez
    private static final class a extends Exception {
        private final int tc;

        public a(String str, int i) {
            super(str);
            this.tc = i;
        }

        public int getErrorCode() {
            return this.tc;
        }
    }

    public fb(Context context, fi.a aVar, k kVar, fa.a aVar2) {
        this.sU = aVar2;
        this.mContext = context;
        this.sW = aVar;
        this.sX = kVar;
    }

    private ay a(fi fiVar) throws a, NumberFormatException {
        if (this.sZ.tL == null) {
            throw new a("The ad response must specify one of the supported ad sizes.", 0);
        }
        String[] strArrSplit = this.sZ.tL.split("x");
        if (strArrSplit.length != 2) {
            throw new a("Could not parse the ad size from the ad response: " + this.sZ.tL, 0);
        }
        try {
            int i = Integer.parseInt(strArrSplit[0]);
            int i2 = Integer.parseInt(strArrSplit[1]);
            for (ay ayVar : fiVar.lH.oh) {
                float f = this.mContext.getResources().getDisplayMetrics().density;
                int i3 = ayVar.width == -1 ? (int) (ayVar.widthPixels / f) : ayVar.width;
                int i4 = ayVar.height == -2 ? (int) (ayVar.heightPixels / f) : ayVar.height;
                if (i == i3 && i2 == i4) {
                    return new ay(ayVar, fiVar.lH.oh);
                }
            }
            throw new a("The ad size from the ad response was not one of the requested sizes: " + this.sZ.tL, 0);
        } catch (NumberFormatException e) {
            throw new a("Could not parse the ad size from the ad response: " + this.sZ.tL, 0);
        }
    }

    private boolean c(long j) throws a, InterruptedException {
        long jElapsedRealtime = 60000 - (SystemClock.elapsedRealtime() - j);
        if (jElapsedRealtime <= 0) {
            return false;
        }
        try {
            this.mw.wait(jElapsedRealtime);
            return true;
        } catch (InterruptedException e) {
            throw new a("Ad request cancelled.", -1);
        }
    }

    private void cy() throws a {
        if (this.sZ.errorCode == -3) {
            return;
        }
        if (TextUtils.isEmpty(this.sZ.tG)) {
            throw new a("No fill from ad server.", 3);
        }
        gb.a(this.mContext, this.sZ.tF);
        if (this.sZ.tI) {
            try {
                this.pR = new cm(this.sZ.tG);
            } catch (JSONException e) {
                throw new a("Could not parse mediation config: " + this.sZ.tG, 0);
            }
        }
    }

    private void e(long j) throws a {
        while (c(j)) {
            if (this.sZ != null) {
                synchronized (this.sV) {
                    this.sY = null;
                }
                if (this.sZ.errorCode != -2 && this.sZ.errorCode != -3) {
                    throw new a("There was a problem getting an ad response. ErrorCode: " + this.sZ.errorCode, this.sZ.errorCode);
                }
                return;
            }
        }
        throw new a("Timed out waiting for ad response.", 2);
    }

    private void r(boolean z) {
        gb.cV().v(z);
        an anVarL = gb.cV().l(this.mContext);
        if (anVarL == null || anVarL.isAlive()) {
            return;
        }
        gs.S("start fetching content...");
        anVarL.aV();
    }

    @Override // com.google.android.gms.internal.ff.a
    public void a(fk fkVar) {
        synchronized (this.mw) {
            gs.S("Received ad response.");
            this.sZ = fkVar;
            this.mw.notify();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00ce  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0071 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // com.google.android.gms.internal.gg
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void cp() {
        /*
            Method dump skipped, instructions count: 213
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.fb.cp():void");
    }

    @Override // com.google.android.gms.internal.gg
    public void onStop() {
        synchronized (this.sV) {
            if (this.sY != null) {
                this.sY.cancel();
            }
        }
    }
}
