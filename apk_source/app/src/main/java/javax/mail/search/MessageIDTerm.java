package javax.mail.search;

import javax.mail.Message;

/* loaded from: classes.dex */
public final class MessageIDTerm extends StringTerm {
    private static final long serialVersionUID = -2121096296454691963L;

    public MessageIDTerm(String msgid) {
        super(msgid);
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            String[] s = msg.getHeader("Message-ID");
            if (s == null) {
                return false;
            }
            for (String str : s) {
                if (super.match(str)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // javax.mail.search.StringTerm
    public boolean equals(Object obj) {
        if (obj instanceof MessageIDTerm) {
            return super.equals(obj);
        }
        return false;
    }
}
