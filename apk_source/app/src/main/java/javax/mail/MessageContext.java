package javax.mail;

/* loaded from: classes.dex */
public class MessageContext {
    private Part part;

    public MessageContext(Part part) {
        this.part = part;
    }

    public Part getPart() {
        return this.part;
    }

    public Message getMessage() {
        try {
            return getMessage(this.part);
        } catch (MessagingException e) {
            return null;
        }
    }

    private static Message getMessage(Part p) throws MessagingException {
        while (p != null) {
            if (p instanceof Message) {
                return (Message) p;
            }
            BodyPart bp = (BodyPart) p;
            Multipart mp = bp.getParent();
            if (mp == null) {
                return null;
            }
            p = mp.getParent();
        }
        return null;
    }

    public Session getSession() {
        Message msg = getMessage();
        if (msg != null) {
            return msg.session;
        }
        return null;
    }
}
