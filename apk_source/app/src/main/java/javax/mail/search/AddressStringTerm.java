package javax.mail.search;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/* loaded from: classes.dex */
public abstract class AddressStringTerm extends StringTerm {
    private static final long serialVersionUID = 3086821234204980368L;

    protected AddressStringTerm(String pattern) {
        super(pattern, true);
    }

    protected boolean match(Address a) {
        if (!(a instanceof InternetAddress)) {
            return super.match(a.toString());
        }
        InternetAddress ia = (InternetAddress) a;
        return super.match(ia.toUnicodeString());
    }

    @Override // javax.mail.search.StringTerm
    public boolean equals(Object obj) {
        if (obj instanceof AddressStringTerm) {
            return super.equals(obj);
        }
        return false;
    }
}
