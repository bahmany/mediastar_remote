package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.mail.MessagingException;

/* loaded from: classes.dex */
public class LineOutputStream extends FilterOutputStream {
    private static byte[] newline = new byte[2];

    static {
        newline[0] = 13;
        newline[1] = 10;
    }

    public LineOutputStream(OutputStream out) {
        super(out);
    }

    public void writeln(String s) throws MessagingException, IOException {
        try {
            byte[] bytes = ASCIIUtility.getBytes(s);
            this.out.write(bytes);
            this.out.write(newline);
        } catch (Exception ex) {
            throw new MessagingException("IOException", ex);
        }
    }

    public void writeln() throws MessagingException, IOException {
        try {
            this.out.write(newline);
        } catch (Exception ex) {
            throw new MessagingException("IOException", ex);
        }
    }
}
