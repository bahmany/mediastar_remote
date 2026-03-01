package com.sun.mail.imap;

import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ResponseHandler;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.Namespaces;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.QuotaAwareStore;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.URLName;

/* loaded from: classes.dex */
public class IMAPStore extends Store implements QuotaAwareStore, ResponseHandler {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int RESPONSE = 1000;
    private int appendBufferSize;
    private String authorizationID;
    private int blksize;
    private volatile boolean connected;
    private int defaultPort;
    private boolean disableAuthLogin;
    private boolean disableAuthPlain;
    private boolean enableImapEvents;
    private boolean enableSASL;
    private boolean enableStartTLS;
    private boolean forcePasswordRefresh;
    private String host;
    private boolean isSSL;
    private int minIdleTime;
    private String name;
    private Namespaces namespaces;
    private PrintStream out;
    private String password;
    private ConnectionPool pool;
    private int port;
    private String proxyAuthUser;
    private String[] saslMechanisms;
    private String saslRealm;
    private int statusCacheTimeout;
    private String user;

    static {
        $assertionsDisabled = !IMAPStore.class.desiredAssertionStatus();
    }

    static class ConnectionPool {
        private static final int ABORTING = 2;
        private static final int IDLE = 1;
        private static final int RUNNING = 0;
        private Vector folders;
        private IMAPProtocol idleProtocol;
        private long lastTimePruned;
        private Vector authenticatedConnections = new Vector();
        private boolean separateStoreConnection = false;
        private boolean storeConnectionInUse = false;
        private long clientTimeoutInterval = 45000;
        private long serverTimeoutInterval = 1800000;
        private int poolSize = 1;
        private long pruningInterval = 60000;
        private boolean debug = false;
        private int idleState = 0;

        ConnectionPool() {
        }
    }

    public IMAPStore(Session session, URLName url) {
        this(session, url, "imap", 143, false);
    }

    protected IMAPStore(Session session, URLName url, String name, int defaultPort, boolean isSSL) throws NumberFormatException {
        String s;
        super(session, url);
        this.name = "imap";
        this.defaultPort = 143;
        this.isSSL = false;
        this.port = -1;
        this.blksize = 16384;
        this.statusCacheTimeout = 1000;
        this.appendBufferSize = -1;
        this.minIdleTime = 10;
        this.disableAuthLogin = false;
        this.disableAuthPlain = false;
        this.enableStartTLS = false;
        this.enableSASL = false;
        this.forcePasswordRefresh = false;
        this.enableImapEvents = false;
        this.connected = false;
        this.pool = new ConnectionPool();
        name = url != null ? url.getProtocol() : name;
        this.name = name;
        this.defaultPort = defaultPort;
        this.isSSL = isSSL;
        this.pool.lastTimePruned = System.currentTimeMillis();
        this.debug = session.getDebug();
        this.out = session.getDebugOut();
        if (this.out == null) {
            this.out = System.out;
        }
        String s2 = session.getProperty("mail." + name + ".connectionpool.debug");
        if (s2 != null && s2.equalsIgnoreCase("true")) {
            this.pool.debug = true;
        }
        String s3 = session.getProperty("mail." + name + ".partialfetch");
        if (s3 != null && s3.equalsIgnoreCase("false")) {
            this.blksize = -1;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.partialfetch: false");
            }
        } else {
            String s4 = session.getProperty("mail." + name + ".fetchsize");
            if (s4 != null) {
                this.blksize = Integer.parseInt(s4);
            }
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.fetchsize: " + this.blksize);
            }
        }
        String s5 = session.getProperty("mail." + name + ".statuscachetimeout");
        if (s5 != null) {
            this.statusCacheTimeout = Integer.parseInt(s5);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.statuscachetimeout: " + this.statusCacheTimeout);
            }
        }
        String s6 = session.getProperty("mail." + name + ".appendbuffersize");
        if (s6 != null) {
            this.appendBufferSize = Integer.parseInt(s6);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.appendbuffersize: " + this.appendBufferSize);
            }
        }
        String s7 = session.getProperty("mail." + name + ".minidletime");
        if (s7 != null) {
            this.minIdleTime = Integer.parseInt(s7);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.minidletime: " + this.minIdleTime);
            }
        }
        String s8 = session.getProperty("mail." + name + ".connectionpoolsize");
        if (s8 != null) {
            try {
                int size = Integer.parseInt(s8);
                if (size > 0) {
                    this.pool.poolSize = size;
                }
            } catch (NumberFormatException e) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.connectionpoolsize: " + this.pool.poolSize);
            }
        }
        String s9 = session.getProperty("mail." + name + ".connectionpooltimeout");
        if (s9 != null) {
            try {
                int connectionPoolTimeout = Integer.parseInt(s9);
                if (connectionPoolTimeout > 0) {
                    this.pool.clientTimeoutInterval = connectionPoolTimeout;
                }
            } catch (NumberFormatException e2) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.connectionpooltimeout: " + this.pool.clientTimeoutInterval);
            }
        }
        String s10 = session.getProperty("mail." + name + ".servertimeout");
        if (s10 != null) {
            try {
                int serverTimeout = Integer.parseInt(s10);
                if (serverTimeout > 0) {
                    this.pool.serverTimeoutInterval = serverTimeout;
                }
            } catch (NumberFormatException e3) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.servertimeout: " + this.pool.serverTimeoutInterval);
            }
        }
        String s11 = session.getProperty("mail." + name + ".separatestoreconnection");
        if (s11 != null && s11.equalsIgnoreCase("true")) {
            if (this.pool.debug) {
                this.out.println("DEBUG: dedicate a store connection");
            }
            this.pool.separateStoreConnection = true;
        }
        String s12 = session.getProperty("mail." + name + ".proxyauth.user");
        if (s12 != null) {
            this.proxyAuthUser = s12;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.proxyauth.user: " + this.proxyAuthUser);
            }
        }
        String s13 = session.getProperty("mail." + name + ".auth.login.disable");
        if (s13 != null && s13.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: disable AUTH=LOGIN");
            }
            this.disableAuthLogin = true;
        }
        String s14 = session.getProperty("mail." + name + ".auth.plain.disable");
        if (s14 != null && s14.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: disable AUTH=PLAIN");
            }
            this.disableAuthPlain = true;
        }
        String s15 = session.getProperty("mail." + name + ".starttls.enable");
        if (s15 != null && s15.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable STARTTLS");
            }
            this.enableStartTLS = true;
        }
        String s16 = session.getProperty("mail." + name + ".sasl.enable");
        if (s16 != null && s16.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable SASL");
            }
            this.enableSASL = true;
        }
        if (this.enableSASL && (s = session.getProperty("mail." + name + ".sasl.mechanisms")) != null && s.length() > 0) {
            if (this.debug) {
                this.out.println("DEBUG: SASL mechanisms allowed: " + s);
            }
            Vector v = new Vector(5);
            StringTokenizer st = new StringTokenizer(s, " ,");
            while (st.hasMoreTokens()) {
                String m = st.nextToken();
                if (m.length() > 0) {
                    v.addElement(m);
                }
            }
            this.saslMechanisms = new String[v.size()];
            v.copyInto(this.saslMechanisms);
        }
        String s17 = session.getProperty("mail." + name + ".sasl.authorizationid");
        if (s17 != null) {
            this.authorizationID = s17;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.sasl.authorizationid: " + this.authorizationID);
            }
        }
        String s18 = session.getProperty("mail." + name + ".sasl.realm");
        if (s18 != null) {
            this.saslRealm = s18;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.sasl.realm: " + this.saslRealm);
            }
        }
        String s19 = session.getProperty("mail." + name + ".forcepasswordrefresh");
        if (s19 != null && s19.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable forcePasswordRefresh");
            }
            this.forcePasswordRefresh = true;
        }
        String s20 = session.getProperty("mail." + name + ".enableimapevents");
        if (s20 != null && s20.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable IMAP events");
            }
            this.enableImapEvents = true;
        }
    }

    @Override // javax.mail.Service
    protected synchronized boolean protocolConnect(String host, int pport, String user, String password) throws Throwable {
        boolean z;
        IMAPProtocol protocol;
        boolean poolEmpty;
        try {
            if (host == null || password == null || user == null) {
                if (this.debug) {
                    this.out.println("DEBUG: protocolConnect returning false, host=" + host + ", user=" + user + ", password=" + (password != null ? "<non-null>" : "<null>"));
                }
                z = false;
            } else {
                if (pport != -1) {
                    this.port = pport;
                } else {
                    String portstring = this.session.getProperty("mail." + this.name + ".port");
                    if (portstring != null) {
                        this.port = Integer.parseInt(portstring);
                    }
                }
                if (this.port == -1) {
                    this.port = this.defaultPort;
                }
                try {
                    try {
                        synchronized (this.pool) {
                            poolEmpty = this.pool.authenticatedConnections.isEmpty();
                        }
                        if (poolEmpty) {
                            protocol = new IMAPProtocol(this.name, host, this.port, this.session.getDebug(), this.session.getDebugOut(), this.session.getProperties(), this.isSSL);
                            try {
                                if (this.debug) {
                                    this.out.println("DEBUG: protocolConnect login, host=" + host + ", user=" + user + ", password=<non-null>");
                                }
                                login(protocol, user, password);
                                protocol.addResponseHandler(this);
                                this.host = host;
                                this.user = user;
                                this.password = password;
                                synchronized (this.pool) {
                                    this.pool.authenticatedConnections.addElement(protocol);
                                }
                            } catch (CommandFailedException e) {
                                cex = e;
                                if (protocol != null) {
                                    protocol.disconnect();
                                }
                                throw new AuthenticationFailedException(cex.getResponse().getRest());
                            } catch (ProtocolException e2) {
                                pex = e2;
                                throw new MessagingException(pex.getMessage(), pex);
                            } catch (IOException e3) {
                                ioex = e3;
                                throw new MessagingException(ioex.getMessage(), ioex);
                            }
                        }
                        this.connected = true;
                        z = true;
                    } catch (CommandFailedException e4) {
                        cex = e4;
                        protocol = null;
                    } catch (ProtocolException e5) {
                        pex = e5;
                    } catch (IOException e6) {
                        ioex = e6;
                    }
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
            return z;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void login(IMAPProtocol p, String u, String pw) throws ProtocolException, NoSuchMethodException, ClassNotFoundException, SecurityException {
        String authzid;
        if (this.enableStartTLS && p.hasCapability("STARTTLS")) {
            p.startTLS();
            p.capability();
        }
        if (!p.isAuthenticated()) {
            p.getCapabilities().put("__PRELOGIN__", "");
            if (this.authorizationID != null) {
                authzid = this.authorizationID;
            } else if (this.proxyAuthUser != null) {
                authzid = this.proxyAuthUser;
            } else {
                authzid = u;
            }
            if (this.enableSASL) {
                p.sasllogin(this.saslMechanisms, this.saslRealm, authzid, u, pw);
            }
            if (!p.isAuthenticated()) {
                if (p.hasCapability("AUTH=PLAIN") && !this.disableAuthPlain) {
                    p.authplain(authzid, u, pw);
                } else if ((p.hasCapability("AUTH-LOGIN") || p.hasCapability("AUTH=LOGIN")) && !this.disableAuthLogin) {
                    p.authlogin(u, pw);
                } else if (!p.hasCapability("LOGINDISABLED")) {
                    p.login(u, pw);
                } else {
                    throw new ProtocolException("No login methods supported!");
                }
            }
            if (this.proxyAuthUser != null) {
                p.proxyauth(this.proxyAuthUser);
            }
            if (p.hasCapability("__PRELOGIN__")) {
                try {
                    p.capability();
                } catch (ConnectionException cex) {
                    throw cex;
                } catch (ProtocolException e) {
                }
            }
        }
    }

    public synchronized void setUsername(String user) {
        this.user = user;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x013a A[Catch: all -> 0x00c6, TryCatch #4 {all -> 0x00c6, blocks: (B:43:0x010c, B:46:0x012d, B:47:0x0130, B:48:0x0135, B:50:0x013a, B:52:0x0144, B:53:0x0150, B:54:0x015d, B:59:0x016c, B:25:0x00af, B:27:0x00be, B:28:0x00c5, B:30:0x00c7, B:37:0x00d1), top: B:77:0x010c, inners: #6 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x00be A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    com.sun.mail.imap.protocol.IMAPProtocol getProtocol(com.sun.mail.imap.IMAPFolder r21) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 382
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.getProtocol(com.sun.mail.imap.IMAPFolder):com.sun.mail.imap.protocol.IMAPProtocol");
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0062 A[Catch: all -> 0x0056, TRY_ENTER, TryCatch #2 {all -> 0x0056, blocks: (B:13:0x0045, B:15:0x004e, B:16:0x0055, B:25:0x0062, B:26:0x006e, B:29:0x0077, B:30:0x007c, B:31:0x007f, B:38:0x00b5, B:40:0x00c3, B:23:0x005d, B:18:0x0057), top: B:54:0x0045 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x004e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    com.sun.mail.imap.protocol.IMAPProtocol getStoreProtocol() throws java.lang.Throwable {
        /*
            r11 = this;
            r0 = 0
            r9 = r0
        L2:
            if (r9 == 0) goto L5
            return r9
        L5:
            com.sun.mail.imap.IMAPStore$ConnectionPool r10 = r11.pool
            monitor-enter(r10)
            r11.waitIfIdle()     // Catch: java.lang.Throwable -> Lcd
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> Lcd
            java.util.Vector r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r1)     // Catch: java.lang.Throwable -> Lcd
            boolean r1 = r1.isEmpty()     // Catch: java.lang.Throwable -> Lcd
            if (r1 == 0) goto L82
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> Lcd
            boolean r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$3(r1)     // Catch: java.lang.Throwable -> Lcd
            if (r1 == 0) goto L26
            java.io.PrintStream r1 = r11.out     // Catch: java.lang.Throwable -> Lcd
            java.lang.String r2 = "DEBUG: getStoreProtocol() - no connections in the pool, creating a new one"
            r1.println(r2)     // Catch: java.lang.Throwable -> Lcd
        L26:
            com.sun.mail.imap.protocol.IMAPProtocol r0 = new com.sun.mail.imap.protocol.IMAPProtocol     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            java.lang.String r1 = r11.name     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            java.lang.String r2 = r11.host     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            int r3 = r11.port     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            javax.mail.Session r4 = r11.session     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            boolean r4 = r4.getDebug()     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            javax.mail.Session r5 = r11.session     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            java.io.PrintStream r5 = r5.getDebugOut()     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            javax.mail.Session r6 = r11.session     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            java.util.Properties r6 = r6.getProperties()     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            boolean r7 = r11.isSSL     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> Lcd
            java.lang.String r1 = r11.user     // Catch: java.lang.Throwable -> L56 java.lang.Exception -> Ld2
            java.lang.String r2 = r11.password     // Catch: java.lang.Throwable -> L56 java.lang.Exception -> Ld2
            r11.login(r0, r1, r2)     // Catch: java.lang.Throwable -> L56 java.lang.Exception -> Ld2
        L4c:
            if (r0 != 0) goto L62
            com.sun.mail.iap.ConnectionException r1 = new com.sun.mail.iap.ConnectionException     // Catch: java.lang.Throwable -> L56
            java.lang.String r2 = "failed to create new store connection"
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L56
            throw r1     // Catch: java.lang.Throwable -> L56
        L56:
            r1 = move-exception
        L57:
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L56
            throw r1
        L59:
            r8 = move-exception
            r0 = r9
        L5b:
            if (r0 == 0) goto L60
            r0.logout()     // Catch: java.lang.Throwable -> L56 java.lang.Exception -> Lcb
        L60:
            r0 = 0
            goto L4c
        L62:
            r0.addResponseHandler(r11)     // Catch: java.lang.Throwable -> L56
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> L56
            java.util.Vector r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r1)     // Catch: java.lang.Throwable -> L56
            r1.addElement(r0)     // Catch: java.lang.Throwable -> L56
        L6e:
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> L56
            boolean r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$12(r1)     // Catch: java.lang.Throwable -> L56
            if (r1 == 0) goto Lb5
            r0 = 0
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> L56 java.lang.InterruptedException -> Ld0
            r1.wait()     // Catch: java.lang.Throwable -> L56 java.lang.InterruptedException -> Ld0
        L7c:
            r11.timeoutConnections()     // Catch: java.lang.Throwable -> L56
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L56
            r9 = r0
            goto L2
        L82:
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> Lcd
            boolean r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$3(r1)     // Catch: java.lang.Throwable -> Lcd
            if (r1 == 0) goto La8
            java.io.PrintStream r1 = r11.out     // Catch: java.lang.Throwable -> Lcd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lcd
            java.lang.String r3 = "DEBUG: getStoreProtocol() - connection available -- size: "
            r2.<init>(r3)     // Catch: java.lang.Throwable -> Lcd
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r11.pool     // Catch: java.lang.Throwable -> Lcd
            java.util.Vector r3 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r3)     // Catch: java.lang.Throwable -> Lcd
            int r3 = r3.size()     // Catch: java.lang.Throwable -> Lcd
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Lcd
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> Lcd
            r1.println(r2)     // Catch: java.lang.Throwable -> Lcd
        La8:
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> Lcd
            java.util.Vector r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r1)     // Catch: java.lang.Throwable -> Lcd
            java.lang.Object r0 = r1.firstElement()     // Catch: java.lang.Throwable -> Lcd
            com.sun.mail.imap.protocol.IMAPProtocol r0 = (com.sun.mail.imap.protocol.IMAPProtocol) r0     // Catch: java.lang.Throwable -> Lcd
            goto L6e
        Lb5:
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> L56
            r2 = 1
            com.sun.mail.imap.IMAPStore.ConnectionPool.access$15(r1, r2)     // Catch: java.lang.Throwable -> L56
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool     // Catch: java.lang.Throwable -> L56
            boolean r1 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$3(r1)     // Catch: java.lang.Throwable -> L56
            if (r1 == 0) goto L7c
            java.io.PrintStream r1 = r11.out     // Catch: java.lang.Throwable -> L56
            java.lang.String r2 = "DEBUG: getStoreProtocol() -- storeConnectionInUse"
            r1.println(r2)     // Catch: java.lang.Throwable -> L56
            goto L7c
        Lcb:
            r1 = move-exception
            goto L60
        Lcd:
            r1 = move-exception
            r0 = r9
            goto L57
        Ld0:
            r1 = move-exception
            goto L7c
        Ld2:
            r8 = move-exception
            goto L5b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.getStoreProtocol():com.sun.mail.imap.protocol.IMAPProtocol");
    }

    boolean allowReadOnlySelect() {
        String s = this.session.getProperty("mail." + this.name + ".allowreadonlyselect");
        return s != null && s.equalsIgnoreCase("true");
    }

    boolean hasSeparateStoreConnection() {
        return this.pool.separateStoreConnection;
    }

    boolean getConnectionPoolDebug() {
        return this.pool.debug;
    }

    boolean isConnectionPoolFull() {
        boolean z;
        synchronized (this.pool) {
            if (this.pool.debug) {
                this.out.println("DEBUG: current size: " + this.pool.authenticatedConnections.size() + "   pool size: " + this.pool.poolSize);
            }
            z = this.pool.authenticatedConnections.size() >= this.pool.poolSize;
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0041 A[Catch: all -> 0x0060, TryCatch #0 {, blocks: (B:5:0x0005, B:7:0x000b, B:9:0x001b, B:16:0x004f, B:18:0x0053, B:19:0x005a, B:10:0x0039, B:12:0x0041, B:13:0x004a, B:14:0x004d), top: B:26:0x0005 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void releaseProtocol(com.sun.mail.imap.IMAPFolder r5, com.sun.mail.imap.protocol.IMAPProtocol r6) {
        /*
            r4 = this;
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r4.pool
            monitor-enter(r1)
            if (r6 == 0) goto L39
            boolean r0 = r4.isConnectionPoolFull()     // Catch: java.lang.Throwable -> L60
            if (r0 != 0) goto L4f
            r6.addResponseHandler(r4)     // Catch: java.lang.Throwable -> L60
            com.sun.mail.imap.IMAPStore$ConnectionPool r0 = r4.pool     // Catch: java.lang.Throwable -> L60
            java.util.Vector r0 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r0)     // Catch: java.lang.Throwable -> L60
            r0.addElement(r6)     // Catch: java.lang.Throwable -> L60
            boolean r0 = r4.debug     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L39
            java.io.PrintStream r0 = r4.out     // Catch: java.lang.Throwable -> L60
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L60
            java.lang.String r3 = "DEBUG: added an Authenticated connection -- size: "
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L60
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r4.pool     // Catch: java.lang.Throwable -> L60
            java.util.Vector r3 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$10(r3)     // Catch: java.lang.Throwable -> L60
            int r3 = r3.size()     // Catch: java.lang.Throwable -> L60
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L60
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L60
            r0.println(r2)     // Catch: java.lang.Throwable -> L60
        L39:
            com.sun.mail.imap.IMAPStore$ConnectionPool r0 = r4.pool     // Catch: java.lang.Throwable -> L60
            java.util.Vector r0 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$13(r0)     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L4a
            com.sun.mail.imap.IMAPStore$ConnectionPool r0 = r4.pool     // Catch: java.lang.Throwable -> L60
            java.util.Vector r0 = com.sun.mail.imap.IMAPStore.ConnectionPool.access$13(r0)     // Catch: java.lang.Throwable -> L60
            r0.removeElement(r5)     // Catch: java.lang.Throwable -> L60
        L4a:
            r4.timeoutConnections()     // Catch: java.lang.Throwable -> L60
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L60
            return
        L4f:
            boolean r0 = r4.debug     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L5a
            java.io.PrintStream r0 = r4.out     // Catch: java.lang.Throwable -> L60
            java.lang.String r2 = "DEBUG: pool is full, not adding an Authenticated connection"
            r0.println(r2)     // Catch: java.lang.Throwable -> L60
        L5a:
            r6.logout()     // Catch: com.sun.mail.iap.ProtocolException -> L5e java.lang.Throwable -> L60
            goto L39
        L5e:
            r0 = move-exception
            goto L39
        L60:
            r0 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L60
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.releaseProtocol(com.sun.mail.imap.IMAPFolder, com.sun.mail.imap.protocol.IMAPProtocol):void");
    }

    void releaseStoreProtocol(IMAPProtocol protocol) {
        if (protocol != null) {
            synchronized (this.pool) {
                this.pool.storeConnectionInUse = false;
                this.pool.notifyAll();
                if (this.pool.debug) {
                    this.out.println("DEBUG: releaseStoreProtocol()");
                }
                timeoutConnections();
            }
        }
    }

    private void emptyConnectionPool(boolean force) {
        synchronized (this.pool) {
            for (int index = this.pool.authenticatedConnections.size() - 1; index >= 0; index--) {
                try {
                    IMAPProtocol p = (IMAPProtocol) this.pool.authenticatedConnections.elementAt(index);
                    p.removeResponseHandler(this);
                    if (force) {
                        p.disconnect();
                    } else {
                        p.logout();
                    }
                } catch (ProtocolException e) {
                }
            }
            this.pool.authenticatedConnections.removeAllElements();
        }
        if (this.pool.debug) {
            this.out.println("DEBUG: removed all authenticated connections");
        }
    }

    private void timeoutConnections() {
        synchronized (this.pool) {
            if (System.currentTimeMillis() - this.pool.lastTimePruned > this.pool.pruningInterval && this.pool.authenticatedConnections.size() > 1) {
                if (this.pool.debug) {
                    this.out.println("DEBUG: checking for connections to prune: " + (System.currentTimeMillis() - this.pool.lastTimePruned));
                    this.out.println("DEBUG: clientTimeoutInterval: " + this.pool.clientTimeoutInterval);
                }
                for (int index = this.pool.authenticatedConnections.size() - 1; index > 0; index--) {
                    IMAPProtocol p = (IMAPProtocol) this.pool.authenticatedConnections.elementAt(index);
                    if (this.pool.debug) {
                        this.out.println("DEBUG: protocol last used: " + (System.currentTimeMillis() - p.getTimestamp()));
                    }
                    if (System.currentTimeMillis() - p.getTimestamp() > this.pool.clientTimeoutInterval) {
                        if (this.pool.debug) {
                            this.out.println("DEBUG: authenticated connection timed out");
                            this.out.println("DEBUG: logging out the connection");
                        }
                        p.removeResponseHandler(this);
                        this.pool.authenticatedConnections.removeElementAt(index);
                        try {
                            p.logout();
                        } catch (ProtocolException e) {
                        }
                    }
                }
                this.pool.lastTimePruned = System.currentTimeMillis();
            }
        }
    }

    int getFetchBlockSize() {
        return this.blksize;
    }

    Session getSession() {
        return this.session;
    }

    int getStatusCacheTimeout() {
        return this.statusCacheTimeout;
    }

    int getAppendBufferSize() {
        return this.appendBufferSize;
    }

    int getMinIdleTime() {
        return this.minIdleTime;
    }

    public synchronized boolean hasCapability(String capability) throws MessagingException {
        IMAPProtocol p;
        p = null;
        try {
            try {
                p = getStoreProtocol();
            } catch (ProtocolException pex) {
                if (p == null) {
                    cleanup();
                }
                throw new MessagingException(pex.getMessage(), pex);
            }
        } finally {
            releaseStoreProtocol(p);
        }
        return p.hasCapability(capability);
    }

    @Override // javax.mail.Service
    public synchronized boolean isConnected() {
        boolean zIsConnected = false;
        synchronized (this) {
            if (!this.connected) {
                super.setConnected(false);
            } else {
                IMAPProtocol p = null;
                try {
                    try {
                        p = getStoreProtocol();
                        p.noop();
                    } finally {
                        releaseStoreProtocol(p);
                    }
                } catch (ProtocolException e) {
                    if (p == null) {
                        cleanup();
                    }
                    releaseStoreProtocol(p);
                }
                zIsConnected = super.isConnected();
            }
        }
        return zIsConnected;
    }

    @Override // javax.mail.Service
    public synchronized void close() throws MessagingException {
        boolean isEmpty;
        if (super.isConnected()) {
            try {
                try {
                    synchronized (this.pool) {
                        isEmpty = this.pool.authenticatedConnections.isEmpty();
                    }
                    if (isEmpty) {
                        if (this.pool.debug) {
                            this.out.println("DEBUG: close() - no connections ");
                        }
                        cleanup();
                    } else {
                        IMAPProtocol protocol = getStoreProtocol();
                        synchronized (this.pool) {
                            this.pool.authenticatedConnections.removeElement(protocol);
                        }
                        protocol.logout();
                        releaseStoreProtocol(protocol);
                    }
                } catch (ProtocolException pex) {
                    cleanup();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            } finally {
                releaseStoreProtocol(null);
            }
        }
    }

    @Override // javax.mail.Service
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private void cleanup() {
        cleanup(false);
    }

    private void cleanup(boolean force) {
        boolean done;
        if (this.debug) {
            this.out.println("DEBUG: IMAPStore cleanup, force " + force);
        }
        Vector foldersCopy = null;
        while (true) {
            synchronized (this.pool) {
                if (this.pool.folders != null) {
                    done = false;
                    foldersCopy = this.pool.folders;
                    this.pool.folders = null;
                } else {
                    done = true;
                }
            }
            if (done) {
                break;
            }
            int fsize = foldersCopy.size();
            for (int i = 0; i < fsize; i++) {
                IMAPFolder f = (IMAPFolder) foldersCopy.elementAt(i);
                if (force) {
                    try {
                        if (this.debug) {
                            this.out.println("DEBUG: force folder to close");
                        }
                        f.forceClose();
                    } catch (IllegalStateException e) {
                    } catch (MessagingException e2) {
                    }
                } else {
                    if (this.debug) {
                        this.out.println("DEBUG: close folder");
                    }
                    f.close(false);
                }
            }
        }
        synchronized (this.pool) {
            emptyConnectionPool(force);
        }
        this.connected = false;
        notifyConnectionListeners(3);
        if (this.debug) {
            this.out.println("DEBUG: IMAPStore cleanup done");
        }
    }

    @Override // javax.mail.Store
    public synchronized Folder getDefaultFolder() throws MessagingException {
        checkConnected();
        return new DefaultFolder(this);
    }

    @Override // javax.mail.Store
    public synchronized Folder getFolder(String name) throws MessagingException {
        checkConnected();
        return new IMAPFolder(name, (char) 65535, this);
    }

    @Override // javax.mail.Store
    public synchronized Folder getFolder(URLName url) throws MessagingException {
        checkConnected();
        return new IMAPFolder(url.getFile(), (char) 65535, this);
    }

    @Override // javax.mail.Store
    public Folder[] getPersonalNamespaces() throws MessagingException {
        Namespaces ns = getNamespaces();
        return (ns == null || ns.personal == null) ? super.getPersonalNamespaces() : namespaceToFolders(ns.personal, null);
    }

    @Override // javax.mail.Store
    public Folder[] getUserNamespaces(String user) throws MessagingException {
        Namespaces ns = getNamespaces();
        return (ns == null || ns.otherUsers == null) ? super.getUserNamespaces(user) : namespaceToFolders(ns.otherUsers, user);
    }

    @Override // javax.mail.Store
    public Folder[] getSharedNamespaces() throws MessagingException {
        Namespaces ns = getNamespaces();
        return (ns == null || ns.shared == null) ? super.getSharedNamespaces() : namespaceToFolders(ns.shared, null);
    }

    private synchronized Namespaces getNamespaces() throws MessagingException {
        checkConnected();
        IMAPProtocol p = null;
        try {
            if (this.namespaces == null) {
                try {
                    try {
                        p = getStoreProtocol();
                        this.namespaces = p.namespace();
                    } catch (BadCommandException e) {
                        releaseStoreProtocol(p);
                        if (p == null) {
                            cleanup();
                        }
                    } catch (ProtocolException pex) {
                        throw new MessagingException(pex.getMessage(), pex);
                    }
                } catch (ConnectionException cex) {
                    throw new StoreClosedException(this, cex.getMessage());
                }
            }
        } finally {
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
        }
        return this.namespaces;
    }

    private Folder[] namespaceToFolders(Namespaces.Namespace[] ns, String user) {
        Folder[] fa = new Folder[ns.length];
        for (int i = 0; i < fa.length; i++) {
            String name = ns[i].prefix;
            if (user == null) {
                int len = name.length();
                if (len > 0 && name.charAt(len - 1) == ns[i].delimiter) {
                    name = name.substring(0, len - 1);
                }
            } else {
                name = String.valueOf(name) + user;
            }
            fa[i] = new IMAPFolder(name, ns[i].delimiter, this, user == null);
        }
        return fa;
    }

    @Override // javax.mail.QuotaAwareStore
    public synchronized Quota[] getQuota(String root) throws MessagingException {
        Quota[] qa;
        checkConnected();
        IMAPProtocol p = null;
        try {
            try {
                try {
                    try {
                        p = getStoreProtocol();
                        qa = p.getQuotaRoot(root);
                    } catch (ConnectionException cex) {
                        throw new StoreClosedException(this, cex.getMessage());
                    }
                } catch (BadCommandException bex) {
                    throw new MessagingException("QUOTA not supported", bex);
                }
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } finally {
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
        }
        return qa;
    }

    @Override // javax.mail.QuotaAwareStore
    public synchronized void setQuota(Quota quota) throws MessagingException {
        checkConnected();
        IMAPProtocol p = null;
        try {
            try {
                p = getStoreProtocol();
                p.setQuota(quota);
            } catch (BadCommandException bex) {
                throw new MessagingException("QUOTA not supported", bex);
            } catch (ConnectionException cex) {
                throw new StoreClosedException(this, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } finally {
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
        }
    }

    private void checkConnected() {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (!this.connected) {
            super.setConnected(false);
            throw new IllegalStateException("Not connected");
        }
    }

    @Override // com.sun.mail.iap.ResponseHandler
    public void handleResponse(Response r) {
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            handleResponseCode(r);
        }
        if (r.isBYE()) {
            if (this.debug) {
                this.out.println("DEBUG: IMAPStore connection dead");
            }
            if (this.connected) {
                cleanup(r.isSynthetic());
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:68:0x00b0, code lost:
    
        if (r10.enableImapEvents == false) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00b6, code lost:
    
        if (r5.isUnTagged() == false) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00b8, code lost:
    
        notifyStoreListeners(1000, r5.toString());
     */
    /* JADX WARN: Removed duplicated region for block: B:106:0x005e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0057  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void idle() throws javax.mail.MessagingException {
        /*
            Method dump skipped, instructions count: 231
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.idle():void");
    }

    private void waitIfIdle() throws ProtocolException, InterruptedException {
        if (!$assertionsDisabled && !Thread.holdsLock(this.pool)) {
            throw new AssertionError();
        }
        while (this.pool.idleState != 0) {
            if (this.pool.idleState == 1) {
                this.pool.idleProtocol.idleAbort();
                this.pool.idleState = 2;
            }
            try {
                this.pool.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    void handleResponseCode(Response r) {
        String s = r.getRest();
        boolean isAlert = false;
        if (s.startsWith("[")) {
            int i = s.indexOf(93);
            if (i > 0 && s.substring(0, i + 1).equalsIgnoreCase("[ALERT]")) {
                isAlert = true;
            }
            s = s.substring(i + 1).trim();
        }
        if (isAlert) {
            notifyStoreListeners(1, s);
        } else if (r.isUnTagged() && s.length() > 0) {
            notifyStoreListeners(2, s);
        }
    }
}
