package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.gms.ads.AdActivity;

@ez
/* loaded from: classes.dex */
public class gc {
    private final String vL;
    private final Object mw = new Object();
    private int vX = 0;
    private long vY = -1;
    private long vZ = -1;
    private int wa = 0;
    private int wb = -1;

    public gc(String str) {
        this.vL = str;
    }

    public static boolean m(Context context) {
        int identifier = context.getResources().getIdentifier("Theme.Translucent", "style", "android");
        if (identifier == 0) {
            gs.U("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        }
        try {
            if (identifier == context.getPackageManager().getActivityInfo(new ComponentName(context.getPackageName(), AdActivity.CLASS_NAME), 0).theme) {
                return true;
            }
            gs.U("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            gs.W("Fail to fetch AdActivity theme");
            gs.U("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        }
    }

    public Bundle b(Context context, String str) {
        Bundle bundle;
        synchronized (this.mw) {
            bundle = new Bundle();
            bundle.putString("session_id", this.vL);
            bundle.putLong("basets", this.vZ);
            bundle.putLong("currts", this.vY);
            bundle.putString("seq_num", str);
            bundle.putInt("preqs", this.wb);
            bundle.putInt("pclick", this.vX);
            bundle.putInt("pimp", this.wa);
            bundle.putBoolean("support_transparent_background", m(context));
        }
        return bundle;
    }

    public void b(av avVar, long j) {
        synchronized (this.mw) {
            if (this.vZ == -1) {
                this.vZ = j;
                this.vY = this.vZ;
            } else {
                this.vY = j;
            }
            if (avVar.extras == null || avVar.extras.getInt("gw", 2) != 1) {
                this.wb++;
            }
        }
    }

    public void cP() {
        synchronized (this.mw) {
            this.wa++;
        }
    }

    public void cQ() {
        synchronized (this.mw) {
            this.vX++;
        }
    }

    public long di() {
        return this.vZ;
    }
}
