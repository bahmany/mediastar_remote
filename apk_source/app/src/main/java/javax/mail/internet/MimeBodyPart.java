package javax.mail.internet;

import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.FolderClosedIOException;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.MessageRemovedIOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.HeaderTokenizer;
import org.cybergarage.http.HTTP;

/* loaded from: classes.dex */
public class MimeBodyPart extends BodyPart implements MimePart {
    static boolean cacheMultipart;
    private static boolean decodeFileName;
    private static boolean encodeFileName;
    private static boolean setContentTypeFileName;
    private static boolean setDefaultTextCharset;
    private Object cachedContent;
    protected byte[] content;
    protected InputStream contentStream;
    protected DataHandler dh;
    protected InternetHeaders headers;

    static {
        setDefaultTextCharset = true;
        setContentTypeFileName = true;
        encodeFileName = false;
        decodeFileName = false;
        cacheMultipart = true;
        try {
            String s = System.getProperty("mail.mime.setdefaulttextcharset");
            setDefaultTextCharset = s == null || !s.equalsIgnoreCase("false");
            String s2 = System.getProperty("mail.mime.setcontenttypefilename");
            setContentTypeFileName = s2 == null || !s2.equalsIgnoreCase("false");
            String s3 = System.getProperty("mail.mime.encodefilename");
            encodeFileName = (s3 == null || s3.equalsIgnoreCase("false")) ? false : true;
            String s4 = System.getProperty("mail.mime.decodefilename");
            decodeFileName = (s4 == null || s4.equalsIgnoreCase("false")) ? false : true;
            String s5 = System.getProperty("mail.mime.cachemultipart");
            cacheMultipart = s5 == null || !s5.equalsIgnoreCase("false");
        } catch (SecurityException e) {
        }
    }

    public MimeBodyPart() {
        this.headers = new InternetHeaders();
    }

    public MimeBodyPart(InputStream is) throws MessagingException {
        if (!(is instanceof ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream)) {
            is = new BufferedInputStream(is);
        }
        this.headers = new InternetHeaders(is);
        if (is instanceof SharedInputStream) {
            SharedInputStream sis = (SharedInputStream) is;
            this.contentStream = sis.newStream(sis.getPosition(), -1L);
        } else {
            try {
                this.content = ASCIIUtility.getBytes(is);
            } catch (IOException ioex) {
                throw new MessagingException("Error reading input stream", ioex);
            }
        }
    }

    public MimeBodyPart(InternetHeaders headers, byte[] content) throws MessagingException {
        this.headers = headers;
        this.content = content;
    }

    @Override // javax.mail.Part
    public int getSize() throws MessagingException, IOException {
        if (this.content != null) {
            return this.content.length;
        }
        if (this.contentStream != null) {
            try {
                int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            } catch (IOException e) {
            }
        }
        return -1;
    }

    @Override // javax.mail.Part
    public int getLineCount() throws MessagingException {
        return -1;
    }

    @Override // javax.mail.Part
    public String getContentType() throws MessagingException {
        String s = getHeader("Content-Type", null);
        if (s == null) {
            return HttpServer.MIME_PLAINTEXT;
        }
        return s;
    }

    @Override // javax.mail.Part
    public boolean isMimeType(String mimeType) throws MessagingException {
        return isMimeType(this, mimeType);
    }

    @Override // javax.mail.Part
    public String getDisposition() throws MessagingException {
        return getDisposition(this);
    }

    @Override // javax.mail.Part
    public void setDisposition(String disposition) throws MessagingException {
        setDisposition(this, disposition);
    }

    public String getEncoding() throws MessagingException {
        return getEncoding(this);
    }

    public String getContentID() throws MessagingException {
        return getHeader("Content-Id", null);
    }

    public void setContentID(String cid) throws MessagingException {
        if (cid == null) {
            removeHeader("Content-ID");
        } else {
            setHeader("Content-ID", cid);
        }
    }

    public String getContentMD5() throws MessagingException {
        return getHeader("Content-MD5", null);
    }

    public void setContentMD5(String md5) throws MessagingException {
        setHeader("Content-MD5", md5);
    }

    @Override // javax.mail.internet.MimePart
    public String[] getContentLanguage() throws MessagingException {
        return getContentLanguage(this);
    }

    @Override // javax.mail.internet.MimePart
    public void setContentLanguage(String[] languages) throws MessagingException {
        setContentLanguage(this, languages);
    }

    @Override // javax.mail.Part
    public String getDescription() throws MessagingException {
        return getDescription(this);
    }

    @Override // javax.mail.Part
    public void setDescription(String description) throws MessagingException {
        setDescription(description, null);
    }

    public void setDescription(String description, String charset) throws MessagingException {
        setDescription(this, description, charset);
    }

    @Override // javax.mail.Part
    public String getFileName() throws MessagingException {
        return getFileName(this);
    }

    @Override // javax.mail.Part
    public void setFileName(String filename) throws MessagingException {
        setFileName(this, filename);
    }

    @Override // javax.mail.Part
    public InputStream getInputStream() throws MessagingException, IOException {
        return getDataHandler().getInputStream();
    }

    protected InputStream getContentStream() throws MessagingException {
        if (this.contentStream != null) {
            return ((SharedInputStream) this.contentStream).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new ByteArrayInputStream(this.content);
        }
        throw new MessagingException("No content");
    }

    public InputStream getRawInputStream() throws MessagingException {
        return getContentStream();
    }

    @Override // javax.mail.Part
    public DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            this.dh = new DataHandler(new MimePartDataSource(this));
        }
        return this.dh;
    }

    @Override // javax.mail.Part
    public Object getContent() throws MessagingException, IOException {
        if (this.cachedContent != null) {
            return this.cachedContent;
        }
        try {
            Object c = getDataHandler().getContent();
            if (cacheMultipart) {
                if ((c instanceof Multipart) || (c instanceof Message)) {
                    if (this.content != null || this.contentStream != null) {
                        this.cachedContent = c;
                        return c;
                    }
                    return c;
                }
                return c;
            }
            return c;
        } catch (FolderClosedIOException fex) {
            throw new FolderClosedException(fex.getFolder(), fex.getMessage());
        } catch (MessageRemovedIOException mex) {
            throw new MessageRemovedException(mex.getMessage());
        }
    }

    @Override // javax.mail.Part
    public void setDataHandler(DataHandler dh) throws MessagingException {
        this.dh = dh;
        this.cachedContent = null;
        invalidateContentHeaders(this);
    }

    @Override // javax.mail.Part
    public void setContent(Object o, String type) throws MessagingException {
        if (o instanceof Multipart) {
            setContent((Multipart) o);
        } else {
            setDataHandler(new DataHandler(o, type));
        }
    }

    @Override // javax.mail.Part, javax.mail.internet.MimePart
    public void setText(String text) throws MessagingException {
        setText(text, null);
    }

    @Override // javax.mail.internet.MimePart
    public void setText(String text, String charset) throws MessagingException {
        setText(this, text, charset, "plain");
    }

    @Override // javax.mail.internet.MimePart
    public void setText(String text, String charset, String subtype) throws MessagingException {
        setText(this, text, charset, subtype);
    }

    @Override // javax.mail.Part
    public void setContent(Multipart mp) throws MessagingException {
        setDataHandler(new DataHandler(mp, mp.getContentType()));
        mp.setParent(this);
    }

    public void attachFile(File file) throws MessagingException, IOException {
        FileDataSource fds = new FileDataSource(file);
        setDataHandler(new DataHandler(fds));
        setFileName(fds.getName());
    }

    public void attachFile(String file) throws MessagingException, IOException {
        File f = new File(file);
        attachFile(f);
    }

    public void saveFile(File file) throws Throwable {
        OutputStream out;
        OutputStream out2 = null;
        InputStream in = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Throwable th) {
            th = th;
        }
        try {
            in = getInputStream();
            byte[] buf = new byte[8192];
            while (true) {
                int len = in.read(buf);
                if (len <= 0) {
                    break;
                } else {
                    out.write(buf, 0, len);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e2) {
                }
            }
        } catch (Throwable th2) {
            th = th2;
            out2 = out;
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
            if (out2 != null) {
                try {
                    out2.close();
                    throw th;
                } catch (IOException e4) {
                    throw th;
                }
            }
            throw th;
        }
    }

    public void saveFile(String file) throws Throwable {
        File f = new File(file);
        saveFile(f);
    }

    @Override // javax.mail.Part
    public void writeTo(OutputStream os) throws MessagingException, IOException {
        writeTo(this, os, null);
    }

    @Override // javax.mail.Part
    public String[] getHeader(String name) throws MessagingException {
        return this.headers.getHeader(name);
    }

    @Override // javax.mail.internet.MimePart
    public String getHeader(String name, String delimiter) throws MessagingException {
        return this.headers.getHeader(name, delimiter);
    }

    @Override // javax.mail.Part
    public void setHeader(String name, String value) throws MessagingException {
        this.headers.setHeader(name, value);
    }

    @Override // javax.mail.Part
    public void addHeader(String name, String value) throws MessagingException {
        this.headers.addHeader(name, value);
    }

    @Override // javax.mail.Part
    public void removeHeader(String name) throws MessagingException {
        this.headers.removeHeader(name);
    }

    @Override // javax.mail.Part
    public Enumeration getAllHeaders() throws MessagingException {
        return this.headers.getAllHeaders();
    }

    @Override // javax.mail.Part
    public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
        return this.headers.getMatchingHeaders(names);
    }

    @Override // javax.mail.Part
    public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaders(names);
    }

    public void addHeaderLine(String line) throws MessagingException {
        this.headers.addHeaderLine(line);
    }

    public Enumeration getAllHeaderLines() throws MessagingException {
        return this.headers.getAllHeaderLines();
    }

    public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
        return this.headers.getMatchingHeaderLines(names);
    }

    public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaderLines(names);
    }

    protected void updateHeaders() throws MessagingException, IOException {
        updateHeaders(this);
        if (this.cachedContent != null) {
            this.dh = new DataHandler(this.cachedContent, getContentType());
            this.cachedContent = null;
            this.content = null;
            if (this.contentStream != null) {
                try {
                    this.contentStream.close();
                } catch (IOException e) {
                }
            }
            this.contentStream = null;
        }
    }

    static boolean isMimeType(MimePart part, String mimeType) throws MessagingException {
        try {
            ContentType ct = new ContentType(part.getContentType());
            return ct.match(mimeType);
        } catch (ParseException e) {
            return part.getContentType().equalsIgnoreCase(mimeType);
        }
    }

    static void setText(MimePart part, String text, String charset, String subtype) throws MessagingException {
        if (charset == null) {
            if (MimeUtility.checkAscii(text) != 1) {
                charset = MimeUtility.getDefaultMIMECharset();
            } else {
                charset = "us-ascii";
            }
        }
        part.setContent(text, "text/" + subtype + "; charset=" + MimeUtility.quote(charset, HeaderTokenizer.MIME));
    }

    static String getDisposition(MimePart part) throws MessagingException {
        String s = part.getHeader("Content-Disposition", null);
        if (s == null) {
            return null;
        }
        ContentDisposition cd = new ContentDisposition(s);
        return cd.getDisposition();
    }

    static void setDisposition(MimePart part, String disposition) throws MessagingException {
        if (disposition == null) {
            part.removeHeader("Content-Disposition");
            return;
        }
        String s = part.getHeader("Content-Disposition", null);
        if (s != null) {
            ContentDisposition cd = new ContentDisposition(s);
            cd.setDisposition(disposition);
            disposition = cd.toString();
        }
        part.setHeader("Content-Disposition", disposition);
    }

    static String getDescription(MimePart part) throws MessagingException {
        String rawvalue = part.getHeader("Content-Description", null);
        if (rawvalue == null) {
            return null;
        }
        try {
            return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
        } catch (UnsupportedEncodingException e) {
            return rawvalue;
        }
    }

    static void setDescription(MimePart part, String description, String charset) throws MessagingException {
        if (description == null) {
            part.removeHeader("Content-Description");
            return;
        }
        try {
            part.setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(description, charset, null)));
        } catch (UnsupportedEncodingException uex) {
            throw new MessagingException("Encoding error", uex);
        }
    }

    static String getFileName(MimePart part) throws MessagingException {
        String s;
        String filename = null;
        String s2 = part.getHeader("Content-Disposition", null);
        if (s2 != null) {
            ContentDisposition cd = new ContentDisposition(s2);
            filename = cd.getParameter("filename");
        }
        if (filename == null && (s = part.getHeader("Content-Type", null)) != null) {
            try {
                ContentType ct = new ContentType(s);
                filename = ct.getParameter("name");
            } catch (ParseException e) {
            }
        }
        if (decodeFileName && filename != null) {
            try {
                return MimeUtility.decodeText(filename);
            } catch (UnsupportedEncodingException ex) {
                throw new MessagingException("Can't decode filename", ex);
            }
        }
        return filename;
    }

    static void setFileName(MimePart part, String name) throws MessagingException {
        String s;
        if (encodeFileName && name != null) {
            try {
                name = MimeUtility.encodeText(name);
            } catch (UnsupportedEncodingException ex) {
                throw new MessagingException("Can't encode filename", ex);
            }
        }
        String s2 = part.getHeader("Content-Disposition", null);
        ContentDisposition cd = new ContentDisposition(s2 == null ? Part.ATTACHMENT : s2);
        cd.setParameter("filename", name);
        part.setHeader("Content-Disposition", cd.toString());
        if (setContentTypeFileName && (s = part.getHeader("Content-Type", null)) != null) {
            try {
                ContentType cType = new ContentType(s);
                cType.setParameter("name", name);
                part.setHeader("Content-Type", cType.toString());
            } catch (ParseException e) {
            }
        }
    }

    static String[] getContentLanguage(MimePart part) throws MessagingException {
        String s = part.getHeader("Content-Language", null);
        if (s == null) {
            return null;
        }
        HeaderTokenizer h = new HeaderTokenizer(s, HeaderTokenizer.MIME);
        Vector v = new Vector();
        while (true) {
            HeaderTokenizer.Token tk = h.next();
            int tkType = tk.getType();
            if (tkType == -4) {
                break;
            }
            if (tkType == -1) {
                v.addElement(tk.getValue());
            }
        }
        if (v.size() == 0) {
            return null;
        }
        String[] language = new String[v.size()];
        v.copyInto(language);
        return language;
    }

    static void setContentLanguage(MimePart part, String[] languages) throws MessagingException {
        StringBuffer sb = new StringBuffer(languages[0]);
        for (int i = 1; i < languages.length; i++) {
            sb.append(',').append(languages[i]);
        }
        part.setHeader("Content-Language", sb.toString());
    }

    static String getEncoding(MimePart part) throws MessagingException {
        HeaderTokenizer.Token tk;
        int tkType;
        String s = part.getHeader("Content-Transfer-Encoding", null);
        if (s == null) {
            return null;
        }
        String s2 = s.trim();
        if (s2.equalsIgnoreCase("7bit") || s2.equalsIgnoreCase("8bit") || s2.equalsIgnoreCase("quoted-printable") || s2.equalsIgnoreCase("binary") || s2.equalsIgnoreCase("base64")) {
            return s2;
        }
        HeaderTokenizer h = new HeaderTokenizer(s2, HeaderTokenizer.MIME);
        do {
            tk = h.next();
            tkType = tk.getType();
            if (tkType == -4) {
                return s2;
            }
        } while (tkType != -1);
        return tk.getValue();
    }

    static void setEncoding(MimePart part, String encoding) throws MessagingException {
        part.setHeader("Content-Transfer-Encoding", encoding);
    }

    static void updateHeaders(MimePart part) throws MessagingException {
        String charset;
        Object o;
        DataHandler dh = part.getDataHandler();
        if (dh != null) {
            try {
                String type = dh.getContentType();
                boolean composite = false;
                boolean needCTHeader = part.getHeader("Content-Type") == null;
                ContentType cType = new ContentType(type);
                if (cType.match("multipart/*")) {
                    composite = true;
                    if (part instanceof MimeBodyPart) {
                        MimeBodyPart mbp = (MimeBodyPart) part;
                        o = mbp.cachedContent != null ? mbp.cachedContent : dh.getContent();
                    } else if (part instanceof MimeMessage) {
                        MimeMessage msg = (MimeMessage) part;
                        o = msg.cachedContent != null ? msg.cachedContent : dh.getContent();
                    } else {
                        o = dh.getContent();
                    }
                    if (o instanceof MimeMultipart) {
                        ((MimeMultipart) o).updateHeaders();
                    } else {
                        throw new MessagingException("MIME part of type \"" + type + "\" contains object of type " + o.getClass().getName() + " instead of MimeMultipart");
                    }
                } else if (cType.match("message/rfc822")) {
                    composite = true;
                }
                if (!composite) {
                    if (part.getHeader("Content-Transfer-Encoding") == null) {
                        setEncoding(part, MimeUtility.getEncoding(dh));
                    }
                    if (needCTHeader && setDefaultTextCharset && cType.match("text/*") && cType.getParameter(HTTP.CHARSET) == null) {
                        String enc = part.getEncoding();
                        if (enc != null && enc.equalsIgnoreCase("7bit")) {
                            charset = "us-ascii";
                        } else {
                            charset = MimeUtility.getDefaultMIMECharset();
                        }
                        cType.setParameter(HTTP.CHARSET, charset);
                        type = cType.toString();
                    }
                }
                if (needCTHeader) {
                    String s = part.getHeader("Content-Disposition", null);
                    if (s != null) {
                        ContentDisposition cd = new ContentDisposition(s);
                        String filename = cd.getParameter("filename");
                        if (filename != null) {
                            cType.setParameter("name", filename);
                            type = cType.toString();
                        }
                    }
                    part.setHeader("Content-Type", type);
                }
            } catch (IOException ex) {
                throw new MessagingException("IOException updating headers", ex);
            }
        }
    }

    static void invalidateContentHeaders(MimePart part) throws MessagingException {
        part.removeHeader("Content-Type");
        part.removeHeader("Content-Transfer-Encoding");
    }

    static void writeTo(MimePart part, OutputStream os, String[] ignoreList) throws MessagingException, IOException {
        LineOutputStream los;
        if (os instanceof LineOutputStream) {
            los = (LineOutputStream) os;
        } else {
            los = new LineOutputStream(os);
        }
        Enumeration hdrLines = part.getNonMatchingHeaderLines(ignoreList);
        while (hdrLines.hasMoreElements()) {
            los.writeln((String) hdrLines.nextElement());
        }
        los.writeln();
        OutputStream os2 = MimeUtility.encode(os, part.getEncoding());
        part.getDataHandler().writeTo(os2);
        os2.flush();
    }
}
