package com.google.android.gms.games.internal.notification;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;
import com.google.android.gms.common.internal.m;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;

/* loaded from: classes.dex */
public final class GameNotificationRef extends d implements GameNotification {
    GameNotificationRef(DataHolder holder, int dataRow) {
        super(holder, dataRow);
    }

    public long getId() {
        return getLong("_id");
    }

    public String getText() {
        return getString("text");
    }

    public String getTitle() {
        return getString("title");
    }

    public int getType() {
        return getInteger(PlaylistSQLiteHelper.COL_TYPE);
    }

    public String li() {
        return getString("notification_id");
    }

    public String lj() {
        return getString("ticker");
    }

    public String lk() {
        return getString("coalesced_text");
    }

    public boolean ll() {
        return getInteger("acknowledged") > 0;
    }

    public boolean lm() {
        return getInteger("alert_level") == 0;
    }

    public String toString() {
        return m.h(this).a("Id", Long.valueOf(getId())).a("NotificationId", li()).a("Type", Integer.valueOf(getType())).a("Title", getTitle()).a("Ticker", lj()).a("Text", getText()).a("CoalescedText", lk()).a("isAcknowledged", Boolean.valueOf(ll())).a("isSilent", Boolean.valueOf(lm())).toString();
    }
}
