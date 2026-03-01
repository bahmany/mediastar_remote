package javax.mail.search;

import javax.mail.Message;

/* loaded from: classes.dex */
public final class MessageNumberTerm extends IntegerComparisonTerm {
    private static final long serialVersionUID = -5379625829658623812L;

    public MessageNumberTerm(int number) {
        super(3, number);
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            int msgno = msg.getMessageNumber();
            return super.match(msgno);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // javax.mail.search.IntegerComparisonTerm, javax.mail.search.ComparisonTerm
    public boolean equals(Object obj) {
        if (obj instanceof MessageNumberTerm) {
            return super.equals(obj);
        }
        return false;
    }
}
