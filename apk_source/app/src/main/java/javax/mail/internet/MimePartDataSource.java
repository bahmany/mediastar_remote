package javax.mail.internet;

import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;

/* loaded from: classes.dex */
public class MimePartDataSource implements DataSource, MessageAware {
    private static boolean ignoreMultipartEncoding;
    private MessageContext context;
    protected MimePart part;

    static {
        boolean z = true;
        ignoreMultipartEncoding = true;
        try {
            String s = System.getProperty("mail.mime.ignoremultipartencoding");
            if (s != null && s.equalsIgnoreCase("false")) {
                z = false;
            }
            ignoreMultipartEncoding = z;
        } catch (SecurityException e) {
        }
    }

    public MimePartDataSource(MimePart part) {
        this.part = part;
    }

    @Override // javax.activation.DataSource
    public InputStream getInputStream() throws MessagingException, IOException {
        InputStream is;
        try {
            if (this.part instanceof MimeBodyPart) {
                is = ((MimeBodyPart) this.part).getContentStream();
            } else if (this.part instanceof MimeMessage) {
                is = ((MimeMessage) this.part).getContentStream();
            } else {
                throw new MessagingException("Unknown part");
            }
            String encoding = restrictEncoding(this.part.getEncoding(), this.part);
            if (encoding != null) {
                return MimeUtility.decode(is, encoding);
            }
            return is;
        } catch (MessagingException mex) {
            throw new IOException(mex.getMessage());
        }
    }

    private static String restrictEncoding(String encoding, MimePart part) throws MessagingException {
        String type;
        if (ignoreMultipartEncoding && encoding != null && !encoding.equalsIgnoreCase("7bit") && !encoding.equalsIgnoreCase("8bit") && !encoding.equalsIgnoreCase("binary") && (type = part.getContentType()) != null) {
            try {
                ContentType cType = new ContentType(type);
                if (!cType.match("multipart/*")) {
                    if (!cType.match("message/*")) {
                        return encoding;
                    }
                }
                return null;
            } catch (ParseException e) {
                return encoding;
            }
        }
        return encoding;
    }

    @Override // javax.activation.DataSource
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException();
    }

    @Override // javax.activation.DataSource
    public String getContentType() {
        try {
            return this.part.getContentType();
        } catch (MessagingException e) {
            return HttpServer.MIME_DEFAULT_BINARY;
        }
    }

    @Override // javax.activation.DataSource
    public String getName() {
        try {
            if (this.part instanceof MimeBodyPart) {
                return ((MimeBodyPart) this.part).getFileName();
            }
        } catch (MessagingException e) {
        }
        return "";
    }

    @Override // javax.mail.MessageAware
    public synchronized MessageContext getMessageContext() {
        if (this.context == null) {
            this.context = new MessageContext(this.part);
        }
        return this.context;
    }
}
