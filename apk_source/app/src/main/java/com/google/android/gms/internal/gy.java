package com.google.android.gms.internal;

import android.content.Context;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.cybergarage.http.HTTP;

@ez
/* loaded from: classes.dex */
public class gy extends gw {
    public gy(gv gvVar, boolean z) {
        super(gvVar, z);
    }

    protected WebResourceResponse d(Context context, String str, String str2) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str2).openConnection();
        try {
            gj.a(context, str, true, httpURLConnection, true);
            httpURLConnection.addRequestProperty(HTTP.CACHE_CONTROL, "max-stale=3600");
            httpURLConnection.connect();
            return new WebResourceResponse("application/javascript", "UTF-8", new ByteArrayInputStream(gj.a(new InputStreamReader(httpURLConnection.getInputStream())).getBytes("UTF-8")));
        } finally {
            httpURLConnection.disconnect();
        }
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
        WebResourceResponse webResourceResponseD;
        try {
            if (!"mraid.js".equalsIgnoreCase(new File(url).getName())) {
                webResourceResponseD = super.shouldInterceptRequest(webView, url);
            } else if (webView instanceof gv) {
                gv gvVar = (gv) webView;
                gvVar.dv().bY();
                if (gvVar.Y().og) {
                    gs.V("shouldInterceptRequest(https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_interstitial.js)");
                    webResourceResponseD = d(gvVar.getContext(), this.md.dy().wD, "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_interstitial.js");
                } else if (gvVar.dz()) {
                    gs.V("shouldInterceptRequest(https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_expanded_banner.js)");
                    webResourceResponseD = d(gvVar.getContext(), this.md.dy().wD, "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_expanded_banner.js");
                } else {
                    gs.V("shouldInterceptRequest(https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_banner.js)");
                    webResourceResponseD = d(gvVar.getContext(), this.md.dy().wD, "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_banner.js");
                }
            } else {
                gs.W("Tried to intercept request from a WebView that wasn't an AdWebView.");
                webResourceResponseD = super.shouldInterceptRequest(webView, url);
            }
            return webResourceResponseD;
        } catch (IOException e) {
            gs.W("Could not fetch MRAID JS. " + e.getMessage());
            return super.shouldInterceptRequest(webView, url);
        }
    }
}
