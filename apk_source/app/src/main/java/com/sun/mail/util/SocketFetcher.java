package com.sun.mail.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class SocketFetcher {
    private SocketFetcher() {
    }

    public static Socket getSocket(String host, int port, Properties props, String prefix, boolean useSSL) throws Exception {
        if (prefix == null) {
            prefix = "socket";
        }
        if (props == null) {
            props = new Properties();
        }
        String s = props.getProperty(String.valueOf(prefix) + ".connectiontimeout", null);
        int cto = -1;
        if (s != null) {
            try {
                cto = Integer.parseInt(s);
            } catch (NumberFormatException e) {
            }
        }
        Socket socket = null;
        String timeout = props.getProperty(String.valueOf(prefix) + ".timeout", null);
        String localaddrstr = props.getProperty(String.valueOf(prefix) + ".localaddress", null);
        InetAddress localaddr = null;
        if (localaddrstr != null) {
            localaddr = InetAddress.getByName(localaddrstr);
        }
        String localportstr = props.getProperty(String.valueOf(prefix) + ".localport", null);
        int localport = 0;
        if (localportstr != null) {
            try {
                localport = Integer.parseInt(localportstr);
            } catch (NumberFormatException e2) {
            }
        }
        String fallback = props.getProperty(String.valueOf(prefix) + ".socketFactory.fallback", null);
        boolean fb = fallback == null || !fallback.equalsIgnoreCase("false");
        String sfClass = props.getProperty(String.valueOf(prefix) + ".socketFactory.class", null);
        int sfPort = -1;
        try {
            SocketFactory sf = getSocketFactory(sfClass);
            if (sf != null) {
                String sfPortStr = props.getProperty(String.valueOf(prefix) + ".socketFactory.port", null);
                if (sfPortStr != null) {
                    try {
                        sfPort = Integer.parseInt(sfPortStr);
                    } catch (NumberFormatException e3) {
                    }
                }
                if (sfPort == -1) {
                    sfPort = port;
                }
                socket = createSocket(localaddr, localport, host, sfPort, cto, sf, useSSL);
            }
        } catch (SocketTimeoutException sex) {
            throw sex;
        } catch (Exception e4) {
            ex = e4;
            if (!fb) {
                if (ex instanceof InvocationTargetException) {
                    Throwable t = ((InvocationTargetException) ex).getTargetException();
                    if (t instanceof Exception) {
                        ex = (Exception) t;
                    }
                }
                if (ex instanceof IOException) {
                    throw ((IOException) ex);
                }
                IOException ioex = new IOException("Couldn't connect using \"" + sfClass + "\" socket factory to host, port: " + host + ", " + sfPort + "; Exception: " + ex);
                ioex.initCause(ex);
                throw ioex;
            }
        }
        if (socket == null) {
            socket = createSocket(localaddr, localport, host, port, cto, null, useSSL);
        }
        int to = -1;
        if (timeout != null) {
            try {
                to = Integer.parseInt(timeout);
            } catch (NumberFormatException e5) {
            }
        }
        if (to >= 0) {
            socket.setSoTimeout(to);
        }
        configureSSLSocket(socket, props, prefix);
        return socket;
    }

    public static Socket getSocket(String host, int port, Properties props, String prefix) throws IOException {
        return getSocket(host, port, props, prefix, false);
    }

    private static Socket createSocket(InetAddress localaddr, int localport, String host, int port, int cto, SocketFactory sf, boolean useSSL) throws IOException {
        Socket socket;
        if (sf != null) {
            socket = sf.createSocket();
        } else if (useSSL) {
            socket = SSLSocketFactory.getDefault().createSocket();
        } else {
            socket = new Socket();
        }
        if (localaddr != null) {
            socket.bind(new InetSocketAddress(localaddr, localport));
        }
        if (cto >= 0) {
            socket.connect(new InetSocketAddress(host, port), cto);
        } else {
            socket.connect(new InetSocketAddress(host, port));
        }
        return socket;
    }

    private static SocketFactory getSocketFactory(String sfClass) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, InvocationTargetException {
        if (sfClass == null || sfClass.length() == 0) {
            return null;
        }
        ClassLoader cl = getContextClassLoader();
        Class clsSockFact = null;
        if (cl != null) {
            try {
                clsSockFact = cl.loadClass(sfClass);
            } catch (ClassNotFoundException e) {
            }
        }
        if (clsSockFact == null) {
            clsSockFact = Class.forName(sfClass);
        }
        Method mthGetDefault = clsSockFact.getMethod("getDefault", new Class[0]);
        return (SocketFactory) mthGetDefault.invoke(new Object(), new Object[0]);
    }

    public static Socket startTLS(Socket socket) throws IOException {
        return startTLS(socket, new Properties(), "socket");
    }

    public static Socket startTLS(Socket socket, Properties props, String prefix) throws Exception {
        SSLSocketFactory ssf;
        InetAddress a = socket.getInetAddress();
        String host = a.getHostName();
        int port = socket.getPort();
        try {
            String sfClass = props.getProperty(String.valueOf(prefix) + ".socketFactory.class", null);
            SocketFactory sf = getSocketFactory(sfClass);
            if (sf != null && (sf instanceof SSLSocketFactory)) {
                ssf = (SSLSocketFactory) sf;
            } else {
                ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            }
            Socket socket2 = ssf.createSocket(socket, host, port, true);
            configureSSLSocket(socket2, props, prefix);
            return socket2;
        } catch (Exception e) {
            ex = e;
            if (ex instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) ex).getTargetException();
                if (t instanceof Exception) {
                    ex = (Exception) t;
                }
            }
            if (ex instanceof IOException) {
                throw ((IOException) ex);
            }
            IOException ioex = new IOException("Exception in startTLS: host " + host + ", port " + port + "; Exception: " + ex);
            ioex.initCause(ex);
            throw ioex;
        }
    }

    private static void configureSSLSocket(Socket socket, Properties props, String prefix) {
        if (socket instanceof SSLSocket) {
            SSLSocket sslsocket = (SSLSocket) socket;
            String protocols = props.getProperty(String.valueOf(prefix) + ".ssl.protocols", null);
            if (protocols != null) {
                sslsocket.setEnabledProtocols(stringArray(protocols));
            } else {
                sslsocket.setEnabledProtocols(new String[]{"TLSv1"});
            }
            String ciphers = props.getProperty(String.valueOf(prefix) + ".ssl.ciphersuites", null);
            if (ciphers != null) {
                sslsocket.setEnabledCipherSuites(stringArray(ciphers));
            }
        }
    }

    private static String[] stringArray(String s) {
        StringTokenizer st = new StringTokenizer(s);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    private static ClassLoader getContextClassLoader() {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.sun.mail.util.SocketFetcher.1
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
}
