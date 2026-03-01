package com.sun.mail.pop3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.IllegalWriteException;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.SharedInputStream;

/* loaded from: classes.dex */
public class POP3Message extends MimeMessage {
    static final String UNKNOWN = "UNKNOWN";
    private POP3Folder folder;
    private int hdrSize;
    private int msgSize;
    String uid;

    public POP3Message(Folder folder, int msgno) throws MessagingException {
        super(folder, msgno);
        this.hdrSize = -1;
        this.msgSize = -1;
        this.uid = "UNKNOWN";
        this.folder = (POP3Folder) folder;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void setFlags(Flags newFlags, boolean set) throws MessagingException {
        Flags oldFlags = (Flags) this.flags.clone();
        super.setFlags(newFlags, set);
        if (!this.flags.equals(oldFlags)) {
            this.folder.notifyMessageChangedListeners(1, this);
        }
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public int getSize() throws MessagingException {
        int i;
        try {
            synchronized (this) {
                if (this.msgSize >= 0) {
                    i = this.msgSize;
                } else {
                    if (this.msgSize < 0) {
                        if (this.headers == null) {
                            loadHeaders();
                        }
                        if (this.contentStream != null) {
                            this.msgSize = this.contentStream.available();
                        } else {
                            this.msgSize = this.folder.getProtocol().list(this.msgnum) - this.hdrSize;
                        }
                    }
                    i = this.msgSize;
                }
            }
            return i;
        } catch (EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        } catch (IOException ex) {
            throw new MessagingException("error getting size", ex);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // javax.mail.internet.MimeMessage
    protected InputStream getContentStream() throws MessagingException {
        int len;
        try {
            synchronized (this) {
                if (this.contentStream == null) {
                    InputStream inputStreamRetr = this.folder.getProtocol().retr(this.msgnum, this.msgSize > 0 ? this.msgSize + this.hdrSize : 0);
                    if (inputStreamRetr == 0) {
                        this.expunged = true;
                        throw new MessageRemovedException();
                    }
                    if (this.headers == null || ((POP3Store) this.folder.getStore()).forgetTopHeaders) {
                        this.headers = new InternetHeaders(inputStreamRetr);
                        this.hdrSize = (int) ((SharedInputStream) inputStreamRetr).getPosition();
                    } else {
                        do {
                            len = 0;
                            while (true) {
                                int c1 = inputStreamRetr.read();
                                if (c1 < 0 || c1 == 10) {
                                    break;
                                }
                                if (c1 == 13) {
                                    if (inputStreamRetr.available() > 0) {
                                        inputStreamRetr.mark(1);
                                        if (inputStreamRetr.read() != 10) {
                                            inputStreamRetr.reset();
                                        }
                                    }
                                } else {
                                    len++;
                                }
                            }
                            if (inputStreamRetr.available() == 0) {
                                break;
                            }
                        } while (len != 0);
                        this.hdrSize = (int) ((SharedInputStream) inputStreamRetr).getPosition();
                    }
                    this.contentStream = ((SharedInputStream) inputStreamRetr).newStream(this.hdrSize, -1L);
                }
            }
            return super.getContentStream();
        } catch (EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        } catch (IOException ex) {
            throw new MessagingException("error fetching POP3 content", ex);
        }
    }

    public synchronized void invalidate(boolean invalidateHeaders) {
        this.content = null;
        this.contentStream = null;
        this.msgSize = -1;
        if (invalidateHeaders) {
            this.headers = null;
            this.hdrSize = -1;
        }
    }

    public InputStream top(int n) throws MessagingException {
        InputStream pVar;
        try {
            synchronized (this) {
                pVar = this.folder.getProtocol().top(this.msgnum, n);
            }
            return pVar;
        } catch (EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        } catch (IOException ex) {
            throw new MessagingException("error getting size", ex);
        }
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String[] getHeader(String name) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getHeader(name);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String getHeader(String name, String delimiter) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getHeader(name, delimiter);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void addHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void removeHeader(String name) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getAllHeaders() throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getAllHeaders();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getMatchingHeaders(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getNonMatchingHeaders(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public void addHeaderLine(String line) throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getAllHeaderLines() throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getAllHeaderLines();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getMatchingHeaderLines(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
        if (this.headers == null) {
            loadHeaders();
        }
        return this.headers.getNonMatchingHeaderLines(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void saveChanges() throws MessagingException {
        throw new IllegalWriteException("POP3 messages are read-only");
    }

    private void loadHeaders() throws MessagingException {
        InputStream hdrs;
        try {
            synchronized (this) {
                if (this.headers == null) {
                    if (((POP3Store) this.folder.getStore()).disableTop || (hdrs = this.folder.getProtocol().top(this.msgnum, 0)) == null) {
                        InputStream cs = getContentStream();
                        cs.close();
                    } else {
                        this.hdrSize = hdrs.available();
                        this.headers = new InternetHeaders(hdrs);
                    }
                }
            }
        } catch (EOFException eex) {
            this.folder.close(false);
            throw new FolderClosedException(this.folder, eex.toString());
        } catch (IOException ex) {
            throw new MessagingException("error loading POP3 headers", ex);
        }
    }
}
