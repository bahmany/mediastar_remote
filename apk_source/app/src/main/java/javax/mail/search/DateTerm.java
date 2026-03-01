package javax.mail.search;

import java.util.Date;

/* loaded from: classes.dex */
public abstract class DateTerm extends ComparisonTerm {
    private static final long serialVersionUID = 4818873430063720043L;
    protected Date date;

    protected DateTerm(int comparison, Date date) {
        this.comparison = comparison;
        this.date = date;
    }

    public Date getDate() {
        return new Date(this.date.getTime());
    }

    public int getComparison() {
        return this.comparison;
    }

    protected boolean match(Date d) {
        switch (this.comparison) {
            case 1:
                if (d.before(this.date) || d.equals(this.date)) {
                    break;
                }
                break;
            case 4:
                if (!d.equals(this.date)) {
                    break;
                }
                break;
            case 6:
                if (d.after(this.date) || d.equals(this.date)) {
                    break;
                }
                break;
        }
        return false;
    }

    @Override // javax.mail.search.ComparisonTerm
    public boolean equals(Object obj) {
        if (!(obj instanceof DateTerm)) {
            return false;
        }
        DateTerm dt = (DateTerm) obj;
        return dt.date.equals(this.date) && super.equals(obj);
    }

    @Override // javax.mail.search.ComparisonTerm
    public int hashCode() {
        return this.date.hashCode() + super.hashCode();
    }
}
