package com.iflytek.cloud.a.f;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import com.iflytek.cloud.Setting;

/* loaded from: classes.dex */
public class b {
    public static b a = null;
    private SharedPreferences b;
    private Context c;
    private boolean d;
    private long e = 0;

    private b(Context context) {
        this.b = null;
        this.c = null;
        this.d = true;
        this.c = context;
        this.b = context.getSharedPreferences("com.iflytek.msc", 0);
        this.d = b(context);
    }

    public static b a(Context context) {
        if (a == null && context != null) {
            c(context);
        }
        return a;
    }

    public static boolean b(Context context) {
        try {
            if (!Setting.c || context == null) {
                return false;
            }
            String[] strArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
            for (int i = 0; i < strArr.length; i++) {
                if ("android.permission.ACCESS_FINE_LOCATION".equalsIgnoreCase(strArr[i]) || "android.permission.ACCESS_COARSE_LOCATION".equalsIgnoreCase(strArr[i])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static synchronized b c(Context context) {
        if (a == null) {
            a = new b(context);
        }
        e.a(context);
        a.a(context);
        return a;
    }

    public synchronized float a(String str) {
        try {
            if (this.d && System.currentTimeMillis() - this.e > 216000) {
                LocationManager locationManager = (LocationManager) this.c.getApplicationContext().getSystemService("location");
                this.e = System.currentTimeMillis();
                a("loction_last_update", this.e);
                com.iflytek.cloud.a.f.a.a.c("getLocation begin:" + System.currentTimeMillis());
                Criteria criteria = new Criteria();
                criteria.setAccuracy(1);
                com.iflytek.cloud.a.f.a.a.c("bestProvider:" + locationManager.getBestProvider(criteria, true));
                Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
                if (lastKnownLocation != null) {
                    com.iflytek.cloud.a.f.a.a.a(lastKnownLocation.toString());
                    a(lastKnownLocation);
                } else {
                    Location lastKnownLocation2 = locationManager.getLastKnownLocation("network");
                    if (lastKnownLocation2 != null) {
                        com.iflytek.cloud.a.f.a.a.a(lastKnownLocation2.toString());
                        a(lastKnownLocation2);
                    }
                }
                com.iflytek.cloud.a.f.a.a.c("getLocation end:" + System.currentTimeMillis());
            }
        } catch (Exception e) {
        }
        return this.b.getFloat(str, -0.1f);
    }

    public void a(Location location) {
        if (location == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = this.b.edit();
        editorEdit.putFloat("msc.lat", (float) location.getLatitude());
        editorEdit.putFloat("msc.lng", (float) location.getLongitude());
        editorEdit.commit();
    }

    public void a(String str, long j) {
        SharedPreferences.Editor editorEdit = this.b.edit();
        editorEdit.putLong(str, j);
        editorEdit.commit();
    }
}
