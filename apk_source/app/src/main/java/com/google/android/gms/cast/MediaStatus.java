package com.google.android.gms.cast;

import com.google.android.gms.internal.ik;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.iflytek.cloud.SpeechConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class MediaStatus {
    public static final long COMMAND_PAUSE = 1;
    public static final long COMMAND_SEEK = 2;
    public static final long COMMAND_SET_VOLUME = 4;
    public static final long COMMAND_SKIP_BACKWARD = 32;
    public static final long COMMAND_SKIP_FORWARD = 16;
    public static final long COMMAND_TOGGLE_MUTE = 8;
    public static final int IDLE_REASON_CANCELED = 2;
    public static final int IDLE_REASON_ERROR = 4;
    public static final int IDLE_REASON_FINISHED = 1;
    public static final int IDLE_REASON_INTERRUPTED = 3;
    public static final int IDLE_REASON_NONE = 0;
    public static final int PLAYER_STATE_BUFFERING = 4;
    public static final int PLAYER_STATE_IDLE = 1;
    public static final int PLAYER_STATE_PAUSED = 3;
    public static final int PLAYER_STATE_PLAYING = 2;
    public static final int PLAYER_STATE_UNKNOWN = 0;
    private double FA;
    private boolean FB;
    private long[] FC;
    private JSONObject Fl;
    private MediaInfo Fm;
    private long Fu;
    private double Fv;
    private int Fw;
    private int Fx;
    private long Fy;
    private long Fz;

    public MediaStatus(JSONObject json) throws JSONException {
        a(json, 0);
    }

    public int a(JSONObject jSONObject, int i) throws JSONException {
        int i2;
        long[] jArr;
        boolean z = false;
        boolean z2 = true;
        long j = jSONObject.getLong("mediaSessionId");
        if (j != this.Fu) {
            this.Fu = j;
            i2 = 1;
        } else {
            i2 = 0;
        }
        if (jSONObject.has("playerState")) {
            String string = jSONObject.getString("playerState");
            int i3 = string.equals("IDLE") ? 1 : string.equals("PLAYING") ? 2 : string.equals("PAUSED") ? 3 : string.equals("BUFFERING") ? 4 : 0;
            if (i3 != this.Fw) {
                this.Fw = i3;
                i2 |= 2;
            }
            if (i3 == 1 && jSONObject.has("idleReason")) {
                String string2 = jSONObject.getString("idleReason");
                int i4 = string2.equals("CANCELLED") ? 2 : string2.equals("INTERRUPTED") ? 3 : string2.equals("FINISHED") ? 1 : string2.equals("ERROR") ? 4 : 0;
                if (i4 != this.Fx) {
                    this.Fx = i4;
                    i2 |= 2;
                }
            }
        }
        if (jSONObject.has("playbackRate")) {
            double d = jSONObject.getDouble("playbackRate");
            if (this.Fv != d) {
                this.Fv = d;
                i2 |= 2;
            }
        }
        if (jSONObject.has("currentTime") && (i & 2) == 0) {
            long jB = ik.b(jSONObject.getDouble("currentTime"));
            if (jB != this.Fy) {
                this.Fy = jB;
                i2 |= 2;
            }
        }
        if (jSONObject.has("supportedMediaCommands")) {
            long j2 = jSONObject.getLong("supportedMediaCommands");
            if (j2 != this.Fz) {
                this.Fz = j2;
                i2 |= 2;
            }
        }
        if (jSONObject.has(SpeechConstant.VOLUME) && (i & 1) == 0) {
            JSONObject jSONObject2 = jSONObject.getJSONObject(SpeechConstant.VOLUME);
            double d2 = jSONObject2.getDouble("level");
            if (d2 != this.FA) {
                this.FA = d2;
                i2 |= 2;
            }
            boolean z3 = jSONObject2.getBoolean("muted");
            if (z3 != this.FB) {
                this.FB = z3;
                i2 |= 2;
            }
        }
        if (jSONObject.has("activeTrackIds")) {
            JSONArray jSONArray = jSONObject.getJSONArray("activeTrackIds");
            int length = jSONArray.length();
            long[] jArr2 = new long[length];
            for (int i5 = 0; i5 < length; i5++) {
                jArr2[i5] = jSONArray.getLong(i5);
            }
            if (this.FC != null && this.FC.length == length) {
                int i6 = 0;
                while (true) {
                    if (i6 >= length) {
                        z2 = false;
                        break;
                    }
                    if (this.FC[i6] != jArr2[i6]) {
                        break;
                    }
                    i6++;
                }
            }
            if (z2) {
                this.FC = jArr2;
            }
            z = z2;
            jArr = jArr2;
        } else if (this.FC != null) {
            z = true;
            jArr = null;
        } else {
            jArr = null;
        }
        if (z) {
            this.FC = jArr;
            i2 |= 2;
        }
        if (jSONObject.has("customData")) {
            this.Fl = jSONObject.getJSONObject("customData");
            i2 |= 2;
        }
        if (!jSONObject.has("media")) {
            return i2;
        }
        JSONObject jSONObject3 = jSONObject.getJSONObject("media");
        this.Fm = new MediaInfo(jSONObject3);
        int i7 = i2 | 2;
        return jSONObject3.has(PlaylistSQLiteHelper.COL_METADATA) ? i7 | 4 : i7;
    }

    public long fx() {
        return this.Fu;
    }

    public long[] getActiveTrackIds() {
        return this.FC;
    }

    public JSONObject getCustomData() {
        return this.Fl;
    }

    public int getIdleReason() {
        return this.Fx;
    }

    public MediaInfo getMediaInfo() {
        return this.Fm;
    }

    public double getPlaybackRate() {
        return this.Fv;
    }

    public int getPlayerState() {
        return this.Fw;
    }

    public long getStreamPosition() {
        return this.Fy;
    }

    public double getStreamVolume() {
        return this.FA;
    }

    public boolean isMediaCommandSupported(long mediaCommand) {
        return (this.Fz & mediaCommand) != 0;
    }

    public boolean isMute() {
        return this.FB;
    }
}
