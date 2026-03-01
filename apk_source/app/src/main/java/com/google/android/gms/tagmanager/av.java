package com.google.android.gms.tagmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import mktvsmart.screen.GlobalConstantValue;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/* loaded from: classes.dex */
class av implements bm {
    private HttpClient apj;

    av() {
    }

    private InputStream a(HttpClient httpClient, HttpResponse httpResponse) throws IOException {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            bh.V("Success response");
            return httpResponse.getEntity().getContent();
        }
        String str = "Bad response: " + statusCode;
        if (statusCode == 404) {
            throw new FileNotFoundException(str);
        }
        throw new IOException(str);
    }

    private void a(HttpClient httpClient) {
        if (httpClient == null || httpClient.getConnectionManager() == null) {
            return;
        }
        httpClient.getConnectionManager().shutdown();
    }

    @Override // com.google.android.gms.tagmanager.bm
    public InputStream cA(String str) throws IOException {
        this.apj = ot();
        return a(this.apj, this.apj.execute(new HttpGet(str)));
    }

    @Override // com.google.android.gms.tagmanager.bm
    public void close() {
        a(this.apj);
    }

    HttpClient ot() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        HttpConnectionParams.setSoTimeout(basicHttpParams, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        return new DefaultHttpClient(basicHttpParams);
    }
}
