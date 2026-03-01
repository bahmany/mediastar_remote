package org.videolan.vlc.util;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class Preferences {
    public static final String TAG = "VLC/Util/Preferences";

    public static float[] getFloatArray(SharedPreferences pref, String key) {
        float[] array = null;
        String s = pref.getString(key, null);
        if (s != null) {
            try {
                JSONArray json = new JSONArray(s);
                array = new float[json.length()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = (float) json.getDouble(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public static void putFloatArray(SharedPreferences.Editor editor, String key, float[] array) throws JSONException {
        try {
            JSONArray json = new JSONArray();
            for (float f : array) {
                json.put(f);
            }
            editor.putString("equalizer_values", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
