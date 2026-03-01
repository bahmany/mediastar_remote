package com.sun.mail.imap;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.ListInfo;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;

/* loaded from: classes.dex */
public class DefaultFolder extends IMAPFolder {
    protected DefaultFolder(IMAPStore store) {
        super("", (char) 65535, store);
        this.exists = true;
        this.type = 2;
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public String getName() {
        return this.fullName;
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public Folder getParent() {
        return null;
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public Folder[] list(String pattern) throws MessagingException {
        ListInfo[] li = (ListInfo[]) doCommand(new IMAPFolder.ProtocolCommand() { // from class: com.sun.mail.imap.DefaultFolder.1
            private final /* synthetic */ String val$pattern;

            AnonymousClass1(String pattern2) {
                str = pattern2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.list("", str);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        IMAPFolder[] folders = new IMAPFolder[li.length];
        for (int i = 0; i < folders.length; i++) {
            folders[i] = new IMAPFolder(li[i], (IMAPStore) this.store);
        }
        return folders;
    }

    /* renamed from: com.sun.mail.imap.DefaultFolder$1 */
    class AnonymousClass1 implements IMAPFolder.ProtocolCommand {
        private final /* synthetic */ String val$pattern;

        AnonymousClass1(String pattern2) {
            str = pattern2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.list("", str);
        }
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public Folder[] listSubscribed(String pattern) throws MessagingException {
        ListInfo[] li = (ListInfo[]) doCommand(new IMAPFolder.ProtocolCommand() { // from class: com.sun.mail.imap.DefaultFolder.2
            private final /* synthetic */ String val$pattern;

            AnonymousClass2(String pattern2) {
                str = pattern2;
            }

            @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.lsub("", str);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        IMAPFolder[] folders = new IMAPFolder[li.length];
        for (int i = 0; i < folders.length; i++) {
            folders[i] = new IMAPFolder(li[i], (IMAPStore) this.store);
        }
        return folders;
    }

    /* renamed from: com.sun.mail.imap.DefaultFolder$2 */
    class AnonymousClass2 implements IMAPFolder.ProtocolCommand {
        private final /* synthetic */ String val$pattern;

        AnonymousClass2(String pattern2) {
            str = pattern2;
        }

        @Override // com.sun.mail.imap.IMAPFolder.ProtocolCommand
        public Object doCommand(IMAPProtocol p) throws ProtocolException {
            return p.lsub("", str);
        }
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public Folder getFolder(String name) throws MessagingException {
        return new IMAPFolder(name, (char) 65535, (IMAPStore) this.store);
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public boolean delete(boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("Cannot delete Default Folder");
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public boolean renameTo(Folder f) throws MessagingException {
        throw new MethodNotSupportedException("Cannot rename Default Folder");
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public void appendMessages(Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Cannot append to Default Folder");
    }

    @Override // com.sun.mail.imap.IMAPFolder, javax.mail.Folder
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("Cannot expunge Default Folder");
    }
}
