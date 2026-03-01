package com.google.android.gms.internal;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import com.google.android.gms.tagmanager.DataLayer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@ez
/* renamed from: com.google.android.gms.internal.do, reason: invalid class name */
/* loaded from: classes.dex */
public final class Cdo extends FrameLayout implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private final gv md;
    private final MediaController rX;
    private final a rY;
    private final VideoView rZ;
    private long sa;
    private String sb;

    /* renamed from: com.google.android.gms.internal.do$a */
    private static final class a {
        private final Runnable mk;
        private volatile boolean sc = false;

        public a(final Cdo cdo) {
            this.mk = new Runnable() { // from class: com.google.android.gms.internal.do.a.1
                private final WeakReference<Cdo> sd;

                {
                    this.sd = new WeakReference<>(cdo);
                }

                @Override // java.lang.Runnable
                public void run() {
                    Cdo cdo2 = this.sd.get();
                    if (a.this.sc || cdo2 == null) {
                        return;
                    }
                    cdo2.cj();
                    a.this.ck();
                }
            };
        }

        public void cancel() {
            this.sc = true;
            gr.wC.removeCallbacks(this.mk);
        }

        public void ck() {
            gr.wC.postDelayed(this.mk, 250L);
        }
    }

    public Cdo(Context context, gv gvVar) {
        super(context);
        this.md = gvVar;
        this.rZ = new VideoView(context);
        addView(this.rZ, new FrameLayout.LayoutParams(-1, -1, 17));
        this.rX = new MediaController(context);
        this.rY = new a(this);
        this.rY.ck();
        this.rZ.setOnCompletionListener(this);
        this.rZ.setOnPreparedListener(this);
        this.rZ.setOnErrorListener(this);
    }

    private static void a(gv gvVar, String str) {
        a(gvVar, str, new HashMap(1));
    }

    public static void a(gv gvVar, String str, String str2) {
        boolean z = str2 == null;
        HashMap map = new HashMap(z ? 2 : 3);
        map.put("what", str);
        if (!z) {
            map.put("extra", str2);
        }
        a(gvVar, "error", map);
    }

    private static void a(gv gvVar, String str, String str2, String str3) {
        HashMap map = new HashMap(2);
        map.put(str2, str3);
        a(gvVar, str, map);
    }

    private static void a(gv gvVar, String str, Map<String, String> map) {
        map.put(DataLayer.EVENT_KEY, str);
        gvVar.a("onVideoEvent", map);
    }

    public void C(String str) {
        this.sb = str;
    }

    public void b(MotionEvent motionEvent) {
        this.rZ.dispatchTouchEvent(motionEvent);
    }

    public void ci() {
        if (TextUtils.isEmpty(this.sb)) {
            a(this.md, "no_src", (String) null);
        } else {
            this.rZ.setVideoPath(this.sb);
        }
    }

    public void cj() {
        long currentPosition = this.rZ.getCurrentPosition();
        if (this.sa != currentPosition) {
            a(this.md, "timeupdate", "time", String.valueOf(currentPosition / 1000.0f));
            this.sa = currentPosition;
        }
    }

    public void destroy() {
        this.rY.cancel();
        this.rZ.stopPlayback();
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        a(this.md, "ended");
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        a(this.md, String.valueOf(what), String.valueOf(extra));
        return true;
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        a(this.md, "canplaythrough", "duration", String.valueOf(this.rZ.getDuration() / 1000.0f));
    }

    public void pause() {
        this.rZ.pause();
    }

    public void play() {
        this.rZ.start();
    }

    public void q(boolean z) {
        if (z) {
            this.rZ.setMediaController(this.rX);
        } else {
            this.rX.hide();
            this.rZ.setMediaController(null);
        }
    }

    public void seekTo(int timeInMilliseconds) {
        this.rZ.seekTo(timeInMilliseconds);
    }
}
