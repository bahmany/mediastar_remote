package org.videolan.vlc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.cybergarage.soap.SOAP;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class Strings {
    public static final String TAG = "VLC/Util/Strings";

    public static String stripTrailingSlash(String s) {
        if (s.endsWith(ServiceReference.DELIMITER) && s.length() > 1) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    static boolean StartsWith(String[] array, String text) {
        for (String item : array) {
            if (text.startsWith(item)) {
                return true;
            }
        }
        return false;
    }

    public static String millisToString(long millis) {
        return millisToString(millis, false);
    }

    public static String millisToText(long millis) {
        return millisToString(millis, true);
    }

    static String millisToString(long millis, boolean text) {
        boolean negative = millis < 0;
        long millis2 = Math.abs(millis) / 1000;
        int sec = (int) (millis2 % 60);
        long millis3 = millis2 / 60;
        int min = (int) (millis3 % 60);
        long millis4 = millis3 / 60;
        int hours = (int) millis4;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (!text) {
            if (millis4 > 0) {
                String time = String.valueOf(negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
                return time;
            }
            String time2 = String.valueOf(negative ? "-" : "") + min + ":" + format.format(sec);
            return time2;
        }
        if (millis4 > 0) {
            String time3 = String.valueOf(negative ? "-" : "") + hours + "h" + format.format(min) + "min";
            return time3;
        }
        if (min > 0) {
            String time4 = String.valueOf(negative ? "-" : "") + min + "min";
            return time4;
        }
        String time5 = String.valueOf(negative ? "-" : "") + sec + SOAP.XMLNS;
        return time5;
    }

    public static boolean nullEquals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    public static String formatRateString(float rate) {
        return String.format(Locale.US, "%.2fx", Float.valueOf(rate));
    }
}
