package javax.mail.search;

import javax.mail.Message;

/* loaded from: classes.dex */
public final class NotTerm extends SearchTerm {
    private static final long serialVersionUID = 7152293214217310216L;
    protected SearchTerm term;

    public NotTerm(SearchTerm t) {
        this.term = t;
    }

    public SearchTerm getTerm() {
        return this.term;
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        return !this.term.match(msg);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotTerm)) {
            return false;
        }
        NotTerm nt = (NotTerm) obj;
        return nt.term.equals(this.term);
    }

    public int hashCode() {
        return this.term.hashCode() << 1;
    }
}
