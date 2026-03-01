package javax.mail.search;

import javax.mail.Message;

/* loaded from: classes.dex */
public final class SubjectTerm extends StringTerm {
    private static final long serialVersionUID = 7481568618055573432L;

    public SubjectTerm(String pattern) {
        super(pattern);
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            String subj = msg.getSubject();
            if (subj == null) {
                return false;
            }
            return super.match(subj);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // javax.mail.search.StringTerm
    public boolean equals(Object obj) {
        if (obj instanceof SubjectTerm) {
            return super.equals(obj);
        }
        return false;
    }
}
