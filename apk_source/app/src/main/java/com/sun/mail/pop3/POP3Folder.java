package com.sun.mail.pop3;

import com.sun.mail.util.LineInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import org.teleal.cling.model.ServerClientTokens;

/* loaded from: classes.dex */
public class POP3Folder extends Folder {
    private boolean doneUidl;
    private boolean exists;
    private Vector message_cache;
    private String name;
    private boolean opened;
    private Protocol port;
    private int size;
    private int total;

    POP3Folder(POP3Store store, String name) {
        super(store);
        this.exists = false;
        this.opened = false;
        this.doneUidl = false;
        this.name = name;
        if (name.equalsIgnoreCase("INBOX")) {
            this.exists = true;
        }
    }

    @Override // javax.mail.Folder
    public String getName() {
        return this.name;
    }

    @Override // javax.mail.Folder
    public String getFullName() {
        return this.name;
    }

    @Override // javax.mail.Folder
    public Folder getParent() {
        return new DefaultFolder((POP3Store) this.store);
    }

    @Override // javax.mail.Folder
    public boolean exists() {
        return this.exists;
    }

    @Override // javax.mail.Folder
    public Folder[] list(String pattern) throws MessagingException {
        throw new MessagingException("not a directory");
    }

    @Override // javax.mail.Folder
    public char getSeparator() {
        return (char) 0;
    }

    @Override // javax.mail.Folder
    public int getType() {
        return 1;
    }

    @Override // javax.mail.Folder
    public boolean create(int type) throws MessagingException {
        return false;
    }

    @Override // javax.mail.Folder
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }

    @Override // javax.mail.Folder
    public Folder getFolder(String name) throws MessagingException {
        throw new MessagingException("not a directory");
    }

    @Override // javax.mail.Folder
    public boolean delete(boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("delete");
    }

    @Override // javax.mail.Folder
    public boolean renameTo(Folder f) throws MessagingException {
        throw new MethodNotSupportedException("renameTo");
    }

    @Override // javax.mail.Folder
    public synchronized void open(int mode) throws MessagingException {
        checkClosed();
        if (!this.exists) {
            throw new FolderNotFoundException(this, "folder is not INBOX");
        }
        try {
            this.port = ((POP3Store) this.store).getPort(this);
            Status s = this.port.stat();
            this.total = s.total;
            this.size = s.size;
            this.mode = mode;
            this.opened = true;
            this.message_cache = new Vector(this.total);
            this.message_cache.setSize(this.total);
            this.doneUidl = false;
            notifyConnectionListeners(1);
        } catch (IOException ioex) {
            try {
                try {
                    if (this.port != null) {
                        this.port.quit();
                    }
                    this.port = null;
                    ((POP3Store) this.store).closePort(this);
                } catch (IOException e) {
                    this.port = null;
                    ((POP3Store) this.store).closePort(this);
                }
                throw new MessagingException("Open failed", ioex);
            } catch (Throwable th) {
                this.port = null;
                ((POP3Store) this.store).closePort(this);
                throw th;
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // javax.mail.Folder
    public synchronized void close(boolean expunge) throws MessagingException {
        checkOpen();
        try {
            try {
                if (((POP3Store) this.store).rsetBeforeQuit) {
                    this.port.rset();
                }
                if (expunge && this.mode == 2) {
                    for (int i = 0; i < this.message_cache.size(); i++) {
                        POP3Message m = (POP3Message) this.message_cache.elementAt(i);
                        if (m != null && m.isSet(Flags.Flag.DELETED)) {
                            try {
                                this.port.dele(i + 1);
                            } catch (IOException ioex) {
                                throw new MessagingException("Exception deleting messages during close", ioex);
                            }
                        }
                    }
                }
                this.port.quit();
                this.port = null;
                ((POP3Store) this.store).closePort(this);
                this.message_cache = null;
                this.opened = false;
                notifyConnectionListeners(3);
            } catch (Throwable th) {
                this.port = null;
                ((POP3Store) this.store).closePort(this);
                this.message_cache = null;
                this.opened = false;
                notifyConnectionListeners(3);
                throw th;
            }
        } catch (IOException e) {
            this.port = null;
            ((POP3Store) this.store).closePort(this);
            this.message_cache = null;
            this.opened = false;
            notifyConnectionListeners(3);
        }
    }

    @Override // javax.mail.Folder
    public boolean isOpen() {
        if (!this.opened) {
            return false;
        }
        if (this.store.isConnected()) {
            return true;
        }
        try {
            close(false);
            return false;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override // javax.mail.Folder
    public Flags getPermanentFlags() {
        return new Flags();
    }

    @Override // javax.mail.Folder
    public synchronized int getMessageCount() throws MessagingException {
        int i;
        if (!this.opened) {
            i = -1;
        } else {
            checkReadable();
            i = this.total;
        }
        return i;
    }

    @Override // javax.mail.Folder
    public synchronized Message getMessage(int msgno) throws MessagingException {
        POP3Message m;
        checkOpen();
        m = (POP3Message) this.message_cache.elementAt(msgno - 1);
        if (m == null) {
            m = createMessage(this, msgno);
            this.message_cache.setElementAt(m, msgno - 1);
        }
        return m;
    }

    protected POP3Message createMessage(Folder f, int msgno) throws MessagingException {
        POP3Message m = null;
        Constructor cons = ((POP3Store) this.store).messageConstructor;
        if (cons != null) {
            try {
                Object[] o = {this, new Integer(msgno)};
                m = (POP3Message) cons.newInstance(o);
            } catch (Exception e) {
            }
        }
        if (m == null) {
            return new POP3Message(this, msgno);
        }
        return m;
    }

    @Override // javax.mail.Folder
    public void appendMessages(Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Append not supported");
    }

    @Override // javax.mail.Folder
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("Expunge not supported");
    }

    /* JADX WARN: Removed duplicated region for block: B:79:0x004c  */
    @Override // javax.mail.Folder
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void fetch(javax.mail.Message[] r9, javax.mail.FetchProfile r10) throws javax.mail.MessagingException {
        /*
            r8 = this;
            monitor-enter(r8)
            r8.checkReadable()     // Catch: java.lang.Throwable -> L31
            boolean r6 = r8.doneUidl     // Catch: java.lang.Throwable -> L31
            if (r6 != 0) goto L44
            javax.mail.UIDFolder$FetchProfileItem r6 = javax.mail.UIDFolder.FetchProfileItem.UID     // Catch: java.lang.Throwable -> L31
            boolean r6 = r10.contains(r6)     // Catch: java.lang.Throwable -> L31
            if (r6 == 0) goto L44
            java.util.Vector r6 = r8.message_cache     // Catch: java.lang.Throwable -> L31
            int r6 = r6.size()     // Catch: java.lang.Throwable -> L31
            java.lang.String[] r5 = new java.lang.String[r6]     // Catch: java.lang.Throwable -> L31
            com.sun.mail.pop3.Protocol r6 = r8.port     // Catch: java.io.EOFException -> L22 java.lang.Throwable -> L31 java.io.IOException -> L34
            boolean r6 = r6.uidl(r5)     // Catch: java.io.EOFException -> L22 java.lang.Throwable -> L31 java.io.IOException -> L34
            if (r6 != 0) goto L3d
        L20:
            monitor-exit(r8)
            return
        L22:
            r0 = move-exception
            r6 = 0
            r8.close(r6)     // Catch: java.lang.Throwable -> L31
            javax.mail.FolderClosedException r6 = new javax.mail.FolderClosedException     // Catch: java.lang.Throwable -> L31
            java.lang.String r7 = r0.toString()     // Catch: java.lang.Throwable -> L31
            r6.<init>(r8, r7)     // Catch: java.lang.Throwable -> L31
            throw r6     // Catch: java.lang.Throwable -> L31
        L31:
            r6 = move-exception
            monitor-exit(r8)
            throw r6
        L34:
            r1 = move-exception
            javax.mail.MessagingException r6 = new javax.mail.MessagingException     // Catch: java.lang.Throwable -> L31
            java.lang.String r7 = "error getting UIDL"
            r6.<init>(r7, r1)     // Catch: java.lang.Throwable -> L31
            throw r6     // Catch: java.lang.Throwable -> L31
        L3d:
            r2 = 0
        L3e:
            int r6 = r5.length     // Catch: java.lang.Throwable -> L31
            if (r2 < r6) goto L5f
            r6 = 1
            r8.doneUidl = r6     // Catch: java.lang.Throwable -> L31
        L44:
            javax.mail.FetchProfile$Item r6 = javax.mail.FetchProfile.Item.ENVELOPE     // Catch: java.lang.Throwable -> L31
            boolean r6 = r10.contains(r6)     // Catch: java.lang.Throwable -> L31
            if (r6 == 0) goto L20
            r2 = 0
        L4d:
            int r6 = r9.length     // Catch: java.lang.Throwable -> L31
            if (r2 >= r6) goto L20
            r4 = r9[r2]     // Catch: java.lang.Throwable -> L31 javax.mail.MessageRemovedException -> L73
            com.sun.mail.pop3.POP3Message r4 = (com.sun.mail.pop3.POP3Message) r4     // Catch: java.lang.Throwable -> L31 javax.mail.MessageRemovedException -> L73
            java.lang.String r6 = ""
            r4.getHeader(r6)     // Catch: java.lang.Throwable -> L31 javax.mail.MessageRemovedException -> L73
            r4.getSize()     // Catch: java.lang.Throwable -> L31 javax.mail.MessageRemovedException -> L73
        L5c:
            int r2 = r2 + 1
            goto L4d
        L5f:
            r6 = r5[r2]     // Catch: java.lang.Throwable -> L31
            if (r6 != 0) goto L66
        L63:
            int r2 = r2 + 1
            goto L3e
        L66:
            int r6 = r2 + 1
            javax.mail.Message r3 = r8.getMessage(r6)     // Catch: java.lang.Throwable -> L31
            com.sun.mail.pop3.POP3Message r3 = (com.sun.mail.pop3.POP3Message) r3     // Catch: java.lang.Throwable -> L31
            r6 = r5[r2]     // Catch: java.lang.Throwable -> L31
            r3.uid = r6     // Catch: java.lang.Throwable -> L31
            goto L63
        L73:
            r6 = move-exception
            goto L5c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.pop3.POP3Folder.fetch(javax.mail.Message[], javax.mail.FetchProfile):void");
    }

    public synchronized String getUID(Message msg) throws MessagingException {
        POP3Message m;
        checkOpen();
        m = (POP3Message) msg;
        try {
            if (m.uid == ServerClientTokens.UNKNOWN_PLACEHOLDER) {
                m.uid = this.port.uidl(m.getMessageNumber());
            }
        } catch (EOFException eex) {
            close(false);
            throw new FolderClosedException(this, eex.toString());
        } catch (IOException ex) {
            throw new MessagingException("error getting UIDL", ex);
        }
        return m.uid;
    }

    public synchronized int getSize() throws MessagingException {
        checkOpen();
        return this.size;
    }

    public synchronized int[] getSizes() throws MessagingException {
        int[] sizes;
        checkOpen();
        sizes = new int[this.total];
        InputStream is = null;
        LineInputStream lis = null;
        try {
            is = this.port.list();
            LineInputStream lis2 = new LineInputStream(is);
            while (true) {
                try {
                    String line = lis2.readLine();
                    if (line == null) {
                        break;
                    }
                    try {
                        StringTokenizer st = new StringTokenizer(line);
                        int msgnum = Integer.parseInt(st.nextToken());
                        int size = Integer.parseInt(st.nextToken());
                        if (msgnum > 0 && msgnum <= this.total) {
                            sizes[msgnum - 1] = size;
                        }
                    } catch (Exception e) {
                    }
                } catch (IOException e2) {
                    lis = lis2;
                    if (lis != null) {
                        try {
                            lis.close();
                        } catch (IOException e3) {
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e4) {
                        }
                    }
                    return sizes;
                } catch (Throwable th) {
                    th = th;
                    lis = lis2;
                    if (lis != null) {
                        try {
                            lis.close();
                        } catch (IOException e5) {
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                            throw th;
                        } catch (IOException e6) {
                            throw th;
                        }
                    }
                    throw th;
                }
            }
            if (lis2 != null) {
                try {
                    lis2.close();
                } catch (IOException e7) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e8) {
                }
            }
        } catch (IOException e9) {
        } catch (Throwable th2) {
            th = th2;
        }
        return sizes;
    }

    public synchronized InputStream listCommand() throws MessagingException, IOException {
        checkOpen();
        return this.port.list();
    }

    @Override // javax.mail.Folder
    protected void finalize() throws Throwable {
        super.finalize();
        close(false);
    }

    void checkOpen() throws IllegalStateException {
        if (!this.opened) {
            throw new IllegalStateException("Folder is not Open");
        }
    }

    void checkClosed() throws IllegalStateException {
        if (this.opened) {
            throw new IllegalStateException("Folder is Open");
        }
    }

    void checkReadable() throws IllegalStateException {
        if (!this.opened || (this.mode != 1 && this.mode != 2)) {
            throw new IllegalStateException("Folder is not Readable");
        }
    }

    void checkWritable() throws IllegalStateException {
        if (!this.opened || this.mode != 2) {
            throw new IllegalStateException("Folder is not Writable");
        }
    }

    Protocol getProtocol() throws IllegalStateException, MessagingException {
        checkOpen();
        return this.port;
    }

    @Override // javax.mail.Folder
    protected void notifyMessageChangedListeners(int type, Message m) {
        super.notifyMessageChangedListeners(type, m);
    }
}
