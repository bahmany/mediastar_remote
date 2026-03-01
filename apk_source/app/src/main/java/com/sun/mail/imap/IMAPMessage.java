package com.sun.mail.imap;

import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.Utility;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.sun.mail.imap.protocol.Item;
import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.RFC822DATA;
import com.sun.mail.imap.protocol.RFC822SIZE;
import com.sun.mail.imap.protocol.UID;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.FolderClosedException;
import javax.mail.Header;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/* loaded from: classes.dex */
public class IMAPMessage extends MimeMessage {
    private static String EnvelopeCmd = "ENVELOPE INTERNALDATE RFC822.SIZE";
    protected BODYSTRUCTURE bs;
    private String description;
    protected ENVELOPE envelope;
    private boolean headersLoaded;
    private Hashtable loadedHeaders;
    private boolean peek;
    private Date receivedDate;
    protected String sectionId;
    private int seqnum;
    private int size;
    private String subject;
    private String type;
    private long uid;

    protected IMAPMessage(IMAPFolder folder, int msgnum, int seqnum) {
        super(folder, msgnum);
        this.size = -1;
        this.uid = -1L;
        this.headersLoaded = false;
        this.seqnum = seqnum;
        this.flags = null;
    }

    protected IMAPMessage(Session session) {
        super(session);
        this.size = -1;
        this.uid = -1L;
        this.headersLoaded = false;
    }

    protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException, InterruptedException {
        ((IMAPFolder) this.folder).waitIfIdle();
        IMAPProtocol p = ((IMAPFolder) this.folder).protocol;
        if (p == null) {
            throw new FolderClosedException(this.folder);
        }
        return p;
    }

    protected boolean isREV1() throws FolderClosedException {
        IMAPProtocol p = ((IMAPFolder) this.folder).protocol;
        if (p == null) {
            throw new FolderClosedException(this.folder);
        }
        return p.isREV1();
    }

    protected Object getMessageCacheLock() {
        return ((IMAPFolder) this.folder).messageCacheLock;
    }

    protected int getSequenceNumber() {
        return this.seqnum;
    }

    protected void setSequenceNumber(int seqnum) {
        this.seqnum = seqnum;
    }

    @Override // javax.mail.Message
    protected void setMessageNumber(int msgnum) {
        super.setMessageNumber(msgnum);
    }

    protected long getUID() {
        return this.uid;
    }

    protected void setUID(long uid) {
        this.uid = uid;
    }

    @Override // javax.mail.Message
    protected void setExpunged(boolean set) {
        super.setExpunged(set);
        this.seqnum = -1;
    }

    protected void checkExpunged() throws MessageRemovedException {
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }

    protected void forceCheckExpunged() throws FolderClosedException, MessageRemovedException {
        synchronized (getMessageCacheLock()) {
            try {
                getProtocol().noop();
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException e) {
            }
        }
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }

    protected int getFetchBlockSize() {
        return ((IMAPStore) this.folder.getStore()).getFetchBlockSize();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public Address[] getFrom() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return aaclone(this.envelope.from);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void setFrom(Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void addFrom(Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage
    public Address getSender() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.envelope.sender != null) {
            return this.envelope.sender[0];
        }
        return null;
    }

    @Override // javax.mail.internet.MimeMessage
    public void setSender(Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public Address[] getRecipients(Message.RecipientType type) throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (type == Message.RecipientType.TO) {
            return aaclone(this.envelope.to);
        }
        if (type == Message.RecipientType.CC) {
            return aaclone(this.envelope.cc);
        }
        if (type == Message.RecipientType.BCC) {
            return aaclone(this.envelope.bcc);
        }
        return super.getRecipients(type);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void setRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void addRecipients(Message.RecipientType type, Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public Address[] getReplyTo() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return aaclone(this.envelope.replyTo);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void setReplyTo(Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public String getSubject() throws MessagingException {
        checkExpunged();
        if (this.subject != null) {
            return this.subject;
        }
        loadEnvelope();
        if (this.envelope.subject == null) {
            return null;
        }
        try {
            this.subject = MimeUtility.decodeText(this.envelope.subject);
        } catch (UnsupportedEncodingException e) {
            this.subject = this.envelope.subject;
        }
        return this.subject;
    }

    @Override // javax.mail.internet.MimeMessage
    public void setSubject(String subject, String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public Date getSentDate() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.envelope.date == null) {
            return null;
        }
        return new Date(this.envelope.date.getTime());
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public void setSentDate(Date d) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public Date getReceivedDate() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.receivedDate == null) {
            return null;
        }
        return new Date(this.receivedDate.getTime());
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public int getSize() throws MessagingException {
        checkExpunged();
        if (this.size == -1) {
            loadEnvelope();
        }
        return this.size;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public int getLineCount() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.lines;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String[] getContentLanguage() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        if (this.bs.language != null) {
            return (String[]) this.bs.language.clone();
        }
        return null;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public void setContentLanguage(String[] languages) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getInReplyTo() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return this.envelope.inReplyTo;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String getContentType() throws MessagingException {
        checkExpunged();
        if (this.type == null) {
            loadBODYSTRUCTURE();
            ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
            this.type = ct.toString();
        }
        return this.type;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String getDisposition() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.disposition;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setDisposition(String disposition) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String getEncoding() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.encoding;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String getContentID() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.id;
    }

    @Override // javax.mail.internet.MimeMessage
    public void setContentID(String cid) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String getContentMD5() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.md5;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public void setContentMD5(String md5) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String getDescription() throws MessagingException {
        checkExpunged();
        if (this.description != null) {
            return this.description;
        }
        loadBODYSTRUCTURE();
        if (this.bs.description == null) {
            return null;
        }
        try {
            this.description = MimeUtility.decodeText(this.bs.description);
        } catch (UnsupportedEncodingException e) {
            this.description = this.bs.description;
        }
        return this.description;
    }

    @Override // javax.mail.internet.MimeMessage
    public void setDescription(String description, String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage
    public String getMessageID() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return this.envelope.messageId;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String getFileName() throws MessagingException {
        checkExpunged();
        String filename = null;
        loadBODYSTRUCTURE();
        if (this.bs.dParams != null) {
            filename = this.bs.dParams.get("filename");
        }
        if (filename == null && this.bs.cParams != null) {
            String filename2 = this.bs.cParams.get("name");
            return filename2;
        }
        return filename;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setFileName(String filename) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage
    protected InputStream getContentStream() throws MessagingException {
        BODY b;
        InputStream is = null;
        boolean pk = getPeek();
        synchronized (getMessageCacheLock()) {
            try {
                IMAPProtocol p = getProtocol();
                checkExpunged();
                if (p.isREV1() && getFetchBlockSize() != -1) {
                    return new IMAPInputStream(this, toSection("TEXT"), this.bs != null ? this.bs.size : -1, pk);
                }
                if (p.isREV1()) {
                    if (pk) {
                        b = p.peekBody(getSequenceNumber(), toSection("TEXT"));
                    } else {
                        b = p.fetchBody(getSequenceNumber(), toSection("TEXT"));
                    }
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                } else {
                    RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "TEXT");
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
                if (is == null) {
                    throw new MessagingException("No content");
                }
                return is;
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException pex) {
                forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public synchronized DataHandler getDataHandler() throws MessagingException {
        checkExpunged();
        if (this.dh == null) {
            loadBODYSTRUCTURE();
            if (this.type == null) {
                ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
                this.type = ct.toString();
            }
            if (this.bs.isMulti()) {
                this.dh = new DataHandler(new IMAPMultipartDataSource(this, this.bs.bodies, this.sectionId, this));
            } else if (this.bs.isNested() && isREV1()) {
                this.dh = new DataHandler(new IMAPNestedMessage(this, this.bs.bodies[0], this.bs.envelope, this.sectionId == null ? "1" : String.valueOf(this.sectionId) + ".1"), this.type);
            }
        }
        return super.getDataHandler();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setDataHandler(DataHandler content) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void writeTo(OutputStream os) throws MessagingException, IOException {
        BODY b;
        InputStream is = null;
        boolean pk = getPeek();
        synchronized (getMessageCacheLock()) {
            try {
                IMAPProtocol p = getProtocol();
                checkExpunged();
                if (p.isREV1()) {
                    if (pk) {
                        b = p.peekBody(getSequenceNumber(), this.sectionId);
                    } else {
                        b = p.fetchBody(getSequenceNumber(), this.sectionId);
                    }
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                } else {
                    RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), null);
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException pex) {
                forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            throw new MessagingException("No content");
        }
        byte[] bytes = new byte[1024];
        while (true) {
            int count = is.read(bytes);
            if (count != -1) {
                os.write(bytes, 0, count);
            } else {
                return;
            }
        }
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public String[] getHeader(String name) throws MessagingException {
        checkExpunged();
        if (isHeaderLoaded(name)) {
            return this.headers.getHeader(name);
        }
        InputStream is = null;
        synchronized (getMessageCacheLock()) {
            try {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    if (p.isREV1()) {
                        BODY b = p.peekBody(getSequenceNumber(), toSection("HEADER.FIELDS (" + name + ")"));
                        if (b != null) {
                            is = b.getByteArrayInputStream();
                        }
                    } else {
                        RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "HEADER.LINES (" + name + ")");
                        if (rd != null) {
                            is = rd.getByteArrayInputStream();
                        }
                    }
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
        }
        if (is == null) {
            return null;
        }
        if (this.headers == null) {
            this.headers = new InternetHeaders();
        }
        this.headers.load(is);
        setHeaderLoaded(name);
        return this.headers.getHeader(name);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public String getHeader(String name, String delimiter) throws MessagingException {
        checkExpunged();
        if (getHeader(name) == null) {
            return null;
        }
        return this.headers.getHeader(name, delimiter);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void addHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void removeHeader(String name) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getAllHeaders() throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getAllHeaders();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getMatchingHeaders(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getNonMatchingHeaders(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public void addHeaderLine(String line) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getAllHeaderLines() throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getAllHeaderLines();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getMatchingHeaderLines(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.internet.MimePart
    public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getNonMatchingHeaderLines(names);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public synchronized Flags getFlags() throws MessagingException {
        checkExpunged();
        loadFlags();
        return super.getFlags();
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public synchronized boolean isSet(Flags.Flag flag) throws MessagingException {
        checkExpunged();
        loadFlags();
        return super.isSet(flag);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Message
    public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
        synchronized (getMessageCacheLock()) {
            try {
                IMAPProtocol p = getProtocol();
                checkExpunged();
                p.storeFlags(getSequenceNumber(), flag, set);
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }

    public synchronized void setPeek(boolean peek) {
        this.peek = peek;
    }

    public synchronized boolean getPeek() {
        return this.peek;
    }

    public synchronized void invalidateHeaders() {
        this.headersLoaded = false;
        this.loadedHeaders = null;
        this.envelope = null;
        this.bs = null;
        this.receivedDate = null;
        this.size = -1;
        this.type = null;
        this.subject = null;
        this.description = null;
    }

    static void fetch(IMAPFolder folder, Message[] msgs, FetchProfile fp) throws MessagingException {
        InputStream headerStream;
        StringBuffer command = new StringBuffer();
        boolean first = true;
        boolean allHeaders = false;
        if (fp.contains(FetchProfile.Item.ENVELOPE)) {
            command.append(EnvelopeCmd);
            first = false;
        }
        if (fp.contains(FetchProfile.Item.FLAGS)) {
            command.append(first ? "FLAGS" : " FLAGS");
            first = false;
        }
        if (fp.contains(FetchProfile.Item.CONTENT_INFO)) {
            command.append(first ? "BODYSTRUCTURE" : " BODYSTRUCTURE");
            first = false;
        }
        if (fp.contains(UIDFolder.FetchProfileItem.UID)) {
            command.append(first ? "UID" : " UID");
            first = false;
        }
        if (fp.contains(IMAPFolder.FetchProfileItem.HEADERS)) {
            allHeaders = true;
            if (folder.protocol.isREV1()) {
                command.append(first ? "BODY.PEEK[HEADER]" : " BODY.PEEK[HEADER]");
            } else {
                command.append(first ? "RFC822.HEADER" : " RFC822.HEADER");
            }
            first = false;
        }
        if (fp.contains(IMAPFolder.FetchProfileItem.SIZE)) {
            command.append(first ? "RFC822.SIZE" : " RFC822.SIZE");
            first = false;
        }
        String[] hdrs = (String[]) null;
        if (!allHeaders) {
            hdrs = fp.getHeaderNames();
            if (hdrs.length > 0) {
                if (!first) {
                    command.append(" ");
                }
                command.append(craftHeaderCmd(folder.protocol, hdrs));
            }
        }
        Utility.Condition condition = new Utility.Condition(fp) { // from class: com.sun.mail.imap.IMAPMessage.1FetchProfileCondition
            private String[] hdrs;
            private boolean needBodyStructure;
            private boolean needEnvelope;
            private boolean needFlags;
            private boolean needHeaders;
            private boolean needSize;
            private boolean needUID;

            {
                this.needEnvelope = false;
                this.needFlags = false;
                this.needBodyStructure = false;
                this.needUID = false;
                this.needHeaders = false;
                this.needSize = false;
                this.hdrs = null;
                if (fp.contains(FetchProfile.Item.ENVELOPE)) {
                    this.needEnvelope = true;
                }
                if (fp.contains(FetchProfile.Item.FLAGS)) {
                    this.needFlags = true;
                }
                if (fp.contains(FetchProfile.Item.CONTENT_INFO)) {
                    this.needBodyStructure = true;
                }
                if (fp.contains(UIDFolder.FetchProfileItem.UID)) {
                    this.needUID = true;
                }
                if (fp.contains(IMAPFolder.FetchProfileItem.HEADERS)) {
                    this.needHeaders = true;
                }
                if (fp.contains(IMAPFolder.FetchProfileItem.SIZE)) {
                    this.needSize = true;
                }
                this.hdrs = fp.getHeaderNames();
            }

            @Override // com.sun.mail.imap.Utility.Condition
            public boolean test(IMAPMessage m) {
                if (this.needEnvelope && m._getEnvelope() == null) {
                    return true;
                }
                if (this.needFlags && m._getFlags() == null) {
                    return true;
                }
                if (this.needBodyStructure && m._getBodyStructure() == null) {
                    return true;
                }
                if (this.needUID && m.getUID() == -1) {
                    return true;
                }
                if (this.needHeaders && !m.areHeadersLoaded()) {
                    return true;
                }
                if (this.needSize && m.size == -1) {
                    return true;
                }
                for (int i = 0; i < this.hdrs.length; i++) {
                    if (!m.isHeaderLoaded(this.hdrs[i])) {
                        return true;
                    }
                }
                return false;
            }
        };
        synchronized (folder.messageCacheLock) {
            MessageSet[] msgsets = Utility.toMessageSet(msgs, condition);
            if (msgsets != null) {
                Response[] r = (Response[]) null;
                Vector v = new Vector();
                try {
                    try {
                        r = folder.protocol.fetch(msgsets, command.toString());
                    } catch (CommandFailedException e) {
                    } catch (ProtocolException pex) {
                        throw new MessagingException(pex.getMessage(), pex);
                    }
                    if (r != null) {
                        for (int i = 0; i < r.length; i++) {
                            if (r[i] != null) {
                                if (!(r[i] instanceof FetchResponse)) {
                                    v.addElement(r[i]);
                                } else {
                                    FetchResponse f = (FetchResponse) r[i];
                                    IMAPMessage msg = folder.getMessageBySeqNumber(f.getNumber());
                                    int count = f.getItemCount();
                                    boolean unsolicitedFlags = false;
                                    for (int j = 0; j < count; j++) {
                                        Object item = f.getItem(j);
                                        if (item instanceof Flags) {
                                            if (!fp.contains(FetchProfile.Item.FLAGS) || msg == null) {
                                                unsolicitedFlags = true;
                                            } else {
                                                msg.flags = (Flags) item;
                                            }
                                        } else if (item instanceof ENVELOPE) {
                                            msg.envelope = (ENVELOPE) item;
                                        } else if (item instanceof INTERNALDATE) {
                                            msg.receivedDate = ((INTERNALDATE) item).getDate();
                                        } else if (item instanceof RFC822SIZE) {
                                            msg.size = ((RFC822SIZE) item).size;
                                        } else if (item instanceof BODYSTRUCTURE) {
                                            msg.bs = (BODYSTRUCTURE) item;
                                        } else if (item instanceof UID) {
                                            UID u = (UID) item;
                                            msg.uid = u.uid;
                                            if (folder.uidTable == null) {
                                                folder.uidTable = new Hashtable();
                                            }
                                            folder.uidTable.put(new Long(u.uid), msg);
                                        } else if ((item instanceof RFC822DATA) || (item instanceof BODY)) {
                                            if (item instanceof RFC822DATA) {
                                                headerStream = ((RFC822DATA) item).getByteArrayInputStream();
                                            } else {
                                                headerStream = ((BODY) item).getByteArrayInputStream();
                                            }
                                            InternetHeaders h = new InternetHeaders();
                                            h.load(headerStream);
                                            if (msg.headers == null || allHeaders) {
                                                msg.headers = h;
                                            } else {
                                                Enumeration e2 = h.getAllHeaders();
                                                while (e2.hasMoreElements()) {
                                                    Header he = (Header) e2.nextElement();
                                                    if (!msg.isHeaderLoaded(he.getName())) {
                                                        msg.headers.addHeader(he.getName(), he.getValue());
                                                    }
                                                }
                                            }
                                            if (allHeaders) {
                                                msg.setHeadersLoaded(true);
                                            } else {
                                                for (String str : hdrs) {
                                                    msg.setHeaderLoaded(str);
                                                }
                                            }
                                        }
                                    }
                                    if (unsolicitedFlags) {
                                        v.addElement(f);
                                    }
                                }
                            }
                        }
                        int size = v.size();
                        if (size != 0) {
                            Response[] responses = new Response[size];
                            v.copyInto(responses);
                            folder.handleResponses(responses);
                        }
                    }
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(folder, cex.getMessage());
                }
            }
        }
    }

    private synchronized void loadEnvelope() throws MessagingException {
        if (this.envelope == null) {
            synchronized (getMessageCacheLock()) {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    int seqnum = getSequenceNumber();
                    Response[] r = p.fetch(seqnum, EnvelopeCmd);
                    for (int i = 0; i < r.length; i++) {
                        if (r[i] != null && (r[i] instanceof FetchResponse) && ((FetchResponse) r[i]).getNumber() == seqnum) {
                            FetchResponse f = (FetchResponse) r[i];
                            int count = f.getItemCount();
                            for (int j = 0; j < count; j++) {
                                Item item = f.getItem(j);
                                if (item instanceof ENVELOPE) {
                                    this.envelope = (ENVELOPE) item;
                                } else if (item instanceof INTERNALDATE) {
                                    this.receivedDate = ((INTERNALDATE) item).getDate();
                                } else if (item instanceof RFC822SIZE) {
                                    this.size = ((RFC822SIZE) item).size;
                                }
                            }
                        }
                    }
                    p.notifyResponseHandlers(r);
                    p.handleResult(r[r.length - 1]);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this.folder, cex.getMessage());
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            if (this.envelope == null) {
                throw new MessagingException("Failed to load IMAP envelope");
            }
        }
    }

    private static String craftHeaderCmd(IMAPProtocol p, String[] hdrs) {
        StringBuffer sb;
        if (p.isREV1()) {
            sb = new StringBuffer("BODY.PEEK[HEADER.FIELDS (");
        } else {
            sb = new StringBuffer("RFC822.HEADER.LINES (");
        }
        for (int i = 0; i < hdrs.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(hdrs[i]);
        }
        if (p.isREV1()) {
            sb.append(")]");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

    private synchronized void loadBODYSTRUCTURE() throws MessagingException {
        if (this.bs == null) {
            synchronized (getMessageCacheLock()) {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    this.bs = p.fetchBodyStructure(getSequenceNumber());
                    if (this.bs == null) {
                        forceCheckExpunged();
                        throw new MessagingException("Unable to load BODYSTRUCTURE");
                    }
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this.folder, cex.getMessage());
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
    }

    private synchronized void loadHeaders() throws MessagingException {
        if (!this.headersLoaded) {
            InputStream is = null;
            synchronized (getMessageCacheLock()) {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    if (p.isREV1()) {
                        BODY b = p.peekBody(getSequenceNumber(), toSection("HEADER"));
                        if (b != null) {
                            is = b.getByteArrayInputStream();
                        }
                    } else {
                        RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "HEADER");
                        if (rd != null) {
                            is = rd.getByteArrayInputStream();
                        }
                    }
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this.folder, cex.getMessage());
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            if (is == null) {
                throw new MessagingException("Cannot load header");
            }
            this.headers = new InternetHeaders(is);
            this.headersLoaded = true;
        }
    }

    private synchronized void loadFlags() throws MessagingException {
        if (this.flags == null) {
            synchronized (getMessageCacheLock()) {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    this.flags = p.fetchFlags(getSequenceNumber());
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this.folder, cex.getMessage());
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized boolean areHeadersLoaded() {
        return this.headersLoaded;
    }

    private synchronized void setHeadersLoaded(boolean loaded) {
        this.headersLoaded = loaded;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized boolean isHeaderLoaded(String name) {
        boolean zContainsKey;
        if (this.headersLoaded) {
            zContainsKey = true;
        } else if (this.loadedHeaders != null) {
            zContainsKey = this.loadedHeaders.containsKey(name.toUpperCase(Locale.ENGLISH));
        } else {
            zContainsKey = false;
        }
        return zContainsKey;
    }

    private synchronized void setHeaderLoaded(String name) {
        if (this.loadedHeaders == null) {
            this.loadedHeaders = new Hashtable(1);
        }
        this.loadedHeaders.put(name.toUpperCase(Locale.ENGLISH), name);
    }

    private String toSection(String what) {
        return this.sectionId == null ? what : String.valueOf(this.sectionId) + "." + what;
    }

    private InternetAddress[] aaclone(InternetAddress[] aa) {
        if (aa == null) {
            return null;
        }
        return (InternetAddress[]) aa.clone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Flags _getFlags() {
        return this.flags;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ENVELOPE _getEnvelope() {
        return this.envelope;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BODYSTRUCTURE _getBodyStructure() {
        return this.bs;
    }

    void _setFlags(Flags flags) {
        this.flags = flags;
    }

    Session _getSession() {
        return this.session;
    }
}
