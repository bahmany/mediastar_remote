package com.google.android.gms.internal;

import android.content.Context;
import android.webkit.WebSettings;

@ez
/* loaded from: classes.dex */
public final class gp {
    public static void a(Context context, WebSettings webSettings) {
        gn.a(context, webSettings);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
    }

    public static String getDefaultUserAgent(Context context) {
        return WebSettings.getDefaultUserAgent(context);
    }
}
