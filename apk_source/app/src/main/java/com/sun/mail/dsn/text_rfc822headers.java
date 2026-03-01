package com.sun.mail.dsn;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import myjava.awt.datatransfer.DataFlavor;
import org.cybergarage.http.HTTP;

/* loaded from: classes.dex */
public class text_rfc822headers implements DataContentHandler {
    private static ActivationDataFlavor myDF = new ActivationDataFlavor(MessageHeaders.class, "text/rfc822-headers", "RFC822 headers");
    private static ActivationDataFlavor myDFs = new ActivationDataFlavor(String.class, "text/rfc822-headers", "RFC822 headers");

    @Override // javax.activation.DataContentHandler
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{myDF, myDFs};
    }

    @Override // javax.activation.DataContentHandler
    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        if (myDF.equals(df)) {
            return getContent(ds);
        }
        if (myDFs.equals(df)) {
            return getStringContent(ds);
        }
        return null;
    }

    @Override // javax.activation.DataContentHandler
    public Object getContent(DataSource ds) throws IOException {
        try {
            return new MessageHeaders(ds.getInputStream());
        } catch (MessagingException mex) {
            throw new IOException("Exception creating MessageHeaders: " + mex);
        }
    }

    private Object getStringContent(DataSource ds) throws IOException {
        int size;
        String enc = null;
        try {
            enc = getCharset(ds.getContentType());
            InputStreamReader is = new InputStreamReader(ds.getInputStream(), enc);
            int pos = 0;
            char[] buf = new char[1024];
            while (true) {
                int count = is.read(buf, pos, buf.length - pos);
                if (count != -1) {
                    pos += count;
                    if (pos >= buf.length) {
                        int size2 = buf.length;
                        if (size2 < 262144) {
                            size = size2 + size2;
                        } else {
                            size = size2 + 262144;
                        }
                        char[] tbuf = new char[size];
                        System.arraycopy(buf, 0, tbuf, 0, pos);
                        buf = tbuf;
                    }
                } else {
                    return new String(buf, 0, pos);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedEncodingException(enc);
        }
    }

    @Override // javax.activation.DataContentHandler
    public void writeTo(Object obj, String type, OutputStream os) throws Exception {
        if (obj instanceof MessageHeaders) {
            MessageHeaders mh = (MessageHeaders) obj;
            try {
                mh.writeTo(os);
                return;
            } catch (MessagingException mex) {
                Exception ex = mex.getNextException();
                if (ex instanceof IOException) {
                    throw ((IOException) ex);
                }
                throw new IOException("Exception writing headers: " + mex);
            }
        }
        if (!(obj instanceof String)) {
            throw new IOException("\"" + myDFs.getMimeType() + "\" DataContentHandler requires String object, was given object of type " + obj.getClass().toString());
        }
        String enc = null;
        try {
            enc = getCharset(type);
            OutputStreamWriter osw = new OutputStreamWriter(os, enc);
            String s = (String) obj;
            osw.write(s, 0, s.length());
            osw.flush();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedEncodingException(enc);
        }
    }

    private String getCharset(String type) {
        try {
            ContentType ct = new ContentType(type);
            String charset = ct.getParameter(HTTP.CHARSET);
            if (charset == null) {
                charset = "us-ascii";
            }
            return MimeUtility.javaCharset(charset);
        } catch (Exception e) {
            return null;
        }
    }
}
