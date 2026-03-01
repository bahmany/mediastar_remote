package com.sun.mail.pop3;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
class Protocol {
    private static final String CRLF = "\r\n";
    private static final int POP3_PORT = 110;
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private String apopChallenge;
    private boolean debug;
    private DataInputStream input;
    private PrintStream out;
    private PrintWriter output;
    private Socket socket;

    Protocol(String host, int port, boolean debug, PrintStream out, Properties props, String prefix, boolean isSSL) throws IOException {
        this.debug = false;
        this.apopChallenge = null;
        this.debug = debug;
        this.out = out;
        String apop = props.getProperty(String.valueOf(prefix) + ".apop.enable");
        boolean enableAPOP = apop != null && apop.equalsIgnoreCase("true");
        port = port == -1 ? POP3_PORT : port;
        if (debug) {
            try {
                out.println("DEBUG POP3: connecting to host \"" + host + "\", port " + port + ", isSSL " + isSSL);
            } catch (IOException ioe) {
                try {
                    this.socket.close();
                } catch (Throwable th) {
                }
                throw ioe;
            }
        }
        this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
        this.input = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "iso-8859-1")));
        Response r = simpleCommand(null);
        if (!r.ok) {
            try {
                this.socket.close();
            } catch (Throwable th2) {
            }
            throw new IOException("Connect failed");
        }
        if (enableAPOP) {
            int challStart = r.data.indexOf(60);
            int challEnd = r.data.indexOf(62, challStart);
            if (challStart != -1 && challEnd != -1) {
                this.apopChallenge = r.data.substring(challStart, challEnd + 1);
            }
            if (debug) {
                out.println("DEBUG POP3: APOP challenge: " + this.apopChallenge);
            }
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.socket != null) {
            quit();
        }
    }

    synchronized String login(String user, String password) throws IOException {
        Response r;
        String str;
        String dpw = this.apopChallenge != null ? getDigest(password) : null;
        if (this.apopChallenge != null && dpw != null) {
            r = simpleCommand("APOP " + user + " " + dpw);
        } else {
            Response r2 = simpleCommand("USER " + user);
            if (!r2.ok) {
                str = r2.data != null ? r2.data : "USER command failed";
            } else {
                r = simpleCommand("PASS " + password);
            }
        }
        if (!r.ok) {
            str = r.data != null ? r.data : "login failed";
        } else {
            str = null;
        }
        return str;
    }

    private String getDigest(String password) throws NoSuchAlgorithmException {
        String key = String.valueOf(this.apopChallenge) + password;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes("iso-8859-1"));
            return toHex(digest);
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        char[] result = new char[bytes.length * 2];
        int i = 0;
        for (byte b : bytes) {
            int temp = b & 255;
            int i2 = i + 1;
            result[i] = digits[temp >> 4];
            i = i2 + 1;
            result[i2] = digits[temp & 15];
        }
        return new String(result);
    }

    /* JADX WARN: Finally extract failed */
    synchronized boolean quit() throws IOException {
        boolean ok;
        try {
            Response r = simpleCommand("QUIT");
            ok = r.ok;
            try {
                this.socket.close();
                this.socket = null;
                this.input = null;
                this.output = null;
            } catch (Throwable th) {
                this.socket = null;
                this.input = null;
                this.output = null;
                throw th;
            }
        } catch (Throwable th2) {
            try {
                this.socket.close();
                this.socket = null;
                this.input = null;
                this.output = null;
                throw th2;
            } catch (Throwable th3) {
                this.socket = null;
                this.input = null;
                this.output = null;
                throw th3;
            }
        }
        return ok;
    }

    synchronized Status stat() throws IOException {
        Status s;
        Response r = simpleCommand("STAT");
        s = new Status();
        if (r.ok && r.data != null) {
            try {
                StringTokenizer st = new StringTokenizer(r.data);
                s.total = Integer.parseInt(st.nextToken());
                s.size = Integer.parseInt(st.nextToken());
            } catch (Exception e) {
            }
        }
        return s;
    }

    synchronized int list(int msg) throws IOException {
        int size;
        Response r = simpleCommand("LIST " + msg);
        size = -1;
        if (r.ok && r.data != null) {
            try {
                StringTokenizer st = new StringTokenizer(r.data);
                st.nextToken();
                size = Integer.parseInt(st.nextToken());
            } catch (Exception e) {
            }
        }
        return size;
    }

    synchronized InputStream list() throws IOException {
        Response r;
        r = multilineCommand("LIST", 128);
        return r.bytes;
    }

    synchronized InputStream retr(int msg, int size) throws IOException {
        Response r;
        r = multilineCommand("RETR " + msg, size);
        return r.bytes;
    }

    synchronized InputStream top(int msg, int n) throws IOException {
        Response r;
        r = multilineCommand("TOP " + msg + " " + n, 0);
        return r.bytes;
    }

    synchronized boolean dele(int msg) throws IOException {
        Response r;
        r = simpleCommand("DELE " + msg);
        return r.ok;
    }

    synchronized String uidl(int msg) throws IOException {
        int i;
        String strSubstring = null;
        synchronized (this) {
            Response r = simpleCommand("UIDL " + msg);
            if (r.ok && (i = r.data.indexOf(32)) > 0) {
                strSubstring = r.data.substring(i + 1);
            }
        }
        return strSubstring;
    }

    synchronized boolean uidl(String[] uids) throws IOException {
        int n;
        boolean z = false;
        synchronized (this) {
            Response r = multilineCommand("UIDL", uids.length * 15);
            if (r.ok) {
                LineInputStream lis = new LineInputStream(r.bytes);
                while (true) {
                    String line = lis.readLine();
                    if (line == null) {
                        break;
                    }
                    int i = line.indexOf(32);
                    if (i >= 1 && i < line.length() && (n = Integer.parseInt(line.substring(0, i))) > 0 && n <= uids.length) {
                        uids[n - 1] = line.substring(i + 1);
                    }
                }
                z = true;
            }
        }
        return z;
    }

    synchronized boolean noop() throws IOException {
        Response r;
        r = simpleCommand("NOOP");
        return r.ok;
    }

    synchronized boolean rset() throws IOException {
        Response r;
        r = simpleCommand("RSET");
        return r.ok;
    }

    private Response simpleCommand(String cmd) throws IOException {
        if (this.socket == null) {
            throw new IOException("Folder is closed");
        }
        if (cmd != null) {
            if (this.debug) {
                this.out.println("C: " + cmd);
            }
            this.output.print(String.valueOf(cmd) + "\r\n");
            this.output.flush();
        }
        String line = this.input.readLine();
        if (line == null) {
            if (this.debug) {
                this.out.println("S: EOF");
            }
            throw new EOFException("EOF on socket");
        }
        if (this.debug) {
            this.out.println("S: " + line);
        }
        Response r = new Response();
        if (line.startsWith("+OK")) {
            r.ok = true;
        } else if (line.startsWith("-ERR")) {
            r.ok = false;
        } else {
            throw new IOException("Unexpected response: " + line);
        }
        int i = line.indexOf(32);
        if (i >= 0) {
            r.data = line.substring(i + 1);
        }
        return r;
    }

    private Response multilineCommand(String cmd, int size) throws IOException {
        int b;
        Response r = simpleCommand(cmd);
        if (r.ok) {
            SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size);
            int lastb = 10;
            while (true) {
                b = this.input.read();
                if (b < 0) {
                    break;
                }
                if (lastb == 10 && b == 46) {
                    if (this.debug) {
                        this.out.write(b);
                    }
                    b = this.input.read();
                    if (b == 13) {
                        if (this.debug) {
                            this.out.write(b);
                        }
                        b = this.input.read();
                        if (this.debug) {
                            this.out.write(b);
                        }
                    }
                }
                buf.write(b);
                if (this.debug) {
                    this.out.write(b);
                }
                lastb = b;
            }
            if (b < 0) {
                throw new EOFException("EOF on socket");
            }
            r.bytes = buf.toStream();
        }
        return r;
    }
}
