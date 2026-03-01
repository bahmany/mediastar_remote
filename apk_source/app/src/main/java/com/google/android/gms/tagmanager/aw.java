package com.google.android.gms.tagmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import mktvsmart.screen.GlobalConstantValue;

/* loaded from: classes.dex */
class aw implements bm {
    private HttpURLConnection apk;

    aw() {
    }

    private InputStream a(HttpURLConnection httpURLConnection) throws IOException {
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == 200) {
            return httpURLConnection.getInputStream();
        }
        String str = "Bad response: " + responseCode;
        if (responseCode == 404) {
            throw new FileNotFoundException(str);
        }
        throw new IOException(str);
    }

    private void b(HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
    }

    @Override // com.google.android.gms.tagmanager.bm
    public InputStream cA(String str) throws IOException {
        this.apk = cB(str);
        return a(this.apk);
    }

    HttpURLConnection cB(String str) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setReadTimeout(GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        httpURLConnection.setConnectTimeout(GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        return httpURLConnection;
    }

    @Override // com.google.android.gms.tagmanager.bm
    public void close() {
        b(this.apk);
    }
}
