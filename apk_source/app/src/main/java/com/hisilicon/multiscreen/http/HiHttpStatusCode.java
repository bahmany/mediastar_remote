package com.hisilicon.multiscreen.http;

/* loaded from: classes.dex */
public class HiHttpStatusCode {
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_FOUND = 404;
    public static final int OK = 200;

    public static final String code2String(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown Code (" + code + ")";
        }
    }
}
