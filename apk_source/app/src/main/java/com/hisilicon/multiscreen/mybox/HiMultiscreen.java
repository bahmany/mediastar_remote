package com.hisilicon.multiscreen.mybox;

import android.app.Application;
import android.content.ContentResolver;

/* loaded from: classes.dex */
public class HiMultiscreen extends Application {
    private static String STBIP = "";
    private static Application app;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        initialize(this);
    }

    public static Application getApplication() {
        return app;
    }

    public static void initialize(Application app2) {
        app = app2;
    }

    public static ContentResolver getResolver() {
        if (app != null) {
            return app.getContentResolver();
        }
        return null;
    }

    public static String getSTBIP() {
        return STBIP;
    }

    public static void setSTBIP(String nowSTBIP) {
        STBIP = nowSTBIP;
    }
}
