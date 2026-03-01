package com.hisilicon.dlna.dmc.gui.customview;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

/* loaded from: classes.dex */
public class LocalMediaPlayer extends MediaPlayer {
    public static int surface_height;
    public static int surface_width;
    public static int video_height;
    public static int video_width;
    private SurfaceHolder m_surfaceHolder = null;
    private ViewChange viewChange;

    public interface ViewChange {
        void change(int i, int i2);
    }

    @Override // android.media.MediaPlayer
    public void setDisplay(SurfaceHolder sh) {
        try {
            super.setDisplay(sh);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                super.setDisplay(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.m_surfaceHolder = sh;
    }

    public void setDisplay(SurfaceHolder sh, ViewChange viewChange) {
        try {
            super.setDisplay(sh);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                super.setDisplay(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.viewChange = viewChange;
        this.m_surfaceHolder = sh;
    }

    @Override // android.media.MediaPlayer
    public void start() throws IllegalStateException {
        super.start();
        scaleContent();
    }

    public void scaleContent() {
        try {
            video_width = getVideoWidth();
            video_height = getVideoHeight();
            if (surface_width != 0 && surface_height != 0 && video_width != 0 && video_height != 0 && this.m_surfaceHolder != null) {
                float scale_width = video_width / surface_width;
                float scale_height = video_height / surface_height;
                float max_scale = scale_width > scale_height ? scale_width : scale_height;
                int target_width = (int) (video_width / max_scale);
                int target_height = (int) (video_height / max_scale);
                this.m_surfaceHolder.setFixedSize(target_width, target_height);
                if (this.viewChange != null) {
                    this.viewChange.change(target_width, target_height);
                }
            }
        } catch (Exception e) {
        }
    }
}
