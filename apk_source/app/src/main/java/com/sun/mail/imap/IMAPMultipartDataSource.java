package com.sun.mail.imap;

import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import java.util.Vector;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;

/* loaded from: classes.dex */
public class IMAPMultipartDataSource extends MimePartDataSource implements MultipartDataSource {
    private Vector parts;

    protected IMAPMultipartDataSource(MimePart part, BODYSTRUCTURE[] bs, String sectionId, IMAPMessage msg) {
        String string;
        super(part);
        this.parts = new Vector(bs.length);
        for (int i = 0; i < bs.length; i++) {
            Vector vector = this.parts;
            BODYSTRUCTURE bodystructure = bs[i];
            if (sectionId == null) {
                string = Integer.toString(i + 1);
            } else {
                string = String.valueOf(sectionId) + "." + Integer.toString(i + 1);
            }
            vector.addElement(new IMAPBodyPart(bodystructure, string, msg));
        }
    }

    @Override // javax.mail.MultipartDataSource
    public int getCount() {
        return this.parts.size();
    }

    @Override // javax.mail.MultipartDataSource
    public BodyPart getBodyPart(int index) throws MessagingException {
        return (BodyPart) this.parts.elementAt(index);
    }
}
