package com.google.android.gms.cast;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.images.WebImage;
import com.google.android.gms.internal.iu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MediaMetadata {
    public static final int MEDIA_TYPE_GENERIC = 0;
    public static final int MEDIA_TYPE_MOVIE = 1;
    public static final int MEDIA_TYPE_MUSIC_TRACK = 3;
    public static final int MEDIA_TYPE_PHOTO = 4;
    public static final int MEDIA_TYPE_TV_SHOW = 2;
    public static final int MEDIA_TYPE_USER = 100;
    private final List<WebImage> EA;
    private final Bundle Fp;
    private int Fq;
    private static final String[] Fn = {null, "String", "int", "double", "ISO-8601 date String"};
    public static final String KEY_CREATION_DATE = "com.google.android.gms.cast.metadata.CREATION_DATE";
    public static final String KEY_RELEASE_DATE = "com.google.android.gms.cast.metadata.RELEASE_DATE";
    public static final String KEY_BROADCAST_DATE = "com.google.android.gms.cast.metadata.BROADCAST_DATE";
    public static final String KEY_TITLE = "com.google.android.gms.cast.metadata.TITLE";
    public static final String KEY_SUBTITLE = "com.google.android.gms.cast.metadata.SUBTITLE";
    public static final String KEY_ARTIST = "com.google.android.gms.cast.metadata.ARTIST";
    public static final String KEY_ALBUM_ARTIST = "com.google.android.gms.cast.metadata.ALBUM_ARTIST";
    public static final String KEY_ALBUM_TITLE = "com.google.android.gms.cast.metadata.ALBUM_TITLE";
    public static final String KEY_COMPOSER = "com.google.android.gms.cast.metadata.COMPOSER";
    public static final String KEY_DISC_NUMBER = "com.google.android.gms.cast.metadata.DISC_NUMBER";
    public static final String KEY_TRACK_NUMBER = "com.google.android.gms.cast.metadata.TRACK_NUMBER";
    public static final String KEY_SEASON_NUMBER = "com.google.android.gms.cast.metadata.SEASON_NUMBER";
    public static final String KEY_EPISODE_NUMBER = "com.google.android.gms.cast.metadata.EPISODE_NUMBER";
    public static final String KEY_SERIES_TITLE = "com.google.android.gms.cast.metadata.SERIES_TITLE";
    public static final String KEY_STUDIO = "com.google.android.gms.cast.metadata.STUDIO";
    public static final String KEY_WIDTH = "com.google.android.gms.cast.metadata.WIDTH";
    public static final String KEY_HEIGHT = "com.google.android.gms.cast.metadata.HEIGHT";
    public static final String KEY_LOCATION_NAME = "com.google.android.gms.cast.metadata.LOCATION_NAME";
    public static final String KEY_LOCATION_LATITUDE = "com.google.android.gms.cast.metadata.LOCATION_LATITUDE";
    public static final String KEY_LOCATION_LONGITUDE = "com.google.android.gms.cast.metadata.LOCATION_LONGITUDE";
    private static final a Fo = new a().a(KEY_CREATION_DATE, "creationDateTime", 4).a(KEY_RELEASE_DATE, "releaseDate", 4).a(KEY_BROADCAST_DATE, "originalAirdate", 4).a(KEY_TITLE, "title", 1).a(KEY_SUBTITLE, "subtitle", 1).a(KEY_ARTIST, "artist", 1).a(KEY_ALBUM_ARTIST, "albumArtist", 1).a(KEY_ALBUM_TITLE, "albumName", 1).a(KEY_COMPOSER, "composer", 1).a(KEY_DISC_NUMBER, "discNumber", 2).a(KEY_TRACK_NUMBER, "trackNumber", 2).a(KEY_SEASON_NUMBER, "season", 2).a(KEY_EPISODE_NUMBER, "episode", 2).a(KEY_SERIES_TITLE, "seriesTitle", 1).a(KEY_STUDIO, "studio", 1).a(KEY_WIDTH, "width", 2).a(KEY_HEIGHT, "height", 2).a(KEY_LOCATION_NAME, "location", 1).a(KEY_LOCATION_LATITUDE, "latitude", 3).a(KEY_LOCATION_LONGITUDE, "longitude", 3);

    private static class a {
        private final Map<String, String> Fr = new HashMap();
        private final Map<String, String> Fs = new HashMap();
        private final Map<String, Integer> Ft = new HashMap();

        public a a(String str, String str2, int i) {
            this.Fr.put(str, str2);
            this.Fs.put(str2, str);
            this.Ft.put(str, Integer.valueOf(i));
            return this;
        }

        public String aA(String str) {
            return this.Fs.get(str);
        }

        public int aB(String str) {
            Integer num = this.Ft.get(str);
            if (num != null) {
                return num.intValue();
            }
            return 0;
        }

        public String az(String str) {
            return this.Fr.get(str);
        }
    }

    public MediaMetadata() {
        this(0);
    }

    public MediaMetadata(int mediaType) {
        this.EA = new ArrayList();
        this.Fp = new Bundle();
        this.Fq = mediaType;
    }

    private void a(JSONObject jSONObject, String... strArr) throws JSONException {
        try {
            for (String str : strArr) {
                if (this.Fp.containsKey(str)) {
                    switch (Fo.aB(str)) {
                        case 1:
                        case 4:
                            jSONObject.put(Fo.az(str), this.Fp.getString(str));
                            break;
                        case 2:
                            jSONObject.put(Fo.az(str), this.Fp.getInt(str));
                            break;
                        case 3:
                            jSONObject.put(Fo.az(str), this.Fp.getDouble(str));
                            break;
                    }
                }
            }
            for (String str2 : this.Fp.keySet()) {
                if (!str2.startsWith("com.google.")) {
                    Object obj = this.Fp.get(str2);
                    if (obj instanceof String) {
                        jSONObject.put(str2, obj);
                    } else if (obj instanceof Integer) {
                        jSONObject.put(str2, obj);
                    } else if (obj instanceof Double) {
                        jSONObject.put(str2, obj);
                    }
                }
            }
        } catch (JSONException e) {
        }
    }

    private boolean a(Bundle bundle, Bundle bundle2) {
        if (bundle.size() != bundle2.size()) {
            return false;
        }
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            Object obj2 = bundle2.get(str);
            if ((obj instanceof Bundle) && (obj2 instanceof Bundle) && !a((Bundle) obj, (Bundle) obj2)) {
                return false;
            }
            if (obj == null) {
                if (obj2 != null || !bundle2.containsKey(str)) {
                    return false;
                }
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    private void b(JSONObject jSONObject, String... strArr) throws JSONException {
        HashSet hashSet = new HashSet(Arrays.asList(strArr));
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                if (!"metadataType".equals(next)) {
                    String strAA = Fo.aA(next);
                    if (strAA == null) {
                        Object obj = jSONObject.get(next);
                        if (obj instanceof String) {
                            this.Fp.putString(next, (String) obj);
                        } else if (obj instanceof Integer) {
                            this.Fp.putInt(next, ((Integer) obj).intValue());
                        } else if (obj instanceof Double) {
                            this.Fp.putDouble(next, ((Double) obj).doubleValue());
                        }
                    } else if (hashSet.contains(strAA)) {
                        try {
                            Object obj2 = jSONObject.get(next);
                            if (obj2 != null) {
                                switch (Fo.aB(strAA)) {
                                    case 1:
                                        if (!(obj2 instanceof String)) {
                                            break;
                                        } else {
                                            this.Fp.putString(strAA, (String) obj2);
                                            break;
                                        }
                                    case 2:
                                        if (!(obj2 instanceof Integer)) {
                                            break;
                                        } else {
                                            this.Fp.putInt(strAA, ((Integer) obj2).intValue());
                                            break;
                                        }
                                    case 3:
                                        if (!(obj2 instanceof Double)) {
                                            break;
                                        } else {
                                            this.Fp.putDouble(strAA, ((Double) obj2).doubleValue());
                                            break;
                                        }
                                    case 4:
                                        if ((obj2 instanceof String) && iu.aL((String) obj2) != null) {
                                            this.Fp.putString(strAA, (String) obj2);
                                            break;
                                        } else {
                                            break;
                                        }
                                }
                            }
                        } catch (JSONException e) {
                        }
                    }
                }
            }
        } catch (JSONException e2) {
        }
    }

    private void f(String str, int i) throws IllegalArgumentException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("null and empty keys are not allowed");
        }
        int iAB = Fo.aB(str);
        if (iAB != i && iAB != 0) {
            throw new IllegalArgumentException("Value for " + str + " must be a " + Fn[i]);
        }
    }

    public void addImage(WebImage image) {
        this.EA.add(image);
    }

    public JSONObject bL() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("metadataType", this.Fq);
        } catch (JSONException e) {
        }
        iu.a(jSONObject, this.EA);
        switch (this.Fq) {
            case 0:
                a(jSONObject, KEY_TITLE, KEY_ARTIST, KEY_SUBTITLE, KEY_RELEASE_DATE);
                return jSONObject;
            case 1:
                a(jSONObject, KEY_TITLE, KEY_STUDIO, KEY_SUBTITLE, KEY_RELEASE_DATE);
                return jSONObject;
            case 2:
                a(jSONObject, KEY_TITLE, KEY_SERIES_TITLE, KEY_SEASON_NUMBER, KEY_EPISODE_NUMBER, KEY_BROADCAST_DATE);
                return jSONObject;
            case 3:
                a(jSONObject, KEY_TITLE, KEY_ARTIST, KEY_ALBUM_TITLE, KEY_ALBUM_ARTIST, KEY_COMPOSER, KEY_TRACK_NUMBER, KEY_DISC_NUMBER, KEY_RELEASE_DATE);
                return jSONObject;
            case 4:
                a(jSONObject, KEY_TITLE, KEY_ARTIST, KEY_LOCATION_NAME, KEY_LOCATION_LATITUDE, KEY_LOCATION_LONGITUDE, KEY_WIDTH, KEY_HEIGHT, KEY_CREATION_DATE);
                return jSONObject;
            default:
                a(jSONObject, new String[0]);
                return jSONObject;
        }
    }

    public void c(JSONObject jSONObject) throws JSONException {
        clear();
        this.Fq = 0;
        try {
            this.Fq = jSONObject.getInt("metadataType");
        } catch (JSONException e) {
        }
        iu.a(this.EA, jSONObject);
        switch (this.Fq) {
            case 0:
                b(jSONObject, KEY_TITLE, KEY_ARTIST, KEY_SUBTITLE, KEY_RELEASE_DATE);
                break;
            case 1:
                b(jSONObject, KEY_TITLE, KEY_STUDIO, KEY_SUBTITLE, KEY_RELEASE_DATE);
                break;
            case 2:
                b(jSONObject, KEY_TITLE, KEY_SERIES_TITLE, KEY_SEASON_NUMBER, KEY_EPISODE_NUMBER, KEY_BROADCAST_DATE);
                break;
            case 3:
                b(jSONObject, KEY_TITLE, KEY_ALBUM_TITLE, KEY_ARTIST, KEY_ALBUM_ARTIST, KEY_COMPOSER, KEY_TRACK_NUMBER, KEY_DISC_NUMBER, KEY_RELEASE_DATE);
                break;
            case 4:
                b(jSONObject, KEY_TITLE, KEY_ARTIST, KEY_LOCATION_NAME, KEY_LOCATION_LATITUDE, KEY_LOCATION_LONGITUDE, KEY_WIDTH, KEY_HEIGHT, KEY_CREATION_DATE);
                break;
            default:
                b(jSONObject, new String[0]);
                break;
        }
    }

    public void clear() {
        this.Fp.clear();
        this.EA.clear();
    }

    public void clearImages() {
        this.EA.clear();
    }

    public boolean containsKey(String key) {
        return this.Fp.containsKey(key);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaMetadata)) {
            return false;
        }
        MediaMetadata mediaMetadata = (MediaMetadata) other;
        return a(this.Fp, mediaMetadata.Fp) && this.EA.equals(mediaMetadata.EA);
    }

    public Calendar getDate(String key) throws IllegalArgumentException {
        f(key, 4);
        String string = this.Fp.getString(key);
        if (string != null) {
            return iu.aL(string);
        }
        return null;
    }

    public String getDateAsString(String key) throws IllegalArgumentException {
        f(key, 4);
        return this.Fp.getString(key);
    }

    public double getDouble(String key) throws IllegalArgumentException {
        f(key, 3);
        return this.Fp.getDouble(key);
    }

    public List<WebImage> getImages() {
        return this.EA;
    }

    public int getInt(String key) throws IllegalArgumentException {
        f(key, 2);
        return this.Fp.getInt(key);
    }

    public int getMediaType() {
        return this.Fq;
    }

    public String getString(String key) throws IllegalArgumentException {
        f(key, 1);
        return this.Fp.getString(key);
    }

    public boolean hasImages() {
        return (this.EA == null || this.EA.isEmpty()) ? false : true;
    }

    public int hashCode() {
        int iHashCode = 17;
        Iterator<String> it = this.Fp.keySet().iterator();
        while (true) {
            int i = iHashCode;
            if (!it.hasNext()) {
                return (i * 31) + this.EA.hashCode();
            }
            iHashCode = this.Fp.get(it.next()).hashCode() + (i * 31);
        }
    }

    public Set<String> keySet() {
        return this.Fp.keySet();
    }

    public void putDate(String key, Calendar value) throws IllegalArgumentException {
        f(key, 4);
        this.Fp.putString(key, iu.a(value));
    }

    public void putDouble(String key, double value) throws IllegalArgumentException {
        f(key, 3);
        this.Fp.putDouble(key, value);
    }

    public void putInt(String key, int value) throws IllegalArgumentException {
        f(key, 2);
        this.Fp.putInt(key, value);
    }

    public void putString(String key, String value) throws IllegalArgumentException {
        f(key, 1);
        this.Fp.putString(key, value);
    }
}
