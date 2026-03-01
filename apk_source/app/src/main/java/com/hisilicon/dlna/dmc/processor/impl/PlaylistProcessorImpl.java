package com.hisilicon.dlna.dmc.processor.impl;

import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor;
import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.item.AudioItem;

/* loaded from: classes.dex */
public class PlaylistProcessorImpl implements PlaylistProcessor {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$ViewMode;
    private static Random RANDOM = new Random(System.currentTimeMillis());
    private int m_currentItemIdx = -1;
    private List<PlaylistItem> m_playlistItems = new ArrayList();
    private List<PlaylistProcessor.PlaylistListener> m_listeners = new ArrayList();

    static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$ViewMode() {
        int[] iArr = $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$ViewMode;
        if (iArr == null) {
            iArr = new int[PlaylistItem.ViewMode.valuesCustom().length];
            try {
                iArr[PlaylistItem.ViewMode.ALL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[PlaylistItem.ViewMode.AUDIO_ONLY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[PlaylistItem.ViewMode.IMAGE_ONLY.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[PlaylistItem.ViewMode.VIDEO_ONLY.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$ViewMode = iArr;
        }
        return iArr;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public int getSize() {
        return this.m_playlistItems.size();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void next() {
        if (AppPreference.getShuffle() && isMusic()) {
            nextShuffle();
        } else {
            nextNormal();
        }
    }

    private boolean isMusic() {
        return getCurrentItem() != null && (getCurrentItem().getData() instanceof AudioItem);
    }

    private void nextNormal() {
        List<PlaylistItem> playlistItems = getAllItems();
        if (playlistItems.size() != 0) {
            int currentIdx = playlistItems.indexOf(this.m_playlistItems.get(this.m_currentItemIdx)) + 1;
            if (currentIdx >= playlistItems.size()) {
                currentIdx = 0;
            }
            this.m_currentItemIdx = this.m_playlistItems.indexOf(playlistItems.get(currentIdx));
            fireOnItemChangedEvent(PlaylistProcessor.ChangeMode.NEXT);
        }
    }

    private void nextShuffle() {
        List<PlaylistItem> playlistItems = getAllItems();
        if (playlistItems.size() != 0) {
            if (playlistItems.size() != 1) {
                int currentIdx = playlistItems.indexOf(this.m_playlistItems.get(this.m_currentItemIdx));
                int newIdx = currentIdx;
                while (newIdx == currentIdx) {
                    newIdx = RANDOM.nextInt(playlistItems.size());
                }
                this.m_currentItemIdx = this.m_playlistItems.indexOf(playlistItems.get(newIdx));
            }
            fireOnItemChangedEvent(PlaylistProcessor.ChangeMode.NEXT);
        }
    }

    private void fireOnItemChangedEvent(PlaylistProcessor.ChangeMode changeMode) {
        if (this.m_currentItemIdx >= 0 && this.m_currentItemIdx < this.m_playlistItems.size()) {
            synchronized (this.m_listeners) {
                for (PlaylistProcessor.PlaylistListener listener : this.m_listeners) {
                    listener.onItemChanged(this.m_playlistItems.get(this.m_currentItemIdx), changeMode);
                }
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void previous() {
        List<PlaylistItem> playlistItems = getAllItems();
        if (playlistItems.size() != 0) {
            int currentIdx = playlistItems.indexOf(this.m_playlistItems.get(this.m_currentItemIdx)) - 1;
            if (currentIdx < 0) {
                currentIdx = playlistItems.size() - 1;
            }
            this.m_currentItemIdx = this.m_playlistItems.indexOf(playlistItems.get(currentIdx));
            fireOnItemChangedEvent(PlaylistProcessor.ChangeMode.PREV);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem getCurrentItem() {
        if (this.m_currentItemIdx != -1 && this.m_playlistItems.size() > 0 && this.m_currentItemIdx < this.m_playlistItems.size()) {
            return this.m_playlistItems.get(this.m_currentItemIdx);
        }
        return null;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public int setCurrentItem(int idx) {
        if (idx < 0 || idx >= this.m_playlistItems.size()) {
            return -1;
        }
        this.m_currentItemIdx = idx;
        fireOnItemChangedEvent(PlaylistProcessor.ChangeMode.UNKNOW);
        return this.m_currentItemIdx;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public int setCurrentItem(PlaylistItem item) {
        int i;
        synchronized (this.m_playlistItems) {
            this.m_currentItemIdx = this.m_playlistItems.indexOf(item);
            fireOnItemChangedEvent(PlaylistProcessor.ChangeMode.UNKNOW);
            i = this.m_currentItemIdx;
        }
        return i;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem addItem(PlaylistItem item) {
        synchronized (this.m_playlistItems) {
            if (!this.m_playlistItems.contains(item)) {
                this.m_playlistItems.add(item);
                if (this.m_playlistItems.size() == 1) {
                    this.m_currentItemIdx = 0;
                }
            }
        }
        return item;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem removeItem(PlaylistItem item) {
        synchronized (this.m_playlistItems) {
            int itemIdx = this.m_playlistItems.indexOf(item);
            if (itemIdx < 0) {
                return null;
            }
            this.m_playlistItems.remove(item);
            DMRProcessor dmrProcessor = UpnpProcessorImpl.getSington().getDMRProcessor();
            if (dmrProcessor != null && dmrProcessor.getCurrentTrackURI().equals(item.getUrl())) {
                dmrProcessor.stop();
            }
            if (itemIdx == this.m_currentItemIdx) {
                this.m_currentItemIdx = 0;
            }
            return item;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void setItems(List<PlaylistItem> list) {
        synchronized (this.m_playlistItems) {
            this.m_currentItemIdx = -1;
            this.m_playlistItems.clear();
            if (list != null) {
                this.m_playlistItems.addAll(list);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public List<PlaylistItem> getAllItems() {
        return this.m_playlistItems;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem addDIDLObject(DIDLObject object) {
        return addItem(PlaylistItem.createFromDLDIObject(object));
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem removeDIDLObject(DIDLObject object) {
        return removeItem(PlaylistItem.createFromDLDIObject(object));
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public PlaylistItem getItemAt(int idx) {
        return this.m_playlistItems.get(idx);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void addListener(PlaylistProcessor.PlaylistListener listener) {
        synchronized (this.m_listeners) {
            if (!this.m_listeners.contains(listener)) {
                this.m_listeners.add(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void removeListener(PlaylistProcessor.PlaylistListener listener) {
        synchronized (this.m_listeners) {
            if (!this.m_listeners.contains(listener)) {
                this.m_listeners.remove(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public int getCurrentItemIndex() {
        return this.m_currentItemIdx;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public List<PlaylistItem> getItemsByViewMode(PlaylistItem.ViewMode viewMode) {
        List<PlaylistItem> result = new ArrayList<>();
        switch ($SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$ViewMode()[viewMode.ordinal()]) {
            case 1:
                return this.m_playlistItems;
            case 2:
                for (PlaylistItem item : this.m_playlistItems) {
                    if (item.getType().equals(PlaylistItem.Type.AUDIO_LOCAL) || item.getType().equals(PlaylistItem.Type.AUDIO_REMOTE)) {
                        result.add(item);
                    }
                }
                return result;
            case 3:
                for (PlaylistItem item2 : this.m_playlistItems) {
                    if (item2.getType().equals(PlaylistItem.Type.VIDEO_LOCAL) || item2.getType().equals(PlaylistItem.Type.VIDEO_REMOTE)) {
                        result.add(item2);
                    }
                }
                return result;
            case 4:
                for (PlaylistItem item3 : this.m_playlistItems) {
                    if (item3.getType().equals(PlaylistItem.Type.IMAGE_LOCAL) || item3.getType().equals(PlaylistItem.Type.IMAGE_REMOTE)) {
                        result.add(item3);
                    }
                }
                return result;
            default:
                return result;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor
    public void updateForViewMode() {
        List<PlaylistItem> playlistItems = getAllItems();
        this.m_currentItemIdx = playlistItems.size() > 0 ? this.m_playlistItems.indexOf(playlistItems.get(0)) : -1;
    }
}
