package com.iflytek.cloud.c;

import android.media.AudioManager;

/* loaded from: classes.dex */
final class c implements AudioManager.OnAudioFocusChangeListener {
    final /* synthetic */ b a;

    c(b bVar) {
        this.a = bVar;
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(int i) {
        if (i == -2 || i == -3 || i == -1) {
            com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "pause start");
            if (this.a.c()) {
                com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "pause success");
                this.a.l = true;
                if (this.a.f != null) {
                    this.a.f.a();
                    return;
                }
                return;
            }
            return;
        }
        if (i == 1) {
            com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "resume start");
            if (this.a.l) {
                this.a.l = false;
                if (this.a.d()) {
                    com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "resume success");
                    if (this.a.f != null) {
                        this.a.f.b();
                    }
                }
            }
        }
    }
}
