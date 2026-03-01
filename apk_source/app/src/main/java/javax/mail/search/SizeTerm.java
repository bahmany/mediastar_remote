package javax.mail.search;

import javax.mail.Message;

/* loaded from: classes.dex */
public final class SizeTerm extends IntegerComparisonTerm {
    private static final long serialVersionUID = -2556219451005103709L;

    public SizeTerm(int comparison, int size) {
        super(comparison, size);
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            int size = msg.getSize();
            if (size == -1) {
                return false;
            }
            return super.match(size);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // javax.mail.search.IntegerComparisonTerm, javax.mail.search.ComparisonTerm
    public boolean equals(Object obj) {
        if (obj instanceof SizeTerm) {
            return super.equals(obj);
        }
        return false;
    }
}
