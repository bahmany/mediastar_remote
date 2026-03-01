package org.teleal.cling.support.model;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* loaded from: classes.dex */
public class ContentItem {
    private DIDLObject content;
    private Device device;
    private String id;
    private Boolean isContainer = true;
    private Service service;

    public ContentItem(Container container, Service service) {
        this.service = service;
        this.content = container;
        this.id = container.getId();
    }

    public ContentItem(Item item, Service service) {
        this.service = service;
        this.content = item;
        this.id = item.getId();
    }

    public Container getContainer() {
        if (this.isContainer.booleanValue()) {
            return (Container) this.content;
        }
        return null;
    }

    public Item getItem() {
        if (this.isContainer.booleanValue()) {
            return null;
        }
        return (Item) this.content;
    }

    public Service getService() {
        return this.service;
    }

    public Boolean isContainer() {
        return this.isContainer;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentItem that = (ContentItem) o;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.content.hashCode();
    }

    public String toString() {
        return this.content.getTitle();
    }
}
