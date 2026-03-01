package com.hisilicon.dlna.dmc.processor.interfaces;

import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import java.util.List;
import org.teleal.cling.support.model.DIDLObject;

/* loaded from: classes.dex */
public interface PlaylistProcessor {

    public enum ChangeMode {
        NEXT,
        PREV,
        RANDOM,
        UNKNOW;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ChangeMode[] valuesCustom() {
            ChangeMode[] changeModeArrValuesCustom = values();
            int length = changeModeArrValuesCustom.length;
            ChangeMode[] changeModeArr = new ChangeMode[length];
            System.arraycopy(changeModeArrValuesCustom, 0, changeModeArr, 0, length);
            return changeModeArr;
        }
    }

    public interface PlaylistListener {
        void onItemChanged(PlaylistItem playlistItem, ChangeMode changeMode);
    }

    PlaylistItem addDIDLObject(DIDLObject dIDLObject);

    PlaylistItem addItem(PlaylistItem playlistItem);

    void addListener(PlaylistListener playlistListener);

    List<PlaylistItem> getAllItems();

    PlaylistItem getCurrentItem();

    int getCurrentItemIndex();

    PlaylistItem getItemAt(int i);

    List<PlaylistItem> getItemsByViewMode(PlaylistItem.ViewMode viewMode);

    int getSize();

    void next();

    void previous();

    PlaylistItem removeDIDLObject(DIDLObject dIDLObject);

    PlaylistItem removeItem(PlaylistItem playlistItem);

    void removeListener(PlaylistListener playlistListener);

    int setCurrentItem(int i);

    int setCurrentItem(PlaylistItem playlistItem);

    void setItems(List<PlaylistItem> list);

    void updateForViewMode();
}
