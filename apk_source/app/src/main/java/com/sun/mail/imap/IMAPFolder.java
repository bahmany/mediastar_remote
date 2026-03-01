package com.sun.mail.imap;

import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ResponseHandler;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.sun.mail.imap.protocol.ListInfo;
import com.sun.mail.imap.protocol.MailboxInfo;
import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.Status;
import com.sun.mail.imap.protocol.UID;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.ReadOnlyFolderException;
import javax.mail.StoreClosedException;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

/* loaded from: classes.dex */
public class IMAPFolder extends Folder implements UIDFolder, ResponseHandler {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int ABORTING = 2;
    private static final int IDLE = 1;
    private static final int RUNNING = 0;
    protected static final char UNKNOWN_SEPARATOR = 65535;
    protected String[] attributes;
    protected Flags availableFlags;
    private Status cachedStatus;
    private long cachedStatusTime;
    private boolean connectionPoolDebug;
    private boolean debug;
    private boolean doExpungeNotification;
    protected boolean exists;
    protected String fullName;
    private int idleState;
    protected boolean isNamespace;
    protected Vector messageCache;
    protected Object messageCacheLock;
    protected String name;
    private boolean opened;
    private PrintStream out;
    protected Flags permanentFlags;
    protected IMAPProtocol protocol;
    private int realTotal;
    private boolean reallyClosed;
    private int recent;
    protected char separator;
    private int total;
    protected int type;
    protected Hashtable uidTable;
    private long uidnext;
    private long uidvalidity;

    public interface ProtocolCommand {
        Object doCommand(IMAPProtocol iMAPProtocol) throws ProtocolException;
    }

    static {
        $assertionsDisabled = !IMAPFolder.class.desiredAssertionStatus();
    }

    public static class FetchProfileItem extends FetchProfile.Item {
        public static final FetchProfileItem HEADERS = new FetchProfileItem("HEADERS");
        public static final FetchProfileItem SIZE = new FetchProfileItem("SIZE");

        protected FetchProfileItem(String name) {
            super(name);
        }
    }

    protected IMAPFolder(String fullName, char separator, IMAPStore store) {
        int i;
        super(store);
        this.exists = false;
        this.isNamespace = false;
        this.opened = false;
        this.reallyClosed = true;
        this.idleState = 0;
        this.total = -1;
        this.recent = -1;
        this.realTotal = -1;
        this.uidvalidity = -1L;
        this.uidnext = -1L;
        this.doExpungeNotification = true;
        this.cachedStatus = null;
        this.cachedStatusTime = 0L;
        this.debug = false;
        if (fullName == null) {
            throw new NullPointerException("Folder name is null");
        }
        this.fullName = fullName;
        this.separator = separator;
        this.messageCacheLock = new Object();
        this.debug = store.getSession().getDebug();
        this.connectionPoolDebug = store.getConnectionPoolDebug();
        this.out = store.getSession().getDebugOut();
        if (this.out == null) {
            this.out = System.out;
        }
        this.isNamespace = false;
        if (separator != 65535 && separator != 0 && (i = this.fullName.indexOf(separator)) > 0 && i == this.fullName.length() - 1) {
            this.fullName = this.fullName.substring(0, i);
            this.isNamespace = true;
        }
    }

    protected IMAPFolder(String fullName, char separator, IMAPStore store, boolean isNamespace) {
        this(fullName, separator, store);
        this.isNamespace = isNamespace;
    }

    protected IMAPFolder(ListInfo li, IMAPStore store) {
        this(li.name, li.separator, store);
        if (li.hasInferiors) {
            this.type |= 2;
        }
        if (li.canOpen) {
            this.type |= 1;
        }
        this.exists = true;
        this.attributes = li.attrs;
    }

    private void checkExists() throws MessagingException {
        if (!this.exists && !exists()) {
            throw new FolderNotFoundException(this, String.valueOf(this.fullName) + " not found");
        }
    }

    private void checkClosed() {
        if (this.opened) {
            throw new IllegalStateException("This operation is not allowed on an open folder");
        }
    }

    private void checkOpened() throws FolderClosedException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (!this.opened) {
            if (this.reallyClosed) {
                throw new IllegalStateException("This operation is not allowed on a closed folder");
            }
            throw new FolderClosedException(this, "Lost folder connection to server");
        }
    }

    private void checkRange(int msgno) throws MessagingException {
        if (msgno < 1) {
            throw new IndexOutOfBoundsException();
        }
        if (msgno > this.total) {
            synchronized (this.messageCacheLock) {
                try {
                    keepConnectionAlive(false);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            if (msgno > this.total) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    private void checkFlags(Flags flags) throws MessagingException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (this.mode != 2) {
            throw new IllegalStateException("Cannot change flags on READ_ONLY folder: " + this.fullName);
        }
    }

    @Override // javax.mail.Folder
    public synchronized String getName() {
        if (this.name == null) {
            try {
                this.name = this.fullName.substring(this.fullName.lastIndexOf(getSeparator()) + 1);
            } catch (MessagingException e) {
            }
        }
        return this.name;
    }

    @Override // javax.mail.Folder
    public synchronized String getFullName() {
        return this.fullName;
    }

    @Override // javax.mail.Folder
    public synchronized Folder getParent() throws MessagingException {
        char c;
        int index;
        c = getSeparator();
        index = this.fullName.lastIndexOf(c);
        return index != -1 ? new IMAPFolder(this.fullName.substring(0, index), c, (IMAPStore) this.store) : new DefaultFolder((IMAPStore) this.store);
    }

    @Override // javax.mail.Folder
    public synchronized boolean exists() throws MessagingException {
        String lname;
        if (this.isNamespace && this.separator != 0) {
            lname = String.valueOf(this.fullName) + this.separator;
        } else {
            lname = this.fullName;
        }
        ListInfo[] li = (ListInfo[]) doCommand(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.1
            private final /* synthetic */ String val$lname;

            AnonymousClass1(String lname2) {
                str = lname2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.list("", str);
            }
        });
        if (li != null) {
            int i = findName(li, lname2);
            this.fullName = li[i].name;
            this.separator = li[i].separator;
            int len = this.fullName.length();
            if (this.separator != 0 && len > 0 && this.fullName.charAt(len - 1) == this.separator) {
                this.fullName = this.fullName.substring(0, len - 1);
            }
            this.type = 0;
            if (li[i].hasInferiors) {
                this.type |= 2;
            }
            if (li[i].canOpen) {
                this.type |= 1;
            }
            this.exists = true;
            this.attributes = li[i].attrs;
        } else {
            this.exists = this.opened;
            this.attributes = null;
        }
        return this.exists;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$1 */
    class AnonymousClass1 implements ProtocolCommand {
        private final /* synthetic */ String val$lname;

        AnonymousClass1(String lname2) {
            str = lname2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.list("", str);
        }
    }

    private int findName(ListInfo[] li, String lname) {
        int i = 0;
        while (i < li.length && !li[i].name.equals(lname)) {
            i++;
        }
        if (i >= li.length) {
            return 0;
        }
        return i;
    }

    @Override // javax.mail.Folder
    public Folder[] list(String pattern) throws MessagingException {
        return doList(pattern, false);
    }

    @Override // javax.mail.Folder
    public Folder[] listSubscribed(String pattern) throws MessagingException {
        return doList(pattern, true);
    }

    private synchronized Folder[] doList(String pattern, boolean subscribed) throws MessagingException {
        Folder[] folderArr;
        checkExists();
        if (!isDirectory()) {
            folderArr = new Folder[0];
        } else {
            char c = getSeparator();
            ListInfo[] li = (ListInfo[]) doCommandIgnoreFailure(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.2
                private final /* synthetic */ char val$c;
                private final /* synthetic */ String val$pattern;
                private final /* synthetic */ boolean val$subscribed;

                AnonymousClass2(boolean subscribed2, char c2, String pattern2) {
                    z = subscribed2;
                    c = c2;
                    str = pattern2;
                }

                @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    return z ? p.lsub("", String.valueOf(IMAPFolder.this.fullName) + c + str) : p.list("", String.valueOf(IMAPFolder.this.fullName) + c + str);
                }
            });
            if (li == null) {
                folderArr = new Folder[0];
            } else {
                int start = 0;
                if (li.length > 0 && li[0].name.equals(String.valueOf(this.fullName) + c2)) {
                    start = 1;
                }
                folderArr = new IMAPFolder[li.length - start];
                for (int i = start; i < li.length; i++) {
                    folderArr[i - start] = new IMAPFolder(li[i], (IMAPStore) this.store);
                }
            }
        }
        return folderArr;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$2 */
    class AnonymousClass2 implements ProtocolCommand {
        private final /* synthetic */ char val$c;
        private final /* synthetic */ String val$pattern;
        private final /* synthetic */ boolean val$subscribed;

        AnonymousClass2(boolean subscribed2, char c2, String pattern2) {
            z = subscribed2;
            c = c2;
            str = pattern2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return z ? p.lsub("", String.valueOf(IMAPFolder.this.fullName) + c + str) : p.list("", String.valueOf(IMAPFolder.this.fullName) + c + str);
        }
    }

    @Override // javax.mail.Folder
    public synchronized char getSeparator() throws MessagingException {
        if (this.separator == 65535) {
            ListInfo[] li = (ListInfo[]) doCommand(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.3
                AnonymousClass3() {
                }

                @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    return p.isREV1() ? p.list(IMAPFolder.this.fullName, "") : p.list("", IMAPFolder.this.fullName);
                }
            });
            if (li != null) {
                this.separator = li[0].separator;
            } else {
                this.separator = '/';
            }
        }
        return this.separator;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$3 */
    class AnonymousClass3 implements ProtocolCommand {
        AnonymousClass3() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.isREV1() ? p.list(IMAPFolder.this.fullName, "") : p.list("", IMAPFolder.this.fullName);
        }
    }

    @Override // javax.mail.Folder
    public synchronized int getType() throws MessagingException {
        if (this.opened) {
            if (this.attributes == null) {
                exists();
            }
        } else {
            checkExists();
        }
        return this.type;
    }

    @Override // javax.mail.Folder
    public synchronized boolean isSubscribed() {
        String lname;
        boolean z;
        ListInfo[] li = (ListInfo[]) null;
        if (this.isNamespace && this.separator != 0) {
            lname = String.valueOf(this.fullName) + this.separator;
        } else {
            lname = this.fullName;
        }
        try {
            li = (ListInfo[]) doProtocolCommand(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.4
                private final /* synthetic */ String val$lname;

                AnonymousClass4(String lname2) {
                    str = lname2;
                }

                @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    return p.lsub("", str);
                }
            });
        } catch (ProtocolException e) {
        }
        if (li != null) {
            int i = findName(li, lname2);
            z = li[i].canOpen;
        } else {
            z = false;
        }
        return z;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$4 */
    class AnonymousClass4 implements ProtocolCommand {
        private final /* synthetic */ String val$lname;

        AnonymousClass4(String lname2) {
            str = lname2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.lsub("", str);
        }
    }

    @Override // javax.mail.Folder
    public synchronized void setSubscribed(boolean subscribe) throws MessagingException {
        doCommandIgnoreFailure(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.5
            private final /* synthetic */ boolean val$subscribe;

            AnonymousClass5(boolean subscribe2) {
                z = subscribe2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                if (z) {
                    p.subscribe(IMAPFolder.this.fullName);
                    return null;
                }
                p.unsubscribe(IMAPFolder.this.fullName);
                return null;
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$5 */
    class AnonymousClass5 implements ProtocolCommand {
        private final /* synthetic */ boolean val$subscribe;

        AnonymousClass5(boolean subscribe2) {
            z = subscribe2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            if (z) {
                p.subscribe(IMAPFolder.this.fullName);
                return null;
            }
            p.unsubscribe(IMAPFolder.this.fullName);
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0016  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0019 A[Catch: all -> 0x0024, TRY_ENTER, TryCatch #0 {, blocks: (B:25:0x0006, B:26:0x000a, B:31:0x0019, B:33:0x001f), top: B:38:0x0006 }] */
    @Override // javax.mail.Folder
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean create(int r6) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            r4 = r6 & 1
            if (r4 != 0) goto La
            char r0 = r5.getSeparator()     // Catch: java.lang.Throwable -> L24
        La:
            r3 = r0
            com.sun.mail.imap.IMAPFolder$6 r4 = new com.sun.mail.imap.IMAPFolder$6     // Catch: java.lang.Throwable -> L24
            r4.<init>()     // Catch: java.lang.Throwable -> L24
            java.lang.Object r1 = r5.doCommandIgnoreFailure(r4)     // Catch: java.lang.Throwable -> L24
            if (r1 != 0) goto L19
            r2 = 0
        L17:
            monitor-exit(r5)
            return r2
        L19:
            boolean r2 = r5.exists()     // Catch: java.lang.Throwable -> L24
            if (r2 == 0) goto L17
            r4 = 1
            r5.notifyFolderListeners(r4)     // Catch: java.lang.Throwable -> L24
            goto L17
        L24:
            r4 = move-exception
            monitor-exit(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.create(int):boolean");
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$6 */
    class AnonymousClass6 implements ProtocolCommand {
        private final /* synthetic */ char val$sep;
        private final /* synthetic */ int val$type;

        AnonymousClass6(int i, char c) {
            i = i;
            c = c;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            ListInfo[] li;
            if ((i & 1) == 0) {
                p.create(String.valueOf(IMAPFolder.this.fullName) + c);
            } else {
                p.create(IMAPFolder.this.fullName);
                if ((i & 2) != 0 && (li = p.list("", IMAPFolder.this.fullName)) != null && !li[0].hasInferiors) {
                    p.delete(IMAPFolder.this.fullName);
                    throw new ProtocolException("Unsupported type");
                }
            }
            return Boolean.TRUE;
        }
    }

    @Override // javax.mail.Folder
    public synchronized boolean hasNewMessages() throws MessagingException {
        synchronized (this) {
            if (this.opened) {
                synchronized (this.messageCacheLock) {
                    try {
                        keepConnectionAlive(true);
                        zBooleanValue = this.recent > 0;
                    } catch (ConnectionException cex) {
                        throw new FolderClosedException(this, cex.getMessage());
                    } catch (ProtocolException pex) {
                        throw new MessagingException(pex.getMessage(), pex);
                    }
                }
            } else {
                checkExists();
                Boolean b = (Boolean) doCommandIgnoreFailure(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.7
                    AnonymousClass7() {
                    }

                    @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                    public Object doCommand(IMAPProtocol p) throws ProtocolException {
                        ListInfo[] li = p.list("", IMAPFolder.this.fullName);
                        if (li != null) {
                            if (li[0].changeState == 1) {
                                return Boolean.TRUE;
                            }
                            if (li[0].changeState == 2) {
                                return Boolean.FALSE;
                            }
                        }
                        Status status = IMAPFolder.this.getStatus();
                        if (status.recent > 0) {
                            return Boolean.TRUE;
                        }
                        return Boolean.FALSE;
                    }
                });
                if (b != null) {
                    zBooleanValue = b.booleanValue();
                }
            }
        }
        return zBooleanValue;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$7 */
    class AnonymousClass7 implements ProtocolCommand {
        AnonymousClass7() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            ListInfo[] li = p.list("", IMAPFolder.this.fullName);
            if (li != null) {
                if (li[0].changeState == 1) {
                    return Boolean.TRUE;
                }
                if (li[0].changeState == 2) {
                    return Boolean.FALSE;
                }
            }
            Status status = IMAPFolder.this.getStatus();
            if (status.recent > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    @Override // javax.mail.Folder
    public Folder getFolder(String name) throws MessagingException {
        if (this.attributes != null && !isDirectory()) {
            throw new MessagingException("Cannot contain subfolders");
        }
        char c = getSeparator();
        return new IMAPFolder(String.valueOf(this.fullName) + c + name, c, (IMAPStore) this.store);
    }

    @Override // javax.mail.Folder
    public synchronized boolean delete(boolean recurse) throws MessagingException {
        boolean z = false;
        synchronized (this) {
            checkClosed();
            if (recurse) {
                Folder[] f = list();
                for (Folder folder : f) {
                    folder.delete(recurse);
                }
            }
            Object ret = doCommandIgnoreFailure(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.8
                AnonymousClass8() {
                }

                @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    p.delete(IMAPFolder.this.fullName);
                    return Boolean.TRUE;
                }
            });
            if (ret != null) {
                this.exists = false;
                this.attributes = null;
                notifyFolderListeners(2);
                z = true;
            }
        }
        return z;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$8 */
    class AnonymousClass8 implements ProtocolCommand {
        AnonymousClass8() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.delete(IMAPFolder.this.fullName);
            return Boolean.TRUE;
        }
    }

    @Override // javax.mail.Folder
    public synchronized boolean renameTo(Folder f) throws MessagingException {
        boolean z = false;
        synchronized (this) {
            checkClosed();
            checkExists();
            if (f.getStore() != this.store) {
                throw new MessagingException("Can't rename across Stores");
            }
            Object ret = doCommandIgnoreFailure(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.9
                private final /* synthetic */ Folder val$f;

                AnonymousClass9(Folder f2) {
                    folder = f2;
                }

                @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    p.rename(IMAPFolder.this.fullName, folder.getFullName());
                    return Boolean.TRUE;
                }
            });
            if (ret != null) {
                this.exists = false;
                this.attributes = null;
                notifyFolderRenamedListeners(f2);
                z = true;
            }
        }
        return z;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$9 */
    class AnonymousClass9 implements ProtocolCommand {
        private final /* synthetic */ Folder val$f;

        AnonymousClass9(Folder f2) {
            folder = f2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.rename(IMAPFolder.this.fullName, folder.getFullName());
            return Boolean.TRUE;
        }
    }

    @Override // javax.mail.Folder
    public synchronized void open(int mode) throws MessagingException {
        MailboxInfo mi;
        checkClosed();
        this.protocol = ((IMAPStore) this.store).getProtocol(this);
        CommandFailedException exc = null;
        synchronized (this.messageCacheLock) {
            this.protocol.addResponseHandler(this);
            try {
                if (mode == 1) {
                    mi = this.protocol.examine(this.fullName);
                } else {
                    mi = this.protocol.select(this.fullName);
                }
                if (mi.mode != mode && (mode != 2 || mi.mode != 1 || !((IMAPStore) this.store).allowReadOnlySelect())) {
                    try {
                        try {
                            this.protocol.close();
                            releaseProtocol(true);
                        } catch (ProtocolException e) {
                            try {
                                this.protocol.logout();
                                releaseProtocol(false);
                            } catch (ProtocolException e2) {
                                releaseProtocol(false);
                            } catch (Throwable th) {
                                releaseProtocol(false);
                                throw th;
                            }
                        }
                    } catch (Throwable th2) {
                    }
                    this.protocol = null;
                    throw new ReadOnlyFolderException(this, "Cannot open in desired mode");
                }
                this.opened = true;
                this.reallyClosed = false;
                this.mode = mi.mode;
                this.availableFlags = mi.availableFlags;
                this.permanentFlags = mi.permanentFlags;
                int i = mi.total;
                this.realTotal = i;
                this.total = i;
                this.recent = mi.recent;
                this.uidvalidity = mi.uidvalidity;
                this.uidnext = mi.uidnext;
                this.messageCache = new Vector(this.total);
                for (int i2 = 0; i2 < this.total; i2++) {
                    this.messageCache.addElement(new IMAPMessage(this, i2 + 1, i2 + 1));
                }
            } catch (CommandFailedException cex) {
                releaseProtocol(true);
                this.protocol = null;
                exc = cex;
            } catch (ProtocolException pex) {
                try {
                    this.protocol.logout();
                } catch (ProtocolException e3) {
                } catch (Throwable th3) {
                }
                releaseProtocol(false);
                this.protocol = null;
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (exc != null) {
            checkExists();
            if ((this.type & 1) == 0) {
                throw new MessagingException("folder cannot contain messages");
            }
            throw new MessagingException(exc.getMessage(), exc);
        }
        this.exists = true;
        this.attributes = null;
        this.type = 1;
        notifyConnectionListeners(1);
    }

    @Override // javax.mail.Folder
    public synchronized void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
        checkOpened();
        IMAPMessage.fetch(this, msgs, fp);
    }

    @Override // javax.mail.Folder
    public synchronized void setFlags(Message[] msgs, Flags flag, boolean value) throws MessagingException {
        checkOpened();
        checkFlags(flag);
        if (msgs.length != 0) {
            synchronized (this.messageCacheLock) {
                try {
                    IMAPProtocol p = getProtocol();
                    MessageSet[] ms = Utility.toMessageSet(msgs, null);
                    if (ms == null) {
                        throw new MessageRemovedException("Messages have been removed");
                    }
                    p.storeFlags(ms, flag, value);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
        }
    }

    @Override // javax.mail.Folder
    public synchronized void close(boolean expunge) throws MessagingException {
        close(expunge, false);
    }

    public synchronized void forceClose() throws MessagingException {
        close(false, true);
    }

    private void close(boolean expunge, boolean force) throws MessagingException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        synchronized (this.messageCacheLock) {
            if (!this.opened && this.reallyClosed) {
                throw new IllegalStateException("This operation is not allowed on a closed folder");
            }
            this.reallyClosed = true;
            try {
                if (this.opened) {
                    try {
                        waitIfIdle();
                        if (force) {
                            if (this.debug) {
                                this.out.println("DEBUG: forcing folder " + this.fullName + " to close");
                            }
                            if (this.protocol != null) {
                                this.protocol.disconnect();
                            }
                        } else if (((IMAPStore) this.store).isConnectionPoolFull()) {
                            if (this.debug) {
                                this.out.println("DEBUG: pool is full, not adding an Authenticated connection");
                            }
                            if (expunge) {
                                this.protocol.close();
                            }
                            if (this.protocol != null) {
                                this.protocol.logout();
                            }
                        } else {
                            if (!expunge && this.mode == 2) {
                                try {
                                    this.protocol.examine(this.fullName);
                                } catch (ProtocolException e) {
                                    if (this.protocol != null) {
                                        this.protocol.disconnect();
                                    }
                                }
                            }
                            if (this.protocol != null) {
                                this.protocol.close();
                            }
                        }
                    } catch (ProtocolException pex) {
                        throw new MessagingException(pex.getMessage(), pex);
                    }
                }
            } finally {
                if (this.opened) {
                    cleanup(true);
                }
            }
        }
    }

    private void cleanup(boolean returnToPool) {
        releaseProtocol(returnToPool);
        this.protocol = null;
        this.messageCache = null;
        this.uidTable = null;
        this.exists = false;
        this.attributes = null;
        this.opened = false;
        this.idleState = 0;
        notifyConnectionListeners(3);
    }

    @Override // javax.mail.Folder
    public synchronized boolean isOpen() {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    keepConnectionAlive(false);
                } catch (ProtocolException e) {
                }
            }
        }
        return this.opened;
    }

    @Override // javax.mail.Folder
    public synchronized Flags getPermanentFlags() {
        return (Flags) this.permanentFlags.clone();
    }

    @Override // javax.mail.Folder
    public synchronized int getMessageCount() throws MessagingException {
        int i;
        if (!this.opened) {
            checkExists();
            try {
                try {
                    Status status = getStatus();
                    i = status.total;
                } catch (BadCommandException e) {
                    IMAPProtocol p = null;
                    try {
                        try {
                            p = getStoreProtocol();
                            MailboxInfo minfo = p.examine(this.fullName);
                            p.close();
                            i = minfo.total;
                        } catch (ProtocolException pex) {
                            throw new MessagingException(pex.getMessage(), pex);
                        }
                    } finally {
                        releaseStoreProtocol(p);
                    }
                } catch (ConnectionException cex) {
                    throw new StoreClosedException(this.store, cex.getMessage());
                }
            } catch (ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
        } else {
            synchronized (this.messageCacheLock) {
                try {
                    keepConnectionAlive(true);
                    i = this.total;
                } catch (ConnectionException cex2) {
                    throw new FolderClosedException(this, cex2.getMessage());
                } catch (ProtocolException pex3) {
                    throw new MessagingException(pex3.getMessage(), pex3);
                }
            }
        }
        return i;
    }

    @Override // javax.mail.Folder
    public synchronized int getNewMessageCount() throws MessagingException {
        int i;
        if (!this.opened) {
            checkExists();
            try {
                try {
                    Status status = getStatus();
                    i = status.recent;
                } catch (BadCommandException e) {
                    IMAPProtocol p = null;
                    try {
                        try {
                            p = getStoreProtocol();
                            MailboxInfo minfo = p.examine(this.fullName);
                            p.close();
                            i = minfo.recent;
                        } catch (ProtocolException pex) {
                            throw new MessagingException(pex.getMessage(), pex);
                        }
                    } finally {
                        releaseStoreProtocol(p);
                    }
                } catch (ConnectionException cex) {
                    throw new StoreClosedException(this.store, cex.getMessage());
                }
            } catch (ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
        } else {
            synchronized (this.messageCacheLock) {
                try {
                    keepConnectionAlive(true);
                    i = this.recent;
                } catch (ConnectionException cex2) {
                    throw new FolderClosedException(this, cex2.getMessage());
                } catch (ProtocolException pex3) {
                    throw new MessagingException(pex3.getMessage(), pex3);
                }
            }
        }
        return i;
    }

    @Override // javax.mail.Folder
    public synchronized int getUnreadMessageCount() throws MessagingException {
        int length;
        if (!this.opened) {
            checkExists();
            try {
                try {
                    Status status = getStatus();
                    length = status.unseen;
                } catch (BadCommandException e) {
                    length = -1;
                } catch (ConnectionException cex) {
                    throw new StoreClosedException(this.store, cex.getMessage());
                }
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } else {
            Flags f = new Flags();
            f.add(Flags.Flag.SEEN);
            try {
                try {
                    synchronized (this.messageCacheLock) {
                        int[] matches = getProtocol().search(new FlagTerm(f, false));
                        length = matches.length;
                    }
                } catch (ConnectionException cex2) {
                    throw new FolderClosedException(this, cex2.getMessage());
                }
            } catch (ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
        }
        return length;
    }

    @Override // javax.mail.Folder
    public synchronized int getDeletedMessageCount() throws MessagingException {
        int length;
        if (!this.opened) {
            checkExists();
            length = -1;
        } else {
            Flags f = new Flags();
            f.add(Flags.Flag.DELETED);
            try {
                synchronized (this.messageCacheLock) {
                    int[] matches = getProtocol().search(new FlagTerm(f, true));
                    length = matches.length;
                }
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        return length;
    }

    public Status getStatus() throws ProtocolException {
        int statusCacheTimeout = ((IMAPStore) this.store).getStatusCacheTimeout();
        if (statusCacheTimeout > 0 && this.cachedStatus != null && System.currentTimeMillis() - this.cachedStatusTime < statusCacheTimeout) {
            return this.cachedStatus;
        }
        IMAPProtocol p = null;
        try {
            p = getStoreProtocol();
            Status s = p.status(this.fullName, null);
            if (statusCacheTimeout > 0) {
                this.cachedStatus = s;
                this.cachedStatusTime = System.currentTimeMillis();
            }
            return s;
        } finally {
            releaseStoreProtocol(p);
        }
    }

    @Override // javax.mail.Folder
    public synchronized Message getMessage(int msgnum) throws MessagingException {
        checkOpened();
        checkRange(msgnum);
        return (Message) this.messageCache.elementAt(msgnum - 1);
    }

    @Override // javax.mail.Folder
    public synchronized void appendMessages(Message[] msgs) throws MessagingException {
        checkExists();
        int maxsize = ((IMAPStore) this.store).getAppendBufferSize();
        for (Message m : msgs) {
            try {
                MessageLiteral mos = new MessageLiteral(m, m.getSize() > maxsize ? 0 : maxsize);
                Date d = m.getReceivedDate();
                if (d == null) {
                    d = m.getSentDate();
                }
                Date dd = d;
                Flags f = m.getFlags();
                doCommand(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.10
                    private final /* synthetic */ Date val$dd;
                    private final /* synthetic */ Flags val$f;
                    private final /* synthetic */ MessageLiteral val$mos;

                    AnonymousClass10(Flags f2, Date dd2, MessageLiteral mos2) {
                        flags = f2;
                        date = dd2;
                        messageLiteral = mos2;
                    }

                    @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                    public Object doCommand(IMAPProtocol p) throws ProtocolException {
                        p.append(IMAPFolder.this.fullName, flags, date, messageLiteral);
                        return null;
                    }
                });
            } catch (IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            } catch (MessageRemovedException e) {
            }
        }
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$10 */
    class AnonymousClass10 implements ProtocolCommand {
        private final /* synthetic */ Date val$dd;
        private final /* synthetic */ Flags val$f;
        private final /* synthetic */ MessageLiteral val$mos;

        AnonymousClass10(Flags f2, Date dd2, MessageLiteral mos2) {
            flags = f2;
            date = dd2;
            messageLiteral = mos2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.append(IMAPFolder.this.fullName, flags, date, messageLiteral);
            return null;
        }
    }

    public synchronized AppendUID[] appendUIDMessages(Message[] msgs) throws MessagingException {
        AppendUID[] uids;
        checkExists();
        int maxsize = ((IMAPStore) this.store).getAppendBufferSize();
        uids = new AppendUID[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            Message m = msgs[i];
            try {
                MessageLiteral mos = new MessageLiteral(m, m.getSize() > maxsize ? 0 : maxsize);
                Date d = m.getReceivedDate();
                if (d == null) {
                    d = m.getSentDate();
                }
                Date dd = d;
                Flags f = m.getFlags();
                AppendUID auid = (AppendUID) doCommand(new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.11
                    private final /* synthetic */ Date val$dd;
                    private final /* synthetic */ Flags val$f;
                    private final /* synthetic */ MessageLiteral val$mos;

                    AnonymousClass11(Flags f2, Date dd2, MessageLiteral mos2) {
                        flags = f2;
                        date = dd2;
                        messageLiteral = mos2;
                    }

                    @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
                    public Object doCommand(IMAPProtocol p) throws ProtocolException {
                        return p.appenduid(IMAPFolder.this.fullName, flags, date, messageLiteral);
                    }
                });
                uids[i] = auid;
            } catch (IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            } catch (MessageRemovedException e) {
            }
        }
        return uids;
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$11 */
    class AnonymousClass11 implements ProtocolCommand {
        private final /* synthetic */ Date val$dd;
        private final /* synthetic */ Flags val$f;
        private final /* synthetic */ MessageLiteral val$mos;

        AnonymousClass11(Flags f2, Date dd2, MessageLiteral mos2) {
            flags = f2;
            date = dd2;
            messageLiteral = mos2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.appenduid(IMAPFolder.this.fullName, flags, date, messageLiteral);
        }
    }

    public synchronized Message[] addMessages(Message[] msgs) throws MessagingException {
        Message[] rmsgs;
        checkOpened();
        rmsgs = new MimeMessage[msgs.length];
        AppendUID[] uids = appendUIDMessages(msgs);
        for (int i = 0; i < uids.length; i++) {
            AppendUID auid = uids[i];
            if (auid != null && auid.uidvalidity == this.uidvalidity) {
                try {
                    rmsgs[i] = getMessageByUID(auid.uid);
                } catch (MessagingException e) {
                }
            }
        }
        return rmsgs;
    }

    @Override // javax.mail.Folder
    public synchronized void copyMessages(Message[] msgs, Folder folder) throws MessagingException {
        checkOpened();
        if (msgs.length != 0) {
            if (folder.getStore() == this.store) {
                synchronized (this.messageCacheLock) {
                    try {
                        try {
                            try {
                                IMAPProtocol p = getProtocol();
                                MessageSet[] ms = Utility.toMessageSet(msgs, null);
                                if (ms == null) {
                                    throw new MessageRemovedException("Messages have been removed");
                                }
                                p.copy(ms, folder.getFullName());
                            } catch (ProtocolException pex) {
                                throw new MessagingException(pex.getMessage(), pex);
                            }
                        } catch (ConnectionException cex) {
                            throw new FolderClosedException(this, cex.getMessage());
                        }
                    } catch (CommandFailedException cfx) {
                        if (cfx.getMessage().indexOf("TRYCREATE") != -1) {
                            throw new FolderNotFoundException(folder, String.valueOf(folder.getFullName()) + " does not exist");
                        }
                        throw new MessagingException(cfx.getMessage(), cfx);
                    }
                }
            } else {
                super.copyMessages(msgs, folder);
            }
        }
    }

    @Override // javax.mail.Folder
    public synchronized Message[] expunge() throws MessagingException {
        return expunge(null);
    }

    /* JADX WARN: Finally extract failed */
    public synchronized Message[] expunge(Message[] msgs) throws MessagingException {
        Message[] rmsgs;
        checkOpened();
        Vector v = new Vector();
        if (msgs != null) {
            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            fetch(msgs, fp);
        }
        synchronized (this.messageCacheLock) {
            this.doExpungeNotification = false;
            try {
                try {
                    IMAPProtocol p = getProtocol();
                    if (msgs != null) {
                        p.uidexpunge(Utility.toUIDSet(msgs));
                    } else {
                        p.expunge();
                    }
                    this.doExpungeNotification = true;
                    int i = 0;
                    while (i < this.messageCache.size()) {
                        IMAPMessage m = (IMAPMessage) this.messageCache.elementAt(i);
                        if (m.isExpunged()) {
                            v.addElement(m);
                            this.messageCache.removeElementAt(i);
                            if (this.uidTable != null) {
                                long uid = m.getUID();
                                if (uid != -1) {
                                    this.uidTable.remove(new Long(uid));
                                }
                            }
                        } else {
                            m.setMessageNumber(m.getSequenceNumber());
                            i++;
                        }
                    }
                } catch (CommandFailedException cfx) {
                    if (this.mode != 2) {
                        throw new IllegalStateException("Cannot expunge READ_ONLY folder: " + this.fullName);
                    }
                    throw new MessagingException(cfx.getMessage(), cfx);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            } catch (Throwable th) {
                this.doExpungeNotification = true;
                throw th;
            }
        }
        this.total = this.messageCache.size();
        rmsgs = new Message[v.size()];
        v.copyInto(rmsgs);
        if (rmsgs.length > 0) {
            notifyMessageRemovedListeners(true, rmsgs);
        }
        return rmsgs;
    }

    @Override // javax.mail.Folder
    public synchronized Message[] search(SearchTerm term) throws MessagingException {
        Message[] matchMsgs;
        checkOpened();
        try {
            try {
                try {
                    matchMsgs = (Message[]) null;
                    synchronized (this.messageCacheLock) {
                        int[] matches = getProtocol().search(term);
                        if (matches != null) {
                            matchMsgs = new IMAPMessage[matches.length];
                            for (int i = 0; i < matches.length; i++) {
                                matchMsgs[i] = getMessageBySeqNumber(matches[i]);
                            }
                        }
                    }
                } catch (CommandFailedException e) {
                    matchMsgs = super.search(term);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } catch (SearchException e2) {
            matchMsgs = super.search(term);
        }
        return matchMsgs;
    }

    @Override // javax.mail.Folder
    public synchronized Message[] search(SearchTerm term, Message[] msgs) throws MessagingException {
        checkOpened();
        if (msgs.length != 0) {
            try {
                try {
                    Message[] matchMsgs = (Message[]) null;
                    synchronized (this.messageCacheLock) {
                        IMAPProtocol p = getProtocol();
                        MessageSet[] ms = Utility.toMessageSet(msgs, null);
                        if (ms == null) {
                            throw new MessageRemovedException("Messages have been removed");
                        }
                        int[] matches = p.search(ms, term);
                        if (matches != null) {
                            matchMsgs = new IMAPMessage[matches.length];
                            for (int i = 0; i < matches.length; i++) {
                                matchMsgs[i] = getMessageBySeqNumber(matches[i]);
                            }
                        }
                    }
                    msgs = matchMsgs;
                } catch (CommandFailedException e) {
                    msgs = super.search(term, msgs);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                }
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            } catch (SearchException e2) {
                msgs = super.search(term, msgs);
            }
        }
        return msgs;
    }

    @Override // javax.mail.UIDFolder
    public synchronized long getUIDValidity() throws MessagingException {
        long j;
        if (this.opened) {
            j = this.uidvalidity;
        } else {
            IMAPProtocol p = null;
            Status status = null;
            try {
                try {
                    p = getStoreProtocol();
                    String[] item = {"UIDVALIDITY"};
                    status = p.status(this.fullName, item);
                } catch (BadCommandException bex) {
                    throw new MessagingException("Cannot obtain UIDValidity", bex);
                } catch (ConnectionException cex) {
                    throwClosedException(cex);
                    releaseStoreProtocol(p);
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
                j = status.uidvalidity;
            } finally {
                releaseStoreProtocol(null);
            }
        }
        return j;
    }

    public synchronized long getUIDNext() throws MessagingException {
        long j;
        if (this.opened) {
            j = this.uidnext;
        } else {
            IMAPProtocol p = null;
            Status status = null;
            try {
                try {
                    p = getStoreProtocol();
                    String[] item = {"UIDNEXT"};
                    status = p.status(this.fullName, item);
                } catch (BadCommandException bex) {
                    throw new MessagingException("Cannot obtain UIDNext", bex);
                } catch (ConnectionException cex) {
                    throwClosedException(cex);
                    releaseStoreProtocol(p);
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
                j = status.uidnext;
            } finally {
                releaseStoreProtocol(null);
            }
        }
        return j;
    }

    @Override // javax.mail.UIDFolder
    public synchronized Message getMessageByUID(long uid) throws MessagingException {
        IMAPMessage m;
        checkOpened();
        IMAPMessage m2 = null;
        try {
            synchronized (this.messageCacheLock) {
                Long l = new Long(uid);
                if (this.uidTable != null) {
                    m2 = (IMAPMessage) this.uidTable.get(l);
                    if (m2 != null) {
                        m = m2;
                    }
                } else {
                    this.uidTable = new Hashtable();
                }
                UID u = getProtocol().fetchSequenceNumber(uid);
                if (u != null && u.seqnum <= this.total) {
                    m2 = getMessageBySeqNumber(u.seqnum);
                    m2.setUID(u.uid);
                    this.uidTable.put(l, m2);
                }
                m = m2;
            }
        } catch (ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return m;
    }

    @Override // javax.mail.UIDFolder
    public synchronized Message[] getMessagesByUID(long start, long end) throws MessagingException {
        Message[] msgs;
        checkOpened();
        try {
            synchronized (this.messageCacheLock) {
                if (this.uidTable == null) {
                    this.uidTable = new Hashtable();
                }
                UID[] ua = getProtocol().fetchSequenceNumbers(start, end);
                msgs = new Message[ua.length];
                for (int i = 0; i < ua.length; i++) {
                    IMAPMessage m = getMessageBySeqNumber(ua[i].seqnum);
                    m.setUID(ua[i].uid);
                    msgs[i] = m;
                    this.uidTable.put(new Long(ua[i].uid), m);
                }
            }
        } catch (ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        return msgs;
    }

    @Override // javax.mail.UIDFolder
    public synchronized Message[] getMessagesByUID(long[] uids) throws MessagingException {
        Message[] msgs;
        checkOpened();
        try {
            try {
                synchronized (this.messageCacheLock) {
                    long[] unavailUids = uids;
                    if (this.uidTable != null) {
                        Vector v = new Vector();
                        for (long j : uids) {
                            Hashtable hashtable = this.uidTable;
                            Long l = new Long(j);
                            if (!hashtable.containsKey(l)) {
                                v.addElement(l);
                            }
                        }
                        int vsize = v.size();
                        unavailUids = new long[vsize];
                        for (int i = 0; i < vsize; i++) {
                            unavailUids[i] = ((Long) v.elementAt(i)).longValue();
                        }
                    } else {
                        this.uidTable = new Hashtable();
                    }
                    if (unavailUids.length > 0) {
                        UID[] ua = getProtocol().fetchSequenceNumbers(unavailUids);
                        for (int i2 = 0; i2 < ua.length; i2++) {
                            IMAPMessage m = getMessageBySeqNumber(ua[i2].seqnum);
                            m.setUID(ua[i2].uid);
                            this.uidTable.put(new Long(ua[i2].uid), m);
                        }
                    }
                    msgs = new Message[uids.length];
                    for (int i3 = 0; i3 < uids.length; i3++) {
                        msgs[i3] = (Message) this.uidTable.get(new Long(uids[i3]));
                    }
                }
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } catch (ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        return msgs;
    }

    @Override // javax.mail.UIDFolder
    public synchronized long getUID(Message message) throws MessagingException {
        long uid;
        if (message.getFolder() != this) {
            throw new NoSuchElementException("Message does not belong to this folder");
        }
        checkOpened();
        IMAPMessage m = (IMAPMessage) message;
        long uid2 = m.getUID();
        if (uid2 != -1) {
            uid = uid2;
        } else {
            synchronized (this.messageCacheLock) {
                try {
                    IMAPProtocol p = getProtocol();
                    m.checkExpunged();
                    UID u = p.fetchUID(m.getSequenceNumber());
                    if (u != null) {
                        uid2 = u.uid;
                        m.setUID(uid2);
                        if (this.uidTable == null) {
                            this.uidTable = new Hashtable();
                        }
                        this.uidTable.put(new Long(uid2), m);
                    }
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            uid = uid2;
        }
        return uid;
    }

    public Quota[] getQuota() throws MessagingException {
        return (Quota[]) doOptionalCommand("QUOTA not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.12
            AnonymousClass12() {
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.getQuotaRoot(IMAPFolder.this.fullName);
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$12 */
    class AnonymousClass12 implements ProtocolCommand {
        AnonymousClass12() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.getQuotaRoot(IMAPFolder.this.fullName);
        }
    }

    public void setQuota(Quota quota) throws MessagingException {
        doOptionalCommand("QUOTA not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.13
            private final /* synthetic */ Quota val$quota;

            AnonymousClass13(Quota quota2) {
                quota = quota2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.setQuota(quota);
                return null;
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$13 */
    class AnonymousClass13 implements ProtocolCommand {
        private final /* synthetic */ Quota val$quota;

        AnonymousClass13(Quota quota2) {
            quota = quota2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.setQuota(quota);
            return null;
        }
    }

    public ACL[] getACL() throws MessagingException {
        return (ACL[]) doOptionalCommand("ACL not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.14
            AnonymousClass14() {
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.getACL(IMAPFolder.this.fullName);
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$14 */
    class AnonymousClass14 implements ProtocolCommand {
        AnonymousClass14() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.getACL(IMAPFolder.this.fullName);
        }
    }

    public void addACL(ACL acl) throws MessagingException {
        setACL(acl, (char) 0);
    }

    public void removeACL(String name) throws MessagingException {
        doOptionalCommand("ACL not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.15
            private final /* synthetic */ String val$name;

            AnonymousClass15(String name2) {
                str = name2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.deleteACL(IMAPFolder.this.fullName, str);
                return null;
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$15 */
    class AnonymousClass15 implements ProtocolCommand {
        private final /* synthetic */ String val$name;

        AnonymousClass15(String name2) {
            str = name2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.deleteACL(IMAPFolder.this.fullName, str);
            return null;
        }
    }

    public void addRights(ACL acl) throws MessagingException {
        setACL(acl, '+');
    }

    public void removeRights(ACL acl) throws MessagingException {
        setACL(acl, '-');
    }

    public Rights[] listRights(String name) throws MessagingException {
        return (Rights[]) doOptionalCommand("ACL not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.16
            private final /* synthetic */ String val$name;

            AnonymousClass16(String name2) {
                str = name2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.listRights(IMAPFolder.this.fullName, str);
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$16 */
    class AnonymousClass16 implements ProtocolCommand {
        private final /* synthetic */ String val$name;

        AnonymousClass16(String name2) {
            str = name2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.listRights(IMAPFolder.this.fullName, str);
        }
    }

    public Rights myRights() throws MessagingException {
        return (Rights) doOptionalCommand("ACL not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.17
            AnonymousClass17() {
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.myRights(IMAPFolder.this.fullName);
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$17 */
    class AnonymousClass17 implements ProtocolCommand {
        AnonymousClass17() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.myRights(IMAPFolder.this.fullName);
        }
    }

    private void setACL(ACL acl, char mod) throws MessagingException {
        doOptionalCommand("ACL not supported", new ProtocolCommand() { // from class: com.sun.mail.imap.IMAPFolder.18
            private final /* synthetic */ ACL val$acl;
            private final /* synthetic */ char val$mod;

            AnonymousClass18(char mod2, ACL acl2) {
                c = mod2;
                acl = acl2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.setACL(IMAPFolder.this.fullName, c, acl);
                return null;
            }
        });
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$18 */
    class AnonymousClass18 implements ProtocolCommand {
        private final /* synthetic */ ACL val$acl;
        private final /* synthetic */ char val$mod;

        AnonymousClass18(char mod2, ACL acl2) {
            c = mod2;
            acl = acl2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            p.setACL(IMAPFolder.this.fullName, c, acl);
            return null;
        }
    }

    public String[] getAttributes() throws MessagingException {
        if (this.attributes == null) {
            exists();
        }
        return (String[]) this.attributes.clone();
    }

    /* JADX WARN: Code restructure failed: missing block: B:123:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x004a, code lost:
    
        r1 = ((com.sun.mail.imap.IMAPStore) r8.store).getMinIdleTime();
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0052, code lost:
    
        if (r1 <= 0) goto L123;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0055, code lost:
    
        java.lang.Thread.sleep(r1);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void idle() throws java.lang.InterruptedException, javax.mail.MessagingException {
        /*
            r8 = this;
            boolean r5 = com.sun.mail.imap.IMAPFolder.$assertionsDisabled
            if (r5 != 0) goto L10
            boolean r5 = java.lang.Thread.holdsLock(r8)
            if (r5 == 0) goto L10
            java.lang.AssertionError r5 = new java.lang.AssertionError
            r5.<init>()
            throw r5
        L10:
            monitor-enter(r8)
            r8.checkOpened()     // Catch: java.lang.Throwable -> L5b
            java.lang.String r5 = "IDLE not supported"
            com.sun.mail.imap.IMAPFolder$19 r6 = new com.sun.mail.imap.IMAPFolder$19     // Catch: java.lang.Throwable -> L5b
            r6.<init>()     // Catch: java.lang.Throwable -> L5b
            java.lang.Object r4 = r8.doOptionalCommand(r5, r6)     // Catch: java.lang.Throwable -> L5b
            java.lang.Boolean r4 = (java.lang.Boolean) r4     // Catch: java.lang.Throwable -> L5b
            boolean r5 = r4.booleanValue()     // Catch: java.lang.Throwable -> L5b
            if (r5 != 0) goto L29
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L5b
        L28:
            return
        L29:
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L5b
        L2a:
            com.sun.mail.imap.protocol.IMAPProtocol r5 = r8.protocol
            com.sun.mail.iap.Response r3 = r5.readIdleResponse()
            java.lang.Object r6 = r8.messageCacheLock     // Catch: com.sun.mail.iap.ConnectionException -> L63 com.sun.mail.iap.ProtocolException -> L68
            monitor-enter(r6)     // Catch: com.sun.mail.iap.ConnectionException -> L63 com.sun.mail.iap.ProtocolException -> L68
            if (r3 == 0) goto L41
            com.sun.mail.imap.protocol.IMAPProtocol r5 = r8.protocol     // Catch: java.lang.Throwable -> L60
            if (r5 == 0) goto L41
            com.sun.mail.imap.protocol.IMAPProtocol r5 = r8.protocol     // Catch: java.lang.Throwable -> L60
            boolean r5 = r5.processIdleResponse(r3)     // Catch: java.lang.Throwable -> L60
            if (r5 != 0) goto L5e
        L41:
            r5 = 0
            r8.idleState = r5     // Catch: java.lang.Throwable -> L60
            java.lang.Object r5 = r8.messageCacheLock     // Catch: java.lang.Throwable -> L60
            r5.notifyAll()     // Catch: java.lang.Throwable -> L60
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L60
            javax.mail.Store r5 = r8.store
            com.sun.mail.imap.IMAPStore r5 = (com.sun.mail.imap.IMAPStore) r5
            int r1 = r5.getMinIdleTime()
            if (r1 <= 0) goto L28
            long r6 = (long) r1
            java.lang.Thread.sleep(r6)     // Catch: java.lang.InterruptedException -> L59
            goto L28
        L59:
            r5 = move-exception
            goto L28
        L5b:
            r5 = move-exception
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L5b
            throw r5
        L5e:
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L60
            goto L2a
        L60:
            r5 = move-exception
            monitor-exit(r6)     // Catch: java.lang.Throwable -> L60
            throw r5     // Catch: com.sun.mail.iap.ConnectionException -> L63 com.sun.mail.iap.ProtocolException -> L68
        L63:
            r0 = move-exception
            r8.throwClosedException(r0)
            goto L2a
        L68:
            r2 = move-exception
            javax.mail.MessagingException r5 = new javax.mail.MessagingException
            java.lang.String r6 = r2.getMessage()
            r5.<init>(r6, r2)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.idle():void");
    }

    /* renamed from: com.sun.mail.imap.IMAPFolder$19 */
    class AnonymousClass19 implements ProtocolCommand {
        AnonymousClass19() {
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException, InterruptedException {
            if (IMAPFolder.this.idleState == 0) {
                p.idleStart();
                IMAPFolder.this.idleState = 1;
                return Boolean.TRUE;
            }
            try {
                IMAPFolder.this.messageCacheLock.wait();
            } catch (InterruptedException e) {
            }
            return Boolean.FALSE;
        }
    }

    void waitIfIdle() throws ProtocolException, InterruptedException {
        if (!$assertionsDisabled && !Thread.holdsLock(this.messageCacheLock)) {
            throw new AssertionError();
        }
        while (this.idleState != 0) {
            if (this.idleState == 1) {
                this.protocol.idleAbort();
                this.idleState = 2;
            }
            try {
                this.messageCacheLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // com.sun.mail.iap.ResponseHandler
    public void handleResponse(Response r) {
        IMAPMessage msg;
        if (!$assertionsDisabled && !Thread.holdsLock(this.messageCacheLock)) {
            throw new AssertionError();
        }
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            ((IMAPStore) this.store).handleResponseCode(r);
        }
        if (r.isBYE()) {
            if (this.opened) {
                cleanup(false);
                return;
            }
            return;
        }
        if (!r.isOK() && r.isUnTagged()) {
            if (!(r instanceof IMAPResponse)) {
                this.out.println("UNEXPECTED RESPONSE : " + r.toString());
                this.out.println("CONTACT javamail@sun.com");
                return;
            }
            IMAPResponse ir = (IMAPResponse) r;
            if (ir.keyEquals("EXISTS")) {
                int exists = ir.getNumber();
                if (exists > this.realTotal) {
                    int count = exists - this.realTotal;
                    Message[] msgs = new Message[count];
                    for (int i = 0; i < count; i++) {
                        int i2 = this.total + 1;
                        this.total = i2;
                        int i3 = this.realTotal + 1;
                        this.realTotal = i3;
                        IMAPMessage msg2 = new IMAPMessage(this, i2, i3);
                        msgs[i] = msg2;
                        this.messageCache.addElement(msg2);
                    }
                    notifyMessageAddedListeners(msgs);
                    return;
                }
                return;
            }
            if (ir.keyEquals("EXPUNGE")) {
                IMAPMessage msg3 = getMessageBySeqNumber(ir.getNumber());
                msg3.setExpunged(true);
                for (int i4 = msg3.getMessageNumber(); i4 < this.total; i4++) {
                    IMAPMessage m = (IMAPMessage) this.messageCache.elementAt(i4);
                    if (!m.isExpunged()) {
                        m.setSequenceNumber(m.getSequenceNumber() - 1);
                    }
                }
                this.realTotal--;
                if (this.doExpungeNotification) {
                    notifyMessageRemovedListeners(false, new Message[]{msg3});
                    return;
                }
                return;
            }
            if (ir.keyEquals("FETCH")) {
                if (!$assertionsDisabled && !(ir instanceof FetchResponse)) {
                    throw new AssertionError("!ir instanceof FetchResponse");
                }
                FetchResponse f = (FetchResponse) ir;
                Flags flags = (Flags) f.getItem(Flags.class);
                if (flags != null && (msg = getMessageBySeqNumber(f.getNumber())) != null) {
                    msg._setFlags(flags);
                    notifyMessageChangedListeners(1, msg);
                    return;
                }
                return;
            }
            if (ir.keyEquals("RECENT")) {
                this.recent = ir.getNumber();
            }
        }
    }

    void handleResponses(Response[] r) {
        for (int i = 0; i < r.length; i++) {
            if (r[i] != null) {
                handleResponse(r[i]);
            }
        }
    }

    protected synchronized IMAPProtocol getStoreProtocol() throws ProtocolException {
        if (this.connectionPoolDebug) {
            this.out.println("DEBUG: getStoreProtocol() - borrowing a connection");
        }
        return ((IMAPStore) this.store).getStoreProtocol();
    }

    private synchronized void throwClosedException(ConnectionException cex) throws StoreClosedException, FolderClosedException {
        if ((this.protocol != null && cex.getProtocol() == this.protocol) || (this.protocol == null && !this.reallyClosed)) {
            throw new FolderClosedException(this, cex.getMessage());
        }
        throw new StoreClosedException(this.store, cex.getMessage());
    }

    private IMAPProtocol getProtocol() throws ProtocolException, InterruptedException {
        if (!$assertionsDisabled && !Thread.holdsLock(this.messageCacheLock)) {
            throw new AssertionError();
        }
        waitIfIdle();
        return this.protocol;
    }

    public Object doCommand(ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    public Object doOptionalCommand(String err, ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (BadCommandException bex) {
            throw new MessagingException(err, bex);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    public Object doCommandIgnoreFailure(ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (CommandFailedException e) {
            return null;
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    protected Object doProtocolCommand(ProtocolCommand cmd) throws ProtocolException {
        Object objDoCommand;
        synchronized (this) {
            if (this.opened && !((IMAPStore) this.store).hasSeparateStoreConnection()) {
                synchronized (this.messageCacheLock) {
                    objDoCommand = cmd.doCommand(getProtocol());
                }
            } else {
                IMAPProtocol p = null;
                try {
                    p = getStoreProtocol();
                    objDoCommand = cmd.doCommand(p);
                } finally {
                    releaseStoreProtocol(p);
                }
            }
        }
        return objDoCommand;
    }

    protected synchronized void releaseStoreProtocol(IMAPProtocol p) {
        if (p != this.protocol) {
            ((IMAPStore) this.store).releaseStoreProtocol(p);
        }
    }

    private void releaseProtocol(boolean returnToPool) {
        if (this.protocol != null) {
            this.protocol.removeResponseHandler(this);
            if (returnToPool) {
                ((IMAPStore) this.store).releaseProtocol(this, this.protocol);
            } else {
                ((IMAPStore) this.store).releaseProtocol(this, null);
            }
        }
    }

    private void keepConnectionAlive(boolean keepStoreAlive) throws ProtocolException, InterruptedException {
        if (System.currentTimeMillis() - this.protocol.getTimestamp() > 1000) {
            waitIfIdle();
            this.protocol.noop();
        }
        if (keepStoreAlive && ((IMAPStore) this.store).hasSeparateStoreConnection()) {
            IMAPProtocol p = null;
            try {
                p = ((IMAPStore) this.store).getStoreProtocol();
                if (System.currentTimeMillis() - p.getTimestamp() > 1000) {
                    p.noop();
                }
            } finally {
                ((IMAPStore) this.store).releaseStoreProtocol(p);
            }
        }
    }

    IMAPMessage getMessageBySeqNumber(int seqnum) {
        for (int i = seqnum - 1; i < this.total; i++) {
            IMAPMessage msg = (IMAPMessage) this.messageCache.elementAt(i);
            if (msg.getSequenceNumber() == seqnum) {
                return msg;
            }
        }
        return null;
    }

    private boolean isDirectory() {
        return (this.type & 2) != 0;
    }
}
