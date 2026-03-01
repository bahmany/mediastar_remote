package javax.mail.search;

import java.util.Locale;
import javax.mail.Message;

/* loaded from: classes.dex */
public final class HeaderTerm extends StringTerm {
    private static final long serialVersionUID = 8342514650333389122L;
    protected String headerName;

    public HeaderTerm(String headerName, String pattern) {
        super(pattern);
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        try {
            String[] headers = msg.getHeader(this.headerName);
            if (headers == null) {
                return false;
            }
            for (String str : headers) {
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
        if (!(obj instanceof HeaderTerm)) {
            return false;
        }
        HeaderTerm ht = (HeaderTerm) obj;
        return ht.headerName.equalsIgnoreCase(this.headerName) && super.equals(ht);
    }

    @Override // javax.mail.search.StringTerm
    public int hashCode() {
        return this.headerName.toLowerCase(Locale.ENGLISH).hashCode() + super.hashCode();
    }
}
