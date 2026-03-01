package org.teleal.cling.model.message;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.teleal.cling.model.ServerClientTokens;

/* loaded from: classes.dex */
public class UpnpRequest extends UpnpOperation {
    private Method method;
    private URI uri;

    public enum Method {
        GET("GET"),
        POST("POST"),
        NOTIFY("NOTIFY"),
        MSEARCH("M-SEARCH"),
        SUBSCRIBE("SUBSCRIBE"),
        UNSUBSCRIBE("UNSUBSCRIBE"),
        UNKNOWN(ServerClientTokens.UNKNOWN_PLACEHOLDER);

        private static Map<String, Method> byName = new HashMap<String, Method>() { // from class: org.teleal.cling.model.message.UpnpRequest.Method.1
            {
                for (Method m : Method.valuesCustom()) {
                    put(m.getHttpName(), m);
                }
            }
        };
        private String httpName;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Method[] valuesCustom() {
            Method[] methodArrValuesCustom = values();
            int length = methodArrValuesCustom.length;
            Method[] methodArr = new Method[length];
            System.arraycopy(methodArrValuesCustom, 0, methodArr, 0, length);
            return methodArr;
        }

        Method(String httpName) {
            this.httpName = httpName;
        }

        public String getHttpName() {
            return this.httpName;
        }

        public static Method getByHttpName(String httpName) {
            Method m;
            return (httpName == null || (m = byName.get(httpName.toUpperCase())) == null) ? UNKNOWN : m;
        }
    }

    public UpnpRequest(Method method) {
        this.method = method;
    }

    public UpnpRequest(Method method, URI uri) {
        this.method = method;
        this.uri = uri;
    }

    public UpnpRequest(Method method, URL url) {
        this.method = method;
        if (url != null) {
            try {
                this.uri = url.toURI();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public Method getMethod() {
        return this.method;
    }

    public String getHttpMethodName() {
        return this.method.getHttpName();
    }

    public URI getURI() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String toString() {
        return String.valueOf(getHttpMethodName()) + (getURI() != null ? " " + getURI() : "");
    }
}
