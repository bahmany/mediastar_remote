package javax.mail.internet;

/* loaded from: classes.dex */
public class AddressException extends ParseException {
    private static final long serialVersionUID = 9134583443539323120L;
    protected int pos;
    protected String ref;

    public AddressException() {
        this.ref = null;
        this.pos = -1;
    }

    public AddressException(String s) {
        super(s);
        this.ref = null;
        this.pos = -1;
    }

    public AddressException(String s, String ref) {
        super(s);
        this.ref = null;
        this.pos = -1;
        this.ref = ref;
    }

    public AddressException(String s, String ref, int pos) {
        super(s);
        this.ref = null;
        this.pos = -1;
        this.ref = ref;
        this.pos = pos;
    }

    public String getRef() {
        return this.ref;
    }

    public int getPos() {
        return this.pos;
    }

    @Override // javax.mail.MessagingException, java.lang.Throwable
    public String toString() {
        String s = super.toString();
        if (this.ref == null) {
            return s;
        }
        String s2 = String.valueOf(s) + " in string ``" + this.ref + "''";
        return this.pos < 0 ? s2 : String.valueOf(s2) + " at position " + this.pos;
    }
}
