package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import org.cybergarage.http.HTTP;
import org.cybergarage.xml.XML;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HttpServer {
    public static final String HTTP_BADREQUEST = "400 Bad Request";
    public static final String HTTP_FORBIDDEN = "403 Forbidden";
    public static final String HTTP_INTERNALERROR = "500 Internal Server Error";
    public static final String HTTP_NOTFOUND = "404 Not Found";
    public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";
    public static final String HTTP_OK = "200 OK";
    public static final String HTTP_PARTIALCONTENT = "206 Partial Content";
    public static final String HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable";
    public static final String HTTP_REDIRECT = "301 Moved Permanently";
    private static final String LICENCE = "Copyright (C) 2001,2005-2011 by Jarno Elonen <elonen@iki.fi>\nand Copyright (C) 2010 by Konstantinos Togias <info@ktogias.gr>\n\nRedistribution and use in source and binary forms, with or without\nmodification, are permitted provided that the following conditions\nare met:\n\nRedistributions of source code must retain the above copyright notice,\nthis list of conditions and the following disclaimer. Redistributions in\nbinary form must reproduce the above copyright notice, this list of\nconditions and the following disclaimer in the documentation and/or other\nmaterials provided with the distribution. The name of the author may not\nbe used to endorse or promote products derived from this software without\nspecific prior written permission. \n \nTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\nIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\nOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\nINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\nNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\nDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\nTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\nOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_XML = "text/xml";
    private static SimpleDateFormat gmtFrmt;
    private static Hashtable theMimeTypes = new Hashtable();
    private final ServerSocket myServerSocket;
    private int myTcpPort;
    private File myRootDir = new File(ServiceReference.DELIMITER);
    private Thread myThread = new Thread(new Runnable() { // from class: com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer.1
        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    HttpServer.this.new HTTPSession(HttpServer.this.myServerSocket.accept());
                } catch (IOException e) {
                    return;
                }
            }
        }
    });

    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        return serveFile(uri, method, header, this.myRootDir, false);
    }

    public class Response {
        public InputStream data;
        public Properties header;
        public String mimeType;
        public String status;

        public Response() {
            this.header = new Properties();
            this.status = HttpServer.HTTP_OK;
        }

        public Response(String status, String mimeType) {
            this.header = new Properties();
            this.status = status;
            this.mimeType = mimeType;
        }

        public Response(String status, String mimeType, InputStream data) {
            this.header = new Properties();
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
        }

        public Response(String status, String mimeType, String txt) {
            this.header = new Properties();
            this.status = status;
            this.mimeType = mimeType;
            try {
                this.data = new ByteArrayInputStream(txt.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }

        public void addHeader(String name, String value) {
            this.header.put(name, value);
        }
    }

    public HttpServer(int port) throws IOException {
        this.myTcpPort = port;
        this.myServerSocket = new ServerSocket(this.myTcpPort);
        this.myThread.setDaemon(true);
        this.myThread.start();
    }

    public void stop() throws InterruptedException, IOException {
        try {
            this.myServerSocket.close();
            this.myThread.join();
        } catch (IOException e) {
        } catch (InterruptedException e2) {
        }
    }

    private class HTTPSession implements Runnable {
        private Socket mySocket;

        public HTTPSession(Socket s) {
            this.mySocket = s;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        @Override // java.lang.Runnable
        public void run() throws IOException {
            try {
                InputStream is = this.mySocket.getInputStream();
                if (is != null) {
                    byte[] buf = new byte[8192];
                    int rlen = is.read(buf, 0, 8192);
                    if (rlen > 0) {
                        ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
                        BufferedReader hin = new BufferedReader(new InputStreamReader(hbis));
                        Properties pre = new Properties();
                        Properties parms = new Properties();
                        Properties header = new Properties();
                        Properties files = new Properties();
                        decodeHeader(hin, pre, parms, header);
                        String method = pre.getProperty("method");
                        String uri = pre.getProperty("uri");
                        if (method.equalsIgnoreCase("POST")) {
                            long size = Long.MAX_VALUE;
                            String contentLength = header.getProperty("content-length");
                            if (contentLength != null) {
                                try {
                                    size = Integer.parseInt(contentLength);
                                } catch (NumberFormatException e) {
                                }
                            }
                            int splitbyte = 0;
                            boolean sbfound = false;
                            while (true) {
                                if (splitbyte >= rlen) {
                                    break;
                                }
                                if (buf[splitbyte] == 13) {
                                    splitbyte++;
                                    if (buf[splitbyte] == 10) {
                                        splitbyte++;
                                        if (buf[splitbyte] == 13) {
                                            splitbyte++;
                                            if (buf[splitbyte] == 10) {
                                                sbfound = true;
                                                break;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                }
                                splitbyte++;
                            }
                            int splitbyte2 = splitbyte + 1;
                            ByteArrayOutputStream f = new ByteArrayOutputStream();
                            if (splitbyte2 < rlen) {
                                f.write(buf, splitbyte2, rlen - splitbyte2);
                            }
                            if (splitbyte2 < rlen) {
                                size -= (rlen - splitbyte2) + 1;
                            } else if (!sbfound || size == Long.MAX_VALUE) {
                                size = 0;
                            }
                            byte[] buf2 = new byte[512];
                            while (rlen >= 0 && size > 0) {
                                rlen = is.read(buf2, 0, 512);
                                size -= rlen;
                                if (rlen > 0) {
                                    f.write(buf2, 0, rlen);
                                }
                            }
                            byte[] fbuf = f.toByteArray();
                            ByteArrayInputStream bin = new ByteArrayInputStream(fbuf);
                            BufferedReader in = new BufferedReader(new InputStreamReader(bin));
                            String contentType = "";
                            String contentTypeHeader = header.getProperty("content-type");
                            StringTokenizer st = new StringTokenizer(contentTypeHeader, "; ");
                            if (st.hasMoreTokens()) {
                                contentType = st.nextToken();
                            }
                            if (contentType.equalsIgnoreCase("multipart/form-data")) {
                                if (!st.hasMoreTokens()) {
                                    sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                                }
                                String boundaryExp = st.nextToken();
                                StringTokenizer st2 = new StringTokenizer(boundaryExp, "=");
                                if (st2.countTokens() != 2) {
                                    sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary syntax error. Usage: GET /example/file.html");
                                }
                                st2.nextToken();
                                String boundary = st2.nextToken();
                                decodeMultipartData(boundary, fbuf, in, parms, files);
                            } else {
                                String postLine = "";
                                char[] pbuf = new char[512];
                                for (int read = in.read(pbuf); read >= 0 && !postLine.endsWith("\r\n"); read = in.read(pbuf)) {
                                    postLine = String.valueOf(postLine) + String.valueOf(pbuf, 0, read);
                                }
                                decodeParms(postLine.trim(), parms);
                            }
                            in.close();
                        }
                        Response r = HttpServer.this.serve(uri, method, header, parms, files);
                        if (r == null) {
                            sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                        } else {
                            sendResponse(r.status, r.mimeType, r.header, r.data);
                        }
                        is.close();
                    }
                }
            } catch (IOException ioe) {
                try {
                    sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                } catch (Throwable th) {
                }
            } catch (InterruptedException e2) {
            }
        }

        private void decodeHeader(BufferedReader in, Properties pre, Properties parms, Properties header) throws InterruptedException, IOException {
            String uri;
            try {
                String inLine = in.readLine();
                if (inLine != null) {
                    StringTokenizer st = new StringTokenizer(inLine);
                    if (!st.hasMoreTokens()) {
                        sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                    }
                    String method = st.nextToken();
                    pre.put("method", method);
                    if (!st.hasMoreTokens()) {
                        sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                    }
                    String uri2 = URLDecoder.decode(st.nextToken(), XML.CHARSET_UTF8);
                    int qmi = uri2.indexOf(63);
                    if (qmi >= 0) {
                        decodeParms(uri2.substring(qmi + 1), parms);
                        uri = decodePercent(uri2.substring(0, qmi));
                    } else {
                        uri = decodePercent(uri2);
                    }
                    if (st.hasMoreTokens()) {
                        String line = in.readLine();
                        while (line != null && line.trim().length() > 0) {
                            int p = line.indexOf(58);
                            if (p >= 0) {
                                header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                            }
                            line = in.readLine();
                        }
                    }
                    pre.put("uri", uri);
                }
            } catch (IOException ioe) {
                sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }
        }

        private void decodeMultipartData(String boundary, byte[] fbuf, BufferedReader in, Properties parms, Properties files) throws InterruptedException, IOException {
            try {
                int[] bpositions = getBoundaryPositions(fbuf, boundary.getBytes());
                int boundarycount = 1;
                String mpline = in.readLine();
                while (mpline != null) {
                    if (mpline.indexOf(boundary) == -1) {
                        sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                    }
                    boundarycount++;
                    Properties item = new Properties();
                    mpline = in.readLine();
                    while (mpline != null && mpline.trim().length() > 0) {
                        int p = mpline.indexOf(58);
                        if (p != -1) {
                            item.put(mpline.substring(0, p).trim().toLowerCase(), mpline.substring(p + 1).trim());
                        }
                        mpline = in.readLine();
                    }
                    if (mpline != null) {
                        String contentDisposition = item.getProperty("content-disposition");
                        if (contentDisposition == null) {
                            sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                        }
                        StringTokenizer st = new StringTokenizer(contentDisposition, "; ");
                        Properties disposition = new Properties();
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            int p2 = token.indexOf(61);
                            if (p2 != -1) {
                                disposition.put(token.substring(0, p2).trim().toLowerCase(), token.substring(p2 + 1).trim());
                            }
                        }
                        String pname = disposition.getProperty("name");
                        String pname2 = pname.substring(1, pname.length() - 1);
                        String value = "";
                        if (item.getProperty("content-type") == null) {
                            while (mpline != null && mpline.indexOf(boundary) == -1) {
                                mpline = in.readLine();
                                if (mpline != null) {
                                    int d = mpline.indexOf(boundary);
                                    if (d == -1) {
                                        value = String.valueOf(value) + mpline;
                                    } else {
                                        value = String.valueOf(value) + mpline.substring(0, d - 2);
                                    }
                                }
                            }
                        } else {
                            if (boundarycount > bpositions.length) {
                                sendError(HttpServer.HTTP_INTERNALERROR, "Error processing request");
                            }
                            int offset = stripMultipartHeaders(fbuf, bpositions[boundarycount - 2]);
                            String path = saveTmpFile(fbuf, offset, (bpositions[boundarycount - 1] - offset) - 4);
                            files.put(pname2, path);
                            String value2 = disposition.getProperty("filename");
                            value = value2.substring(1, value2.length() - 1);
                            do {
                                mpline = in.readLine();
                                if (mpline == null) {
                                    break;
                                }
                            } while (mpline.indexOf(boundary) == -1);
                        }
                        parms.put(pname2, value);
                    }
                }
            } catch (IOException ioe) {
                sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }
        }

        public int[] getBoundaryPositions(byte[] b, byte[] boundary) {
            int matchcount = 0;
            int matchbyte = -1;
            Vector matchbytes = new Vector();
            int i = 0;
            while (i < b.length) {
                if (b[i] == boundary[matchcount]) {
                    if (matchcount == 0) {
                        matchbyte = i;
                    }
                    matchcount++;
                    if (matchcount == boundary.length) {
                        matchbytes.addElement(new Integer(matchbyte));
                        matchcount = 0;
                        matchbyte = -1;
                    }
                } else {
                    i -= matchcount;
                    matchcount = 0;
                    matchbyte = -1;
                }
                i++;
            }
            int[] ret = new int[matchbytes.size()];
            for (int i2 = 0; i2 < ret.length; i2++) {
                ret[i2] = ((Integer) matchbytes.elementAt(i2)).intValue();
            }
            return ret;
        }

        private String saveTmpFile(byte[] b, int offset, int len) throws IOException {
            if (len <= 0) {
                return "";
            }
            String tmpdir = System.getProperty("java.io.tmpdir");
            try {
                File temp = File.createTempFile("NanoHTTPD", "", new File(tmpdir));
                OutputStream fstream = new FileOutputStream(temp);
                fstream.write(b, offset, len);
                fstream.close();
                String path = temp.getAbsolutePath();
                return path;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return "";
            }
        }

        private int stripMultipartHeaders(byte[] b, int offset) {
            int i = offset;
            while (i < b.length) {
                if (b[i] == 13) {
                    i++;
                    if (b[i] == 10) {
                        i++;
                        if (b[i] == 13) {
                            i++;
                            if (b[i] == 10) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                i++;
            }
            return i + 1;
        }

        private String decodePercent(String str) throws InterruptedException, IOException {
            try {
                StringBuffer sb = new StringBuffer();
                int i = 0;
                while (i < str.length()) {
                    char c = str.charAt(i);
                    switch (c) {
                        case '%':
                            sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                            i += 2;
                            break;
                        case '+':
                            sb.append(' ');
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    i++;
                }
                return sb.toString();
            } catch (Exception e) {
                sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
                return null;
            }
        }

        private void decodeParms(String parms, Properties p) throws InterruptedException {
            if (parms != null) {
                StringTokenizer st = new StringTokenizer(parms, "&");
                while (st.hasMoreTokens()) {
                    String e = st.nextToken();
                    int sep = e.indexOf(61);
                    if (sep >= 0) {
                        p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
                    }
                }
            }
        }

        private void sendError(String status, String msg) throws InterruptedException, IOException {
            sendResponse(status, HttpServer.MIME_PLAINTEXT, null, new ByteArrayInputStream(msg.getBytes()));
            throw new InterruptedException();
        }

        private void sendResponse(String status, String mime, Properties header, InputStream data) throws IOException {
            try {
                if (status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                OutputStream out = this.mySocket.getOutputStream();
                PrintWriter pw = new PrintWriter(out);
                pw.print("HTTP/1.1 " + status + " \r\n");
                if (mime != null) {
                    pw.print("Content-Type: " + mime + "\r\n");
                }
                if (header == null || header.getProperty(HTTP.DATE) == null) {
                    pw.print("Date: " + HttpServer.gmtFrmt.format(new Date()) + "\r\n");
                }
                if (header != null) {
                    Enumeration e = header.keys();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = header.getProperty(key);
                        pw.print(String.valueOf(key) + ": " + value + "\r\n");
                    }
                }
                pw.print("\r\n");
                pw.flush();
                if (data != null) {
                    int pending = data.available();
                    byte[] buff = new byte[2048];
                    while (pending > 0) {
                        int read = data.read(buff, 0, pending > 2048 ? 2048 : pending);
                        if (read <= 0) {
                            break;
                        }
                        out.write(buff, 0, read);
                        pending -= read;
                    }
                }
                out.flush();
                out.close();
                if (data != null) {
                    data.close();
                }
            } catch (IOException e2) {
                try {
                    this.mySocket.close();
                } catch (Throwable th) {
                }
            }
        }
    }

    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals(ServiceReference.DELIMITER)) {
                newUri = String.valueOf(newUri) + ServiceReference.DELIMITER;
            } else if (tok.equals(" ")) {
                newUri = String.valueOf(newUri) + "%20";
            } else {
                newUri = String.valueOf(newUri) + URLEncoder.encode(tok);
            }
        }
        return newUri;
    }

    /* JADX WARN: Removed duplicated region for block: B:117:0x05bd A[PHI: r28
  0x05bd: PHI (r28v4 'res' com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer$Response) = 
  (r28v3 'res' com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer$Response)
  (r28v3 'res' com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer$Response)
  (r28v14 'res' com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer$Response)
 binds: [B:21:0x00ac, B:23:0x00b2, B:27:0x0118] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer.Response serveFile(java.lang.String r41, java.lang.String r42, java.util.Properties r43, java.io.File r44, boolean r45) throws java.lang.NumberFormatException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 1473
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer.serveFile(java.lang.String, java.lang.String, java.util.Properties, java.io.File, boolean):com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer$Response");
    }

    static {
        StringTokenizer st = new StringTokenizer("css\t\ttext/css js\t\t\ttext/javascript htm\t\ttext/html html\t\ttext/html txt\t\ttext/plain asc\t\ttext/plain pdf\t\tapplication/pdf doc\t\tapplication/msword ogg\t\tapplication/x-ogg zip\t\tapplication/octet-stream exe\t\tapplication/octet-stream class\t\tapplication/octet-stream gif\t\timage/gif jpg\t\timage/jpeg jpeg\t\timage/jpeg png\t\timage/png bmp\t\timage/bmp mp3\t\taudio/mpeg m3u\t\taudio/mpeg-url m4a\t\taudio/mp4 aac\t\taudio/aac flac\t\taudio/x-flac wma\t\taudio/x-ms-wma amr\t\taudio/amr wav\t\taudio/wav ogg\t\taudio/x-ogg mka\t\taudio/x-matroska aif\t\taudio/x-aiff mp4\t\tvideo/mp4 mpeg\t\tvideo/mpeg 3gp\t\tvideo/3gpp 3gpp\t\tvideo/3gpp 3g2\t\tvideo/3gpp2 3gpp2\t\tvideo/3gpp2 wmv\t\tvideo/x-ms-wmv avi\t\tvideo/vnd.avi mkv\t\tvideo/x-matroska m2ts\t\tvideo/mpeg ts\t\t    video/mpeg mov\t\tvideo/quicktime flv\t\tvideo/x-msvideo rmvb\t\tvideo/rmvb m4v\t\taudio/mp4 ");
        while (st.hasMoreTokens()) {
            theMimeTypes.put(st.nextToken(), st.nextToken());
        }
        gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}
