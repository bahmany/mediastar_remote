package com.hisilicon.dlna.dmc.processor.model;

import com.hisilicon.dlna.dmc.gui.customview.AdapterItem;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import com.hisilicon.dlna.dmc.utility.Utility;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
import java.net.URI;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.VideoItem;

/* loaded from: classes.dex */
public class PlaylistItem extends AdapterItem {
    private String id;
    private String metadata;
    private int playStatue;
    private boolean remotePlay;
    private String title;
    private Type type;
    private String url;

    public enum Type {
        VIDEO_LOCAL,
        AUDIO_LOCAL,
        IMAGE_LOCAL,
        AUDIO_REMOTE,
        VIDEO_REMOTE,
        IMAGE_REMOTE,
        UNKNOW;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Type[] valuesCustom() {
            Type[] typeArrValuesCustom = values();
            int length = typeArrValuesCustom.length;
            Type[] typeArr = new Type[length];
            System.arraycopy(typeArrValuesCustom, 0, typeArr, 0, length);
            return typeArr;
        }
    }

    public PlaylistItem() {
        super(null);
        this.playStatue = 0;
        this.remotePlay = false;
        this.id = MessageDef.DEVICE_NAME_PORT;
        this.url = "";
        this.title = "";
        this.type = Type.UNKNOW;
        this.metadata = "";
    }

    public PlaylistItem(Object data) {
        super(data);
        this.playStatue = 0;
        this.remotePlay = false;
        this.id = MessageDef.DEVICE_NAME_PORT;
        this.url = "";
        this.title = "";
        this.type = Type.UNKNOW;
        this.metadata = "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public boolean isRemotePlay() {
        return this.remotePlay;
    }

    public void setRemotePlay(boolean remotePlay) {
        this.remotePlay = remotePlay;
    }

    public String getUrl() {
        if (fromLocalType()) {
            try {
                String decodeUrl = this.url;
                URI uri = URI.create(decodeUrl);
                String media_id = HttpServerUtil.makeMediaId(uri.getPath());
                return HttpServerUtil.createLinkWithId(media_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.url;
    }

    public boolean fromLocalType() {
        return this.type == Type.IMAGE_LOCAL || this.type == Type.VIDEO_LOCAL || this.type == Type.AUDIO_LOCAL;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMetaData() {
        return "".equals(this.metadata) ? Utility.createMetaData(this.title, this.type, this.url) : this.metadata;
    }

    public void setMetaData(String metadata) {
        this.metadata = metadata;
    }

    public int getPlayStatue() {
        return this.playStatue;
    }

    public void setPlayStatue(int playStatue) {
        this.playStatue = playStatue;
    }

    @Override // com.hisilicon.dlna.dmc.gui.customview.AdapterItem
    public boolean equals(Object o) {
        if (!(o instanceof PlaylistItem)) {
            return false;
        }
        PlaylistItem other = (PlaylistItem) o;
        return super.equals(other) && other.url.equals(this.url) && other.type.equals(this.type) && other.title.equals(this.title);
    }

    public static PlaylistItem createFromDLDIObject(DIDLObject object) {
        PlaylistItem item = new PlaylistItem(object);
        item.setId(object.getId());
        item.setTitle(object.getTitle());
        String url = HttpServerUtil.getUrlFrom(object);
        item.setUrl(url);
        boolean isLocal = HttpServerUtil.mediaFromLocal(url);
        if (object instanceof AudioItem) {
            if (isLocal) {
                item.setType(Type.AUDIO_LOCAL);
            } else {
                item.setType(Type.AUDIO_REMOTE);
            }
        } else if (object instanceof VideoItem) {
            if (isLocal) {
                item.setType(Type.VIDEO_LOCAL);
            } else {
                item.setType(Type.VIDEO_REMOTE);
            }
        } else if (object instanceof ImageItem) {
            if (isLocal) {
                item.setType(Type.IMAGE_LOCAL);
            } else {
                item.setType(Type.IMAGE_REMOTE);
            }
        } else {
            item.setType(Type.UNKNOW);
        }
        item.setMetaData(Utility.createMetaData(object));
        return item;
    }

    public enum ViewMode {
        ALL("All items", "All"),
        AUDIO_ONLY("Audio only", "Audio"),
        VIDEO_ONLY("Video only", "Video"),
        IMAGE_ONLY("Image only", "Image");

        private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type;
        String compactString;
        String viewMode;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ViewMode[] valuesCustom() {
            ViewMode[] viewModeArrValuesCustom = values();
            int length = viewModeArrValuesCustom.length;
            ViewMode[] viewModeArr = new ViewMode[length];
            System.arraycopy(viewModeArrValuesCustom, 0, viewModeArr, 0, length);
            return viewModeArr;
        }

        static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type() {
            int[] iArr = $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type;
            if (iArr == null) {
                iArr = new int[Type.valuesCustom().length];
                try {
                    iArr[Type.AUDIO_LOCAL.ordinal()] = 2;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Type.AUDIO_REMOTE.ordinal()] = 4;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Type.IMAGE_LOCAL.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Type.IMAGE_REMOTE.ordinal()] = 6;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Type.UNKNOW.ordinal()] = 7;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Type.VIDEO_LOCAL.ordinal()] = 1;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Type.VIDEO_REMOTE.ordinal()] = 5;
                } catch (NoSuchFieldError e7) {
                }
                $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type = iArr;
            }
            return iArr;
        }

        ViewMode(String viewMode, String compactString) {
            this.viewMode = "";
            this.viewMode = viewMode;
            this.compactString = compactString;
        }

        public String getString() {
            return this.viewMode;
        }

        public String getCompactString() {
            return this.compactString;
        }

        public boolean compatibleWith(Type type) {
            if (equals(ALL)) {
                return true;
            }
            switch ($SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type()[type.ordinal()]) {
                case 1:
                case 5:
                    return equals(VIDEO_ONLY);
                case 2:
                case 4:
                    return equals(AUDIO_ONLY);
                case 3:
                case 6:
                    return equals(IMAGE_ONLY);
                default:
                    return false;
            }
        }
    }
}
