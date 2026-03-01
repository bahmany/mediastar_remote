package com.google.android.gms.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@ez
/* loaded from: classes.dex */
public class go {
    public static final a<Void> wy = new a() { // from class: com.google.android.gms.internal.go.1
        @Override // com.google.android.gms.internal.go.a
        /* renamed from: c, reason: merged with bridge method [inline-methods] */
        public Void b(InputStream inputStream) {
            return null;
        }

        @Override // com.google.android.gms.internal.go.a
        /* renamed from: dr, reason: merged with bridge method [inline-methods] */
        public Void cK() {
            return null;
        }
    };

    public interface a<T> {
        T b(InputStream inputStream);

        T cK();
    }

    public <T> Future<T> a(final String str, final a<T> aVar) {
        return gi.submit(new Callable<T>() { // from class: com.google.android.gms.internal.go.2
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r1v0 */
            /* JADX WARN: Type inference failed for: r1v1 */
            /* JADX WARN: Type inference failed for: r1v11 */
            /* JADX WARN: Type inference failed for: r1v12, types: [int] */
            /* JADX WARN: Type inference failed for: r1v16 */
            /* JADX WARN: Type inference failed for: r1v17 */
            /* JADX WARN: Type inference failed for: r1v2, types: [java.net.HttpURLConnection] */
            /* JADX WARN: Type inference failed for: r1v3, types: [java.net.HttpURLConnection] */
            /* JADX WARN: Type inference failed for: r1v4, types: [java.net.HttpURLConnection] */
            /* JADX WARN: Type inference failed for: r1v5 */
            /* JADX WARN: Type inference failed for: r1v7 */
            /* JADX WARN: Type inference failed for: r1v9 */
            @Override // java.util.concurrent.Callable
            public T call() throws Throwable {
                HttpURLConnection httpURLConnection;
                ?? responseCode = 0;
                responseCode = 0;
                responseCode = 0;
                try {
                    try {
                        httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                        try {
                            httpURLConnection.connect();
                            responseCode = httpURLConnection.getResponseCode();
                        } catch (MalformedURLException e) {
                            responseCode = httpURLConnection;
                            e = e;
                            gs.d("Error making HTTP request.", e);
                            if (responseCode != 0) {
                                responseCode.disconnect();
                            }
                            return (T) aVar.cK();
                        } catch (IOException e2) {
                            responseCode = httpURLConnection;
                            e = e2;
                            gs.d("Error making HTTP request.", e);
                            if (responseCode != 0) {
                                responseCode.disconnect();
                            }
                            return (T) aVar.cK();
                        } catch (Throwable th) {
                            responseCode = httpURLConnection;
                            th = th;
                            if (responseCode != 0) {
                                responseCode.disconnect();
                            }
                            throw th;
                        }
                    } catch (MalformedURLException e3) {
                        e = e3;
                    } catch (IOException e4) {
                        e = e4;
                    }
                    if (responseCode < 200 || responseCode > 299) {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        return (T) aVar.cK();
                    }
                    T t = (T) aVar.b(httpURLConnection.getInputStream());
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    return t;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        });
    }
}
