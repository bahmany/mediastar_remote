package com.google.android.gms.cast;

import android.text.TextUtils;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.internal.ik;
import com.google.android.gms.internal.jz;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.iflytek.cloud.SpeechConstant;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class MediaTrack {
    public static final int SUBTYPE_CAPTIONS = 2;
    public static final int SUBTYPE_CHAPTERS = 4;
    public static final int SUBTYPE_DESCRIPTIONS = 3;
    public static final int SUBTYPE_METADATA = 5;
    public static final int SUBTYPE_NONE = 0;
    public static final int SUBTYPE_SUBTITLES = 1;
    public static final int SUBTYPE_UNKNOWN = -1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_VIDEO = 3;
    private long Dj;
    private int FD;
    private int FE;
    private String Fc;
    private String Fe;
    private String Fg;
    private JSONObject Fl;
    private String mName;

    public static class Builder {
        private final MediaTrack FF;

        public Builder(long trackId, int trackType) throws IllegalArgumentException {
            this.FF = new MediaTrack(trackId, trackType);
        }

        public MediaTrack build() {
            return this.FF;
        }

        public Builder setContentId(String contentId) {
            this.FF.setContentId(contentId);
            return this;
        }

        public Builder setContentType(String contentType) {
            this.FF.setContentType(contentType);
            return this;
        }

        public Builder setCustomData(JSONObject customData) {
            this.FF.setCustomData(customData);
            return this;
        }

        public Builder setLanguage(String language) {
            this.FF.setLanguage(language);
            return this;
        }

        public Builder setLanguage(Locale locale) {
            this.FF.setLanguage(ik.b(locale));
            return this;
        }

        public Builder setName(String trackName) {
            this.FF.setName(trackName);
            return this;
        }

        public Builder setSubtype(int subtype) throws IllegalArgumentException {
            this.FF.aa(subtype);
            return this;
        }
    }

    MediaTrack(long id, int type) throws IllegalArgumentException {
        clear();
        this.Dj = id;
        if (type <= 0 || type > 3) {
            throw new IllegalArgumentException("invalid type " + type);
        }
        this.FD = type;
    }

    MediaTrack(JSONObject json) throws JSONException {
        c(json);
    }

    private void c(JSONObject jSONObject) throws JSONException {
        clear();
        this.Dj = jSONObject.getLong("trackId");
        String string = jSONObject.getString(PlaylistSQLiteHelper.COL_TYPE);
        if ("TEXT".equals(string)) {
            this.FD = 1;
        } else if ("AUDIO".equals(string)) {
            this.FD = 2;
        } else {
            if (!"VIDEO".equals(string)) {
                throw new JSONException("invalid type: " + string);
            }
            this.FD = 3;
        }
        this.Fe = jSONObject.optString("trackContentId", null);
        this.Fg = jSONObject.optString("trackContentType", null);
        this.mName = jSONObject.optString("name", null);
        this.Fc = jSONObject.optString(SpeechConstant.LANGUAGE, null);
        if (jSONObject.has("subtype")) {
            String string2 = jSONObject.getString("subtype");
            if ("SUBTITLES".equals(string2)) {
                this.FE = 1;
            } else if ("CAPTIONS".equals(string2)) {
                this.FE = 2;
            } else if ("DESCRIPTIONS".equals(string2)) {
                this.FE = 3;
            } else if ("CHAPTERS".equals(string2)) {
                this.FE = 4;
            } else {
                if (!"METADATA".equals(string2)) {
                    throw new JSONException("invalid subtype: " + string2);
                }
                this.FE = 5;
            }
        } else {
            this.FE = 0;
        }
        this.Fl = jSONObject.optJSONObject("customData");
    }

    private void clear() {
        this.Dj = 0L;
        this.FD = 0;
        this.Fe = null;
        this.mName = null;
        this.Fc = null;
        this.FE = -1;
        this.Fl = null;
    }

    void aa(int i) throws IllegalArgumentException {
        if (i <= -1 || i > 5) {
            throw new IllegalArgumentException("invalid subtype " + i);
        }
        if (i != 0 && this.FD != 1) {
            throw new IllegalArgumentException("subtypes are only valid for text tracks");
        }
        this.FE = i;
    }

    public JSONObject bL() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("trackId", this.Dj);
            switch (this.FD) {
                case 1:
                    jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "TEXT");
                    break;
                case 2:
                    jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "AUDIO");
                    break;
                case 3:
                    jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "VIDEO");
                    break;
            }
            if (this.Fe != null) {
                jSONObject.put("trackContentId", this.Fe);
            }
            if (this.Fg != null) {
                jSONObject.put("trackContentType", this.Fg);
            }
            if (this.mName != null) {
                jSONObject.put("name", this.mName);
            }
            if (!TextUtils.isEmpty(this.Fc)) {
                jSONObject.put(SpeechConstant.LANGUAGE, this.Fc);
            }
            switch (this.FE) {
                case 1:
                    jSONObject.put("subtype", "SUBTITLES");
                    break;
                case 2:
                    jSONObject.put("subtype", "CAPTIONS");
                    break;
                case 3:
                    jSONObject.put("subtype", "DESCRIPTIONS");
                    break;
                case 4:
                    jSONObject.put("subtype", "CHAPTERS");
                    break;
                case 5:
                    jSONObject.put("subtype", "METADATA");
                    break;
            }
            if (this.Fl != null) {
                jSONObject.put("customData", this.Fl);
            }
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaTrack)) {
            return false;
        }
        MediaTrack mediaTrack = (MediaTrack) other;
        if ((this.Fl == null) != (mediaTrack.Fl == null)) {
            return false;
        }
        if (this.Fl == null || mediaTrack.Fl == null || jz.d(this.Fl, mediaTrack.Fl)) {
            return this.Dj == mediaTrack.Dj && this.FD == mediaTrack.FD && ik.a(this.Fe, mediaTrack.Fe) && ik.a(this.Fg, mediaTrack.Fg) && ik.a(this.mName, mediaTrack.mName) && ik.a(this.Fc, mediaTrack.Fc) && this.FE == mediaTrack.FE;
        }
        return false;
    }

    public String getContentId() {
        return this.Fe;
    }

    public String getContentType() {
        return this.Fg;
    }

    public JSONObject getCustomData() {
        return this.Fl;
    }

    public long getId() {
        return this.Dj;
    }

    public String getLanguage() {
        return this.Fc;
    }

    public String getName() {
        return this.mName;
    }

    public int getSubtype() {
        return this.FE;
    }

    public int getType() {
        return this.FD;
    }

    public int hashCode() {
        return m.hashCode(Long.valueOf(this.Dj), Integer.valueOf(this.FD), this.Fe, this.Fg, this.mName, this.Fc, Integer.valueOf(this.FE), this.Fl);
    }

    public void setContentId(String contentId) {
        this.Fe = contentId;
    }

    public void setContentType(String contentType) {
        this.Fg = contentType;
    }

    void setCustomData(JSONObject customData) {
        this.Fl = customData;
    }

    void setLanguage(String language) {
        this.Fc = language;
    }

    void setName(String name) {
        this.mName = name;
    }
}
