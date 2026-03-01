package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.common.images.WebImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class iu {
    private static final ip Gr = new ip("MetadataUtils");
    private static final String[] HA = {"Z", "+hh", "+hhmm", "+hh:mm"};
    private static final String HB = "yyyyMMdd'T'HHmmss" + HA[0];

    public static String a(Calendar calendar) {
        if (calendar == null) {
            Gr.b("Calendar object cannot be null", new Object[0]);
            return null;
        }
        String str = HB;
        if (calendar.get(11) == 0 && calendar.get(12) == 0 && calendar.get(13) == 0) {
            str = "yyyyMMdd";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
        simpleDateFormat.setTimeZone(calendar.getTimeZone());
        String str2 = simpleDateFormat.format(calendar.getTime());
        return str2.endsWith("+0000") ? str2.replace("+0000", HA[0]) : str2;
    }

    public static void a(List<WebImage> list, JSONObject jSONObject) throws JSONException {
        try {
            list.clear();
            JSONArray jSONArray = jSONObject.getJSONArray("images");
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    list.add(new WebImage(jSONArray.getJSONObject(i)));
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (JSONException e2) {
        }
    }

    public static void a(JSONObject jSONObject, List<WebImage> list) throws JSONException {
        if (list == null || list.isEmpty()) {
            return;
        }
        JSONArray jSONArray = new JSONArray();
        Iterator<WebImage> it = list.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next().bL());
        }
        try {
            jSONObject.put("images", jSONArray);
        } catch (JSONException e) {
        }
    }

    public static Calendar aL(String str) {
        if (TextUtils.isEmpty(str)) {
            Gr.b("Input string is empty or null", new Object[0]);
            return null;
        }
        String strAM = aM(str);
        if (TextUtils.isEmpty(strAM)) {
            Gr.b("Invalid date format", new Object[0]);
            return null;
        }
        String strAN = aN(str);
        String str2 = "yyyyMMdd";
        if (!TextUtils.isEmpty(strAN)) {
            strAM = strAM + "T" + strAN;
            str2 = strAN.length() == "HHmmss".length() ? "yyyyMMdd'T'HHmmss" : HB;
        }
        Calendar gregorianCalendar = GregorianCalendar.getInstance();
        try {
            gregorianCalendar.setTime(new SimpleDateFormat(str2).parse(strAM));
            return gregorianCalendar;
        } catch (ParseException e) {
            Gr.b("Error parsing string: %s", e.getMessage());
            return null;
        }
    }

    private static String aM(String str) {
        if (TextUtils.isEmpty(str)) {
            Gr.b("Input string is empty or null", new Object[0]);
            return null;
        }
        try {
            return str.substring(0, "yyyyMMdd".length());
        } catch (IndexOutOfBoundsException e) {
            Gr.c("Error extracting the date: %s", e.getMessage());
            return null;
        }
    }

    private static String aN(String str) {
        if (TextUtils.isEmpty(str)) {
            Gr.b("string is empty or null", new Object[0]);
            return null;
        }
        int iIndexOf = str.indexOf(84);
        int i = iIndexOf + 1;
        if (iIndexOf != "yyyyMMdd".length()) {
            Gr.b("T delimeter is not found", new Object[0]);
            return null;
        }
        try {
            String strSubstring = str.substring(i);
            if (strSubstring.length() == "HHmmss".length()) {
                return strSubstring;
            }
            switch (strSubstring.charAt("HHmmss".length())) {
                case '+':
                case '-':
                    if (aO(strSubstring)) {
                        break;
                    }
                    break;
                case 'Z':
                    if (strSubstring.length() == "HHmmss".length() + HA[0].length()) {
                        break;
                    }
                    break;
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            Gr.b("Error extracting the time substring: %s", e.getMessage());
            return null;
        }
    }

    private static boolean aO(String str) {
        int length = str.length();
        int length2 = "HHmmss".length();
        return length == HA[1].length() + length2 || length == HA[2].length() + length2 || length == length2 + HA[3].length();
    }
}
