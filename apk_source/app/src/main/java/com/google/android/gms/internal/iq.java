package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.TextTrackStyle;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.iflytek.cloud.SpeechConstant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class iq extends ii {
    private long Hf;
    private MediaStatus Hg;
    private final it Hh;
    private final it Hi;
    private final it Hj;
    private final it Hk;
    private final it Hl;
    private final it Hm;
    private final it Hn;
    private final it Ho;
    private final it Hp;
    private final it Hq;
    private final List<it> Hr;
    private final Runnable Hs;
    private boolean Ht;
    private final Handler mHandler;
    private static final String NAMESPACE = ik.aG("com.google.cast.media");
    private static final long Hb = TimeUnit.HOURS.toMillis(24);
    private static final long Hc = TimeUnit.HOURS.toMillis(24);
    private static final long Hd = TimeUnit.HOURS.toMillis(24);
    private static final long He = TimeUnit.SECONDS.toMillis(1);

    private class a implements Runnable {
        private a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z = false;
            iq.this.Ht = false;
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            Iterator it = iq.this.Hr.iterator();
            while (it.hasNext()) {
                ((it) it.next()).e(jElapsedRealtime, 2102);
            }
            synchronized (it.Hz) {
                Iterator it2 = iq.this.Hr.iterator();
                while (it2.hasNext()) {
                    z = ((it) it2.next()).fW() ? true : z;
                }
            }
            iq.this.H(z);
        }
    }

    public iq() {
        this(null);
    }

    public iq(String str) {
        super(NAMESPACE, "MediaControlChannel", str);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.Hs = new a();
        this.Hr = new ArrayList();
        this.Hh = new it(Hc);
        this.Hr.add(this.Hh);
        this.Hi = new it(Hb);
        this.Hr.add(this.Hi);
        this.Hj = new it(Hb);
        this.Hr.add(this.Hj);
        this.Hk = new it(Hb);
        this.Hr.add(this.Hk);
        this.Hl = new it(Hd);
        this.Hr.add(this.Hl);
        this.Hm = new it(Hb);
        this.Hr.add(this.Hm);
        this.Hn = new it(Hb);
        this.Hr.add(this.Hn);
        this.Ho = new it(Hb);
        this.Hr.add(this.Ho);
        this.Hp = new it(Hb);
        this.Hr.add(this.Hp);
        this.Hq = new it(Hb);
        this.Hr.add(this.Hq);
        fU();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void H(boolean z) {
        if (this.Ht != z) {
            this.Ht = z;
            if (z) {
                this.mHandler.postDelayed(this.Hs, He);
            } else {
                this.mHandler.removeCallbacks(this.Hs);
            }
        }
    }

    private void a(long j, JSONObject jSONObject) throws JSONException {
        int iA;
        boolean z = true;
        boolean zP = this.Hh.p(j);
        boolean z2 = this.Hl.fW() && !this.Hl.p(j);
        if ((!this.Hm.fW() || this.Hm.p(j)) && (!this.Hn.fW() || this.Hn.p(j))) {
            z = false;
        }
        int i = z2 ? 2 : 0;
        if (z) {
            i |= 1;
        }
        if (zP || this.Hg == null) {
            this.Hg = new MediaStatus(jSONObject);
            this.Hf = SystemClock.elapsedRealtime();
            iA = 7;
        } else {
            iA = this.Hg.a(jSONObject, i);
        }
        if ((iA & 1) != 0) {
            this.Hf = SystemClock.elapsedRealtime();
            onStatusUpdated();
        }
        if ((iA & 2) != 0) {
            this.Hf = SystemClock.elapsedRealtime();
            onStatusUpdated();
        }
        if ((iA & 4) != 0) {
            onMetadataUpdated();
        }
        Iterator<it> it = this.Hr.iterator();
        while (it.hasNext()) {
            it.next().d(j, 0);
        }
    }

    private void fU() {
        H(false);
        this.Hf = 0L;
        this.Hg = null;
        this.Hh.clear();
        this.Hl.clear();
        this.Hm.clear();
    }

    public long a(is isVar) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject();
        long jFA = fA();
        this.Ho.a(jFA, isVar);
        H(true);
        try {
            jSONObject.put("requestId", jFA);
            jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "GET_STATUS");
            if (this.Hg != null) {
                jSONObject.put("mediaSessionId", this.Hg.fx());
            }
        } catch (JSONException e) {
        }
        a(jSONObject.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, double d, JSONObject jSONObject) throws IllegalStateException, JSONException, IOException, IllegalArgumentException {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            throw new IllegalArgumentException("Volume cannot be " + d);
        }
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hm.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "SET_VOLUME");
            jSONObject2.put("mediaSessionId", fx());
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("level", d);
            jSONObject2.put(SpeechConstant.VOLUME, jSONObject3);
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, long j, int i, JSONObject jSONObject) throws IllegalStateException, JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hl.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "SEEK");
            jSONObject2.put("mediaSessionId", fx());
            jSONObject2.put("currentTime", ik.o(j));
            if (i == 1) {
                jSONObject2.put("resumeState", "PLAYBACK_START");
            } else if (i == 2) {
                jSONObject2.put("resumeState", "PLAYBACK_PAUSE");
            }
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, MediaInfo mediaInfo, boolean z, long j, long[] jArr, JSONObject jSONObject) throws JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hh.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "LOAD");
            jSONObject2.put("media", mediaInfo.bL());
            jSONObject2.put("autoplay", z);
            jSONObject2.put("currentTime", ik.o(j));
            if (jArr != null && jArr.length > 0) {
                JSONArray jSONArray = new JSONArray();
                for (int i = 0; i < jArr.length; i++) {
                    jSONArray.put(i, jArr[i]);
                }
                jSONObject2.put("activeTrackIds", jSONArray);
            }
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, TextTrackStyle textTrackStyle) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject();
        long jFA = fA();
        this.Hq.a(jFA, isVar);
        H(true);
        try {
            jSONObject.put("requestId", jFA);
            jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "EDIT_TRACKS_INFO");
            if (textTrackStyle != null) {
                jSONObject.put("textTrackStyle", textTrackStyle.bL());
            }
            jSONObject.put("mediaSessionId", fx());
        } catch (JSONException e) {
        }
        a(jSONObject.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, JSONObject jSONObject) throws JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hi.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "PAUSE");
            jSONObject2.put("mediaSessionId", fx());
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, boolean z, JSONObject jSONObject) throws IllegalStateException, JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hn.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "SET_VOLUME");
            jSONObject2.put("mediaSessionId", fx());
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("muted", z);
            jSONObject2.put(SpeechConstant.VOLUME, jSONObject3);
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    public long a(is isVar, long[] jArr) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject();
        long jFA = fA();
        this.Hp.a(jFA, isVar);
        H(true);
        try {
            jSONObject.put("requestId", jFA);
            jSONObject.put(PlaylistSQLiteHelper.COL_TYPE, "EDIT_TRACKS_INFO");
            jSONObject.put("mediaSessionId", fx());
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < jArr.length; i++) {
                jSONArray.put(i, jArr[i]);
            }
            jSONObject.put("activeTrackIds", jSONArray);
        } catch (JSONException e) {
        }
        a(jSONObject.toString(), jFA, (String) null);
        return jFA;
    }

    @Override // com.google.android.gms.internal.ii
    public final void aD(String str) throws JSONException {
        this.Go.b("message received: %s", str);
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString(PlaylistSQLiteHelper.COL_TYPE);
            long jOptLong = jSONObject.optLong("requestId", -1L);
            if (string.equals("MEDIA_STATUS")) {
                JSONArray jSONArray = jSONObject.getJSONArray("status");
                if (jSONArray.length() > 0) {
                    a(jOptLong, jSONArray.getJSONObject(0));
                    return;
                }
                this.Hg = null;
                onStatusUpdated();
                onMetadataUpdated();
                this.Ho.d(jOptLong, 0);
                return;
            }
            if (string.equals("INVALID_PLAYER_STATE")) {
                this.Go.d("received unexpected error: Invalid Player State.", new Object[0]);
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("customData");
                Iterator<it> it = this.Hr.iterator();
                while (it.hasNext()) {
                    it.next().b(jOptLong, 2100, jSONObjectOptJSONObject);
                }
                return;
            }
            if (string.equals("LOAD_FAILED")) {
                this.Hh.b(jOptLong, 2100, jSONObject.optJSONObject("customData"));
                return;
            }
            if (string.equals("LOAD_CANCELLED")) {
                this.Hh.b(jOptLong, 2101, jSONObject.optJSONObject("customData"));
                return;
            }
            if (string.equals("INVALID_REQUEST")) {
                this.Go.d("received unexpected error: Invalid Request.", new Object[0]);
                JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("customData");
                Iterator<it> it2 = this.Hr.iterator();
                while (it2.hasNext()) {
                    it2.next().b(jOptLong, 2100, jSONObjectOptJSONObject2);
                }
            }
        } catch (JSONException e) {
            this.Go.d("Message is malformed (%s); ignoring: %s", e.getMessage(), str);
        }
    }

    public long b(is isVar, JSONObject jSONObject) throws JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hk.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "STOP");
            jSONObject2.put("mediaSessionId", fx());
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    @Override // com.google.android.gms.internal.ii
    public void b(long j, int i) {
        Iterator<it> it = this.Hr.iterator();
        while (it.hasNext()) {
            it.next().d(j, i);
        }
    }

    public long c(is isVar, JSONObject jSONObject) throws IllegalStateException, JSONException, IOException {
        JSONObject jSONObject2 = new JSONObject();
        long jFA = fA();
        this.Hj.a(jFA, isVar);
        H(true);
        try {
            jSONObject2.put("requestId", jFA);
            jSONObject2.put(PlaylistSQLiteHelper.COL_TYPE, "PLAY");
            jSONObject2.put("mediaSessionId", fx());
            if (jSONObject != null) {
                jSONObject2.put("customData", jSONObject);
            }
        } catch (JSONException e) {
        }
        a(jSONObject2.toString(), jFA, (String) null);
        return jFA;
    }

    @Override // com.google.android.gms.internal.ii
    public void fB() {
        fU();
    }

    public long fx() throws IllegalStateException {
        if (this.Hg == null) {
            throw new IllegalStateException("No current media session");
        }
        return this.Hg.fx();
    }

    public long getApproximateStreamPosition() {
        MediaInfo mediaInfo = getMediaInfo();
        if (mediaInfo == null || this.Hf == 0) {
            return 0L;
        }
        double playbackRate = this.Hg.getPlaybackRate();
        long streamPosition = this.Hg.getStreamPosition();
        int playerState = this.Hg.getPlayerState();
        if (playbackRate == 0.0d || playerState != 2) {
            return streamPosition;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() - this.Hf;
        long j = jElapsedRealtime < 0 ? 0L : jElapsedRealtime;
        if (j == 0) {
            return streamPosition;
        }
        long streamDuration = mediaInfo.getStreamDuration();
        long j2 = streamPosition + ((long) (j * playbackRate));
        if (streamDuration <= 0 || j2 <= streamDuration) {
            streamDuration = j2 < 0 ? 0L : j2;
        }
        return streamDuration;
    }

    public MediaInfo getMediaInfo() {
        if (this.Hg == null) {
            return null;
        }
        return this.Hg.getMediaInfo();
    }

    public MediaStatus getMediaStatus() {
        return this.Hg;
    }

    public long getStreamDuration() {
        MediaInfo mediaInfo = getMediaInfo();
        if (mediaInfo != null) {
            return mediaInfo.getStreamDuration();
        }
        return 0L;
    }

    protected void onMetadataUpdated() {
    }

    protected void onStatusUpdated() {
    }
}
