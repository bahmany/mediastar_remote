package javax.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/* loaded from: classes.dex */
public abstract class Multipart {
    protected Part parent;
    protected Vector parts = new Vector();
    protected String contentType = "multipart/mixed";

    public abstract void writeTo(OutputStream outputStream) throws MessagingException, IOException;

    protected Multipart() {
    }

    protected synchronized void setMultipartDataSource(MultipartDataSource mp) throws MessagingException {
        this.contentType = mp.getContentType();
        int count = mp.getCount();
        for (int i = 0; i < count; i++) {
            addBodyPart(mp.getBodyPart(i));
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public synchronized int getCount() throws MessagingException {
        return this.parts == null ? 0 : this.parts.size();
    }

    public synchronized BodyPart getBodyPart(int index) throws MessagingException {
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        return (BodyPart) this.parts.elementAt(index);
    }

    public synchronized boolean removeBodyPart(BodyPart part) throws MessagingException {
        boolean ret;
        if (this.parts == null) {
            throw new MessagingException("No such body part");
        }
        ret = this.parts.removeElement(part);
        part.setParent(null);
        return ret;
    }

    public synchronized void removeBodyPart(int index) throws MessagingException {
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        BodyPart part = (BodyPart) this.parts.elementAt(index);
        this.parts.removeElementAt(index);
        part.setParent(null);
    }

    public synchronized void addBodyPart(BodyPart part) throws MessagingException {
        if (this.parts == null) {
            this.parts = new Vector();
        }
        this.parts.addElement(part);
        part.setParent(this);
    }

    public synchronized void addBodyPart(BodyPart part, int index) throws MessagingException {
        if (this.parts == null) {
            this.parts = new Vector();
        }
        this.parts.insertElementAt(part, index);
        part.setParent(this);
    }

    public synchronized Part getParent() {
        return this.parent;
    }

    public synchronized void setParent(Part parent) {
        this.parent = parent;
    }
}
