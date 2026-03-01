package com.voicetechnology.rtspclient.concepts;

import java.net.URISyntaxException;

/* loaded from: classes.dex */
public interface Request extends Message {

    public enum Method {
        OPTIONS,
        DESCRIBE,
        SETUP,
        PLAY,
        RECORD,
        TEARDOWN;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Method[] valuesCustom() {
            Method[] methodArrValuesCustom = values();
            int length = methodArrValuesCustom.length;
            Method[] methodArr = new Method[length];
            System.arraycopy(methodArrValuesCustom, 0, methodArr, 0, length);
            return methodArr;
        }
    }

    Method getMethod();

    String getURI();

    void handleResponse(Client client, Response response);

    void setLine(String str, Method method) throws URISyntaxException;
}
