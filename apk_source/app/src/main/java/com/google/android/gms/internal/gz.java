package com.google.android.gms.internal;

import android.view.View;
import android.webkit.WebChromeClient;

@ez
/* loaded from: classes.dex */
public final class gz extends gx {
    public gz(gv gvVar) {
        super(gvVar);
    }

    @Override // android.webkit.WebChromeClient
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback customViewCallback) {
        a(view, requestedOrientation, customViewCallback);
    }
}
