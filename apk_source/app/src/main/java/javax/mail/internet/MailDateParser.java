package javax.mail.internet;

import com.hisilicon.multiscreen.protocol.message.KeyInfo;

/* compiled from: MailDateFormat.java */
/* loaded from: classes.dex */
class MailDateParser {
    int index = 0;
    char[] orig;

    public MailDateParser(char[] orig) {
        this.orig = null;
        this.orig = orig;
    }

    public void skipUntilNumber() throws java.text.ParseException {
        while (true) {
            try {
                switch (this.orig[this.index]) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        return;
                    default:
                        this.index++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new java.text.ParseException("No Number Found", this.index);
            }
            throw new java.text.ParseException("No Number Found", this.index);
        }
    }

    public void skipWhiteSpace() {
        int len = this.orig.length;
        while (this.index < len) {
            switch (this.orig[this.index]) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    this.index++;
                default:
                    return;
            }
        }
    }

    public int peekChar() throws java.text.ParseException {
        if (this.index < this.orig.length) {
            return this.orig[this.index];
        }
        throw new java.text.ParseException("No more characters", this.index);
    }

    public void skipChar(char c) throws java.text.ParseException {
        if (this.index < this.orig.length) {
            if (this.orig[this.index] == c) {
                this.index++;
                return;
            }
            throw new java.text.ParseException("Wrong char", this.index);
        }
        throw new java.text.ParseException("No more characters", this.index);
    }

    public boolean skipIfChar(char c) throws java.text.ParseException {
        if (this.index < this.orig.length) {
            if (this.orig[this.index] != c) {
                return false;
            }
            this.index++;
            return true;
        }
        throw new java.text.ParseException("No more characters", this.index);
    }

    /* JADX WARN: Code restructure failed: missing block: B:6:0x000b, code lost:
    
        return r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int parseNumber() throws java.text.ParseException {
        /*
            r6 = this;
            char[] r3 = r6.orig
            int r1 = r3.length
            r0 = 0
            r2 = 0
        L5:
            int r3 = r6.index
            if (r3 < r1) goto Lc
            if (r0 == 0) goto L61
        Lb:
            return r2
        Lc:
            char[] r3 = r6.orig
            int r4 = r6.index
            char r3 = r3[r4]
            switch(r3) {
                case 48: goto L21;
                case 49: goto L2b;
                case 50: goto L31;
                case 51: goto L37;
                case 52: goto L3d;
                case 53: goto L43;
                case 54: goto L49;
                case 55: goto L4f;
                case 56: goto L55;
                case 57: goto L5b;
                default: goto L15;
            }
        L15:
            if (r0 != 0) goto Lb
            java.text.ParseException r3 = new java.text.ParseException
            java.lang.String r4 = "No Number found"
            int r5 = r6.index
            r3.<init>(r4, r5)
            throw r3
        L21:
            int r2 = r2 * 10
            r0 = 1
        L24:
            int r3 = r6.index
            int r3 = r3 + 1
            r6.index = r3
            goto L5
        L2b:
            int r3 = r2 * 10
            int r2 = r3 + 1
            r0 = 1
            goto L24
        L31:
            int r3 = r2 * 10
            int r2 = r3 + 2
            r0 = 1
            goto L24
        L37:
            int r3 = r2 * 10
            int r2 = r3 + 3
            r0 = 1
            goto L24
        L3d:
            int r3 = r2 * 10
            int r2 = r3 + 4
            r0 = 1
            goto L24
        L43:
            int r3 = r2 * 10
            int r2 = r3 + 5
            r0 = 1
            goto L24
        L49:
            int r3 = r2 * 10
            int r2 = r3 + 6
            r0 = 1
            goto L24
        L4f:
            int r3 = r2 * 10
            int r2 = r3 + 7
            r0 = 1
            goto L24
        L55:
            int r3 = r2 * 10
            int r2 = r3 + 8
            r0 = 1
            goto L24
        L5b:
            int r3 = r2 * 10
            int r2 = r3 + 9
            r0 = 1
            goto L24
        L61:
            java.text.ParseException r3 = new java.text.ParseException
            java.lang.String r4 = "No Number found"
            int r5 = r6.index
            r3.<init>(r4, r5)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.MailDateParser.parseNumber():int");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int parseMonth() throws java.text.ParseException {
        char[] cArr;
        int i;
        try {
            cArr = this.orig;
            i = this.index;
            this.index = i + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        switch (cArr[i]) {
            case 'A':
            case 'a':
                char[] cArr2 = this.orig;
                int i2 = this.index;
                this.index = i2 + 1;
                char curr = cArr2[i2];
                if (curr == 'P' || curr == 'p') {
                    char[] cArr3 = this.orig;
                    int i3 = this.index;
                    this.index = i3 + 1;
                    char curr2 = cArr3[i3];
                    if (curr2 == 'R' || curr2 == 'r') {
                        return 3;
                    }
                } else if (curr == 'U' || curr == 'u') {
                    char[] cArr4 = this.orig;
                    int i4 = this.index;
                    this.index = i4 + 1;
                    char curr3 = cArr4[i4];
                    if (curr3 == 'G' || curr3 == 'g') {
                        return 7;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'D':
            case 'd':
                char[] cArr5 = this.orig;
                int i5 = this.index;
                this.index = i5 + 1;
                char curr4 = cArr5[i5];
                if (curr4 == 'E' || curr4 == 'e') {
                    char[] cArr6 = this.orig;
                    int i6 = this.index;
                    this.index = i6 + 1;
                    char curr5 = cArr6[i6];
                    if (curr5 == 'C' || curr5 == 'c') {
                        return 11;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'F':
            case 'f':
                char[] cArr7 = this.orig;
                int i7 = this.index;
                this.index = i7 + 1;
                char curr6 = cArr7[i7];
                if (curr6 == 'E' || curr6 == 'e') {
                    char[] cArr8 = this.orig;
                    int i8 = this.index;
                    this.index = i8 + 1;
                    char curr7 = cArr8[i8];
                    if (curr7 == 'B' || curr7 == 'b') {
                        return 1;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'J':
            case 'j':
                char[] cArr9 = this.orig;
                int i9 = this.index;
                this.index = i9 + 1;
                switch (cArr9[i9]) {
                    case 'A':
                    case 'a':
                        char[] cArr10 = this.orig;
                        int i10 = this.index;
                        this.index = i10 + 1;
                        char curr8 = cArr10[i10];
                        if (curr8 == 'N' || curr8 == 'n') {
                            return 0;
                        }
                        break;
                    case 'U':
                    case 'u':
                        char[] cArr11 = this.orig;
                        int i11 = this.index;
                        this.index = i11 + 1;
                        char curr9 = cArr11[i11];
                        if (curr9 == 'N' || curr9 == 'n') {
                            return 5;
                        }
                        if (curr9 == 'L' || curr9 == 'l') {
                            return 6;
                        }
                        break;
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'M':
            case KeyInfo.KEYCODE_NEXT /* 109 */:
                char[] cArr12 = this.orig;
                int i12 = this.index;
                this.index = i12 + 1;
                char curr10 = cArr12[i12];
                if (curr10 == 'A' || curr10 == 'a') {
                    char[] cArr13 = this.orig;
                    int i13 = this.index;
                    this.index = i13 + 1;
                    char curr11 = cArr13[i13];
                    if (curr11 == 'R' || curr11 == 'r') {
                        return 2;
                    }
                    if (curr11 == 'Y' || curr11 == 'y') {
                        return 4;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'N':
            case 'n':
                char[] cArr14 = this.orig;
                int i14 = this.index;
                this.index = i14 + 1;
                char curr12 = cArr14[i14];
                if (curr12 == 'O' || curr12 == 'o') {
                    char[] cArr15 = this.orig;
                    int i15 = this.index;
                    this.index = i15 + 1;
                    char curr13 = cArr15[i15];
                    if (curr13 == 'V' || curr13 == 'v') {
                        return 10;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'O':
            case 'o':
                char[] cArr16 = this.orig;
                int i16 = this.index;
                this.index = i16 + 1;
                char curr14 = cArr16[i16];
                if (curr14 == 'C' || curr14 == 'c') {
                    char[] cArr17 = this.orig;
                    int i17 = this.index;
                    this.index = i17 + 1;
                    char curr15 = cArr17[i17];
                    if (curr15 == 'T' || curr15 == 't') {
                        return 9;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            case 'S':
            case KeyInfo.KEYCODE_VOLUME_UP /* 115 */:
                char[] cArr18 = this.orig;
                int i18 = this.index;
                this.index = i18 + 1;
                char curr16 = cArr18[i18];
                if (curr16 == 'E' || curr16 == 'e') {
                    char[] cArr19 = this.orig;
                    int i19 = this.index;
                    this.index = i19 + 1;
                    char curr17 = cArr19[i19];
                    if (curr17 == 'P' || curr17 == 'p') {
                        return 8;
                    }
                }
                throw new java.text.ParseException("Bad Month", this.index);
            default:
                throw new java.text.ParseException("Bad Month", this.index);
        }
    }

    public int parseTimeZone() throws java.text.ParseException {
        if (this.index >= this.orig.length) {
            throw new java.text.ParseException("No more characters", this.index);
        }
        char test = this.orig[this.index];
        return (test == '+' || test == '-') ? parseNumericTimeZone() : parseAlphaTimeZone();
    }

    public int parseNumericTimeZone() throws java.text.ParseException {
        boolean switchSign = false;
        char[] cArr = this.orig;
        int i = this.index;
        this.index = i + 1;
        char first = cArr[i];
        if (first == '+') {
            switchSign = true;
        } else if (first != '-') {
            throw new java.text.ParseException("Bad Numeric TimeZone", this.index);
        }
        int tz = parseNumber();
        int offset = ((tz / 100) * 60) + (tz % 100);
        if (switchSign) {
            return -offset;
        }
        return offset;
    }

    public int parseAlphaTimeZone() throws java.text.ParseException {
        int result;
        boolean foundCommon = false;
        try {
            char[] cArr = this.orig;
            int i = this.index;
            this.index = i + 1;
            switch (cArr[i]) {
                case 'C':
                case 'c':
                    result = 360;
                    foundCommon = true;
                    break;
                case 'E':
                case 'e':
                    result = 300;
                    foundCommon = true;
                    break;
                case 'G':
                case 'g':
                    char[] cArr2 = this.orig;
                    int i2 = this.index;
                    this.index = i2 + 1;
                    char curr = cArr2[i2];
                    if (curr == 'M' || curr == 'm') {
                        char[] cArr3 = this.orig;
                        int i3 = this.index;
                        this.index = i3 + 1;
                        char curr2 = cArr3[i3];
                        if (curr2 == 'T' || curr2 == 't') {
                            result = 0;
                            break;
                        }
                    }
                    throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
                case 'M':
                case KeyInfo.KEYCODE_NEXT /* 109 */:
                    result = 420;
                    foundCommon = true;
                    break;
                case 'P':
                case 'p':
                    result = 480;
                    foundCommon = true;
                    break;
                case 'U':
                case 'u':
                    char[] cArr4 = this.orig;
                    int i4 = this.index;
                    this.index = i4 + 1;
                    char curr3 = cArr4[i4];
                    if (curr3 == 'T' || curr3 == 't') {
                        result = 0;
                        break;
                    } else {
                        throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
                    }
                default:
                    throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
            }
            if (foundCommon) {
                char[] cArr5 = this.orig;
                int i5 = this.index;
                this.index = i5 + 1;
                char curr4 = cArr5[i5];
                if (curr4 == 'S' || curr4 == 's') {
                    char[] cArr6 = this.orig;
                    int i6 = this.index;
                    this.index = i6 + 1;
                    char curr5 = cArr6[i6];
                    if (curr5 != 'T' && curr5 != 't') {
                        throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
                    }
                    return result;
                }
                if (curr4 == 'D' || curr4 == 'd') {
                    char[] cArr7 = this.orig;
                    int i7 = this.index;
                    this.index = i7 + 1;
                    char curr6 = cArr7[i7];
                    if (curr6 == 'T' || curr6 != 't') {
                        return result - 60;
                    }
                    throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
                }
                return result;
            }
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
        }
    }

    int getIndex() {
        return this.index;
    }
}
