package com.hisilicon.dlna.dmc.processor.interfaces;

import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;

/* loaded from: classes.dex */
public interface DMRProcessor {

    public interface DMRProcessorListener {
        void onActionFail(Action action, UpnpResponse upnpResponse, String str);

        void onCompleted();

        void onErrorEvent(String str);

        void onMiddleEndEvent();

        void onPaused();

        void onPlaying();

        void onStoped();

        void onUpdatePosition(long j, long j2);
    }

    void addListener(DMRProcessorListener dMRProcessorListener);

    void dispose();

    PlaylistItem getCurrentItem();

    String getCurrentTrackURI();

    int getMaxVolume();

    String getName();

    int getVolume();

    void pause();

    void play();

    void removeListener(DMRProcessorListener dMRProcessorListener);

    void seek(String str);

    void setRunning(boolean z);

    void setURIandPlay(PlaylistItem playlistItem);

    void setVolume(int i);

    void stop();
}
