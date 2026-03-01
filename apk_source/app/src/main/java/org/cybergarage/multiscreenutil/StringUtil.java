package org.cybergarage.multiscreenutil;

/* loaded from: classes.dex */
public final class StringUtil {
    public static final boolean hasData(String value) {
        return value != null && value.length() > 0;
    }

    public static final int toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0;
        }
    }

    public static final long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0L;
        }
    }
}
