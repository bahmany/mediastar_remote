package javax.mail.internet;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;

/* loaded from: classes.dex */
public class NewsAddress extends Address {
    private static final long serialVersionUID = -4203797299824684143L;
    protected String host;
    protected String newsgroup;

    public NewsAddress() {
    }

    public NewsAddress(String newsgroup) {
        this(newsgroup, null);
    }

    public NewsAddress(String newsgroup, String host) {
        this.newsgroup = newsgroup;
        this.host = host;
    }

    @Override // javax.mail.Address
    public String getType() {
        return "news";
    }

    public void setNewsgroup(String newsgroup) {
        this.newsgroup = newsgroup;
    }

    public String getNewsgroup() {
        return this.newsgroup;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    @Override // javax.mail.Address
    public String toString() {
        return this.newsgroup;
    }

    @Override // javax.mail.Address
    public boolean equals(Object a) {
        if (!(a instanceof NewsAddress)) {
            return false;
        }
        NewsAddress s = (NewsAddress) a;
        if (this.newsgroup.equals(s.newsgroup)) {
            return (this.host == null && s.host == null) || !(this.host == null || s.host == null || !this.host.equalsIgnoreCase(s.host));
        }
        return false;
    }

    public int hashCode() {
        int hash = this.newsgroup != null ? 0 + this.newsgroup.hashCode() : 0;
        if (this.host != null) {
            return hash + this.host.toLowerCase(Locale.ENGLISH).hashCode();
        }
        return hash;
    }

    public static String toString(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        StringBuffer s = new StringBuffer(((NewsAddress) addresses[0]).toString());
        for (int i = 1; i < addresses.length; i++) {
            s.append(ClientInfo.SEPARATOR_BETWEEN_VARS).append(((NewsAddress) addresses[i]).toString());
        }
        return s.toString();
    }

    public static NewsAddress[] parse(String newsgroups) throws AddressException {
        StringTokenizer st = new StringTokenizer(newsgroups, ClientInfo.SEPARATOR_BETWEEN_VARS);
        Vector nglist = new Vector();
        while (st.hasMoreTokens()) {
            String ng = st.nextToken();
            nglist.addElement(new NewsAddress(ng));
        }
        int size = nglist.size();
        NewsAddress[] na = new NewsAddress[size];
        if (size > 0) {
            nglist.copyInto(na);
        }
        return na;
    }
}
