package com.sun.mail.smtp;

import android.support.v7.internal.widget.ActivityChooserView;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class DigestMD5 {
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private String clientResponse;
    private PrintStream debugout;
    private MessageDigest md5;
    private String uri;

    public DigestMD5(PrintStream debugout) {
        this.debugout = debugout;
        if (debugout != null) {
            debugout.println("DEBUG DIGEST-MD5: Loaded");
        }
    }

    public byte[] authClient(String host, String user, String passwd, String realm, String serverChallenge) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream b64os = new BASE64EncoderStream(bos, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        try {
            SecureRandom random = new SecureRandom();
            this.md5 = MessageDigest.getInstance("MD5");
            StringBuffer result = new StringBuffer();
            this.uri = "smtp/" + host;
            byte[] bytes = new byte[32];
            if (this.debugout != null) {
                this.debugout.println("DEBUG DIGEST-MD5: Begin authentication ...");
            }
            Hashtable map = tokenize(serverChallenge);
            if (realm == null) {
                String text = (String) map.get("realm");
                realm = text != null ? new StringTokenizer(text, ClientInfo.SEPARATOR_BETWEEN_VARS).nextToken() : host;
            }
            String nonce = (String) map.get("nonce");
            random.nextBytes(bytes);
            b64os.write(bytes);
            b64os.flush();
            String cnonce = bos.toString();
            bos.reset();
            this.md5.update(this.md5.digest(ASCIIUtility.getBytes(String.valueOf(user) + ":" + realm + ":" + passwd)));
            this.md5.update(ASCIIUtility.getBytes(":" + nonce + ":" + cnonce));
            this.clientResponse = String.valueOf(toHex(this.md5.digest())) + ":" + nonce + ":00000001:" + cnonce + ":auth:";
            this.md5.update(ASCIIUtility.getBytes("AUTHENTICATE:" + this.uri));
            this.md5.update(ASCIIUtility.getBytes(String.valueOf(this.clientResponse) + toHex(this.md5.digest())));
            result.append("username=\"" + user + "\"");
            result.append(",realm=\"" + realm + "\"");
            result.append(",qop=auth");
            result.append(",nc=00000001");
            result.append(",nonce=\"" + nonce + "\"");
            result.append(",cnonce=\"" + cnonce + "\"");
            result.append(",digest-uri=\"" + this.uri + "\"");
            result.append(",response=" + toHex(this.md5.digest()));
            if (this.debugout != null) {
                this.debugout.println("DEBUG DIGEST-MD5: Response => " + result.toString());
            }
            b64os.write(ASCIIUtility.getBytes(result.toString()));
            b64os.flush();
            return bos.toByteArray();
        } catch (NoSuchAlgorithmException ex) {
            if (this.debugout != null) {
                this.debugout.println("DEBUG DIGEST-MD5: " + ex);
            }
            throw new IOException(ex.toString());
        }
    }

    public boolean authServer(String serverResponse) throws IOException {
        Hashtable map = tokenize(serverResponse);
        this.md5.update(ASCIIUtility.getBytes(":" + this.uri));
        this.md5.update(ASCIIUtility.getBytes(String.valueOf(this.clientResponse) + toHex(this.md5.digest())));
        String text = toHex(this.md5.digest());
        if (text.equals((String) map.get("rspauth"))) {
            return true;
        }
        if (this.debugout != null) {
            this.debugout.println("DEBUG DIGEST-MD5: Expected => rspauth=" + text);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x008f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.Hashtable tokenize(java.lang.String r13) throws java.io.IOException {
        /*
            r12 = this;
            r11 = 57
            r10 = 48
            java.util.Hashtable r2 = new java.util.Hashtable
            r2.<init>()
            byte[] r0 = r13.getBytes()
            r1 = 0
            java.io.StreamTokenizer r3 = new java.io.StreamTokenizer
            java.io.InputStreamReader r5 = new java.io.InputStreamReader
            com.sun.mail.util.BASE64DecoderStream r6 = new com.sun.mail.util.BASE64DecoderStream
            java.io.ByteArrayInputStream r7 = new java.io.ByteArrayInputStream
            r8 = 4
            int r9 = r0.length
            int r9 = r9 + (-4)
            r7.<init>(r0, r8, r9)
            r6.<init>(r7)
            r5.<init>(r6)
            r3.<init>(r5)
            r3.ordinaryChars(r10, r11)
            r3.wordChars(r10, r11)
        L2c:
            int r4 = r3.nextToken()
            r5 = -1
            if (r4 != r5) goto L34
            return r2
        L34:
            switch(r4) {
                case -3: goto L38;
                case 34: goto L3d;
                default: goto L37;
            }
        L37:
            goto L2c
        L38:
            if (r1 != 0) goto L3d
            java.lang.String r1 = r3.sval
            goto L2c
        L3d:
            java.io.PrintStream r5 = r12.debugout
            if (r5 == 0) goto L67
            java.io.PrintStream r5 = r12.debugout
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r7 = "DEBUG DIGEST-MD5: Received => "
            r6.<init>(r7)
            java.lang.StringBuilder r6 = r6.append(r1)
            java.lang.String r7 = "='"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r3.sval
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = "'"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.println(r6)
        L67:
            boolean r5 = r2.containsKey(r1)
            if (r5 == 0) goto L8f
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.Object r6 = r2.get(r1)
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = ","
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = r3.sval
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            r2.put(r1, r5)
        L8d:
            r1 = 0
            goto L2c
        L8f:
            java.lang.String r5 = r3.sval
            r2.put(r1, r5)
            goto L8d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.smtp.DigestMD5.tokenize(java.lang.String):java.util.Hashtable");
    }

    private static String toHex(byte[] bytes) {
        char[] result = new char[bytes.length * 2];
        int i = 0;
        for (byte b : bytes) {
            int temp = b & 255;
            int i2 = i + 1;
            result[i] = digits[temp >> 4];
            i = i2 + 1;
            result[i2] = digits[temp & 15];
        }
        return new String(result);
    }
}
