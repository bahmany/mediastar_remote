package javax.mail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MailEvent;

/* loaded from: classes.dex */
public abstract class Service {
    protected boolean debug;
    private EventQueue q;
    protected Session session;
    protected URLName url;
    private boolean connected = false;
    private Vector connectionListeners = null;
    private Object qLock = new Object();

    protected Service(Session session, URLName urlname) {
        this.url = null;
        this.debug = false;
        this.session = session;
        this.url = urlname;
        this.debug = session.getDebug();
    }

    public void connect() throws MessagingException {
        connect(null, null, null);
    }

    public void connect(String host, String user, String password) throws MessagingException {
        connect(host, -1, user, password);
    }

    public void connect(String user, String password) throws MessagingException {
        connect(null, user, password);
    }

    public synchronized void connect(String host, int port, String user, String password) throws MessagingException {
        InetAddress addr;
        if (isConnected()) {
            throw new IllegalStateException("already connected");
        }
        boolean connected = false;
        boolean save = false;
        String protocol = null;
        String file = null;
        if (this.url != null) {
            protocol = this.url.getProtocol();
            if (host == null) {
                host = this.url.getHost();
            }
            if (port == -1) {
                port = this.url.getPort();
            }
            if (user == null) {
                user = this.url.getUsername();
                if (password == null) {
                    password = this.url.getPassword();
                }
            } else if (password == null && user.equals(this.url.getUsername())) {
                password = this.url.getPassword();
            }
            file = this.url.getFile();
        }
        if (protocol != null) {
            if (host == null) {
                host = this.session.getProperty("mail." + protocol + ".host");
            }
            if (user == null) {
                user = this.session.getProperty("mail." + protocol + ".user");
            }
        }
        if (host == null) {
            host = this.session.getProperty("mail.host");
        }
        if (user == null) {
            user = this.session.getProperty("mail.user");
        }
        if (user == null) {
            try {
                user = System.getProperty("user.name");
            } catch (SecurityException sex) {
                if (this.debug) {
                    sex.printStackTrace(this.session.getDebugOut());
                }
            }
        }
        if (password == null && this.url != null) {
            setURLName(new URLName(protocol, host, port, file, user, null));
            PasswordAuthentication pw = this.session.getPasswordAuthentication(getURLName());
            if (pw != null) {
                if (user == null) {
                    user = pw.getUserName();
                    password = pw.getPassword();
                } else {
                    if (user.equals(pw.getUserName())) {
                        password = pw.getPassword();
                    }
                }
            } else {
                save = true;
            }
        }
        AuthenticationFailedException authEx = null;
        try {
            connected = protocolConnect(host, port, user, password);
        } catch (AuthenticationFailedException ex) {
            authEx = ex;
        }
        if (!connected) {
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                addr = null;
            }
            PasswordAuthentication pw2 = this.session.requestPasswordAuthentication(addr, port, protocol, null, user);
            if (pw2 != null) {
                user = pw2.getUserName();
                password = pw2.getPassword();
                connected = protocolConnect(host, port, user, password);
            }
        }
        if (!connected) {
            if (authEx != null) {
                throw authEx;
            }
            throw new AuthenticationFailedException();
        }
        setURLName(new URLName(protocol, host, port, file, user, password));
        if (save) {
            this.session.setPasswordAuthentication(getURLName(), new PasswordAuthentication(user, password));
        }
        setConnected(true);
        notifyConnectionListeners(1);
    }

    protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
        return false;
    }

    public synchronized boolean isConnected() {
        return this.connected;
    }

    protected synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized void close() throws MessagingException {
        setConnected(false);
        notifyConnectionListeners(3);
    }

    public synchronized URLName getURLName() {
        return (this.url == null || (this.url.getPassword() == null && this.url.getFile() == null)) ? this.url : new URLName(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), null, this.url.getUsername(), null);
    }

    protected synchronized void setURLName(URLName url) {
        this.url = url;
    }

    public synchronized void addConnectionListener(ConnectionListener l) {
        if (this.connectionListeners == null) {
            this.connectionListeners = new Vector();
        }
        this.connectionListeners.addElement(l);
    }

    public synchronized void removeConnectionListener(ConnectionListener l) {
        if (this.connectionListeners != null) {
            this.connectionListeners.removeElement(l);
        }
    }

    protected synchronized void notifyConnectionListeners(int type) {
        if (this.connectionListeners != null) {
            ConnectionEvent e = new ConnectionEvent(this, type);
            queueEvent(e, this.connectionListeners);
        }
        if (type == 3) {
            terminateQueue();
        }
    }

    public String toString() {
        URLName url = getURLName();
        return url != null ? url.toString() : super.toString();
    }

    protected void queueEvent(MailEvent event, Vector vector) {
        synchronized (this.qLock) {
            if (this.q == null) {
                this.q = new EventQueue();
            }
        }
        Vector v = (Vector) vector.clone();
        this.q.enqueue(event, v);
    }

    static class TerminatorEvent extends MailEvent {
        private static final long serialVersionUID = 5542172141759168416L;

        TerminatorEvent() {
            super(new Object());
        }

        @Override // javax.mail.event.MailEvent
        public void dispatch(Object listener) {
            Thread.currentThread().interrupt();
        }
    }

    private void terminateQueue() {
        synchronized (this.qLock) {
            if (this.q != null) {
                Vector dummyListeners = new Vector();
                dummyListeners.setSize(1);
                this.q.enqueue(new TerminatorEvent(), dummyListeners);
                this.q = null;
            }
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        terminateQueue();
    }
}
