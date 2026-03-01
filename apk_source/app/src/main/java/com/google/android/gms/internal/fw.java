package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import java.util.Locale;

@ez
/* loaded from: classes.dex */
public final class fw {
    public final int uS;
    public final boolean uT;
    public final boolean uU;
    public final String uV;
    public final String uW;
    public final boolean uX;
    public final boolean uY;
    public final boolean uZ;
    public final String va;
    public final String vb;
    public final int vc;
    public final int vd;
    public final int ve;
    public final int vf;
    public final int vg;
    public final int vh;
    public final float vi;
    public final int vj;
    public final int vk;
    public final double vl;
    public final boolean vm;
    public final boolean vn;
    public final int vo;

    public fw(Context context) {
        boolean z = true;
        AudioManager audioManager = (AudioManager) context.getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Locale locale = Locale.getDefault();
        PackageManager packageManager = context.getPackageManager();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.uS = audioManager.getMode();
        this.uT = a(packageManager, "geo:0,0?q=donuts") != null;
        this.uU = a(packageManager, "http://www.google.com") != null;
        this.uV = telephonyManager.getNetworkOperator();
        this.uW = locale.getCountry();
        this.uX = gr.ds();
        this.uY = audioManager.isMusicActive();
        this.uZ = audioManager.isSpeakerphoneOn();
        this.va = locale.getLanguage();
        this.vb = a(packageManager);
        this.vc = audioManager.getStreamVolume(3);
        this.vd = a(context, connectivityManager, packageManager);
        this.ve = telephonyManager.getNetworkType();
        this.vf = telephonyManager.getPhoneType();
        this.vg = audioManager.getRingerMode();
        this.vh = audioManager.getStreamVolume(2);
        this.vi = displayMetrics.density;
        this.vj = displayMetrics.widthPixels;
        this.vk = displayMetrics.heightPixels;
        if (intentRegisterReceiver != null) {
            int intExtra = intentRegisterReceiver.getIntExtra("status", -1);
            this.vl = intentRegisterReceiver.getIntExtra("level", -1) / intentRegisterReceiver.getIntExtra("scale", -1);
            if (intExtra != 2 && intExtra != 5) {
                z = false;
            }
            this.vm = z;
        } else {
            this.vl = -1.0d;
            this.vm = false;
        }
        if (Build.VERSION.SDK_INT < 16) {
            this.vn = false;
            this.vo = -1;
            return;
        }
        this.vn = connectivityManager.isActiveNetworkMetered();
        if (connectivityManager.getActiveNetworkInfo() != null) {
            this.vo = connectivityManager.getActiveNetworkInfo().getDetailedState().ordinal();
        } else {
            this.vo = -1;
        }
    }

    private static int a(Context context, ConnectivityManager connectivityManager, PackageManager packageManager) {
        if (!gj.a(packageManager, context.getPackageName(), "android.permission.ACCESS_NETWORK_STATE")) {
            return -2;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.getType();
        }
        return -1;
    }

    private static ResolveInfo a(PackageManager packageManager, String str) {
        return packageManager.resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)), 65536);
    }

    private static String a(PackageManager packageManager) throws PackageManager.NameNotFoundException {
        ActivityInfo activityInfo;
        ResolveInfo resolveInfoA = a(packageManager, "market://details?id=com.google.android.gms.ads");
        if (resolveInfoA == null || (activityInfo = resolveInfoA.activityInfo) == null) {
            return null;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(activityInfo.packageName, 0);
            if (packageInfo != null) {
                return packageInfo.versionCode + "." + activityInfo.packageName;
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
