package org.cybergarage.multiscreenhttp;

import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import java.net.URL;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HTTP {
    public static final String CACHE_CONTROL = "CACHE-CONTROL";
    public static final String CALLBACK = "CALLBACK";
    public static final String CONNECTION = "CONNECTION";
    public static final String CONTENT_LENGTH = "CONTENT-LENGTH";
    public static final String CONTENT_TYPE = "CONTENT-TYPE";
    public static final String CRLF = "\r\n";
    public static final String DATE = "DATE";
    public static final int DEFAULT_CHUNK_SIZE = 65536;
    public static final int DEFAULT_PORT = 80;
    public static final String EXT = "EXT";
    public static final String GET = "GET";
    public static final String HEADER_LINE_DELIM = " :";
    public static final String HOST = "HOST";
    public static final String LOCATION = "LOCATION";
    public static final String MAN = "MAN";
    public static final String MAX_AGE = "max-age";
    public static final String MX = "MX";
    public static final String M_SEARCH = "M-SEARCH";
    public static final String NOTIFY = "NOTIFY";
    public static final String NO_CACHE = "no-cache";
    public static final String NT = "NT";
    public static final String NTS = "NTS";
    public static final String POST = "POST";
    public static final String REQEST_LINE_DELIM = " ";
    public static final String SEQ = "SEQ";
    public static final String SERVER = "SERVER";
    public static final String SID = "SID";
    public static final String SOAP_ACTION = "SOAPACTION";
    public static final String ST = "ST";
    public static final String STATUS_LINE_DELIM = " ";
    public static final String SUBSCRIBE = "SUBSCRIBE";
    public static final String TAB = "\t";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
    public static final String USN = "USN";
    public static final String VERSION = "1.0";
    public static final String VERSION_10 = "1.0";
    public static final String VERSION_11 = "1.1";
    private static int chunkSize = 65536;

    public static final boolean isAbsoluteURL(String urlStr) {
        try {
            new URL(urlStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static final String getHost(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return url.getHost();
        } catch (Exception e) {
            return "";
        }
    }

    public static final int getPort(String urlStr) {
        try {
            URL url = new URL(urlStr);
            int port = url.getPort();
            if (port <= 0) {
                return 80;
            }
            return port;
        } catch (Exception e) {
            return 80;
        }
    }

    public static final String getRequestHostURL(String host, int port) {
        String reqHost = "http://" + host + ":" + port;
        return reqHost;
    }

    public static final String toRelativeURL(String urlStr, boolean withParam) {
        if (!isAbsoluteURL(urlStr)) {
            if (urlStr.length() <= 0 || urlStr.charAt(0) == '/') {
                return urlStr;
            }
            return ServiceReference.DELIMITER + urlStr;
        }
        try {
            URL url = new URL(urlStr);
            String uri = url.getPath();
            if (withParam) {
                String queryStr = url.getQuery();
                if (!queryStr.equals("")) {
                    uri = String.valueOf(uri) + "?" + queryStr;
                }
            }
            if (uri.endsWith(ServiceReference.DELIMITER)) {
                return uri.substring(0, uri.length() - 1);
            }
            return uri;
        } catch (Exception e) {
            return urlStr;
        }
    }

    public static final String toRelativeURL(String urlStr) {
        return toRelativeURL(urlStr, true);
    }

    public static final String getAbsoluteURL(String baseURLStr, String relURlStr) {
        try {
            URL baseURL = new URL(baseURLStr);
            return String.valueOf(baseURL.getProtocol()) + HostNetInterface.SEPARATOR_BETWEEN_HEAD_AND_IP + baseURL.getHost() + ":" + baseURL.getPort() + toRelativeURL(relURlStr);
        } catch (Exception e) {
            return "";
        }
    }

    public static final void setChunkSize(int size) {
        chunkSize = size;
    }

    public static final int getChunkSize() {
        return chunkSize;
    }
}
