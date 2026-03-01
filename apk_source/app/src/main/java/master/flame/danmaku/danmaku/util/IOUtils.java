package master.flame.danmaku.danmaku.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class IOUtils {
    public static String getString(InputStream in) throws IOException {
        byte[] data = getBytes(in);
        if (data == null) {
            return null;
        }
        return new String(data);
    }

    public static byte[] getBytes(InputStream in) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while (true) {
                int len = in.read(buffer);
                if (len != -1) {
                    baos.write(buffer, 0, len);
                } else {
                    in.close();
                    return baos.toByteArray();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static void closeQuietly(InputStream in) throws IOException {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(OutputStream out) throws IOException {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }
}
