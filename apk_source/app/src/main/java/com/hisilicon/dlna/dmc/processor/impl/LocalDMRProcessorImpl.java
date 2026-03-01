package com.hisilicon.dlna.dmc.processor.impl;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.dlna.dmc.gui.customview.LocalMediaPlayer;
import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor;
import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.support.model.DIDLObject;

/* loaded from: classes.dex */
public class LocalDMRProcessorImpl implements DMRProcessor {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type = null;
    private static final int SLEEP_INTERVAL = 1000;
    private static final int STATE_PAUSED = 2;
    private static final int STATE_PLAYING = 0;
    private static final int STATE_STOPED = 1;
    private static String itemId = null;
    public static LocalMediaPlayer m_player;
    private boolean isSeeked;
    private AudioManager m_audioManager;
    private int m_currentPosition;
    private int m_currentState;
    private int m_maxVolume;
    private SurfaceHolder m_holder = null;
    private MediaPlayer.OnPreparedListener m_preparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl.1
        AnonymousClass1() {
        }

        @Override // android.media.MediaPlayer.OnPreparedListener
        public void onPrepared(MediaPlayer mp) throws IllegalStateException, InterruptedException {
            int position = AppPreference.getPlayPosition();
            System.out.println("The position:" + position);
            if (position > 0) {
                LocalDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
            }
            LocalDMRProcessorImpl.this.m_currentItem.setPlayStatue(1);
            mp.start();
            LocalDMRProcessorImpl.this.setRunning(true);
            LocalDMRProcessorImpl.this.m_currentState = 0;
        }
    };
    private MediaPlayer.OnCompletionListener m_completeListener = new MediaPlayer.OnCompletionListener() { // from class: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl.2
        AnonymousClass2() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mp) {
            mp.reset();
            AppPreference.setPlayPosition(0);
            LocalDMRProcessorImpl.this.m_currentPosition = 0;
            LocalDMRProcessorImpl.this.fireOnCompleteEvent();
        }
    };
    private MediaPlayer.OnErrorListener m_onErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl.3
        AnonymousClass3() {
        }

        @Override // android.media.MediaPlayer.OnErrorListener
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (LocalDMRProcessorImpl.m_player != null) {
                LocalDMRProcessorImpl.m_player.reset();
                LocalDMRProcessorImpl.m_player.release();
                LocalDMRProcessorImpl.m_player = null;
                LocalDMRProcessorImpl.this.setRunning(false);
            }
            PlaylistProcessor m_playlistProcessor = UpnpProcessorImpl.getSington().getPlaylistProcessor();
            if (m_playlistProcessor != null) {
                m_playlistProcessor.next();
                return true;
            }
            return true;
        }
    };
    private List<DMRProcessor.DMRProcessorListener> m_listeners = new ArrayList();
    private PlaylistItem m_currentItem = new PlaylistItem();
    private UpdateThread m_updateThread = new UpdateThread();

    static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type() {
        int[] iArr = $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type;
        if (iArr == null) {
            iArr = new int[PlaylistItem.Type.valuesCustom().length];
            try {
                iArr[PlaylistItem.Type.AUDIO_LOCAL.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[PlaylistItem.Type.AUDIO_REMOTE.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[PlaylistItem.Type.IMAGE_LOCAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[PlaylistItem.Type.IMAGE_REMOTE.ordinal()] = 6;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[PlaylistItem.Type.UNKNOW.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[PlaylistItem.Type.VIDEO_LOCAL.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[PlaylistItem.Type.VIDEO_REMOTE.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type = iArr;
        }
        return iArr;
    }

    public static void setItemId(String id) {
        itemId = id;
    }

    private class UpdateThread extends Thread {
        private boolean running;

        public UpdateThread() {
            this.running = false;
            this.running = true;
        }

        public void stopThread() {
            this.running = false;
            interrupt();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws InterruptedException {
            while (this.running && LocalDMRProcessorImpl.m_player != null) {
                try {
                    if (LocalDMRProcessorImpl.m_player != null && LocalDMRProcessorImpl.m_player.isPlaying()) {
                        int currentPosition = LocalDMRProcessorImpl.m_player.getCurrentPosition() / 1000;
                        LocalDMRProcessorImpl.this.m_currentPosition = currentPosition;
                        int duration = LocalDMRProcessorImpl.m_player.getDuration() / 1000;
                        LocalDMRProcessorImpl.this.fireUpdatePositionEvent(currentPosition, duration);
                        LocalDMRProcessorImpl.this.m_currentState = 0;
                    }
                    switch (LocalDMRProcessorImpl.this.m_currentState) {
                        case 0:
                            LocalDMRProcessorImpl.this.m_currentItem.setPlayStatue(1);
                            LocalDMRProcessorImpl.this.fireOnPlayingEvent();
                            break;
                        case 1:
                            LocalDMRProcessorImpl.this.m_currentItem.setPlayStatue(0);
                            LocalDMRProcessorImpl.this.fireOnStopedEvent();
                            break;
                        case 2:
                            LocalDMRProcessorImpl.this.m_currentItem.setPlayStatue(2);
                            LocalDMRProcessorImpl.this.fireOnPausedEvent();
                            break;
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!this.running) {
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.running = false;
                    if (LocalDMRProcessorImpl.m_player != null) {
                        try {
                            LocalDMRProcessorImpl.m_player.reset();
                            LocalDMRProcessorImpl.m_player.release();
                        } catch (Exception e2) {
                        } finally {
                            LocalDMRProcessorImpl.m_player = null;
                        }
                    }
                    LocalDMRProcessorImpl.this.fireOnStopedEvent();
                    PlaylistProcessor m_playlistProcessor = UpnpProcessorImpl.getSington().getPlaylistProcessor();
                    if (m_playlistProcessor != null) {
                        m_playlistProcessor.next();
                        return;
                    }
                    return;
                }
            }
        }
    }

    public LocalDMRProcessorImpl(Context context) {
        this.m_audioManager = (AudioManager) context.getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY);
        this.m_maxVolume = this.m_audioManager.getStreamMaxVolume(3);
        this.m_updateThread.start();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setURIandPlay(PlaylistItem item) {
        System.out.println("---come into setURIandPlay---");
        System.out.println("------Position:" + AppPreference.getPlayPosition());
        if (item == null) {
            this.m_currentItem = null;
            stop();
            return;
        }
        if (itemId != null) {
            if (!itemId.equals(item.getId())) {
                if (item.isRemotePlay()) {
                    itemId = item.getId();
                    item.setRemotePlay(false);
                } else {
                    AppPreference.setPlayPosition(0);
                    this.m_currentPosition = 0;
                    itemId = item.getId();
                }
            }
        } else {
            AppPreference.setPlayPosition(0);
            itemId = item.getId();
        }
        if (this.m_currentItem == null || !this.m_currentItem.equals(item) || this.m_currentState == 1) {
            System.out.println("----second way----");
            this.m_currentItem = item;
            setRunning(true);
            switch ($SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type()[this.m_currentItem.getType().ordinal()]) {
                case 3:
                case 6:
                case 7:
                    stop();
                    return;
                case 4:
                case 5:
                default:
                    stop();
                    m_player = new LocalMediaPlayer();
                    m_player.setDisplay(this.m_holder);
                    m_player.setOnPreparedListener(this.m_preparedListener);
                    m_player.setOnCompletionListener(this.m_completeListener);
                    m_player.setOnErrorListener(this.m_onErrorListener);
                    m_player.setScreenOnWhilePlaying(true);
                    m_player.setOnSeekCompleteListener(new SeekCompleteListener(this, null));
                    synchronized (this.m_currentItem) {
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (m_player != null) {
                            m_player.setDataSource(this.m_currentItem.getUrl());
                            m_player.prepareAsync();
                        }
                    }
                    return;
            }
        }
    }

    private class SeekCompleteListener implements MediaPlayer.OnSeekCompleteListener {
        private SeekCompleteListener() {
        }

        /* synthetic */ SeekCompleteListener(LocalDMRProcessorImpl localDMRProcessorImpl, SeekCompleteListener seekCompleteListener) {
            this();
        }

        @Override // android.media.MediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(MediaPlayer mp) {
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl$1 */
    class AnonymousClass1 implements MediaPlayer.OnPreparedListener {
        AnonymousClass1() {
        }

        @Override // android.media.MediaPlayer.OnPreparedListener
        public void onPrepared(MediaPlayer mp) throws IllegalStateException, InterruptedException {
            int position = AppPreference.getPlayPosition();
            System.out.println("The position:" + position);
            if (position > 0) {
                LocalDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
            }
            LocalDMRProcessorImpl.this.m_currentItem.setPlayStatue(1);
            mp.start();
            LocalDMRProcessorImpl.this.setRunning(true);
            LocalDMRProcessorImpl.this.m_currentState = 0;
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl$2 */
    class AnonymousClass2 implements MediaPlayer.OnCompletionListener {
        AnonymousClass2() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mp) {
            mp.reset();
            AppPreference.setPlayPosition(0);
            LocalDMRProcessorImpl.this.m_currentPosition = 0;
            LocalDMRProcessorImpl.this.fireOnCompleteEvent();
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl$3 */
    class AnonymousClass3 implements MediaPlayer.OnErrorListener {
        AnonymousClass3() {
        }

        @Override // android.media.MediaPlayer.OnErrorListener
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (LocalDMRProcessorImpl.m_player != null) {
                LocalDMRProcessorImpl.m_player.reset();
                LocalDMRProcessorImpl.m_player.release();
                LocalDMRProcessorImpl.m_player = null;
                LocalDMRProcessorImpl.this.setRunning(false);
            }
            PlaylistProcessor m_playlistProcessor = UpnpProcessorImpl.getSington().getPlaylistProcessor();
            if (m_playlistProcessor != null) {
                m_playlistProcessor.next();
                return true;
            }
            return true;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void play() {
        try {
            if (m_player != null) {
                m_player.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playingSeek(String time) throws InterruptedException {
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        seek(time);
        this.isSeeked = true;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void pause() {
        if (m_player != null && m_player.isPlaying()) {
            try {
                if (this.m_currentPosition != 0) {
                    AppPreference.setPlayPosition(this.m_currentPosition);
                }
                m_player.pause();
                this.m_currentState = 2;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void stop() {
        if (m_player != null) {
            try {
                if (this.m_currentPosition != 0) {
                    AppPreference.setPlayPosition(this.m_currentPosition);
                }
                this.m_currentItem.setPlayStatue(0);
                m_player.reset();
                m_player.release();
                m_player = null;
                this.m_currentState = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearPlay() {
        if (m_player != null) {
            try {
                m_player.reset();
                m_player.release();
                m_player = null;
                this.m_currentState = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void seek(String position) {
        try {
            long miliSec = ModelUtil.fromTimeString(position) * 1000;
            m_player.seekTo((int) miliSec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setVolume(int newVolume) {
        try {
            this.m_audioManager.setStreamVolume(3, newVolume, 16);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public int getVolume() {
        return this.m_audioManager.getStreamVolume(3);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public int getMaxVolume() {
        return this.m_maxVolume;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void addListener(DMRProcessor.DMRProcessorListener listener) {
        synchronized (this.m_listeners) {
            if (this.m_listeners.contains(listener)) {
                this.m_listeners.remove(listener);
            }
            this.m_listeners.add(listener);
            setRunning(true);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void removeListener(DMRProcessor.DMRProcessorListener listener) {
        synchronized (this.m_listeners) {
            this.m_listeners.remove(listener);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void dispose() {
        synchronized (this.m_listeners) {
            this.m_listeners.clear();
        }
        stop();
        this.isSeeked = true;
        if (this.m_updateThread != null) {
            this.m_updateThread.stopThread();
            this.m_updateThread = null;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public String getName() {
        return "Local Player";
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public String getCurrentTrackURI() {
        return this.m_currentItem != null ? this.m_currentItem.getData() instanceof DIDLObject ? HttpServerUtil.getUrlFrom((DIDLObject) this.m_currentItem.getData()) : this.m_currentItem.getUrl() : "";
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setRunning(boolean running) {
        if (this.m_updateThread != null) {
            this.m_updateThread.stopThread();
            this.m_updateThread = null;
        }
        if (running) {
            this.m_updateThread = new UpdateThread();
            this.m_updateThread.start();
        }
    }

    public void fireUpdatePositionEvent(long current, long max) {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onUpdatePosition(current, max);
            }
        }
    }

    public void fireOnStopedEvent() {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onStoped();
            }
        }
    }

    public void fireOnCompleteEvent() {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onCompleted();
            }
        }
    }

    public void fireOnPausedEvent() {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onPaused();
            }
        }
    }

    public void fireOnPlayingEvent() {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onPlaying();
            }
        }
    }

    public void setHolder(SurfaceHolder holder, LocalMediaPlayer.ViewChange playBack) {
        this.m_holder = holder;
        if (m_player != null) {
            m_player.setDisplay(holder, playBack);
            m_player.scaleContent();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public PlaylistItem getCurrentItem() {
        return this.m_currentItem;
    }
}
