package com.sun.mail.smtp;

import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;
import org.cybergarage.multiscreenhttp.HTTPStatus;

/* loaded from: classes.dex */
public class SMTPTransport extends Transport {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final byte[] CRLF;
    private static final String UNKNOWN = "UNKNOWN";
    private static char[] hexchar;
    private static final String[] ignoreList;
    private Address[] addresses;
    private SMTPOutputStream dataStream;
    private int defaultPort;
    private MessagingException exception;
    private Hashtable extMap;
    private Address[] invalidAddr;
    private boolean isSSL;
    private int lastReturnCode;
    private String lastServerResponse;
    private LineInputStream lineInputStream;
    private String localHostName;
    private DigestMD5 md5support;
    private MimeMessage message;
    private String name;
    private PrintStream out;
    private boolean quitWait;
    private boolean reportSuccess;
    private String saslRealm;
    private boolean sendPartiallyFailed;
    private BufferedInputStream serverInput;
    private OutputStream serverOutput;
    private Socket serverSocket;
    private boolean useRset;
    private boolean useStartTLS;
    private Address[] validSentAddr;
    private Address[] validUnsentAddr;

    static {
        $assertionsDisabled = !SMTPTransport.class.desiredAssertionStatus();
        ignoreList = new String[]{"Bcc", "Content-Length"};
        CRLF = new byte[]{13, 10};
        hexchar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    public SMTPTransport(Session session, URLName urlname) {
        this(session, urlname, "smtp", 25, false);
    }

    protected SMTPTransport(Session session, URLName urlname, String name, int defaultPort, boolean isSSL) {
        super(session, urlname);
        this.name = "smtp";
        this.defaultPort = 25;
        this.isSSL = false;
        this.sendPartiallyFailed = false;
        this.quitWait = false;
        this.saslRealm = "UNKNOWN";
        name = urlname != null ? urlname.getProtocol() : name;
        this.name = name;
        this.defaultPort = defaultPort;
        this.isSSL = isSSL;
        this.out = session.getDebugOut();
        String s = session.getProperty("mail." + name + ".quitwait");
        this.quitWait = s == null || s.equalsIgnoreCase("true");
        String s2 = session.getProperty("mail." + name + ".reportsuccess");
        this.reportSuccess = s2 != null && s2.equalsIgnoreCase("true");
        String s3 = session.getProperty("mail." + name + ".starttls.enable");
        this.useStartTLS = s3 != null && s3.equalsIgnoreCase("true");
        String s4 = session.getProperty("mail." + name + ".userset");
        this.useRset = s4 != null && s4.equalsIgnoreCase("true");
    }

    public synchronized String getLocalHost() {
        try {
            if (this.localHostName == null || this.localHostName.length() <= 0) {
                this.localHostName = this.session.getProperty("mail." + this.name + ".localhost");
            }
            if (this.localHostName == null || this.localHostName.length() <= 0) {
                this.localHostName = this.session.getProperty("mail." + this.name + ".localaddress");
            }
            if (this.localHostName == null || this.localHostName.length() <= 0) {
                InetAddress localHost = InetAddress.getLocalHost();
                this.localHostName = localHost.getHostName();
                if (this.localHostName == null) {
                    this.localHostName = "[" + localHost.getHostAddress() + "]";
                }
            }
        } catch (UnknownHostException e) {
        }
        return this.localHostName;
    }

    public synchronized void setLocalHost(String localhost) {
        this.localHostName = localhost;
    }

    public synchronized void connect(Socket socket) throws MessagingException {
        this.serverSocket = socket;
        super.connect();
    }

    public synchronized String getSASLRealm() {
        if (this.saslRealm == "UNKNOWN") {
            this.saslRealm = this.session.getProperty("mail." + this.name + ".sasl.realm");
            if (this.saslRealm == null) {
                this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm");
            }
        }
        return this.saslRealm;
    }

    public synchronized void setSASLRealm(String saslRealm) {
        this.saslRealm = saslRealm;
    }

    public synchronized boolean getReportSuccess() {
        return this.reportSuccess;
    }

    public synchronized void setReportSuccess(boolean reportSuccess) {
        this.reportSuccess = reportSuccess;
    }

    public synchronized boolean getStartTLS() {
        return this.useStartTLS;
    }

    public synchronized void setStartTLS(boolean useStartTLS) {
        this.useStartTLS = useStartTLS;
    }

    public synchronized boolean getUseRset() {
        return this.useRset;
    }

    public synchronized void setUseRset(boolean useRset) {
        this.useRset = useRset;
    }

    public synchronized String getLastServerResponse() {
        return this.lastServerResponse;
    }

    public synchronized int getLastReturnCode() {
        return this.lastReturnCode;
    }

    private synchronized DigestMD5 getMD5() {
        if (this.md5support == null) {
            this.md5support = new DigestMD5(this.debug ? this.out : null);
        }
        return this.md5support;
    }

    /* JADX WARN: Removed duplicated region for block: B:115:0x0295  */
    @Override // javax.mail.Service
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean protocolConnect(java.lang.String r19, int r20, java.lang.String r21, java.lang.String r22) throws javax.mail.MessagingException, java.lang.NumberFormatException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 731
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.smtp.SMTPTransport.protocolConnect(java.lang.String, int, java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARN: Finally extract failed */
    @Override // javax.mail.Transport
    public synchronized void sendMessage(Message message, Address[] addresses) throws MessagingException {
        checkConnected();
        if (!(message instanceof MimeMessage)) {
            if (this.debug) {
                this.out.println("DEBUG SMTP: Can only send RFC822 msgs");
            }
            throw new MessagingException("SMTP can only send RFC822 messages");
        }
        for (int i = 0; i < addresses.length; i++) {
            if (!(addresses[i] instanceof InternetAddress)) {
                throw new MessagingException(addresses[i] + " is not an InternetAddress");
            }
        }
        this.message = (MimeMessage) message;
        this.addresses = addresses;
        this.validUnsentAddr = addresses;
        expandGroups();
        boolean use8bit = false;
        if (message instanceof SMTPMessage) {
            use8bit = ((SMTPMessage) message).getAllow8bitMIME();
        }
        if (!use8bit) {
            String ebStr = this.session.getProperty("mail." + this.name + ".allow8bitmime");
            use8bit = ebStr != null && ebStr.equalsIgnoreCase("true");
        }
        if (this.debug) {
            this.out.println("DEBUG SMTP: use8bit " + use8bit);
        }
        if (use8bit && supportsExtension("8BITMIME") && convertTo8Bit(this.message)) {
            try {
                this.message.saveChanges();
            } catch (MessagingException e) {
            }
        }
        try {
            try {
                mailFrom();
                rcptTo();
                this.message.writeTo(data(), ignoreList);
                finishData();
                if (this.sendPartiallyFailed) {
                    if (this.debug) {
                        this.out.println("DEBUG SMTP: Sending partially failed because of invalid destination addresses");
                    }
                    notifyTransportListeners(3, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                    throw new SMTPSendFailedException(".", this.lastReturnCode, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
                }
                notifyTransportListeners(1, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                this.invalidAddr = null;
                this.validUnsentAddr = null;
                this.validSentAddr = null;
                this.addresses = null;
                this.message = null;
                this.exception = null;
                this.sendPartiallyFailed = false;
            } catch (IOException ex) {
                if (this.debug) {
                    ex.printStackTrace(this.out);
                }
                try {
                    closeConnection();
                } catch (MessagingException e2) {
                }
                notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                throw new MessagingException("IOException while sending message", ex);
            } catch (MessagingException mex) {
                if (this.debug) {
                    mex.printStackTrace(this.out);
                }
                notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                throw mex;
            }
        } catch (Throwable th) {
            this.invalidAddr = null;
            this.validUnsentAddr = null;
            this.validSentAddr = null;
            this.addresses = null;
            this.message = null;
            this.exception = null;
            this.sendPartiallyFailed = false;
            throw th;
        }
    }

    @Override // javax.mail.Service
    public synchronized void close() throws MessagingException {
        int resp;
        if (super.isConnected()) {
            try {
                if (this.serverSocket != null) {
                    sendCommand("QUIT");
                    if (this.quitWait && (resp = readServerResponse()) != 221 && resp != -1) {
                        this.out.println("DEBUG SMTP: QUIT failed with " + resp);
                    }
                }
            } finally {
                closeConnection();
            }
        }
    }

    private void closeConnection() throws MessagingException {
        try {
            try {
                if (this.serverSocket != null) {
                    this.serverSocket.close();
                }
            } catch (IOException e) {
                throw new MessagingException("Server Close Failed", e);
            }
        } finally {
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (super.isConnected()) {
                super.close();
            }
        }
    }

    @Override // javax.mail.Service
    public synchronized boolean isConnected() {
        boolean z = false;
        synchronized (this) {
            if (super.isConnected()) {
                try {
                    if (this.useRset) {
                        sendCommand("RSET");
                    } else {
                        sendCommand("NOOP");
                    }
                    int resp = readServerResponse();
                    if (resp >= 0 && resp != 421) {
                        z = true;
                    } else {
                        try {
                            closeConnection();
                        } catch (MessagingException e) {
                        }
                    }
                } catch (Exception e2) {
                    try {
                        closeConnection();
                    } catch (MessagingException e3) {
                    }
                }
            }
        }
        return z;
    }

    private void expandGroups() {
        Vector groups = null;
        for (int i = 0; i < this.addresses.length; i++) {
            InternetAddress a = (InternetAddress) this.addresses[i];
            if (a.isGroup()) {
                if (groups == null) {
                    groups = new Vector();
                    for (int k = 0; k < i; k++) {
                        groups.addElement(this.addresses[k]);
                    }
                }
                try {
                    InternetAddress[] ia = a.getGroup(true);
                    if (ia != null) {
                        for (InternetAddress internetAddress : ia) {
                            groups.addElement(internetAddress);
                        }
                    } else {
                        groups.addElement(a);
                    }
                } catch (ParseException e) {
                    groups.addElement(a);
                }
            } else if (groups != null) {
                groups.addElement(a);
            }
        }
        if (groups != null) {
            InternetAddress[] newa = new InternetAddress[groups.size()];
            groups.copyInto(newa);
            this.addresses = newa;
        }
    }

    private boolean convertTo8Bit(MimePart part) {
        boolean changed = false;
        try {
            if (part.isMimeType("text/*")) {
                String enc = part.getEncoding();
                if (enc == null) {
                    return false;
                }
                if (!enc.equalsIgnoreCase("quoted-printable") && !enc.equalsIgnoreCase("base64")) {
                    return false;
                }
                InputStream is = part.getInputStream();
                if (!is8Bit(is)) {
                    return false;
                }
                part.setContent(part.getContent(), part.getContentType());
                part.setHeader("Content-Transfer-Encoding", "8bit");
                return true;
            }
            if (!part.isMimeType("multipart/*")) {
                return false;
            }
            MimeMultipart mp = (MimeMultipart) part.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                if (convertTo8Bit((MimePart) mp.getBodyPart(i))) {
                    changed = true;
                }
            }
            return changed;
        } catch (IOException e) {
            return false;
        } catch (MessagingException e2) {
            return false;
        }
    }

    private boolean is8Bit(InputStream is) throws IOException {
        int linelen = 0;
        boolean need8bit = false;
        while (true) {
            try {
                int b = is.read();
                if (b >= 0) {
                    int b2 = b & 255;
                    if (b2 == 13 || b2 == 10) {
                        linelen = 0;
                    } else {
                        if (b2 == 0) {
                            return false;
                        }
                        linelen++;
                        if (linelen > 998) {
                            return false;
                        }
                    }
                    if (b2 > 127) {
                        need8bit = true;
                    }
                } else {
                    if (this.debug && need8bit) {
                        this.out.println("DEBUG SMTP: found an 8bit part");
                        return need8bit;
                    }
                    return need8bit;
                }
            } catch (IOException e) {
                return false;
            }
        }
    }

    @Override // javax.mail.Service
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            closeConnection();
        } catch (MessagingException e) {
        }
    }

    protected void helo(String domain) throws MessagingException {
        if (domain != null) {
            issueCommand("HELO " + domain, KeyInfo.KEYCODE_M);
        } else {
            issueCommand("HELO", KeyInfo.KEYCODE_M);
        }
    }

    protected boolean ehlo(String domain) throws MessagingException, IOException, NumberFormatException {
        String cmd;
        if (domain != null) {
            cmd = "EHLO " + domain;
        } else {
            cmd = "EHLO";
        }
        sendCommand(cmd);
        int resp = readServerResponse();
        if (resp == 250) {
            BufferedReader rd = new BufferedReader(new StringReader(this.lastServerResponse));
            this.extMap = new Hashtable();
            boolean first = true;
            while (true) {
                try {
                    String line = rd.readLine();
                    if (line == null) {
                        break;
                    }
                    if (first) {
                        first = false;
                    } else if (line.length() >= 5) {
                        String line2 = line.substring(4);
                        int i = line2.indexOf(32);
                        String arg = "";
                        if (i > 0) {
                            arg = line2.substring(i + 1);
                            line2 = line2.substring(0, i);
                        }
                        if (this.debug) {
                            this.out.println("DEBUG SMTP: Found extension \"" + line2 + "\", arg \"" + arg + "\"");
                        }
                        this.extMap.put(line2.toUpperCase(Locale.ENGLISH), arg);
                    }
                } catch (IOException e) {
                }
            }
        }
        return resp == 250;
    }

    protected void mailFrom() throws MessagingException, IOException, NumberFormatException {
        Address me;
        Address[] fa;
        String from = null;
        if (this.message instanceof SMTPMessage) {
            from = ((SMTPMessage) this.message).getEnvelopeFrom();
        }
        if (from == null || from.length() <= 0) {
            from = this.session.getProperty("mail." + this.name + ".from");
        }
        if (from == null || from.length() <= 0) {
            if (this.message != null && (fa = this.message.getFrom()) != null && fa.length > 0) {
                me = fa[0];
            } else {
                me = InternetAddress.getLocalAddress(this.session);
            }
            if (me != null) {
                from = ((InternetAddress) me).getAddress();
            } else {
                throw new MessagingException("can't determine local email address");
            }
        }
        String cmd = "MAIL FROM:" + normalizeAddress(from);
        if (supportsExtension("DSN")) {
            String ret = null;
            if (this.message instanceof SMTPMessage) {
                ret = ((SMTPMessage) this.message).getDSNRet();
            }
            if (ret == null) {
                ret = this.session.getProperty("mail." + this.name + ".dsn.ret");
            }
            if (ret != null) {
                cmd = String.valueOf(cmd) + " RET=" + ret;
            }
        }
        if (supportsExtension("AUTH")) {
            String submitter = null;
            if (this.message instanceof SMTPMessage) {
                submitter = ((SMTPMessage) this.message).getSubmitter();
            }
            if (submitter == null) {
                submitter = this.session.getProperty("mail." + this.name + ".submitter");
            }
            if (submitter != null) {
                try {
                    String s = xtext(submitter);
                    cmd = String.valueOf(cmd) + " AUTH=" + s;
                } catch (IllegalArgumentException ex) {
                    if (this.debug) {
                        this.out.println("DEBUG SMTP: ignoring invalid submitter: " + submitter + ", Exception: " + ex);
                    }
                }
            }
        }
        String ext = null;
        if (this.message instanceof SMTPMessage) {
            ext = ((SMTPMessage) this.message).getMailExtension();
        }
        if (ext == null) {
            ext = this.session.getProperty("mail." + this.name + ".mailextension");
        }
        if (ext != null && ext.length() > 0) {
            cmd = String.valueOf(cmd) + " " + ext;
        }
        issueSendCommand(cmd, KeyInfo.KEYCODE_M);
    }

    protected void rcptTo() throws MessagingException, IOException, NumberFormatException {
        Vector valid = new Vector();
        Vector validUnsent = new Vector();
        Vector invalid = new Vector();
        MessagingException mex = null;
        boolean sendFailed = false;
        this.invalidAddr = null;
        this.validUnsentAddr = null;
        this.validSentAddr = null;
        boolean sendPartial = false;
        if (this.message instanceof SMTPMessage) {
            sendPartial = ((SMTPMessage) this.message).getSendPartial();
        }
        if (!sendPartial) {
            String sp = this.session.getProperty("mail." + this.name + ".sendpartial");
            sendPartial = sp != null && sp.equalsIgnoreCase("true");
        }
        if (this.debug && sendPartial) {
            this.out.println("DEBUG SMTP: sendPartial set");
        }
        boolean dsn = false;
        String notify = null;
        if (supportsExtension("DSN")) {
            if (this.message instanceof SMTPMessage) {
                notify = ((SMTPMessage) this.message).getDSNNotify();
            }
            if (notify == null) {
                notify = this.session.getProperty("mail." + this.name + ".dsn.notify");
            }
            if (notify != null) {
                dsn = true;
            }
        }
        for (int i = 0; i < this.addresses.length; i++) {
            InternetAddress ia = (InternetAddress) this.addresses[i];
            String cmd = "RCPT TO:" + normalizeAddress(ia.getAddress());
            if (dsn) {
                cmd = String.valueOf(cmd) + " NOTIFY=" + notify;
            }
            sendCommand(cmd);
            int retCode = readServerResponse();
            switch (retCode) {
                case KeyInfo.KEYCODE_M /* 250 */:
                case 251:
                    valid.addElement(ia);
                    if (this.reportSuccess) {
                        MessagingException sfex = new SMTPAddressSucceededException(ia, cmd, retCode, this.lastServerResponse);
                        if (mex == null) {
                            mex = sfex;
                            break;
                        } else {
                            mex.setNextException(sfex);
                            break;
                        }
                    } else {
                        break;
                    }
                case 450:
                case 451:
                case 452:
                case 552:
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    validUnsent.addElement(ia);
                    MessagingException sfex2 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex2;
                        break;
                    } else {
                        mex.setNextException(sfex2);
                        break;
                    }
                case 501:
                case HTTPStatus.SERVICE_UNAVAILABLE /* 503 */:
                case 550:
                case 551:
                case 553:
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    invalid.addElement(ia);
                    MessagingException sfex3 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex3;
                        break;
                    } else {
                        mex.setNextException(sfex3);
                        break;
                    }
                default:
                    if (retCode >= 400 && retCode <= 499) {
                        validUnsent.addElement(ia);
                    } else if (retCode >= 500 && retCode <= 599) {
                        invalid.addElement(ia);
                    } else {
                        if (this.debug) {
                            this.out.println("DEBUG SMTP: got response code " + retCode + ", with response: " + this.lastServerResponse);
                        }
                        String _lsr = this.lastServerResponse;
                        int _lrc = this.lastReturnCode;
                        if (this.serverSocket != null) {
                            issueCommand("RSET", KeyInfo.KEYCODE_M);
                        }
                        this.lastServerResponse = _lsr;
                        this.lastReturnCode = _lrc;
                        throw new SMTPAddressFailedException(ia, cmd, retCode, _lsr);
                    }
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    MessagingException sfex4 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex4;
                        break;
                    } else {
                        mex.setNextException(sfex4);
                        break;
                    }
                    break;
            }
        }
        if (sendPartial && valid.size() == 0) {
            sendFailed = true;
        }
        if (sendFailed) {
            this.invalidAddr = new Address[invalid.size()];
            invalid.copyInto(this.invalidAddr);
            this.validUnsentAddr = new Address[valid.size() + validUnsent.size()];
            int i2 = 0;
            int j = 0;
            while (j < valid.size()) {
                this.validUnsentAddr[i2] = (Address) valid.elementAt(j);
                j++;
                i2++;
            }
            int j2 = 0;
            while (j2 < validUnsent.size()) {
                this.validUnsentAddr[i2] = (Address) validUnsent.elementAt(j2);
                j2++;
                i2++;
            }
        } else if (this.reportSuccess || (sendPartial && (invalid.size() > 0 || validUnsent.size() > 0))) {
            this.sendPartiallyFailed = true;
            this.exception = mex;
            this.invalidAddr = new Address[invalid.size()];
            invalid.copyInto(this.invalidAddr);
            this.validUnsentAddr = new Address[validUnsent.size()];
            validUnsent.copyInto(this.validUnsentAddr);
            this.validSentAddr = new Address[valid.size()];
            valid.copyInto(this.validSentAddr);
        } else {
            this.validSentAddr = this.addresses;
        }
        if (this.debug) {
            if (this.validSentAddr != null && this.validSentAddr.length > 0) {
                this.out.println("DEBUG SMTP: Verified Addresses");
                for (int l = 0; l < this.validSentAddr.length; l++) {
                    this.out.println("DEBUG SMTP:   " + this.validSentAddr[l]);
                }
            }
            if (this.validUnsentAddr != null && this.validUnsentAddr.length > 0) {
                this.out.println("DEBUG SMTP: Valid Unsent Addresses");
                for (int j3 = 0; j3 < this.validUnsentAddr.length; j3++) {
                    this.out.println("DEBUG SMTP:   " + this.validUnsentAddr[j3]);
                }
            }
            if (this.invalidAddr != null && this.invalidAddr.length > 0) {
                this.out.println("DEBUG SMTP: Invalid Addresses");
                for (int k = 0; k < this.invalidAddr.length; k++) {
                    this.out.println("DEBUG SMTP:   " + this.invalidAddr[k]);
                }
            }
        }
        if (sendFailed) {
            if (this.debug) {
                this.out.println("DEBUG SMTP: Sending failed because of invalid destination addresses");
            }
            notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
            String lsr = this.lastServerResponse;
            int lrc = this.lastReturnCode;
            try {
                try {
                    if (this.serverSocket != null) {
                        issueCommand("RSET", KeyInfo.KEYCODE_M);
                    }
                    this.lastServerResponse = lsr;
                    this.lastReturnCode = lrc;
                } catch (MessagingException e) {
                    try {
                        close();
                    } catch (MessagingException ex2) {
                        if (this.debug) {
                            ex2.printStackTrace(this.out);
                        }
                    }
                    this.lastServerResponse = lsr;
                    this.lastReturnCode = lrc;
                }
                throw new SendFailedException("Invalid Addresses", mex, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
            } catch (Throwable th) {
                this.lastServerResponse = lsr;
                this.lastReturnCode = lrc;
                throw th;
            }
        }
    }

    protected OutputStream data() throws MessagingException, IOException, NumberFormatException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        issueSendCommand("DATA", 354);
        this.dataStream = new SMTPOutputStream(this.serverOutput);
        return this.dataStream;
    }

    protected void finishData() throws MessagingException, IOException, NumberFormatException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        this.dataStream.ensureAtBOL();
        issueSendCommand(".", KeyInfo.KEYCODE_M);
    }

    protected void startTLS() throws MessagingException {
        issueCommand("STARTTLS", KeyInfo.KEYCODE_T);
        try {
            this.serverSocket = SocketFetcher.startTLS(this.serverSocket, this.session.getProperties(), "mail." + this.name);
            initStreams();
        } catch (IOException ioex) {
            closeConnection();
            throw new MessagingException("Could not convert socket to TLS", ioex);
        }
    }

    private void openServer(String server, int port) throws MessagingException, NumberFormatException, IOException {
        if (this.debug) {
            this.out.println("DEBUG SMTP: trying to connect to host \"" + server + "\", port " + port + ", isSSL " + this.isSSL);
        }
        try {
            Properties props = this.session.getProperties();
            this.serverSocket = SocketFetcher.getSocket(server, port, props, "mail." + this.name, this.isSSL);
            int port2 = this.serverSocket.getPort();
            initStreams();
            int r = readServerResponse();
            if (r != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.debug) {
                    this.out.println("DEBUG SMTP: could not connect to host \"" + server + "\", port: " + port2 + ", response: " + r + "\n");
                }
                throw new MessagingException("Could not connect to SMTP host: " + server + ", port: " + port2 + ", response: " + r);
            }
            if (this.debug) {
                this.out.println("DEBUG SMTP: connected to host \"" + server + "\", port: " + port2 + "\n");
            }
        } catch (UnknownHostException uhex) {
            throw new MessagingException("Unknown SMTP host: " + server, uhex);
        } catch (IOException ioe) {
            throw new MessagingException("Could not connect to SMTP host: " + server + ", port: " + port, ioe);
        }
    }

    private void openServer() throws MessagingException, NumberFormatException, IOException {
        try {
            int port = this.serverSocket.getPort();
            String server = this.serverSocket.getInetAddress().getHostName();
            if (this.debug) {
                this.out.println("DEBUG SMTP: starting protocol to host \"" + server + "\", port " + port);
            }
            initStreams();
            int r = readServerResponse();
            if (r != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.debug) {
                    this.out.println("DEBUG SMTP: got bad greeting from host \"" + server + "\", port: " + port + ", response: " + r + "\n");
                }
                throw new MessagingException("Got bad greeting from SMTP host: " + server + ", port: " + port + ", response: " + r);
            }
            if (this.debug) {
                this.out.println("DEBUG SMTP: protocol started to host \"" + server + "\", port: " + port + "\n");
            }
        } catch (IOException ioe) {
            throw new MessagingException("Could not start protocol to SMTP host: UNKNOWN, port: -1", ioe);
        }
    }

    private void initStreams() throws IOException {
        Properties props = this.session.getProperties();
        PrintStream out = this.session.getDebugOut();
        boolean debug = this.session.getDebug();
        String s = props.getProperty("mail.debug.quote");
        boolean quote = s != null && s.equalsIgnoreCase("true");
        TraceInputStream traceInput = new TraceInputStream(this.serverSocket.getInputStream(), out);
        traceInput.setTrace(debug);
        traceInput.setQuote(quote);
        TraceOutputStream traceOutput = new TraceOutputStream(this.serverSocket.getOutputStream(), out);
        traceOutput.setTrace(debug);
        traceOutput.setQuote(quote);
        this.serverOutput = new BufferedOutputStream(traceOutput);
        this.serverInput = new BufferedInputStream(traceInput);
        this.lineInputStream = new LineInputStream(this.serverInput);
    }

    public synchronized void issueCommand(String cmd, int expect) throws MessagingException {
        sendCommand(cmd);
        if (readServerResponse() != expect) {
            throw new MessagingException(this.lastServerResponse);
        }
    }

    private void issueSendCommand(String cmd, int expect) throws MessagingException, IOException, NumberFormatException {
        sendCommand(cmd);
        int ret = readServerResponse();
        if (ret != expect) {
            int vsl = this.validSentAddr == null ? 0 : this.validSentAddr.length;
            int vul = this.validUnsentAddr == null ? 0 : this.validUnsentAddr.length;
            Address[] valid = new Address[vsl + vul];
            if (vsl > 0) {
                System.arraycopy(this.validSentAddr, 0, valid, 0, vsl);
            }
            if (vul > 0) {
                System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul);
            }
            this.validSentAddr = null;
            this.validUnsentAddr = valid;
            if (this.debug) {
                this.out.println("DEBUG SMTP: got response code " + ret + ", with response: " + this.lastServerResponse);
            }
            String _lsr = this.lastServerResponse;
            int _lrc = this.lastReturnCode;
            if (this.serverSocket != null) {
                issueCommand("RSET", KeyInfo.KEYCODE_M);
            }
            this.lastServerResponse = _lsr;
            this.lastReturnCode = _lrc;
            throw new SMTPSendFailedException(cmd, ret, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
        }
    }

    public synchronized int simpleCommand(String cmd) throws MessagingException {
        sendCommand(cmd);
        return readServerResponse();
    }

    protected int simpleCommand(byte[] cmd) throws MessagingException, IOException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        sendCommand(cmd);
        return readServerResponse();
    }

    protected void sendCommand(String cmd) throws MessagingException, IOException {
        sendCommand(ASCIIUtility.getBytes(cmd));
    }

    private void sendCommand(byte[] cmdBytes) throws MessagingException, IOException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        try {
            this.serverOutput.write(cmdBytes);
            this.serverOutput.write(CRLF);
            this.serverOutput.flush();
        } catch (IOException ex) {
            throw new MessagingException("Can't send command to SMTP host", ex);
        }
    }

    protected int readServerResponse() throws MessagingException, NumberFormatException {
        String line;
        int returnCode;
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        StringBuffer buf = new StringBuffer(100);
        do {
            try {
                line = this.lineInputStream.readLine();
                if (line == null) {
                    String serverResponse = buf.toString();
                    if (serverResponse.length() == 0) {
                        serverResponse = "[EOF]";
                    }
                    this.lastServerResponse = serverResponse;
                    this.lastReturnCode = -1;
                    if (!this.debug) {
                        return -1;
                    }
                    this.out.println("DEBUG SMTP: EOF: " + serverResponse);
                    return -1;
                }
                buf.append(line);
                buf.append("\n");
            } catch (IOException ioex) {
                if (this.debug) {
                    this.out.println("DEBUG SMTP: exception reading response: " + ioex);
                }
                this.lastServerResponse = "";
                this.lastReturnCode = 0;
                throw new MessagingException("Exception reading response", ioex);
            }
        } while (isNotLastLine(line));
        String serverResponse2 = buf.toString();
        if (serverResponse2 != null && serverResponse2.length() >= 3) {
            try {
                returnCode = Integer.parseInt(serverResponse2.substring(0, 3));
            } catch (NumberFormatException e) {
                try {
                    close();
                } catch (MessagingException mex) {
                    if (this.debug) {
                        mex.printStackTrace(this.out);
                    }
                }
                returnCode = -1;
            } catch (StringIndexOutOfBoundsException e2) {
                try {
                    close();
                } catch (MessagingException mex2) {
                    if (this.debug) {
                        mex2.printStackTrace(this.out);
                    }
                }
                returnCode = -1;
            }
        } else {
            returnCode = -1;
        }
        if (returnCode == -1 && this.debug) {
            this.out.println("DEBUG SMTP: bad server response: " + serverResponse2);
        }
        this.lastServerResponse = serverResponse2;
        this.lastReturnCode = returnCode;
        return returnCode;
    }

    protected void checkConnected() {
        if (!super.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
    }

    private boolean isNotLastLine(String line) {
        return line != null && line.length() >= 4 && line.charAt(3) == '-';
    }

    private String normalizeAddress(String addr) {
        if (!addr.startsWith("<") && !addr.endsWith(">")) {
            return "<" + addr + ">";
        }
        return addr;
    }

    public boolean supportsExtension(String ext) {
        return (this.extMap == null || this.extMap.get(ext.toUpperCase(Locale.ENGLISH)) == null) ? false : true;
    }

    public String getExtensionParameter(String ext) {
        if (this.extMap == null) {
            return null;
        }
        return (String) this.extMap.get(ext.toUpperCase(Locale.ENGLISH));
    }

    protected boolean supportsAuthentication(String auth) {
        String a;
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (this.extMap == null || (a = (String) this.extMap.get("AUTH")) == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(a);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equalsIgnoreCase(auth)) {
                return true;
            }
        }
        return false;
    }

    protected static String xtext(String s) {
        StringBuffer sb = null;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 128) {
                throw new IllegalArgumentException("Non-ASCII character in SMTP submitter: " + s);
            }
            if (c < '!' || c > '~' || c == '+' || c == '=') {
                if (sb == null) {
                    sb = new StringBuffer(s.length() + 4);
                    sb.append(s.substring(0, i));
                }
                sb.append('+');
                sb.append(hexchar[(c & 240) >> 4]);
                sb.append(hexchar[c & 15]);
            } else if (sb != null) {
                sb.append(c);
            }
        }
        return sb != null ? sb.toString() : s;
    }
}
