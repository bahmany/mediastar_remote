package javax.mail.internet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class ParameterList {
    private static boolean applehack;
    private static boolean decodeParameters;
    private static boolean decodeParametersStrict;
    private static boolean encodeParameters;
    private static final char[] hex;
    private String lastName;
    private Map list;
    private Set multisegmentNames;
    private Map slist;

    static {
        encodeParameters = false;
        decodeParameters = false;
        decodeParametersStrict = false;
        applehack = false;
        try {
            String s = System.getProperty("mail.mime.encodeparameters");
            encodeParameters = s != null && s.equalsIgnoreCase("true");
            String s2 = System.getProperty("mail.mime.decodeparameters");
            decodeParameters = s2 != null && s2.equalsIgnoreCase("true");
            String s3 = System.getProperty("mail.mime.decodeparameters.strict");
            decodeParametersStrict = s3 != null && s3.equalsIgnoreCase("true");
            String s4 = System.getProperty("mail.mime.applefilenames");
            applehack = s4 != null && s4.equalsIgnoreCase("true");
        } catch (SecurityException e) {
        }
        hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    private static class Value {
        String charset;
        String encodedValue;
        String value;

        private Value() {
        }

        /* synthetic */ Value(Value value) {
            this();
        }
    }

    private static class MultiValue extends ArrayList {
        String value;

        private MultiValue() {
        }

        /* synthetic */ MultiValue(MultiValue multiValue) {
            this();
        }
    }

    private static class ParamEnum implements Enumeration {
        private Iterator it;

        ParamEnum(Iterator it) {
            this.it = it;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            return this.it.next();
        }
    }

    public ParameterList() {
        this.list = new LinkedHashMap();
        this.lastName = null;
        if (decodeParameters) {
            this.multisegmentNames = new HashSet();
            this.slist = new HashMap();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:60:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0018, code lost:
    
        if (javax.mail.internet.ParameterList.decodeParameters == false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x001a, code lost:
    
        combineMultisegmentNames(false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001e, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public ParameterList(java.lang.String r11) throws javax.mail.internet.ParseException {
        /*
            Method dump skipped, instructions count: 310
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.ParameterList.<init>(java.lang.String):void");
    }

    private void putEncodedName(String name, String value) throws ParseException {
        Object value2;
        int star = name.indexOf(42);
        if (star < 0) {
            this.list.put(name, value);
            return;
        }
        if (star == name.length() - 1) {
            this.list.put(name.substring(0, star), decodeValue(value));
            return;
        }
        String rname = name.substring(0, star);
        this.multisegmentNames.add(rname);
        this.list.put(rname, "");
        if (name.endsWith("*")) {
            value2 = new Value(null);
            ((Value) value2).encodedValue = value;
            ((Value) value2).value = value;
            name = name.substring(0, name.length() - 1);
        } else {
            value2 = value;
        }
        this.slist.put(name, value2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0096, code lost:
    
        if (r11 != 0) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0098, code lost:
    
        r24.list.remove(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x018d, code lost:
    
        r7.value = r10.toString();
        r24.list.put(r8, r7);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void combineMultisegmentNames(boolean r25) throws javax.mail.internet.ParseException {
        /*
            Method dump skipped, instructions count: 531
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.ParameterList.combineMultisegmentNames(boolean):void");
    }

    public int size() {
        return this.list.size();
    }

    public String get(String name) {
        Object v = this.list.get(name.trim().toLowerCase(Locale.ENGLISH));
        if (v instanceof MultiValue) {
            String value = ((MultiValue) v).value;
            return value;
        }
        if (v instanceof Value) {
            String value2 = ((Value) v).value;
            return value2;
        }
        String value3 = (String) v;
        return value3;
    }

    public void set(String name, String value) {
        if (name == null && value != null && value.equals("DONE")) {
            if (decodeParameters && this.multisegmentNames.size() > 0) {
                try {
                    combineMultisegmentNames(true);
                    return;
                } catch (ParseException e) {
                    return;
                }
            }
            return;
        }
        String name2 = name.trim().toLowerCase(Locale.ENGLISH);
        if (decodeParameters) {
            try {
                putEncodedName(name2, value);
                return;
            } catch (ParseException e2) {
                this.list.put(name2, value);
                return;
            }
        }
        this.list.put(name2, value);
    }

    public void set(String name, String value, String charset) throws UnsupportedEncodingException {
        if (encodeParameters) {
            Value ev = encodeValue(value, charset);
            if (ev != null) {
                this.list.put(name.trim().toLowerCase(Locale.ENGLISH), ev);
                return;
            } else {
                set(name, value);
                return;
            }
        }
        set(name, value);
    }

    public void remove(String name) {
        this.list.remove(name.trim().toLowerCase(Locale.ENGLISH));
    }

    public Enumeration getNames() {
        return new ParamEnum(this.list.keySet().iterator());
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int used) {
        ToStringBuffer sb = new ToStringBuffer(used);
        for (String name : this.list.keySet()) {
            Object v = this.list.get(name);
            if (v instanceof MultiValue) {
                MultiValue vv = (MultiValue) v;
                String ns = String.valueOf(name) + "*";
                for (int i = 0; i < vv.size(); i++) {
                    Object va = vv.get(i);
                    if (va instanceof Value) {
                        sb.addNV(String.valueOf(ns) + i + "*", ((Value) va).encodedValue);
                    } else {
                        sb.addNV(String.valueOf(ns) + i, (String) va);
                    }
                }
            } else if (v instanceof Value) {
                sb.addNV(String.valueOf(name) + "*", ((Value) v).encodedValue);
            } else {
                sb.addNV(name, (String) v);
            }
        }
        return sb.toString();
    }

    private static class ToStringBuffer {
        private StringBuffer sb = new StringBuffer();
        private int used;

        public ToStringBuffer(int used) {
            this.used = used;
        }

        public void addNV(String name, String value) {
            String value2 = ParameterList.quote(value);
            this.sb.append("; ");
            this.used += 2;
            int len = name.length() + value2.length() + 1;
            if (this.used + len > 76) {
                this.sb.append("\r\n\t");
                this.used = 8;
            }
            this.sb.append(name).append('=');
            this.used += name.length() + 1;
            if (this.used + value2.length() > 76) {
                String s = MimeUtility.fold(this.used, value2);
                this.sb.append(s);
                int lastlf = s.lastIndexOf(10);
                if (lastlf >= 0) {
                    this.used += (s.length() - lastlf) - 1;
                    return;
                } else {
                    this.used += s.length();
                    return;
                }
            }
            this.sb.append(value2);
            this.used += value2.length();
        }

        public String toString() {
            return this.sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String quote(String value) {
        return MimeUtility.quote(value, HeaderTokenizer.MIME);
    }

    private static Value encodeValue(String value, String charset) throws UnsupportedEncodingException {
        Value value2 = null;
        if (MimeUtility.checkAscii(value) == 1) {
            return null;
        }
        try {
            byte[] b = value.getBytes(MimeUtility.javaCharset(charset));
            StringBuffer sb = new StringBuffer(b.length + charset.length() + 2);
            sb.append(charset).append("''");
            for (byte b2 : b) {
                char c = (char) (b2 & 255);
                if (c <= ' ' || c >= 127 || c == '*' || c == '\'' || c == '%' || HeaderTokenizer.MIME.indexOf(c) >= 0) {
                    sb.append('%').append(hex[c >> 4]).append(hex[c & 15]);
                } else {
                    sb.append(c);
                }
            }
            Value v = new Value(value2);
            v.charset = charset;
            v.value = value;
            v.encodedValue = sb.toString();
            return v;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static Value decodeValue(String value) throws ParseException {
        Value v = new Value(null);
        v.encodedValue = value;
        v.value = value;
        try {
            int i = value.indexOf(39);
            if (i <= 0) {
                if (decodeParametersStrict) {
                    throw new ParseException("Missing charset in encoded value: " + value);
                }
            } else {
                String charset = value.substring(0, i);
                int li = value.indexOf(39, i + 1);
                if (li < 0) {
                    if (decodeParametersStrict) {
                        throw new ParseException("Missing language in encoded value: " + value);
                    }
                } else {
                    value.substring(i + 1, li);
                    String value2 = value.substring(li + 1);
                    v.charset = charset;
                    v.value = decodeBytes(value2, charset);
                }
            }
        } catch (UnsupportedEncodingException uex) {
            if (decodeParametersStrict) {
                throw new ParseException(uex.toString());
            }
        } catch (NumberFormatException nex) {
            if (decodeParametersStrict) {
                throw new ParseException(nex.toString());
            }
        } catch (StringIndexOutOfBoundsException ex) {
            if (decodeParametersStrict) {
                throw new ParseException(ex.toString());
            }
        }
        return v;
    }

    private static String decodeBytes(String value, String charset) throws UnsupportedEncodingException {
        byte[] b = new byte[value.length()];
        int i = 0;
        int bi = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c == '%') {
                String hex2 = value.substring(i + 1, i + 3);
                c = (char) Integer.parseInt(hex2, 16);
                i += 2;
            }
            b[bi] = (byte) c;
            i++;
            bi++;
        }
        return new String(b, 0, bi, MimeUtility.javaCharset(charset));
    }
}
