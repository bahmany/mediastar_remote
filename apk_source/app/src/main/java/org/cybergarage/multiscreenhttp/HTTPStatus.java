package org.cybergarage.multiscreenhttp;

import java.util.StringTokenizer;
import org.cybergarage.multiscreenutil.Debug;

/* loaded from: classes.dex */
public class HTTPStatus {
    public static final int ACCEPTED = 202;
    public static final int BAD_GATEWAY = 502;
    public static final int BAD_REQUEST = 400;
    public static final int CONFLICT = 409;
    public static final int CONTINUE = 100;
    public static final int CREATED = 201;
    public static final int EXPECTATION_FAILED = 417;
    public static final int FORBIDDEN = 403;
    public static final int GATEWAY_TIMEOUT = 504;
    public static final int GONE = 410;
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int LENGTH_REQUIRED = 411;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int NOT_FOUND = 404;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int NO_CONTENT = 204;
    public static final int OK = 200;
    public static final int PARTIAL_CONTENT = 206;
    public static final int PAYMENT_REQUIRED = 402;
    public static final int PRECONDITION_FAILED = 412;
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int REQUEST_URI_TOO_LONG = 414;
    public static final int RESET_CONTENT = 205;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int SWITCHING_PROTOCOLS = 101;
    public static final int UNAUTHORIZED = 401;
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    private String version = "";
    private int statusCode = 0;
    private String reasonPhrase = "";

    public static final String code2String(int code) {
        switch (code) {
            case 100:
                return "Continue";
            case 101:
                return "Switching Protocols";
            case 200:
                return "OK";
            case CREATED /* 201 */:
                return "Created";
            case ACCEPTED /* 202 */:
                return "Accepted";
            case NON_AUTHORITATIVE_INFORMATION /* 203 */:
                return "Non Authoritative Information";
            case NO_CONTENT /* 204 */:
                return "No Content";
            case RESET_CONTENT /* 205 */:
                return "Reset Content";
            case 206:
                return "Partial Content";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 402:
                return "Payment Required";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 406:
                return "Not Acceptable";
            case PROXY_AUTHENTICATION_REQUIRED /* 407 */:
                return "Proxy Authentication Required";
            case REQUEST_TIMEOUT /* 408 */:
                return "Request Timeout";
            case 409:
                return "Conflict";
            case 410:
                return "Gone";
            case 411:
                return "Length Required";
            case 412:
                return "Precondition Failed";
            case 413:
                return "Request Entity Too Large";
            case REQUEST_URI_TOO_LONG /* 414 */:
                return "Request URI Too Long";
            case UNSUPPORTED_MEDIA_TYPE /* 415 */:
                return "Unsupported Media Type";
            case 416:
                return "Requested Range Not Satisfiable";
            case EXPECTATION_FAILED /* 417 */:
                return "Expectation Failed";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            case BAD_GATEWAY /* 502 */:
                return "Bad Gateway";
            case SERVICE_UNAVAILABLE /* 503 */:
                return "Service Unavailable";
            case GATEWAY_TIMEOUT /* 504 */:
                return "Gateway Timeout";
            case HTTP_VERSION_NOT_SUPPORTED /* 505 */:
                return "HTTP Version Not Supported";
            default:
                return "Unknown Code (" + code + ")";
        }
    }

    public HTTPStatus() {
        setVersion("");
        setStatusCode(0);
        setReasonPhrase("");
    }

    public HTTPStatus(String ver, int code, String reason) {
        setVersion(ver);
        setStatusCode(code);
        setReasonPhrase(reason);
    }

    public HTTPStatus(String lineStr) throws NumberFormatException {
        set(lineStr);
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public void setStatusCode(int value) {
        this.statusCode = value;
    }

    public void setReasonPhrase(String value) {
        this.reasonPhrase = value;
    }

    public String getVersion() {
        return this.version;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public void set(String lineStr) throws NumberFormatException {
        if (lineStr == null) {
            setVersion("1.0");
            setStatusCode(500);
            setReasonPhrase(code2String(500));
            return;
        }
        try {
            StringTokenizer st = new StringTokenizer(lineStr, " ");
            if (st.hasMoreTokens()) {
                String ver = st.nextToken();
                if (st.hasMoreTokens()) {
                    String codeStr = st.nextToken();
                    int code = 0;
                    try {
                        code = Integer.parseInt(codeStr);
                    } catch (Exception e) {
                    }
                    if (st.hasMoreTokens()) {
                        String reason = st.nextToken();
                        setVersion(ver.trim());
                        setStatusCode(code);
                        setReasonPhrase(reason.trim());
                    }
                }
            }
        } catch (Exception e2) {
            Debug.message("set Exception");
        }
    }
}
