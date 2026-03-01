package org.teleal.cling.transport.impl.apache;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

/* loaded from: classes.dex */
public class UpnpHttpRequestFactory extends DefaultHttpRequestFactory {
    private static final String[] BASIC = {"SUBSCRIBE", "UNSUBSCRIBE"};
    private static final String[] WITH_ENTITY = {"NOTIFY"};

    private static boolean isOneOf(String[] methods, String method) {
        for (String str : methods) {
            if (str.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.http.impl.DefaultHttpRequestFactory, org.apache.http.HttpRequestFactory
    public HttpRequest newHttpRequest(RequestLine requestline) throws MethodNotSupportedException {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        String method = requestline.getMethod();
        String uri = requestline.getUri();
        return newHttpRequest(method, uri);
    }

    @Override // org.apache.http.impl.DefaultHttpRequestFactory, org.apache.http.HttpRequestFactory
    public HttpRequest newHttpRequest(String method, String uri) throws MethodNotSupportedException {
        if (isOneOf(BASIC, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(WITH_ENTITY, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        return super.newHttpRequest(method, uri);
    }
}
