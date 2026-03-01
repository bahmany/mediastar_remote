package com.google.android.gms.internal;

import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.URI;
import java.net.URISyntaxException;
import org.teleal.cling.model.ServiceReference;

@ez
/* loaded from: classes.dex */
public class ha extends WebViewClient {
    private final gv md;
    private final String xc;
    private boolean xd = false;
    private final fc xe;

    public ha(fc fcVar, gv gvVar, String str) {
        this.xc = Z(str);
        this.md = gvVar;
        this.xe = fcVar;
    }

    private String Z(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            return str.endsWith(ServiceReference.DELIMITER) ? str.substring(0, str.length() - 1) : str;
        } catch (IndexOutOfBoundsException e) {
            gs.T(e.getMessage());
            return str;
        }
    }

    protected boolean Y(String str) {
        boolean z = false;
        String strZ = Z(str);
        if (!TextUtils.isEmpty(strZ)) {
            try {
                URI uri = new URI(strZ);
                if ("passback".equals(uri.getScheme())) {
                    gs.S("Passback received");
                    this.xe.cA();
                    z = true;
                } else if (!TextUtils.isEmpty(this.xc)) {
                    URI uri2 = new URI(this.xc);
                    String host = uri2.getHost();
                    String host2 = uri.getHost();
                    String path = uri2.getPath();
                    String path2 = uri.getPath();
                    if (com.google.android.gms.common.internal.m.equal(host, host2) && com.google.android.gms.common.internal.m.equal(path, path2)) {
                        gs.S("Passback received");
                        this.xe.cA();
                        z = true;
                    }
                }
            } catch (URISyntaxException e) {
                gs.T(e.getMessage());
            }
        }
        return z;
    }

    @Override // android.webkit.WebViewClient
    public void onLoadResource(WebView view, String url) {
        gs.S("JavascriptAdWebViewClient::onLoadResource: " + url);
        if (Y(url)) {
            return;
        }
        this.md.dv().onLoadResource(this.md, url);
    }

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView view, String url) {
        gs.S("JavascriptAdWebViewClient::onPageFinished: " + url);
        if (this.xd) {
            return;
        }
        this.xe.cz();
        this.xd = true;
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        gs.S("JavascriptAdWebViewClient::shouldOverrideUrlLoading: " + url);
        if (!Y(url)) {
            return this.md.dv().shouldOverrideUrlLoading(this.md, url);
        }
        gs.S("shouldOverrideUrlLoading: received passback url");
        return true;
    }
}
