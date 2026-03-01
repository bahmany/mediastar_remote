package mktvsmart.screen.common.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class CommonHelper {
    private static final int ARRAY_SIZE = 1024;

    public static String getStrFromInputSteam(InputStream in) throws IOException {
        String string = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        while (true) {
            try {
                int len = in.read(data);
                if (len != -1) {
                    outputStream.write(data, 0, len);
                } else {
                    String string2 = new String(outputStream.toString());
                    string = string2;
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return string;
            }
        }
    }
}
