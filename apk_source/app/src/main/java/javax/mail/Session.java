package javax.mail;

import com.iflytek.speech.VoiceWakeuperAidl;
import com.sun.mail.util.LineInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Provider;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public final class Session {
    private static Session defaultSession = null;
    private final Authenticator authenticator;
    private boolean debug;
    private PrintStream out;
    private final Properties props;
    private final Hashtable authTable = new Hashtable();
    private final Vector providers = new Vector();
    private final Hashtable providersByProtocol = new Hashtable();
    private final Hashtable providersByClassName = new Hashtable();
    private final Properties addressMap = new Properties();

    private Session(Properties props, Authenticator authenticator) throws Throwable {
        Class cl;
        this.debug = false;
        this.props = props;
        this.authenticator = authenticator;
        if (Boolean.valueOf(props.getProperty("mail.debug")).booleanValue()) {
            this.debug = true;
        }
        if (this.debug) {
            pr("DEBUG: JavaMail version 1.4.1");
        }
        if (authenticator != null) {
            cl = authenticator.getClass();
        } else {
            cl = getClass();
        }
        loadProviders(cl);
        loadAddressMap(cl);
    }

    public static Session getInstance(Properties props, Authenticator authenticator) {
        return new Session(props, authenticator);
    }

    public static Session getInstance(Properties props) {
        return new Session(props, null);
    }

    public static synchronized Session getDefaultInstance(Properties props, Authenticator authenticator) {
        if (defaultSession == null) {
            defaultSession = new Session(props, authenticator);
        } else if (defaultSession.authenticator != authenticator && (defaultSession.authenticator == null || authenticator == null || defaultSession.authenticator.getClass().getClassLoader() != authenticator.getClass().getClassLoader())) {
            throw new SecurityException("Access to default session denied");
        }
        return defaultSession;
    }

    public static Session getDefaultInstance(Properties props) {
        return getDefaultInstance(props, null);
    }

    public synchronized void setDebug(boolean debug) {
        this.debug = debug;
        if (debug) {
            pr("DEBUG: setDebug: JavaMail version 1.4.1");
        }
    }

    public synchronized boolean getDebug() {
        return this.debug;
    }

    public synchronized void setDebugOut(PrintStream out) {
        this.out = out;
    }

    public synchronized PrintStream getDebugOut() {
        return this.out == null ? System.out : this.out;
    }

    public synchronized Provider[] getProviders() {
        Provider[] _providers;
        _providers = new Provider[this.providers.size()];
        this.providers.copyInto(_providers);
        return _providers;
    }

    public synchronized Provider getProvider(String protocol) throws NoSuchProviderException {
        Provider _provider;
        if (protocol != null) {
            if (protocol.length() > 0) {
                Provider _provider2 = null;
                String _className = this.props.getProperty("mail." + protocol + ".class");
                if (_className != null) {
                    if (this.debug) {
                        pr("DEBUG: mail." + protocol + ".class property exists and points to " + _className);
                    }
                    _provider2 = (Provider) this.providersByClassName.get(_className);
                }
                if (_provider2 != null) {
                    _provider = _provider2;
                } else {
                    Provider _provider3 = (Provider) this.providersByProtocol.get(protocol);
                    if (_provider3 == null) {
                        throw new NoSuchProviderException("No provider for " + protocol);
                    }
                    if (this.debug) {
                        pr("DEBUG: getProvider() returning " + _provider3.toString());
                    }
                    _provider = _provider3;
                }
            }
        }
        throw new NoSuchProviderException("Invalid protocol: null");
        return _provider;
    }

    public synchronized void setProvider(Provider provider) throws NoSuchProviderException {
        if (provider == null) {
            throw new NoSuchProviderException("Can't set null provider");
        }
        this.providersByProtocol.put(provider.getProtocol(), provider);
        this.props.put("mail." + provider.getProtocol() + ".class", provider.getClassName());
    }

    public Store getStore() throws NoSuchProviderException {
        return getStore(getProperty("mail.store.protocol"));
    }

    public Store getStore(String protocol) throws NoSuchProviderException {
        return getStore(new URLName(protocol, null, -1, null, null, null));
    }

    public Store getStore(URLName url) throws NoSuchProviderException {
        String protocol = url.getProtocol();
        Provider p = getProvider(protocol);
        return getStore(p, url);
    }

    public Store getStore(Provider provider) throws NoSuchProviderException {
        return getStore(provider, null);
    }

    private Store getStore(Provider provider, URLName url) throws NoSuchProviderException {
        if (provider == null || provider.getType() != Provider.Type.STORE) {
            throw new NoSuchProviderException("invalid provider");
        }
        try {
            return (Store) getService(provider, url);
        } catch (ClassCastException e) {
            throw new NoSuchProviderException("incorrect class");
        }
    }

    public Folder getFolder(URLName url) throws MessagingException {
        Store store = getStore(url);
        store.connect();
        return store.getFolder(url);
    }

    public Transport getTransport() throws NoSuchProviderException {
        return getTransport(getProperty("mail.transport.protocol"));
    }

    public Transport getTransport(String protocol) throws IllegalAccessException, NoSuchMethodException, NoSuchProviderException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        return getTransport(new URLName(protocol, null, -1, null, null, null));
    }

    public Transport getTransport(URLName url) throws IllegalAccessException, NoSuchMethodException, NoSuchProviderException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        String protocol = url.getProtocol();
        Provider p = getProvider(protocol);
        return getTransport(p, url);
    }

    public Transport getTransport(Provider provider) throws NoSuchProviderException {
        return getTransport(provider, null);
    }

    public Transport getTransport(Address address) throws IllegalAccessException, NoSuchMethodException, NoSuchProviderException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        String transportProtocol = (String) this.addressMap.get(address.getType());
        if (transportProtocol == null) {
            throw new NoSuchProviderException("No provider for Address type: " + address.getType());
        }
        return getTransport(transportProtocol);
    }

    private Transport getTransport(Provider provider, URLName url) throws IllegalAccessException, NoSuchMethodException, NoSuchProviderException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        if (provider == null || provider.getType() != Provider.Type.TRANSPORT) {
            throw new NoSuchProviderException("invalid provider");
        }
        try {
            return (Transport) getService(provider, url);
        } catch (ClassCastException e) {
            throw new NoSuchProviderException("incorrect class");
        }
    }

    private Object getService(Provider provider, URLName url) throws IllegalAccessException, NoSuchMethodException, NoSuchProviderException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        ClassLoader cl;
        if (provider == null) {
            throw new NoSuchProviderException("null");
        }
        if (url == null) {
            url = new URLName(provider.getProtocol(), null, -1, null, null, null);
        }
        if (this.authenticator != null) {
            cl = this.authenticator.getClass().getClassLoader();
        } else {
            cl = getClass().getClassLoader();
        }
        Class serviceClass = null;
        try {
            ClassLoader ccl = getContextClassLoader();
            if (ccl != null) {
                try {
                    serviceClass = ccl.loadClass(provider.getClassName());
                } catch (ClassNotFoundException e) {
                }
            }
            if (serviceClass == null) {
                serviceClass = cl.loadClass(provider.getClassName());
            }
        } catch (Exception e2) {
            try {
                serviceClass = Class.forName(provider.getClassName());
            } catch (Exception ex) {
                if (this.debug) {
                    ex.printStackTrace(getDebugOut());
                }
                throw new NoSuchProviderException(provider.getProtocol());
            }
        }
        try {
            Class[] c = {Session.class, URLName.class};
            Constructor cons = serviceClass.getConstructor(c);
            Object[] o = {this, url};
            Object service = cons.newInstance(o);
            return service;
        } catch (Exception ex2) {
            if (this.debug) {
                ex2.printStackTrace(getDebugOut());
            }
            throw new NoSuchProviderException(provider.getProtocol());
        }
    }

    public void setPasswordAuthentication(URLName url, PasswordAuthentication pw) {
        if (pw == null) {
            this.authTable.remove(url);
        } else {
            this.authTable.put(url, pw);
        }
    }

    public PasswordAuthentication getPasswordAuthentication(URLName url) {
        return (PasswordAuthentication) this.authTable.get(url);
    }

    public PasswordAuthentication requestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String defaultUserName) {
        if (this.authenticator != null) {
            return this.authenticator.requestPasswordAuthentication(addr, port, protocol, prompt, defaultUserName);
        }
        return null;
    }

    public Properties getProperties() {
        return this.props;
    }

    public String getProperty(String name) {
        return this.props.getProperty(name);
    }

    private void loadProviders(Class cl) throws Throwable {
        StreamLoader loader = new StreamLoader() { // from class: javax.mail.Session.1
            AnonymousClass1() {
            }

            @Override // javax.mail.StreamLoader
            public void load(InputStream is) throws IOException {
                Session.this.loadProvidersFromStream(is);
            }
        };
        try {
            String res = String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "javamail.providers";
            loadFile(res, loader);
        } catch (SecurityException sex) {
            if (this.debug) {
                pr("DEBUG: can't get java.home: " + sex);
            }
        }
        loadAllResources("META-INF/javamail.providers", cl, loader);
        loadResource("/META-INF/javamail.default.providers", cl, loader);
        if (this.providers.size() == 0) {
            if (this.debug) {
                pr("DEBUG: failed to load any providers, using defaults");
            }
            addProvider(new Provider(Provider.Type.STORE, "imap", "com.sun.mail.imap.IMAPStore", "Sun Microsystems, Inc.", Version.version));
            addProvider(new Provider(Provider.Type.STORE, "imaps", "com.sun.mail.imap.IMAPSSLStore", "Sun Microsystems, Inc.", Version.version));
            addProvider(new Provider(Provider.Type.STORE, "pop3", "com.sun.mail.pop3.POP3Store", "Sun Microsystems, Inc.", Version.version));
            addProvider(new Provider(Provider.Type.STORE, "pop3s", "com.sun.mail.pop3.POP3SSLStore", "Sun Microsystems, Inc.", Version.version));
            addProvider(new Provider(Provider.Type.TRANSPORT, "smtp", "com.sun.mail.smtp.SMTPTransport", "Sun Microsystems, Inc.", Version.version));
            addProvider(new Provider(Provider.Type.TRANSPORT, "smtps", "com.sun.mail.smtp.SMTPSSLTransport", "Sun Microsystems, Inc.", Version.version));
        }
        if (this.debug) {
            pr("DEBUG: Tables of loaded providers");
            pr("DEBUG: Providers Listed By Class Name: " + this.providersByClassName.toString());
            pr("DEBUG: Providers Listed By Protocol: " + this.providersByProtocol.toString());
        }
    }

    /* renamed from: javax.mail.Session$1 */
    class AnonymousClass1 implements StreamLoader {
        AnonymousClass1() {
        }

        @Override // javax.mail.StreamLoader
        public void load(InputStream is) throws IOException {
            Session.this.loadProvidersFromStream(is);
        }
    }

    public void loadProvidersFromStream(InputStream is) throws IOException {
        if (is != null) {
            LineInputStream lis = new LineInputStream(is);
            while (true) {
                String currLine = lis.readLine();
                if (currLine != null) {
                    if (!currLine.startsWith("#")) {
                        Provider.Type type = null;
                        String protocol = null;
                        String className = null;
                        String vendor = null;
                        String version = null;
                        StringTokenizer tuples = new StringTokenizer(currLine, VoiceWakeuperAidl.PARAMS_SEPARATE);
                        while (tuples.hasMoreTokens()) {
                            String currTuple = tuples.nextToken().trim();
                            int sep = currTuple.indexOf("=");
                            if (currTuple.startsWith("protocol=")) {
                                protocol = currTuple.substring(sep + 1);
                            } else if (currTuple.startsWith("type=")) {
                                String strType = currTuple.substring(sep + 1);
                                if (strType.equalsIgnoreCase("store")) {
                                    type = Provider.Type.STORE;
                                } else if (strType.equalsIgnoreCase("transport")) {
                                    type = Provider.Type.TRANSPORT;
                                }
                            } else if (currTuple.startsWith("class=")) {
                                className = currTuple.substring(sep + 1);
                            } else if (currTuple.startsWith("vendor=")) {
                                vendor = currTuple.substring(sep + 1);
                            } else if (currTuple.startsWith("version=")) {
                                version = currTuple.substring(sep + 1);
                            }
                        }
                        if (type == null || protocol == null || className == null || protocol.length() <= 0 || className.length() <= 0) {
                            if (this.debug) {
                                pr("DEBUG: Bad provider entry: " + currLine);
                            }
                        } else {
                            Provider provider = new Provider(type, protocol, className, vendor, version);
                            addProvider(provider);
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    public synchronized void addProvider(Provider provider) {
        this.providers.addElement(provider);
        this.providersByClassName.put(provider.getClassName(), provider);
        if (!this.providersByProtocol.containsKey(provider.getProtocol())) {
            this.providersByProtocol.put(provider.getProtocol(), provider);
        }
    }

    private void loadAddressMap(Class cl) throws Throwable {
        StreamLoader loader = new StreamLoader() { // from class: javax.mail.Session.2
            AnonymousClass2() {
            }

            @Override // javax.mail.StreamLoader
            public void load(InputStream is) throws IOException {
                Session.this.addressMap.load(is);
            }
        };
        loadResource("/META-INF/javamail.default.address.map", cl, loader);
        loadAllResources("META-INF/javamail.address.map", cl, loader);
        try {
            String res = String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "javamail.address.map";
            loadFile(res, loader);
        } catch (SecurityException sex) {
            if (this.debug) {
                pr("DEBUG: can't get java.home: " + sex);
            }
        }
        if (this.addressMap.isEmpty()) {
            if (this.debug) {
                pr("DEBUG: failed to load address map, using defaults");
            }
            this.addressMap.put("rfc822", "smtp");
        }
    }

    /* renamed from: javax.mail.Session$2 */
    class AnonymousClass2 implements StreamLoader {
        AnonymousClass2() {
        }

        @Override // javax.mail.StreamLoader
        public void load(InputStream is) throws IOException {
            Session.this.addressMap.load(is);
        }
    }

    public synchronized void setProtocolForAddress(String addresstype, String protocol) {
        if (protocol == null) {
            this.addressMap.remove(addresstype);
        } else {
            this.addressMap.put(addresstype, protocol);
        }
    }

    private void loadFile(String name, StreamLoader loader) throws Throwable {
        InputStream clis;
        InputStream clis2 = null;
        try {
            try {
                clis = new BufferedInputStream(new FileInputStream(name));
            } catch (IOException e) {
                e = e;
            } catch (SecurityException e2) {
                sex = e2;
            }
            try {
                loader.load(clis);
                if (this.debug) {
                    pr("DEBUG: successfully loaded file: " + name);
                }
                if (clis != null) {
                    try {
                        clis.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (IOException e4) {
                e = e4;
                clis2 = clis;
                if (this.debug) {
                    pr("DEBUG: not loading file: " + name);
                    pr("DEBUG: " + e);
                }
                if (clis2 != null) {
                    try {
                        clis2.close();
                    } catch (IOException e5) {
                    }
                }
            } catch (SecurityException e6) {
                sex = e6;
                clis2 = clis;
                if (this.debug) {
                    pr("DEBUG: not loading file: " + name);
                    pr("DEBUG: " + sex);
                }
                if (clis2 != null) {
                    try {
                        clis2.close();
                    } catch (IOException e7) {
                    }
                }
            } catch (Throwable th) {
                th = th;
                clis2 = clis;
                if (clis2 != null) {
                    try {
                        clis2.close();
                    } catch (IOException e8) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void loadResource(String name, Class cl, StreamLoader loader) throws IOException {
        InputStream clis = null;
        try {
            try {
                try {
                    InputStream clis2 = getResourceAsStream(cl, name);
                    if (clis2 != null) {
                        loader.load(clis2);
                        if (this.debug) {
                            pr("DEBUG: successfully loaded resource: " + name);
                        }
                    } else if (this.debug) {
                        pr("DEBUG: not loading resource: " + name);
                    }
                    if (clis2 != null) {
                        try {
                            clis2.close();
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException e2) {
                    if (this.debug) {
                        pr("DEBUG: " + e2);
                    }
                    if (0 != 0) {
                        try {
                            clis.close();
                        } catch (IOException e3) {
                        }
                    }
                }
            } catch (SecurityException sex) {
                if (this.debug) {
                    pr("DEBUG: " + sex);
                }
                if (0 != 0) {
                    try {
                        clis.close();
                    } catch (IOException e4) {
                    }
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    clis.close();
                } catch (IOException e5) {
                }
            }
            throw th;
        }
    }

    private void loadAllResources(String name, Class cl, StreamLoader loader) throws IOException {
        boolean anyLoaded = false;
        try {
            ClassLoader cld = getContextClassLoader();
            if (cld == null) {
                cld = cl.getClassLoader();
            }
            URL[] urls = cld != null ? getResources(cld, name) : getSystemResources(name);
            if (urls != null) {
                for (URL url : urls) {
                    InputStream clis = null;
                    if (this.debug) {
                        pr("DEBUG: URL " + url);
                    }
                    try {
                        try {
                            try {
                                clis = openStream(url);
                                if (clis != null) {
                                    loader.load(clis);
                                    anyLoaded = true;
                                    if (this.debug) {
                                        pr("DEBUG: successfully loaded resource: " + url);
                                    }
                                } else if (this.debug) {
                                    pr("DEBUG: not loading resource: " + url);
                                }
                                if (clis != null) {
                                    try {
                                        clis.close();
                                    } catch (IOException e) {
                                    }
                                }
                            } catch (IOException ioex) {
                                if (this.debug) {
                                    pr("DEBUG: " + ioex);
                                }
                                if (clis != null) {
                                    try {
                                        clis.close();
                                    } catch (IOException e2) {
                                    }
                                }
                            }
                        } catch (SecurityException sex) {
                            if (this.debug) {
                                pr("DEBUG: " + sex);
                            }
                            if (clis != null) {
                                try {
                                    clis.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (Throwable th) {
                        if (clis != null) {
                            try {
                                clis.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                }
            }
        } catch (Exception ex) {
            if (this.debug) {
                pr("DEBUG: " + ex);
            }
        }
        if (anyLoaded) {
            return;
        }
        if (this.debug) {
            pr("DEBUG: !anyLoaded");
        }
        loadResource(ServiceReference.DELIMITER + name, cl, loader);
    }

    private void pr(String str) {
        getDebugOut().println(str);
    }

    /* renamed from: javax.mail.Session$3 */
    class AnonymousClass3 implements PrivilegedAction {
        AnonymousClass3() {
        }

        @Override // java.security.PrivilegedAction
        public Object run() {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                return cl;
            } catch (SecurityException e) {
                return null;
            }
        }
    }

    private static ClassLoader getContextClassLoader() {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: javax.mail.Session.3
            AnonymousClass3() {
            }

            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    return cl;
                } catch (SecurityException e) {
                    return null;
                }
            }
        });
    }

    private static InputStream getResourceAsStream(Class c, String name) throws IOException {
        try {
            return (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: javax.mail.Session.4
                private final /* synthetic */ Class val$c;
                private final /* synthetic */ String val$name;

                AnonymousClass4(Class c2, String name2) {
                    cls = c2;
                    str = name2;
                }

                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws IOException {
                    return cls.getResourceAsStream(str);
                }
            });
        } catch (PrivilegedActionException e) {
            throw ((IOException) e.getException());
        }
    }

    /* renamed from: javax.mail.Session$4 */
    class AnonymousClass4 implements PrivilegedExceptionAction {
        private final /* synthetic */ Class val$c;
        private final /* synthetic */ String val$name;

        AnonymousClass4(Class c2, String name2) {
            cls = c2;
            str = name2;
        }

        @Override // java.security.PrivilegedExceptionAction
        public Object run() throws IOException {
            return cls.getResourceAsStream(str);
        }
    }

    private static URL[] getResources(ClassLoader cl, String name) {
        return (URL[]) AccessController.doPrivileged(new PrivilegedAction() { // from class: javax.mail.Session.5
            private final /* synthetic */ ClassLoader val$cl;
            private final /* synthetic */ String val$name;

            AnonymousClass5(ClassLoader cl2, String name2) {
                classLoader = cl2;
                str = name2;
            }

            @Override // java.security.PrivilegedAction
            public Object run() throws IOException {
                URL[] ret = (URL[]) null;
                try {
                    Vector v = new Vector();
                    Enumeration e = classLoader.getResources(str);
                    while (e != null && e.hasMoreElements()) {
                        URL url = e.nextElement();
                        if (url != null) {
                            v.addElement(url);
                        }
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        v.copyInto(ret);
                        return ret;
                    }
                    return ret;
                } catch (IOException e2) {
                    return ret;
                } catch (SecurityException e3) {
                    return ret;
                }
            }
        });
    }

    /* renamed from: javax.mail.Session$5 */
    class AnonymousClass5 implements PrivilegedAction {
        private final /* synthetic */ ClassLoader val$cl;
        private final /* synthetic */ String val$name;

        AnonymousClass5(ClassLoader cl2, String name2) {
            classLoader = cl2;
            str = name2;
        }

        @Override // java.security.PrivilegedAction
        public Object run() throws IOException {
            URL[] ret = (URL[]) null;
            try {
                Vector v = new Vector();
                Enumeration e = classLoader.getResources(str);
                while (e != null && e.hasMoreElements()) {
                    URL url = e.nextElement();
                    if (url != null) {
                        v.addElement(url);
                    }
                }
                if (v.size() > 0) {
                    ret = new URL[v.size()];
                    v.copyInto(ret);
                    return ret;
                }
                return ret;
            } catch (IOException e2) {
                return ret;
            } catch (SecurityException e3) {
                return ret;
            }
        }
    }

    private static URL[] getSystemResources(String name) {
        return (URL[]) AccessController.doPrivileged(new PrivilegedAction() { // from class: javax.mail.Session.6
            private final /* synthetic */ String val$name;

            AnonymousClass6(String name2) {
                str = name2;
            }

            @Override // java.security.PrivilegedAction
            public Object run() throws IOException {
                URL[] ret = (URL[]) null;
                try {
                    Vector v = new Vector();
                    Enumeration e = ClassLoader.getSystemResources(str);
                    while (e != null && e.hasMoreElements()) {
                        URL url = e.nextElement();
                        if (url != null) {
                            v.addElement(url);
                        }
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        v.copyInto(ret);
                        return ret;
                    }
                    return ret;
                } catch (IOException e2) {
                    return ret;
                } catch (SecurityException e3) {
                    return ret;
                }
            }
        });
    }

    /* renamed from: javax.mail.Session$6 */
    class AnonymousClass6 implements PrivilegedAction {
        private final /* synthetic */ String val$name;

        AnonymousClass6(String name2) {
            str = name2;
        }

        @Override // java.security.PrivilegedAction
        public Object run() throws IOException {
            URL[] ret = (URL[]) null;
            try {
                Vector v = new Vector();
                Enumeration e = ClassLoader.getSystemResources(str);
                while (e != null && e.hasMoreElements()) {
                    URL url = e.nextElement();
                    if (url != null) {
                        v.addElement(url);
                    }
                }
                if (v.size() > 0) {
                    ret = new URL[v.size()];
                    v.copyInto(ret);
                    return ret;
                }
                return ret;
            } catch (IOException e2) {
                return ret;
            } catch (SecurityException e3) {
                return ret;
            }
        }
    }

    private static InputStream openStream(URL url) throws IOException {
        try {
            return (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: javax.mail.Session.7
                private final /* synthetic */ URL val$url;

                AnonymousClass7(URL url2) {
                    url = url2;
                }

                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws IOException {
                    return url.openStream();
                }
            });
        } catch (PrivilegedActionException e) {
            throw ((IOException) e.getException());
        }
    }

    /* renamed from: javax.mail.Session$7 */
    class AnonymousClass7 implements PrivilegedExceptionAction {
        private final /* synthetic */ URL val$url;

        AnonymousClass7(URL url2) {
            url = url2;
        }

        @Override // java.security.PrivilegedExceptionAction
        public Object run() throws IOException {
            return url.openStream();
        }
    }
}
