package com.google.android.gms.cast;

import android.text.TextUtils;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.internal.ik;
import com.google.android.gms.internal.jz;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class MediaInfo {
    public static final int STREAM_TYPE_BUFFERED = 1;
    public static final int STREAM_TYPE_INVALID = -1;
    public static final int STREAM_TYPE_LIVE = 2;
    public static final int STREAM_TYPE_NONE = 0;
    private final String Fe;
    private int Ff;
    private String Fg;
    private MediaMetadata Fh;
    private long Fi;
    private List<MediaTrack> Fj;
    private TextTrackStyle Fk;
    private JSONObject Fl;

    public static class Builder {
        private final MediaInfo Fm;

        public Builder(String contentId) throws IllegalArgumentException {
            if (TextUtils.isEmpty(contentId)) {
                throw new IllegalArgumentException("Content ID cannot be empty");
            }
            this.Fm = new MediaInfo(contentId);
        }

        public MediaInfo build() throws IllegalArgumentException {
            this.Fm.fw();
            return this.Fm;
        }

        public Builder setContentType(String contentType) throws IllegalArgumentException {
            this.Fm.setContentType(contentType);
            return this;
        }

        public Builder setCustomData(JSONObject customData) {
            this.Fm.setCustomData(customData);
            return this;
        }

        public Builder setMediaTracks(List<MediaTrack> mediaTracks) {
            this.Fm.c(mediaTracks);
            return this;
        }

        public Builder setMetadata(MediaMetadata metadata) {
            this.Fm.a(metadata);
            return this;
        }

        public Builder setStreamDuration(long duration) throws IllegalArgumentException {
            this.Fm.m(duration);
            return this;
        }

        public Builder setStreamType(int streamType) throws IllegalArgumentException {
            this.Fm.setStreamType(streamType);
            return this;
        }

        public Builder setTextTrackStyle(TextTrackStyle textTrackStyle) {
            this.Fm.setTextTrackStyle(textTrackStyle);
            return this;
        }
    }

    MediaInfo(String contentId) throws IllegalArgumentException {
        if (TextUtils.isEmpty(contentId)) {
            throw new IllegalArgumentException("content ID cannot be null or empty");
        }
        this.Fe = contentId;
        this.Ff = -1;
    }

    MediaInfo(JSONObject json) throws JSONException {
        this.Fe = json.getString("contentId");
        String string = json.getString("streamType");
        if ("NONE".equals(string)) {
            this.Ff = 0;
        } else if ("BUFFERED".equals(string)) {
            this.Ff = 1;
        } else if ("LIVE".equals(string)) {
            this.Ff = 2;
        } else {
            this.Ff = -1;
        }
        this.Fg = json.getString("contentType");
        if (json.has(PlaylistSQLiteHelper.COL_METADATA)) {
            JSONObject jSONObject = json.getJSONObject(PlaylistSQLiteHelper.COL_METADATA);
            this.Fh = new MediaMetadata(jSONObject.getInt("metadataType"));
            this.Fh.c(jSONObject);
        }
        this.Fi = ik.b(json.optDouble("duration", 0.0d));
        if (json.has("tracks")) {
            this.Fj = new ArrayList();
            JSONArray jSONArray = json.getJSONArray("tracks");
            for (int i = 0; i < jSONArray.length(); i++) {
                this.Fj.add(new MediaTrack(jSONArray.getJSONObject(i)));
            }
        } else {
            this.Fj = null;
        }
        if (json.has("textTrackStyle")) {
            JSONObject jSONObject2 = json.getJSONObject("textTrackStyle");
            TextTrackStyle textTrackStyle = new TextTrackStyle();
            textTrackStyle.c(jSONObject2);
            this.Fk = textTrackStyle;
        } else {
            this.Fk = null;
        }
        this.Fl = json.optJSONObject("customData");
    }

    void a(MediaMetadata mediaMetadata) {
        this.Fh = mediaMetadata;
    }

    public JSONObject bL() throws JSONException {
        String str;
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("contentId", this.Fe);
            switch (this.Ff) {
                case 1:
                    str = "BUFFERED";
                    break;
                case 2:
                    str = "LIVE";
                    break;
                default:
                    str = "NONE";
                    break;
            }
            jSONObject.put("streamType", str);
            if (this.Fg != null) {
                jSONObject.put("contentType", this.Fg);
            }
            if (this.Fh != null) {
                jSONObject.put(PlaylistSQLiteHelper.COL_METADATA, this.Fh.bL());
            }
            jSONObject.put("duration", ik.o(this.Fi));
            if (this.Fj != null) {
                JSONArray jSONArray = new JSONArray();
                Iterator<MediaTrack> it = this.Fj.iterator();
                while (it.hasNext()) {
                    jSONArray.put(it.next().bL());
                }
                jSONObject.put("tracks", jSONArray);
            }
            if (this.Fk != null) {
                jSONObject.put("textTrackStyle", this.Fk.bL());
            }
            if (this.Fl != null) {
                jSONObject.put("customData", this.Fl);
            }
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    void c(List<MediaTrack> list) {
        this.Fj = list;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaInfo)) {
            return false;
        }
        MediaInfo mediaInfo = (MediaInfo) other;
        if ((this.Fl == null) != (mediaInfo.Fl == null)) {
            return false;
        }
        if (this.Fl == null || mediaInfo.Fl == null || jz.d(this.Fl, mediaInfo.Fl)) {
            return ik.a(this.Fe, mediaInfo.Fe) && this.Ff == mediaInfo.Ff && ik.a(this.Fg, mediaInfo.Fg) && ik.a(this.Fh, mediaInfo.Fh) && this.Fi == mediaInfo.Fi;
        }
        return false;
    }

    void fw() throws IllegalArgumentException {
        if (TextUtils.isEmpty(this.Fe)) {
            throw new IllegalArgumentException("content ID cannot be null or empty");
        }
        if (TextUtils.isEmpty(this.Fg)) {
            throw new IllegalArgumentException("content type cannot be null or empty");
        }
        if (this.Ff == -1) {
            throw new IllegalArgumentException("a valid stream type must be specified");
        }
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

    public List<MediaTrack> getMediaTracks() {
        return this.Fj;
    }

    public MediaMetadata getMetadata() {
        return this.Fh;
    }

    public long getStreamDuration() {
        return this.Fi;
    }

    public int getStreamType() {
        return this.Ff;
    }

    public TextTrackStyle getTextTrackStyle() {
        return this.Fk;
    }

    public int hashCode() {
        return m.hashCode(this.Fe, Integer.valueOf(this.Ff), this.Fg, this.Fh, Long.valueOf(this.Fi), String.valueOf(this.Fl));
    }

    void m(long j) throws IllegalArgumentException {
        if (j < 0) {
            throw new IllegalArgumentException("Stream duration cannot be negative");
        }
        this.Fi = j;
    }

    void setContentType(String contentType) throws IllegalArgumentException {
        if (TextUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException("content type cannot be null or empty");
        }
        this.Fg = contentType;
    }

    void setCustomData(JSONObject customData) {
        this.Fl = customData;
    }

    void setStreamType(int streamType) throws IllegalArgumentException {
        if (streamType < -1 || streamType > 2) {
            throw new IllegalArgumentException("invalid stream type");
        }
        this.Ff = streamType;
    }

    public void setTextTrackStyle(TextTrackStyle textTrackStyle) {
        this.Fk = textTrackStyle;
    }
}
