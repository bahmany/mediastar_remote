package javax.mail.internet;

import com.iflytek.speech.VoiceWakeuperAidl;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import javax.mail.Address;
import javax.mail.Session;

/* loaded from: classes.dex */
public class InternetAddress extends Address implements Cloneable {
    private static final String rfc822phrase = HeaderTokenizer.RFC822.replace(' ', (char) 0).replace('\t', (char) 0);
    private static final long serialVersionUID = -7507595530758302903L;
    private static final String specialsNoDot = "()<>,;:\\\"[]@";
    private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
    protected String address;
    protected String encodedPersonal;
    protected String personal;

    public InternetAddress() {
    }

    public InternetAddress(String address) throws AddressException {
        InternetAddress[] a = parse(address, true);
        if (a.length != 1) {
            throw new AddressException("Illegal address", address);
        }
        this.address = a[0].address;
        this.personal = a[0].personal;
        this.encodedPersonal = a[0].encodedPersonal;
    }

    public InternetAddress(String address, boolean strict) throws AddressException {
        this(address);
        if (strict) {
            checkAddress(this.address, true, true);
        }
    }

    public InternetAddress(String address, String personal) throws UnsupportedEncodingException {
        this(address, personal, null);
    }

    public InternetAddress(String address, String personal, String charset) throws UnsupportedEncodingException {
        this.address = address;
        setPersonal(personal, charset);
    }

    public Object clone() {
        try {
            InternetAddress a = (InternetAddress) super.clone();
            return a;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override // javax.mail.Address
    public String getType() {
        return "rfc822";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPersonal(String name, String charset) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name, charset, null);
        } else {
            this.encodedPersonal = null;
        }
    }

    public void setPersonal(String name) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name);
        } else {
            this.encodedPersonal = null;
        }
    }

    public String getAddress() {
        return this.address;
    }

    public String getPersonal() {
        if (this.personal != null) {
            return this.personal;
        }
        if (this.encodedPersonal != null) {
            try {
                this.personal = MimeUtility.decodeText(this.encodedPersonal);
                return this.personal;
            } catch (Exception e) {
                return this.encodedPersonal;
            }
        }
        return null;
    }

    @Override // javax.mail.Address
    public String toString() {
        if (this.encodedPersonal == null && this.personal != null) {
            try {
                this.encodedPersonal = MimeUtility.encodeWord(this.personal);
            } catch (UnsupportedEncodingException e) {
            }
        }
        if (this.encodedPersonal != null) {
            return String.valueOf(quotePhrase(this.encodedPersonal)) + " <" + this.address + ">";
        }
        if (isGroup() || isSimple()) {
            return this.address;
        }
        return "<" + this.address + ">";
    }

    public String toUnicodeString() {
        String p = getPersonal();
        if (p != null) {
            return String.valueOf(quotePhrase(p)) + " <" + this.address + ">";
        }
        if (isGroup() || isSimple()) {
            return this.address;
        }
        return "<" + this.address + ">";
    }

    private static String quotePhrase(String phrase) {
        int len = phrase.length();
        boolean needQuoting = false;
        for (int i = 0; i < len; i++) {
            char c = phrase.charAt(i);
            if (c == '\"' || c == '\\') {
                StringBuffer sb = new StringBuffer(len + 3);
                sb.append('\"');
                for (int j = 0; j < len; j++) {
                    char cc = phrase.charAt(j);
                    if (cc == '\"' || cc == '\\') {
                        sb.append('\\');
                    }
                    sb.append(cc);
                }
                sb.append('\"');
                return sb.toString();
            }
            if ((c < ' ' && c != '\r' && c != '\n' && c != '\t') || c >= 127 || rfc822phrase.indexOf(c) >= 0) {
                needQuoting = true;
            }
        }
        if (needQuoting) {
            StringBuffer sb2 = new StringBuffer(len + 2);
            sb2.append('\"').append(phrase).append('\"');
            return sb2.toString();
        }
        return phrase;
    }

    private static String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            String s2 = s.substring(1, s.length() - 1);
            if (s2.indexOf(92) >= 0) {
                StringBuffer sb = new StringBuffer(s2.length());
                int i = 0;
                while (i < s2.length()) {
                    char c = s2.charAt(i);
                    if (c == '\\' && i < s2.length() - 1) {
                        i++;
                        c = s2.charAt(i);
                    }
                    sb.append(c);
                    i++;
                }
                return sb.toString();
            }
            return s2;
        }
        return s;
    }

    @Override // javax.mail.Address
    public boolean equals(Object a) {
        if (!(a instanceof InternetAddress)) {
            return false;
        }
        String s = ((InternetAddress) a).getAddress();
        if (s == this.address) {
            return true;
        }
        return this.address != null && this.address.equalsIgnoreCase(s);
    }

    public int hashCode() {
        if (this.address == null) {
            return 0;
        }
        return this.address.toLowerCase(Locale.ENGLISH).hashCode();
    }

    public static String toString(Address[] addresses) {
        return toString(addresses, 0);
    }

    public static String toString(Address[] addresses, int used) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < addresses.length; i++) {
            if (i != 0) {
                sb.append(", ");
                used += 2;
            }
            String s = addresses[i].toString();
            int len = lengthOfFirstSegment(s);
            if (used + len > 76) {
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(s);
            used = lengthOfLastSegment(s, used);
        }
        return sb.toString();
    }

    private static int lengthOfFirstSegment(String s) {
        int pos = s.indexOf("\r\n");
        return pos != -1 ? pos : s.length();
    }

    private static int lengthOfLastSegment(String s, int used) {
        int pos = s.lastIndexOf("\r\n");
        return pos != -1 ? (s.length() - pos) - 2 : s.length() + used;
    }

    public static InternetAddress getLocalAddress(Session session) {
        InetAddress me;
        String user = null;
        String host = null;
        String address = null;
        try {
            if (session == null) {
                user = System.getProperty("user.name");
                host = InetAddress.getLocalHost().getHostName();
            } else {
                address = session.getProperty("mail.from");
                if (address == null) {
                    user = session.getProperty("mail.user");
                    if (user == null || user.length() == 0) {
                        user = session.getProperty("user.name");
                    }
                    if (user == null || user.length() == 0) {
                        user = System.getProperty("user.name");
                    }
                    host = session.getProperty("mail.host");
                    if ((host == null || host.length() == 0) && (me = InetAddress.getLocalHost()) != null) {
                        host = me.getHostName();
                    }
                }
            }
            if (address == null && user != null && user.length() != 0 && host != null && host.length() != 0) {
                address = String.valueOf(user) + "@" + host;
            }
            if (address != null) {
                return new InternetAddress(address);
            }
        } catch (SecurityException e) {
        } catch (UnknownHostException e2) {
        } catch (AddressException e3) {
        }
        return null;
    }

    public static InternetAddress[] parse(String addresslist) throws AddressException {
        return parse(addresslist, true);
    }

    public static InternetAddress[] parse(String addresslist, boolean strict) throws AddressException {
        return parse(addresslist, strict, false);
    }

    public static InternetAddress[] parseHeader(String addresslist, boolean strict) throws AddressException {
        return parse(addresslist, strict, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:153:0x00fb A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0154 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:155:0x0178 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:164:0x007f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:165:0x007f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0131  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static javax.mail.internet.InternetAddress[] parse(java.lang.String r22, boolean r23, boolean r24) throws javax.mail.internet.AddressException {
        /*
            Method dump skipped, instructions count: 792
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.InternetAddress.parse(java.lang.String, boolean, boolean):javax.mail.internet.InternetAddress[]");
    }

    public void validate() throws AddressException {
        checkAddress(getAddress(), true, true);
    }

    private static void checkAddress(String addr, boolean routeAddr, boolean validate) throws AddressException {
        String local;
        String domain;
        int start = 0;
        if (addr.indexOf(34) < 0) {
            if (routeAddr) {
                start = 0;
                while (true) {
                    int i = indexOfAny(addr, ",:", start);
                    if (i < 0) {
                        break;
                    }
                    if (addr.charAt(start) != '@') {
                        throw new AddressException("Illegal route-addr", addr);
                    }
                    if (addr.charAt(i) != ':') {
                        start = i + 1;
                    } else {
                        start = i + 1;
                        break;
                    }
                }
            }
            int i2 = addr.indexOf(64, start);
            if (i2 >= 0) {
                if (i2 == start) {
                    throw new AddressException("Missing local name", addr);
                }
                if (i2 == addr.length() - 1) {
                    throw new AddressException("Missing domain", addr);
                }
                local = addr.substring(start, i2);
                domain = addr.substring(i2 + 1);
            } else {
                if (validate) {
                    throw new AddressException("Missing final '@domain'", addr);
                }
                local = addr;
                domain = null;
            }
            if (indexOfAny(addr, " \t\n\r") >= 0) {
                throw new AddressException("Illegal whitespace in address", addr);
            }
            if (indexOfAny(local, specialsNoDot) >= 0) {
                throw new AddressException("Illegal character in local name", addr);
            }
            if (domain != null && domain.indexOf(91) < 0 && indexOfAny(domain, specialsNoDot) >= 0) {
                throw new AddressException("Illegal character in domain", addr);
            }
        }
    }

    private boolean isSimple() {
        return this.address == null || indexOfAny(this.address, specialsNoDotNoAt) < 0;
    }

    public boolean isGroup() {
        return this.address != null && this.address.endsWith(VoiceWakeuperAidl.PARAMS_SEPARATE) && this.address.indexOf(58) > 0;
    }

    public InternetAddress[] getGroup(boolean strict) throws AddressException {
        int ix;
        String addr = getAddress();
        if (!addr.endsWith(VoiceWakeuperAidl.PARAMS_SEPARATE) || (ix = addr.indexOf(58)) < 0) {
            return null;
        }
        String list = addr.substring(ix + 1, addr.length() - 1);
        return parseHeader(list, strict);
    }

    private static int indexOfAny(String s, String any) {
        return indexOfAny(s, any, 0);
    }

    private static int indexOfAny(String s, String any, int start) {
        try {
            int len = s.length();
            for (int i = start; i < len; i++) {
                if (any.indexOf(s.charAt(i)) >= 0) {
                    return i;
                }
            }
            return -1;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
}
