package com.sun.mail.imap;

/* loaded from: classes.dex */
public class AppendUID {
    public long uid;
    public long uidvalidity;

    public AppendUID(long uidvalidity, long uid) {
        this.uidvalidity = -1L;
        this.uid = -1L;
        this.uidvalidity = uidvalidity;
        this.uid = uid;
    }
}
