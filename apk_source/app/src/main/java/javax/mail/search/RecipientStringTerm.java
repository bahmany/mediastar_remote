package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;

/* loaded from: classes.dex */
public final class RecipientStringTerm extends AddressStringTerm {
    private static final long serialVersionUID = -8293562089611618849L;
    private Message.RecipientType type;

    public RecipientStringTerm(Message.RecipientType type, String pattern) {
        super(pattern);
        this.type = type;
    }

    public Message.RecipientType getRecipientType() {
        return this.type;
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            Address[] recipients = msg.getRecipients(this.type);
            if (recipients == null) {
                return false;
            }
            for (Address address : recipients) {
                if (super.match(address)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // javax.mail.search.AddressStringTerm, javax.mail.search.StringTerm
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipientStringTerm)) {
            return false;
        }
        RecipientStringTerm rst = (RecipientStringTerm) obj;
        return rst.type.equals(this.type) && super.equals(obj);
    }

    @Override // javax.mail.search.StringTerm
    public int hashCode() {
        return this.type.hashCode() + super.hashCode();
    }
}
