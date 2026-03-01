package javax.mail.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import org.cybergarage.http.HTTP;

/* loaded from: classes.dex */
public class ByteArrayDataSource implements DataSource {
    private byte[] data;
    private int len;
    private String name;
    private String type;

    static class DSByteArrayOutputStream extends ByteArrayOutputStream {
        DSByteArrayOutputStream() {
        }

        public byte[] getBuf() {
            return this.buf;
        }

        public int getCount() {
            return this.count;
        }
    }

    public ByteArrayDataSource(InputStream is, String type) throws IOException {
        this.len = -1;
        this.name = "";
        DSByteArrayOutputStream os = new DSByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while (true) {
            int len = is.read(buf);
            if (len <= 0) {
                break;
            } else {
                os.write(buf, 0, len);
            }
        }
        this.data = os.getBuf();
        this.len = os.getCount();
        if (this.data.length - this.len > 262144) {
            this.data = os.toByteArray();
            this.len = this.data.length;
        }
        this.type = type;
    }

    public ByteArrayDataSource(byte[] data, String type) {
        this.len = -1;
        this.name = "";
        this.data = data;
        this.type = type;
    }

    public ByteArrayDataSource(String data, String type) throws IOException {
        this.len = -1;
        this.name = "";
        String charset = null;
        try {
            ContentType ct = new ContentType(type);
            charset = ct.getParameter(HTTP.CHARSET);
        } catch (ParseException e) {
        }
        this.data = data.getBytes(charset == null ? MimeUtility.getDefaultJavaCharset() : charset);
        this.type = type;
    }

    @Override // javax.activation.DataSource
    public InputStream getInputStream() throws IOException {
        if (this.data == null) {
            throw new IOException("no data");
        }
        if (this.len < 0) {
            this.len = this.data.length;
        }
        return new SharedByteArrayInputStream(this.data, 0, this.len);
    }

    @Override // javax.activation.DataSource
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("cannot do this");
    }

    @Override // javax.activation.DataSource
    public String getContentType() {
        return this.type;
    }

    @Override // javax.activation.DataSource
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
