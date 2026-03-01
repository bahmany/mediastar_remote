package com.hisilicon.multiscreen.protocol.message;

/* loaded from: classes.dex */
public abstract class Request {
    protected MSGHeadObject head = new MSGHeadObject();

    public abstract byte[] getBytes();

    public MSGHeadObject getHead() {
        return this.head;
    }
}
