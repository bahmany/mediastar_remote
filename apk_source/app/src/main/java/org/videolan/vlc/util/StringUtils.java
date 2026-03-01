package org.videolan.vlc.util;

import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class StringUtils {
    public static String join(Object[] elements, CharSequence separator) {
        return join(Arrays.asList(elements), separator);
    }

    public static String join(Iterable<? extends Object> elements, CharSequence separator) {
        StringBuilder builder = new StringBuilder();
        if (elements != null) {
            Iterator<? extends Object> iter = elements.iterator();
            if (iter.hasNext()) {
                builder.append(String.valueOf(iter.next()));
                while (iter.hasNext()) {
                    builder.append(separator).append(String.valueOf(iter.next()));
                }
            }
        }
        return builder.toString();
    }

    public static String fixLastSlash(String str) {
        String res = str == null ? ServiceReference.DELIMITER : String.valueOf(str.trim()) + ServiceReference.DELIMITER;
        if (res.length() > 2 && res.charAt(res.length() - 2) == '/') {
            return res.substring(0, res.length() - 1);
        }
        return res;
    }

    public static int convertToInt(String str) throws NumberFormatException {
        int s = 0;
        while (s < str.length() && !Character.isDigit(str.charAt(s))) {
            s++;
        }
        int e = str.length();
        while (e > 0 && !Character.isDigit(str.charAt(e - 1))) {
            e--;
        }
        if (e > s) {
            try {
                return Integer.parseInt(str.substring(s, e));
            } catch (NumberFormatException ex) {
                Log.e("VLC/StringUtil", "convertToInt", ex);
                throw new NumberFormatException();
            }
        }
        throw new NumberFormatException();
    }

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)) : String.format("%02d:%02d", Integer.valueOf(minutes), Integer.valueOf(seconds));
    }
}
