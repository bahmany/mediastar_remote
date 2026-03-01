package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import com.google.android.gms.internal.eg;
import com.iflytek.cloud.SpeechConstant;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@ez
/* loaded from: classes.dex */
public final class dy extends eg.a {
    private Context mContext;
    private String mv;
    private String su;
    private ArrayList<String> sv;

    public dy(String str, ArrayList<String> arrayList, Context context, String str2) {
        this.su = str;
        this.sv = arrayList;
        this.mv = str2;
        this.mContext = context;
    }

    private void cr() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            this.mContext.getClassLoader().loadClass("com.google.ads.conversiontracking.IAPConversionReporter").getDeclaredMethod("reportWithProductId", Context.class, String.class, String.class, Boolean.TYPE).invoke(null, this.mContext, this.su, "", true);
        } catch (ClassNotFoundException e) {
            gs.W("Google Conversion Tracking SDK 1.2.0 or above is required to report a conversion.");
        } catch (NoSuchMethodException e2) {
            gs.W("Google Conversion Tracking SDK 1.2.0 or above is required to report a conversion.");
        } catch (Exception e3) {
            gs.d("Fail to report a conversion.", e3);
        }
    }

    protected String a(String str, HashMap<String, String> map) {
        String str2;
        String packageName = this.mContext.getPackageName();
        try {
            str2 = this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            gs.d("Error to retrieve app version", e);
            str2 = "";
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() - gb.cZ().di();
        for (String str3 : map.keySet()) {
            str = str.replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", str3), String.format("$1%s$2", map.get(str3)));
        }
        return str.replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "sessionid"), String.format("$1%s$2", gb.vK)).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", SpeechConstant.APPID), String.format("$1%s$2", packageName)).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "osversion"), String.format("$1%s$2", String.valueOf(Build.VERSION.SDK_INT))).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "sdkversion"), String.format("$1%s$2", this.mv)).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "appversion"), String.format("$1%s$2", str2)).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "timestamp"), String.format("$1%s$2", String.valueOf(jElapsedRealtime))).replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "[^@]+"), String.format("$1%s$2", "")).replaceAll("@@", "@");
    }

    @Override // com.google.android.gms.internal.eg
    public String getProductId() {
        return this.su;
    }

    protected int o(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1) {
            return 2;
        }
        return i == 4 ? 3 : 0;
    }

    @Override // com.google.android.gms.internal.eg
    public void recordPlayBillingResolution(int billingResponseCode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (billingResponseCode == 0) {
            cr();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("google_play_status", String.valueOf(billingResponseCode));
        map.put("sku", this.su);
        map.put("status", String.valueOf(o(billingResponseCode)));
        Iterator<String> it = this.sv.iterator();
        while (it.hasNext()) {
            new gq(this.mContext, this.mv, a(it.next(), map)).start();
        }
    }

    @Override // com.google.android.gms.internal.eg
    public void recordResolution(int resolution) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (resolution == 1) {
            cr();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(resolution));
        map.put("sku", this.su);
        Iterator<String> it = this.sv.iterator();
        while (it.hasNext()) {
            new gq(this.mContext, this.mv, a(it.next(), map)).start();
        }
    }
}
