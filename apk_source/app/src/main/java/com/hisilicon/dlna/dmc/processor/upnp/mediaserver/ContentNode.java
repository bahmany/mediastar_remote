package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* loaded from: classes.dex */
public class ContentNode {
    private Container container;
    private String fullPath;
    private String id;
    private boolean isItem;
    private Item item;

    public ContentNode(String id, Container container) {
        this.id = id;
        this.container = container;
        this.fullPath = null;
        this.isItem = false;
    }

    public ContentNode(String id, Item item, String fullPath) {
        this.id = id;
        this.item = item;
        this.fullPath = fullPath;
        this.isItem = true;
    }

    public String getId() {
        return this.id;
    }

    public Container getContainer() {
        return this.container;
    }

    public Item getItem() {
        return this.item;
    }

    public String getFullPath() {
        if (!this.isItem || this.fullPath == null) {
            return null;
        }
        return this.fullPath;
    }

    public boolean isItem() {
        return this.isItem;
    }
}
