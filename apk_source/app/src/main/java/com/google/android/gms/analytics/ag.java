package com.google.android.gms.analytics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

/* loaded from: classes.dex */
class ag implements m {
    private final HttpClient Bj;
    private URL Bk;
    private final Context mContext;
    private final String vW;
    private GoogleAnalytics yu;

    ag(HttpClient httpClient, Context context) {
        this(httpClient, GoogleAnalytics.getInstance(context), context);
    }

    ag(HttpClient httpClient, GoogleAnalytics googleAnalytics, Context context) {
        this.mContext = context.getApplicationContext();
        this.vW = a("GoogleAnalytics", "3.0", Build.VERSION.RELEASE, aj.a(Locale.getDefault()), Build.MODEL, Build.ID);
        this.Bj = httpClient;
        this.yu = googleAnalytics;
    }

    private void a(aa aaVar, URL url, boolean z) throws IllegalStateException, IOException {
        URL url2;
        if (TextUtils.isEmpty(aaVar.eM()) || !eT()) {
            return;
        }
        if (url == null) {
            try {
                url2 = this.Bk != null ? this.Bk : new URL("https://ssl.google-analytics.com/collect");
            } catch (MalformedURLException e) {
                return;
            }
        } else {
            url2 = url;
        }
        HttpHost httpHost = new HttpHost(url2.getHost(), url2.getPort(), url2.getProtocol());
        try {
            HttpEntityEnclosingRequest httpEntityEnclosingRequestH = h(aaVar.eM(), url2.getPath());
            if (httpEntityEnclosingRequestH != null) {
                httpEntityEnclosingRequestH.addHeader("Host", httpHost.toHostString());
                if (z.eL()) {
                    a(httpEntityEnclosingRequestH);
                }
                if (z) {
                    p.A(this.mContext);
                }
                HttpResponse httpResponseExecute = this.Bj.execute(httpHost, httpEntityEnclosingRequestH);
                int statusCode = httpResponseExecute.getStatusLine().getStatusCode();
                HttpEntity entity = httpResponseExecute.getEntity();
                if (entity != null) {
                    entity.consumeContent();
                }
                if (statusCode != 200) {
                    z.W("Bad response: " + httpResponseExecute.getStatusLine().getStatusCode());
                }
            }
        } catch (ClientProtocolException e2) {
            z.W("ClientProtocolException sending monitoring hit.");
        } catch (IOException e3) {
            z.W("Exception sending monitoring hit: " + e3.getClass().getSimpleName());
            z.W(e3.getMessage());
        }
    }

    private void a(HttpEntityEnclosingRequest httpEntityEnclosingRequest) throws IllegalStateException, IOException {
        int iAvailable;
        StringBuffer stringBuffer = new StringBuffer();
        for (Header header : httpEntityEnclosingRequest.getAllHeaders()) {
            stringBuffer.append(header.toString()).append("\n");
        }
        stringBuffer.append(httpEntityEnclosingRequest.getRequestLine().toString()).append("\n");
        if (httpEntityEnclosingRequest.getEntity() != null) {
            try {
                InputStream content = httpEntityEnclosingRequest.getEntity().getContent();
                if (content != null && (iAvailable = content.available()) > 0) {
                    byte[] bArr = new byte[iAvailable];
                    content.read(bArr);
                    stringBuffer.append("POST:\n");
                    stringBuffer.append(new String(bArr)).append("\n");
                }
            } catch (IOException e) {
                z.V("Error Writing hit to log...");
            }
        }
        z.V(stringBuffer.toString());
    }

    private HttpEntityEnclosingRequest h(String str, String str2) {
        BasicHttpEntityEnclosingRequest basicHttpEntityEnclosingRequest;
        if (TextUtils.isEmpty(str)) {
            z.W("Empty hit, discarding.");
            return null;
        }
        String str3 = str2 + "?" + str;
        if (str3.length() < 2036) {
            basicHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest("GET", str3);
        } else {
            basicHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest("POST", str2);
            try {
                basicHttpEntityEnclosingRequest.setEntity(new StringEntity(str));
            } catch (UnsupportedEncodingException e) {
                z.W("Encoding error, discarding hit");
                return null;
            }
        }
        basicHttpEntityEnclosingRequest.addHeader("User-Agent", this.vW);
        return basicHttpEntityEnclosingRequest;
    }

    @Override // com.google.android.gms.analytics.m
    public int a(List<w> list, aa aaVar, boolean z) throws IllegalStateException, IOException {
        int i;
        URL url;
        int i2 = 0;
        int iMin = Math.min(list.size(), 40);
        aaVar.e("_hr", list.size());
        int i3 = 0;
        URL url2 = null;
        boolean z2 = true;
        int i4 = 0;
        while (i4 < iMin) {
            w wVar = list.get(i4);
            URL urlA = a(wVar);
            if (urlA == null) {
                if (z.eL()) {
                    z.W("No destination: discarding hit: " + wVar.eG());
                } else {
                    z.W("No destination: discarding hit.");
                }
                i3++;
                URL url3 = url2;
                i = i2 + 1;
                url = url3;
            } else {
                HttpHost httpHost = new HttpHost(urlA.getHost(), urlA.getPort(), urlA.getProtocol());
                String path = urlA.getPath();
                String strA = TextUtils.isEmpty(wVar.eG()) ? "" : x.a(wVar, System.currentTimeMillis());
                HttpEntityEnclosingRequest httpEntityEnclosingRequestH = h(strA, path);
                if (httpEntityEnclosingRequestH == null) {
                    i3++;
                    i = i2 + 1;
                    url = urlA;
                } else {
                    httpEntityEnclosingRequestH.addHeader("Host", httpHost.toHostString());
                    if (z.eL()) {
                        a(httpEntityEnclosingRequestH);
                    }
                    if (strA.length() > 8192) {
                        z.W("Hit too long (> 8192 bytes)--not sent");
                        i3++;
                    } else if (this.yu.isDryRunEnabled()) {
                        z.U("Dry run enabled. Hit not actually sent.");
                    } else {
                        if (z2) {
                            try {
                                p.A(this.mContext);
                                z2 = false;
                            } catch (ClientProtocolException e) {
                                z.W("ClientProtocolException sending hit; discarding hit...");
                                aaVar.e("_hd", i3);
                            } catch (IOException e2) {
                                z.W("Exception sending hit: " + e2.getClass().getSimpleName());
                                z.W(e2.getMessage());
                                aaVar.e("_de", 1);
                                aaVar.e("_hd", i3);
                                aaVar.e("_hs", i2);
                                a(aaVar, urlA, z2);
                                return i2;
                            }
                        }
                        HttpResponse httpResponseExecute = this.Bj.execute(httpHost, httpEntityEnclosingRequestH);
                        int statusCode = httpResponseExecute.getStatusLine().getStatusCode();
                        HttpEntity entity = httpResponseExecute.getEntity();
                        if (entity != null) {
                            entity.consumeContent();
                        }
                        if (statusCode != 200) {
                            z.W("Bad response: " + httpResponseExecute.getStatusLine().getStatusCode());
                        }
                    }
                    aaVar.e("_td", strA.getBytes().length);
                    i = i2 + 1;
                    url = urlA;
                }
            }
            i4++;
            i2 = i;
            url2 = url;
        }
        aaVar.e("_hd", i3);
        aaVar.e("_hs", i2);
        if (z) {
            a(aaVar, url2, z2);
        }
        return i2;
    }

    String a(String str, String str2, String str3, String str4, String str5, String str6) {
        return String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", str, str2, str3, str4, str5, str6);
    }

    URL a(w wVar) {
        if (this.Bk != null) {
            return this.Bk;
        }
        try {
            return new URL("http:".equals(wVar.eJ()) ? "http://www.google-analytics.com/collect" : "https://ssl.google-analytics.com/collect");
        } catch (MalformedURLException e) {
            z.T("Error trying to parse the hardcoded host url. This really shouldn't happen.");
            return null;
        }
    }

    @Override // com.google.android.gms.analytics.m
    public void af(String str) {
        try {
            this.Bk = new URL(str);
        } catch (MalformedURLException e) {
            this.Bk = null;
        }
    }

    @Override // com.google.android.gms.analytics.m
    public boolean dY() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        z.V("...no network connectivity");
        return false;
    }

    boolean eT() {
        return Math.random() * 100.0d <= 1.0d;
    }
}
