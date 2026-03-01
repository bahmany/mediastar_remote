package com.sun.mail.imap;

import com.sun.mail.iap.Literal;
import com.sun.mail.util.CRLFOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.mail.Message;
import javax.mail.MessagingException;

/* compiled from: IMAPFolder.java */
/* loaded from: classes.dex */
class MessageLiteral implements Literal {
    private byte[] buf;
    private Message msg;
    private int msgSize;

    public MessageLiteral(Message msg, int maxsize) throws MessagingException, IOException {
        this.msgSize = -1;
        this.msg = msg;
        LengthCounter lc = new LengthCounter(maxsize);
        OutputStream os = new CRLFOutputStream(lc);
        msg.writeTo(os);
        os.flush();
        this.msgSize = lc.getSize();
        this.buf = lc.getBytes();
    }

    @Override // com.sun.mail.iap.Literal
    public int size() {
        return this.msgSize;
    }

    @Override // com.sun.mail.iap.Literal
    public void writeTo(OutputStream os) throws IOException {
        try {
            if (this.buf != null) {
                os.write(this.buf, 0, this.msgSize);
                return;
            }
            try {
                this.msg.writeTo(new CRLFOutputStream(os));
            } catch (MessagingException e) {
                mex = e;
                throw new IOException("MessagingException while appending message: " + mex);
            }
        } catch (MessagingException e2) {
            mex = e2;
        }
    }
}
