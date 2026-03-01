package com.sun.mail.imap.protocol;

import android.support.v7.internal.widget.ActivityChooserView;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.Literal;
import com.sun.mail.iap.LiteralException;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.ACL;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.Rights;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.mail.Flags;
import javax.mail.Quota;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

/* loaded from: classes.dex */
public class IMAPProtocol extends Protocol {
    private static final byte[] CRLF = {13, 10};
    private static final byte[] DONE = {68, 79, 78, 69, 13, 10};
    private boolean authenticated;
    private List authmechs;
    private ByteArray ba;
    private Map capabilities;
    private boolean connected;
    private String idleTag;
    private String name;
    private boolean rev1;
    private SaslAuthenticator saslAuthenticator;
    private String[] searchCharsets;

    public IMAPProtocol(String name, String host, int port, boolean debug, PrintStream out, Properties props, boolean isSSL) throws ProtocolException, IOException {
        super(host, port, debug, out, props, "mail." + name, isSSL);
        this.connected = false;
        this.rev1 = false;
        this.capabilities = null;
        this.authmechs = null;
        try {
            this.name = name;
            if (this.capabilities == null) {
                capability();
            }
            if (hasCapability("IMAP4rev1")) {
                this.rev1 = true;
            }
            this.searchCharsets = new String[2];
            this.searchCharsets[0] = "UTF-8";
            this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
            this.connected = true;
        } finally {
            if (!this.connected) {
                disconnect();
            }
        }
    }

    public void capability() throws ProtocolException {
        Response[] r = command("CAPABILITY", null);
        if (!r[r.length - 1].isOK()) {
            throw new ProtocolException(r[r.length - 1].toString());
        }
        this.capabilities = new HashMap(10);
        this.authmechs = new ArrayList(5);
        int len = r.length;
        for (int i = 0; i < len; i++) {
            if (r[i] instanceof IMAPResponse) {
                IMAPResponse ir = (IMAPResponse) r[i];
                if (ir.keyEquals("CAPABILITY")) {
                    parseCapabilities(ir);
                }
            }
        }
    }

    protected void setCapabilities(Response r) {
        byte b;
        do {
            b = r.readByte();
            if (b <= 0) {
                break;
            }
        } while (b != 91);
        if (b != 0) {
            String s = r.readAtom();
            if (s.equalsIgnoreCase("CAPABILITY")) {
                this.capabilities = new HashMap(10);
                this.authmechs = new ArrayList(5);
                parseCapabilities(r);
            }
        }
    }

    protected void parseCapabilities(Response r) {
        while (true) {
            String s = r.readAtom(']');
            if (s != null) {
                if (s.length() == 0) {
                    if (r.peekByte() != 93) {
                        r.skipToken();
                    } else {
                        return;
                    }
                } else {
                    this.capabilities.put(s.toUpperCase(Locale.ENGLISH), s);
                    if (s.regionMatches(true, 0, "AUTH=", 0, 5)) {
                        this.authmechs.add(s.substring(5));
                        if (this.debug) {
                            this.out.println("IMAP DEBUG: AUTH: " + s.substring(5));
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    @Override // com.sun.mail.iap.Protocol
    protected void processGreeting(Response r) throws ProtocolException {
        super.processGreeting(r);
        if (r.isOK()) {
            setCapabilities(r);
            return;
        }
        IMAPResponse ir = (IMAPResponse) r;
        if (ir.keyEquals("PREAUTH")) {
            this.authenticated = true;
            setCapabilities(r);
            return;
        }
        throw new ConnectionException(this, r);
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isREV1() {
        return this.rev1;
    }

    @Override // com.sun.mail.iap.Protocol
    protected boolean supportsNonSyncLiterals() {
        return hasCapability("LITERAL+");
    }

    @Override // com.sun.mail.iap.Protocol
    public Response readResponse() throws ProtocolException, IOException {
        return IMAPResponse.readResponse(this);
    }

    public boolean hasCapability(String c) {
        return this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
    }

    public Map getCapabilities() {
        return this.capabilities;
    }

    @Override // com.sun.mail.iap.Protocol
    public void disconnect() {
        super.disconnect();
        this.authenticated = false;
    }

    public void noop() throws ProtocolException {
        if (this.debug) {
            this.out.println("IMAP DEBUG: IMAPProtocol noop");
        }
        simpleCommand("NOOP", null);
    }

    public void logout() throws ProtocolException {
        Response[] r = command("LOGOUT", null);
        this.authenticated = false;
        notifyResponseHandlers(r);
        disconnect();
    }

    public void login(String u, String p) throws ProtocolException {
        Argument args = new Argument();
        args.writeString(u);
        args.writeString(p);
        Response[] r = command("LOGIN", args);
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        setCapabilities(r[r.length - 1]);
        this.authenticated = true;
    }

    public synchronized void authlogin(String u, String p) throws ProtocolException {
        String s;
        Vector v = new Vector();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            tag = writeCommand("AUTHENTICATE LOGIN", null);
        } catch (Exception ex) {
            r = Response.byeResponse(ex);
            done = true;
        }
        OutputStream os = getOutputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream b64os = new BASE64EncoderStream(bos, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        boolean first = true;
        while (!done) {
            try {
                r = readResponse();
                if (r.isContinuation()) {
                    if (first) {
                        s = u;
                        first = false;
                    } else {
                        s = p;
                    }
                    b64os.write(ASCIIUtility.getBytes(s));
                    b64os.flush();
                    bos.write(CRLF);
                    os.write(bos.toByteArray());
                    os.flush();
                    bos.reset();
                } else if (r.isTagged() && r.getTag().equals(tag)) {
                    done = true;
                } else if (r.isBYE()) {
                    done = true;
                } else {
                    v.addElement(r);
                }
            } catch (Exception ioex) {
                r = Response.byeResponse(ioex);
                done = true;
            }
        }
        Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        notifyResponseHandlers(responses);
        handleResult(r);
        setCapabilities(r);
        this.authenticated = true;
    }

    public synchronized void authplain(String authzid, String u, String p) throws ProtocolException {
        Vector v = new Vector();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            tag = writeCommand("AUTHENTICATE PLAIN", null);
        } catch (Exception ex) {
            r = Response.byeResponse(ex);
            done = true;
        }
        OutputStream os = getOutputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream b64os = new BASE64EncoderStream(bos, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        while (!done) {
            try {
                r = readResponse();
                if (r.isContinuation()) {
                    String s = String.valueOf(authzid) + "\u0000" + u + "\u0000" + p;
                    b64os.write(ASCIIUtility.getBytes(s));
                    b64os.flush();
                    bos.write(CRLF);
                    os.write(bos.toByteArray());
                    os.flush();
                    bos.reset();
                } else if (r.isTagged() && r.getTag().equals(tag)) {
                    done = true;
                } else if (r.isBYE()) {
                    done = true;
                } else {
                    v.addElement(r);
                }
            } catch (Exception ioex) {
                r = Response.byeResponse(ioex);
                done = true;
            }
        }
        Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        notifyResponseHandlers(responses);
        handleResult(r);
        setCapabilities(r);
        this.authenticated = true;
    }

    public void sasllogin(String[] allowed, String realm, String authzid, String u, String p) throws ProtocolException, NoSuchMethodException, ClassNotFoundException, SecurityException {
        List v;
        if (this.saslAuthenticator == null) {
            try {
                Class sac = Class.forName("com.sun.mail.imap.protocol.IMAPSaslAuthenticator");
                Constructor c = sac.getConstructor(IMAPProtocol.class, String.class, Properties.class, Boolean.TYPE, PrintStream.class, String.class);
                Object[] objArr = new Object[6];
                objArr[0] = this;
                objArr[1] = this.name;
                objArr[2] = this.props;
                objArr[3] = this.debug ? Boolean.TRUE : Boolean.FALSE;
                objArr[4] = this.out;
                objArr[5] = this.host;
                this.saslAuthenticator = (SaslAuthenticator) c.newInstance(objArr);
            } catch (Exception ex) {
                if (this.debug) {
                    this.out.println("IMAP DEBUG: Can't load SASL authenticator: " + ex);
                    return;
                }
                return;
            }
        }
        if (allowed != null && allowed.length > 0) {
            v = new ArrayList(allowed.length);
            for (int i = 0; i < allowed.length; i++) {
                if (this.authmechs.contains(allowed[i])) {
                    v.add(allowed[i]);
                }
            }
        } else {
            v = this.authmechs;
        }
        String[] mechs = (String[]) v.toArray(new String[v.size()]);
        if (this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p)) {
            this.authenticated = true;
        }
    }

    OutputStream getIMAPOutputStream() {
        return getOutputStream();
    }

    public void proxyauth(String u) throws ProtocolException {
        Argument args = new Argument();
        args.writeString(u);
        simpleCommand("PROXYAUTH", args);
    }

    public void startTLS() throws ProtocolException {
        try {
            super.startTLS("STARTTLS");
        } catch (ProtocolException pex) {
            throw pex;
        } catch (Exception ex) {
            Response[] r = {Response.byeResponse(ex)};
            notifyResponseHandlers(r);
            disconnect();
        }
    }

    public MailboxInfo select(String mbox) throws ProtocolException, IOException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Response[] r = command("SELECT", args);
        MailboxInfo minfo = new MailboxInfo(r);
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            if (response.toString().indexOf("READ-ONLY") != -1) {
                minfo.mode = 1;
            } else {
                minfo.mode = 2;
            }
        }
        handleResult(response);
        return minfo;
    }

    public MailboxInfo examine(String mbox) throws ProtocolException, IOException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Response[] r = command("EXAMINE", args);
        MailboxInfo minfo = new MailboxInfo(r);
        minfo.mode = 1;
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        return minfo;
    }

    public Status status(String mbox, String[] items) throws ProtocolException, IOException {
        if (!isREV1() && !hasCapability("IMAP4SUNVERSION")) {
            throw new BadCommandException("STATUS not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Argument itemArgs = new Argument();
        if (items == null) {
            items = Status.standardItems;
        }
        for (String str : items) {
            itemArgs.writeAtom(str);
        }
        args.writeArgument(itemArgs);
        Response[] r = command("STATUS", args);
        Status status = null;
        Response response = r[r.length - 1];
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("STATUS")) {
                        if (status == null) {
                            status = new Status(ir);
                        } else {
                            Status.add(status, new Status(ir));
                        }
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        return status;
    }

    public void create(String mbox) throws ProtocolException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        simpleCommand("CREATE", args);
    }

    public void delete(String mbox) throws ProtocolException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        simpleCommand("DELETE", args);
    }

    public void rename(String o, String n) throws ProtocolException {
        String o2 = BASE64MailboxEncoder.encode(o);
        String n2 = BASE64MailboxEncoder.encode(n);
        Argument args = new Argument();
        args.writeString(o2);
        args.writeString(n2);
        simpleCommand("RENAME", args);
    }

    public void subscribe(String mbox) throws ProtocolException {
        Argument args = new Argument();
        args.writeString(BASE64MailboxEncoder.encode(mbox));
        simpleCommand("SUBSCRIBE", args);
    }

    public void unsubscribe(String mbox) throws ProtocolException {
        Argument args = new Argument();
        args.writeString(BASE64MailboxEncoder.encode(mbox));
        simpleCommand("UNSUBSCRIBE", args);
    }

    public ListInfo[] list(String ref, String pattern) throws ProtocolException {
        return doList("LIST", ref, pattern);
    }

    public ListInfo[] lsub(String ref, String pattern) throws ProtocolException {
        return doList("LSUB", ref, pattern);
    }

    private ListInfo[] doList(String cmd, String ref, String pat) throws ProtocolException, IOException {
        String ref2 = BASE64MailboxEncoder.encode(ref);
        String pat2 = BASE64MailboxEncoder.encode(pat);
        Argument args = new Argument();
        args.writeString(ref2);
        args.writeString(pat2);
        Response[] r = command(cmd, args);
        ListInfo[] linfo = (ListInfo[]) null;
        Response response = r[r.length - 1];
        if (response.isOK()) {
            Vector v = new Vector(1);
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals(cmd)) {
                        v.addElement(new ListInfo(ir));
                        r[i] = null;
                    }
                }
            }
            if (v.size() > 0) {
                linfo = new ListInfo[v.size()];
                v.copyInto(linfo);
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        return linfo;
    }

    public void append(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
        appenduid(mbox, f, d, data, false);
    }

    public AppendUID appenduid(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
        return appenduid(mbox, f, d, data, true);
    }

    public AppendUID appenduid(String mbox, Flags f, Date d, Literal data, boolean uid) throws ProtocolException, IOException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        if (f != null) {
            if (f.contains(Flags.Flag.RECENT)) {
                Flags f2 = new Flags(f);
                f2.remove(Flags.Flag.RECENT);
                f = f2;
            }
            args.writeAtom(createFlagList(f));
        }
        if (d != null) {
            args.writeString(INTERNALDATE.format(d));
        }
        args.writeBytes(data);
        Response[] r = command("APPEND", args);
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        if (uid) {
            return getAppendUID(r[r.length - 1]);
        }
        return null;
    }

    private AppendUID getAppendUID(Response r) {
        byte b;
        if (!r.isOK()) {
            return null;
        }
        do {
            b = r.readByte();
            if (b <= 0) {
                break;
            }
        } while (b != 91);
        if (b == 0) {
            return null;
        }
        String s = r.readAtom();
        if (!s.equalsIgnoreCase("APPENDUID")) {
            return null;
        }
        long uidvalidity = r.readLong();
        long uid = r.readLong();
        return new AppendUID(uidvalidity, uid);
    }

    public void check() throws ProtocolException {
        simpleCommand("CHECK", null);
    }

    public void close() throws ProtocolException {
        simpleCommand("CLOSE", null);
    }

    public void expunge() throws ProtocolException {
        simpleCommand("EXPUNGE", null);
    }

    public void uidexpunge(UIDSet[] set) throws ProtocolException {
        if (!hasCapability("UIDPLUS")) {
            throw new BadCommandException("UID EXPUNGE not supported");
        }
        simpleCommand("UID EXPUNGE " + UIDSet.toString(set), null);
    }

    public BODYSTRUCTURE fetchBodyStructure(int msgno) throws ProtocolException {
        Response[] r = fetch(msgno, "BODYSTRUCTURE");
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODYSTRUCTURE) FetchResponse.getItem(r, msgno, BODYSTRUCTURE.class);
        }
        if (response.isNO()) {
            return null;
        }
        handleResult(response);
        return null;
    }

    public BODY peekBody(int msgno, String section) throws ProtocolException {
        return fetchBody(msgno, section, true);
    }

    public BODY fetchBody(int msgno, String section) throws ProtocolException {
        return fetchBody(msgno, section, false);
    }

    protected BODY fetchBody(int msgno, String section, boolean peek) throws ProtocolException {
        Response[] r;
        if (peek) {
            r = fetch(msgno, "BODY.PEEK[" + (section == null ? "]" : String.valueOf(section) + "]"));
        } else {
            r = fetch(msgno, "BODY[" + (section == null ? "]" : String.valueOf(section) + "]"));
        }
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODY) FetchResponse.getItem(r, msgno, BODY.class);
        }
        if (response.isNO()) {
            return null;
        }
        handleResult(response);
        return null;
    }

    public BODY peekBody(int msgno, String section, int start, int size) throws ProtocolException {
        return fetchBody(msgno, section, start, size, true, null);
    }

    public BODY fetchBody(int msgno, String section, int start, int size) throws ProtocolException {
        return fetchBody(msgno, section, start, size, false, null);
    }

    public BODY peekBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
        return fetchBody(msgno, section, start, size, true, ba);
    }

    public BODY fetchBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
        return fetchBody(msgno, section, start, size, false, ba);
    }

    protected BODY fetchBody(int msgno, String section, int start, int size, boolean peek, ByteArray ba) throws ProtocolException {
        this.ba = ba;
        Response[] r = fetch(msgno, String.valueOf(peek ? "BODY.PEEK[" : "BODY[") + (section == null ? "]<" : String.valueOf(section) + "]<") + String.valueOf(start) + "." + String.valueOf(size) + ">");
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODY) FetchResponse.getItem(r, msgno, BODY.class);
        }
        if (response.isNO()) {
            return null;
        }
        handleResult(response);
        return null;
    }

    @Override // com.sun.mail.iap.Protocol
    protected ByteArray getResponseBuffer() {
        ByteArray ret = this.ba;
        this.ba = null;
        return ret;
    }

    public RFC822DATA fetchRFC822(int msgno, String what) throws ProtocolException {
        Response[] r = fetch(msgno, what == null ? "RFC822" : "RFC822." + what);
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            return (RFC822DATA) FetchResponse.getItem(r, msgno, RFC822DATA.class);
        }
        if (response.isNO()) {
            return null;
        }
        handleResult(response);
        return null;
    }

    public Flags fetchFlags(int msgno) throws ProtocolException {
        Flags flags = null;
        Response[] r = fetch(msgno, "FLAGS");
        int i = 0;
        int len = r.length;
        while (true) {
            if (i >= len) {
                break;
            }
            if (r[i] != null && (r[i] instanceof FetchResponse) && ((FetchResponse) r[i]).getNumber() == msgno) {
                FetchResponse fr = (FetchResponse) r[i];
                flags = (Flags) fr.getItem(Flags.class);
                if (flags != null) {
                    r[i] = null;
                    break;
                }
            }
            i++;
        }
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        return flags;
    }

    public UID fetchUID(int msgno) throws ProtocolException {
        Response[] r = fetch(msgno, "UID");
        notifyResponseHandlers(r);
        Response response = r[r.length - 1];
        if (response.isOK()) {
            return (UID) FetchResponse.getItem(r, msgno, UID.class);
        }
        if (response.isNO()) {
            return null;
        }
        handleResult(response);
        return null;
    }

    public UID fetchSequenceNumber(long uid) throws ProtocolException {
        UID u = null;
        Response[] r = fetch(String.valueOf(uid), "UID", true);
        int len = r.length;
        for (int i = 0; i < len; i++) {
            if (r[i] != null && (r[i] instanceof FetchResponse)) {
                FetchResponse fr = (FetchResponse) r[i];
                u = (UID) fr.getItem(UID.class);
                if (u != null) {
                    if (u.uid == uid) {
                        break;
                    }
                    u = null;
                } else {
                    continue;
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        return u;
    }

    public UID[] fetchSequenceNumbers(long start, long end) throws ProtocolException {
        Response[] r = fetch(String.valueOf(String.valueOf(start)) + ":" + (end == -1 ? "*" : String.valueOf(end)), "UID", true);
        Vector v = new Vector();
        int len = r.length;
        for (int i = 0; i < len; i++) {
            if (r[i] != null && (r[i] instanceof FetchResponse)) {
                FetchResponse fr = (FetchResponse) r[i];
                UID u = (UID) fr.getItem(UID.class);
                if (u != null) {
                    v.addElement(u);
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        UID[] ua = new UID[v.size()];
        v.copyInto(ua);
        return ua;
    }

    public UID[] fetchSequenceNumbers(long[] uids) throws ProtocolException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < uids.length; i++) {
            if (i > 0) {
                sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
            }
            sb.append(String.valueOf(uids[i]));
        }
        Response[] r = fetch(sb.toString(), "UID", true);
        Vector v = new Vector();
        int len = r.length;
        for (int i2 = 0; i2 < len; i2++) {
            if (r[i2] != null && (r[i2] instanceof FetchResponse)) {
                FetchResponse fr = (FetchResponse) r[i2];
                UID u = (UID) fr.getItem(UID.class);
                if (u != null) {
                    v.addElement(u);
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
        UID[] ua = new UID[v.size()];
        v.copyInto(ua);
        return ua;
    }

    public Response[] fetch(MessageSet[] msgsets, String what) throws ProtocolException {
        return fetch(MessageSet.toString(msgsets), what, false);
    }

    public Response[] fetch(int start, int end, String what) throws ProtocolException {
        return fetch(String.valueOf(String.valueOf(start)) + ":" + String.valueOf(end), what, false);
    }

    public Response[] fetch(int msg, String what) throws ProtocolException {
        return fetch(String.valueOf(msg), what, false);
    }

    private Response[] fetch(String msgSequence, String what, boolean uid) throws ProtocolException {
        return uid ? command("UID FETCH " + msgSequence + " (" + what + ")", null) : command("FETCH " + msgSequence + " (" + what + ")", null);
    }

    public void copy(MessageSet[] msgsets, String mbox) throws ProtocolException, IOException {
        copy(MessageSet.toString(msgsets), mbox);
    }

    public void copy(int start, int end, String mbox) throws ProtocolException, IOException {
        copy(String.valueOf(String.valueOf(start)) + ":" + String.valueOf(end), mbox);
    }

    private void copy(String msgSequence, String mbox) throws ProtocolException, IOException {
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeAtom(msgSequence);
        args.writeString(mbox2);
        simpleCommand("COPY", args);
    }

    public void storeFlags(MessageSet[] msgsets, Flags flags, boolean set) throws ProtocolException {
        storeFlags(MessageSet.toString(msgsets), flags, set);
    }

    public void storeFlags(int start, int end, Flags flags, boolean set) throws ProtocolException {
        storeFlags(String.valueOf(String.valueOf(start)) + ":" + String.valueOf(end), flags, set);
    }

    public void storeFlags(int msg, Flags flags, boolean set) throws ProtocolException {
        storeFlags(String.valueOf(msg), flags, set);
    }

    private void storeFlags(String msgset, Flags flags, boolean set) throws ProtocolException {
        Response[] r;
        if (set) {
            r = command("STORE " + msgset + " +FLAGS " + createFlagList(flags), null);
        } else {
            r = command("STORE " + msgset + " -FLAGS " + createFlagList(flags), null);
        }
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
    }

    private String createFlagList(Flags flags) {
        String s;
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Flags.Flag[] sf = flags.getSystemFlags();
        boolean first = true;
        for (Flags.Flag f : sf) {
            if (f == Flags.Flag.ANSWERED) {
                s = "\\Answered";
            } else if (f == Flags.Flag.DELETED) {
                s = "\\Deleted";
            } else if (f == Flags.Flag.DRAFT) {
                s = "\\Draft";
            } else if (f == Flags.Flag.FLAGGED) {
                s = "\\Flagged";
            } else if (f == Flags.Flag.RECENT) {
                s = "\\Recent";
            } else if (f == Flags.Flag.SEEN) {
                s = "\\Seen";
            }
            if (first) {
                first = false;
            } else {
                sb.append(' ');
            }
            sb.append(s);
        }
        String[] uf = flags.getUserFlags();
        for (String str : uf) {
            if (first) {
                first = false;
            } else {
                sb.append(' ');
            }
            sb.append(str);
        }
        sb.append(")");
        return sb.toString();
    }

    public int[] search(MessageSet[] msgsets, SearchTerm term) throws SearchException, ProtocolException {
        return search(MessageSet.toString(msgsets), term);
    }

    public int[] search(SearchTerm term) throws SearchException, ProtocolException {
        return search("ALL", term);
    }

    private int[] search(String msgSequence, SearchTerm term) throws SearchException, ProtocolException {
        if (SearchSequence.isAscii(term)) {
            try {
                return issueSearch(msgSequence, term, null);
            } catch (IOException e) {
            }
        }
        for (int i = 0; i < this.searchCharsets.length; i++) {
            if (this.searchCharsets[i] != null) {
                try {
                    return issueSearch(msgSequence, term, this.searchCharsets[i]);
                } catch (CommandFailedException e2) {
                    this.searchCharsets[i] = null;
                } catch (ProtocolException pex) {
                    throw pex;
                } catch (IOException e3) {
                } catch (SearchException sex) {
                    throw sex;
                }
            }
        }
        throw new SearchException("Search failed");
    }

    private int[] issueSearch(String msgSequence, SearchTerm term, String charset) throws SearchException, ProtocolException, IOException {
        Response[] r;
        Argument args = SearchSequence.generateSequence(term, charset == null ? null : MimeUtility.javaCharset(charset));
        args.writeAtom(msgSequence);
        if (charset == null) {
            r = command("SEARCH", args);
        } else {
            r = command("SEARCH CHARSET " + charset, args);
        }
        Response response = r[r.length - 1];
        int[] matches = (int[]) null;
        if (response.isOK()) {
            Vector v = new Vector();
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("SEARCH")) {
                        while (true) {
                            int num = ir.readNumber();
                            if (num == -1) {
                                break;
                            }
                            v.addElement(new Integer(num));
                        }
                        r[i] = null;
                    }
                }
            }
            int vsize = v.size();
            matches = new int[vsize];
            for (int i2 = 0; i2 < vsize; i2++) {
                matches[i2] = ((Integer) v.elementAt(i2)).intValue();
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        return matches;
    }

    public Namespaces namespace() throws ProtocolException {
        if (!hasCapability("NAMESPACE")) {
            throw new BadCommandException("NAMESPACE not supported");
        }
        Response[] r = command("NAMESPACE", null);
        Namespaces namespace = null;
        Response response = r[r.length - 1];
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("NAMESPACE")) {
                        if (namespace == null) {
                            namespace = new Namespaces(ir);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        return namespace;
    }

    public Quota[] getQuotaRoot(String mbox) throws ProtocolException, IOException {
        if (!hasCapability("QUOTA")) {
            throw new BadCommandException("GETQUOTAROOT not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Response[] r = command("GETQUOTAROOT", args);
        Response response = r[r.length - 1];
        Hashtable tab = new Hashtable();
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("QUOTAROOT")) {
                        ir.readAtomString();
                        while (true) {
                            String root = ir.readAtomString();
                            if (root == null) {
                                break;
                            }
                            tab.put(root, new Quota(root));
                        }
                        r[i] = null;
                    } else if (ir.keyEquals("QUOTA")) {
                        Quota quota = parseQuota(ir);
                        Quota q = (Quota) tab.get(quota.quotaRoot);
                        if (q != null) {
                            Quota.Resource[] resourceArr = q.resources;
                        }
                        tab.put(quota.quotaRoot, quota);
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        Quota[] qa = new Quota[tab.size()];
        Enumeration e = tab.elements();
        int i2 = 0;
        while (e.hasMoreElements()) {
            qa[i2] = (Quota) e.nextElement();
            i2++;
        }
        return qa;
    }

    public Quota[] getQuota(String root) throws ProtocolException {
        if (!hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        Argument args = new Argument();
        args.writeString(root);
        Response[] r = command("GETQUOTA", args);
        Vector v = new Vector();
        Response response = r[r.length - 1];
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("QUOTA")) {
                        Quota quota = parseQuota(ir);
                        v.addElement(quota);
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        Quota[] qa = new Quota[v.size()];
        v.copyInto(qa);
        return qa;
    }

    public void setQuota(Quota quota) throws ProtocolException {
        if (!hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        Argument args = new Argument();
        args.writeString(quota.quotaRoot);
        Argument qargs = new Argument();
        if (quota.resources != null) {
            for (int i = 0; i < quota.resources.length; i++) {
                qargs.writeAtom(quota.resources[i].name);
                qargs.writeNumber(quota.resources[i].limit);
            }
        }
        args.writeArgument(qargs);
        Response[] r = command("SETQUOTA", args);
        Response response = r[r.length - 1];
        notifyResponseHandlers(r);
        handleResult(response);
    }

    private Quota parseQuota(Response r) throws ParsingException {
        String quotaRoot = r.readAtomString();
        Quota q = new Quota(quotaRoot);
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("parse error in QUOTA");
        }
        Vector v = new Vector();
        while (r.peekByte() != 41) {
            String name = r.readAtom();
            if (name != null) {
                long usage = r.readLong();
                long limit = r.readLong();
                Quota.Resource res = new Quota.Resource(name, usage, limit);
                v.addElement(res);
            }
        }
        r.readByte();
        q.resources = new Quota.Resource[v.size()];
        v.copyInto(q.resources);
        return q;
    }

    public void setACL(String mbox, char modifier, ACL acl) throws ProtocolException {
        if (!hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        args.writeString(acl.getName());
        String rights = acl.getRights().toString();
        if (modifier == '+' || modifier == '-') {
            rights = String.valueOf(modifier) + rights;
        }
        args.writeString(rights);
        Response[] r = command("SETACL", args);
        Response response = r[r.length - 1];
        notifyResponseHandlers(r);
        handleResult(response);
    }

    public void deleteACL(String mbox, String user) throws ProtocolException {
        if (!hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        args.writeString(user);
        Response[] r = command("DELETEACL", args);
        Response response = r[r.length - 1];
        notifyResponseHandlers(r);
        handleResult(response);
    }

    public ACL[] getACL(String mbox) throws ProtocolException, IOException {
        String rights;
        if (!hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Response[] r = command("GETACL", args);
        Response response = r[r.length - 1];
        Vector v = new Vector();
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("ACL")) {
                        ir.readAtomString();
                        while (true) {
                            String name = ir.readAtomString();
                            if (name == null || (rights = ir.readAtomString()) == null) {
                                break;
                            }
                            ACL acl = new ACL(name, new Rights(rights));
                            v.addElement(acl);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        ACL[] aa = new ACL[v.size()];
        v.copyInto(aa);
        return aa;
    }

    public Rights[] listRights(String mbox, String user) throws ProtocolException, IOException {
        if (!hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        args.writeString(user);
        Response[] r = command("LISTRIGHTS", args);
        Response response = r[r.length - 1];
        Vector v = new Vector();
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("LISTRIGHTS")) {
                        ir.readAtomString();
                        ir.readAtomString();
                        while (true) {
                            String rights = ir.readAtomString();
                            if (rights == null) {
                                break;
                            }
                            v.addElement(new Rights(rights));
                        }
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        Rights[] ra = new Rights[v.size()];
        v.copyInto(ra);
        return ra;
    }

    public Rights myRights(String mbox) throws ProtocolException, IOException {
        if (!hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        String mbox2 = BASE64MailboxEncoder.encode(mbox);
        Argument args = new Argument();
        args.writeString(mbox2);
        Response[] r = command("MYRIGHTS", args);
        Response response = r[r.length - 1];
        Rights rights = null;
        if (response.isOK()) {
            int len = r.length;
            for (int i = 0; i < len; i++) {
                if (r[i] instanceof IMAPResponse) {
                    IMAPResponse ir = (IMAPResponse) r[i];
                    if (ir.keyEquals("MYRIGHTS")) {
                        ir.readAtomString();
                        String rs = ir.readAtomString();
                        if (rights == null) {
                            rights = new Rights(rs);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        notifyResponseHandlers(r);
        handleResult(response);
        return rights;
    }

    public synchronized void idleStart() throws ProtocolException {
        Response r;
        if (!hasCapability("IDLE")) {
            throw new BadCommandException("IDLE not supported");
        }
        try {
            try {
                this.idleTag = writeCommand("IDLE", null);
                r = readResponse();
            } catch (Exception ex) {
                r = Response.byeResponse(ex);
            }
        } catch (LiteralException lex) {
            r = lex.getResponse();
        }
        if (!r.isContinuation()) {
            handleResult(r);
        }
    }

    public synchronized Response readIdleResponse() {
        Response responseByeResponse;
        if (this.idleTag == null) {
            responseByeResponse = null;
        } else {
            try {
                try {
                    responseByeResponse = readResponse();
                } catch (ProtocolException pex) {
                    responseByeResponse = Response.byeResponse(pex);
                }
            } catch (IOException ioex) {
                responseByeResponse = Response.byeResponse(ioex);
            }
        }
        return responseByeResponse;
    }

    public boolean processIdleResponse(Response r) throws ProtocolException {
        Response[] responses = {r};
        boolean done = false;
        notifyResponseHandlers(responses);
        if (r.isBYE()) {
            done = true;
        }
        if (r.isTagged() && r.getTag().equals(this.idleTag)) {
            done = true;
        }
        if (done) {
            this.idleTag = null;
        }
        handleResult(r);
        return !done;
    }

    public void idleAbort() throws ProtocolException {
        OutputStream os = getOutputStream();
        try {
            os.write(DONE);
            os.flush();
        } catch (IOException e) {
        }
    }
}
