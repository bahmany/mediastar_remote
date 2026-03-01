package javax.mail.internet;

/* loaded from: classes.dex */
public class HeaderTokenizer {
    private static final Token EOFToken = new Token(-4, null);
    public static final String MIME = "()<>@,;:\\\"\t []/?=";
    public static final String RFC822 = "()<>@,;:\\\"\t .[]";
    private int currentPos;
    private String delimiters;
    private int maxPos;
    private int nextPos;
    private int peekPos;
    private boolean skipComments;
    private String string;

    public static class Token {
        public static final int ATOM = -1;
        public static final int COMMENT = -3;
        public static final int EOF = -4;
        public static final int QUOTEDSTRING = -2;
        private int type;
        private String value;

        public Token(int type, String value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return this.type;
        }

        public String getValue() {
            return this.value;
        }
    }

    public HeaderTokenizer(String header, String delimiters, boolean skipComments) {
        this.string = header == null ? "" : header;
        this.skipComments = skipComments;
        this.delimiters = delimiters;
        this.peekPos = 0;
        this.nextPos = 0;
        this.currentPos = 0;
        this.maxPos = this.string.length();
    }

    public HeaderTokenizer(String header, String delimiters) {
        this(header, delimiters, true);
    }

    public HeaderTokenizer(String header) {
        this(header, RFC822);
    }

    public Token next() throws ParseException {
        this.currentPos = this.nextPos;
        Token tk = getNext();
        int i = this.currentPos;
        this.peekPos = i;
        this.nextPos = i;
        return tk;
    }

    public Token peek() throws ParseException {
        this.currentPos = this.peekPos;
        Token tk = getNext();
        this.peekPos = this.currentPos;
        return tk;
    }

    public String getRemainder() {
        return this.string.substring(this.nextPos);
    }

    private Token getNext() throws ParseException {
        char c;
        String s;
        String s2;
        if (this.currentPos >= this.maxPos) {
            return EOFToken;
        }
        if (skipWhiteSpace() == -4) {
            return EOFToken;
        }
        boolean filter = false;
        char c2 = this.string.charAt(this.currentPos);
        while (c2 == '(') {
            int start = this.currentPos + 1;
            this.currentPos = start;
            int nesting = 1;
            while (nesting > 0 && this.currentPos < this.maxPos) {
                char c3 = this.string.charAt(this.currentPos);
                if (c3 == '\\') {
                    this.currentPos++;
                    filter = true;
                } else if (c3 == '\r') {
                    filter = true;
                } else if (c3 == '(') {
                    nesting++;
                } else if (c3 == ')') {
                    nesting--;
                }
                this.currentPos++;
            }
            if (nesting != 0) {
                throw new ParseException("Unbalanced comments");
            }
            if (!this.skipComments) {
                if (filter) {
                    s2 = filterToken(this.string, start, this.currentPos - 1);
                } else {
                    s2 = this.string.substring(start, this.currentPos - 1);
                }
                return new Token(-3, s2);
            }
            if (skipWhiteSpace() == -4) {
                return EOFToken;
            }
            c2 = this.string.charAt(this.currentPos);
        }
        if (c2 == '\"') {
            int start2 = this.currentPos + 1;
            this.currentPos = start2;
            while (this.currentPos < this.maxPos) {
                char c4 = this.string.charAt(this.currentPos);
                if (c4 == '\\') {
                    this.currentPos++;
                    filter = true;
                } else if (c4 == '\r') {
                    filter = true;
                } else if (c4 == '\"') {
                    this.currentPos++;
                    if (filter) {
                        s = filterToken(this.string, start2, this.currentPos - 1);
                    } else {
                        s = this.string.substring(start2, this.currentPos - 1);
                    }
                    return new Token(-2, s);
                }
                this.currentPos++;
            }
            throw new ParseException("Unbalanced quoted string");
        }
        if (c2 < ' ' || c2 >= 127 || this.delimiters.indexOf(c2) >= 0) {
            this.currentPos++;
            char[] ch = {c2};
            return new Token(c2, new String(ch));
        }
        int start3 = this.currentPos;
        while (this.currentPos < this.maxPos && (c = this.string.charAt(this.currentPos)) >= ' ' && c < 127 && c != '(' && c != ' ' && c != '\"' && this.delimiters.indexOf(c) < 0) {
            this.currentPos++;
        }
        return new Token(-1, this.string.substring(start3, this.currentPos));
    }

    private int skipWhiteSpace() {
        while (this.currentPos < this.maxPos) {
            char c = this.string.charAt(this.currentPos);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                this.currentPos++;
            } else {
                return this.currentPos;
            }
        }
        return -4;
    }

    private static String filterToken(String s, int start, int end) {
        StringBuffer sb = new StringBuffer();
        boolean gotEscape = false;
        boolean gotCR = false;
        for (int i = start; i < end; i++) {
            char c = s.charAt(i);
            if (c == '\n' && gotCR) {
                gotCR = false;
            } else {
                gotCR = false;
                if (!gotEscape) {
                    if (c == '\\') {
                        gotEscape = true;
                    } else if (c == '\r') {
                        gotCR = true;
                    } else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                    gotEscape = false;
                }
            }
        }
        return sb.toString();
    }
}
