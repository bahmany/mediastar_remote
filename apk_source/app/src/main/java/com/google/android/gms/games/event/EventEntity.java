package com.google.android.gms.games.event;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerEntity;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class EventEntity implements SafeParcelable, Event {
    public static final EventEntityCreator CREATOR = new EventEntityCreator();
    private final int BR;
    private final String Tg;
    private final Uri UW;
    private final PlayerEntity VW;
    private final String Vh;
    private final String Wb;
    private final long Wc;
    private final String Wd;
    private final boolean We;
    private final String mName;

    EventEntity(int versionCode, String eventId, String name, String description, Uri iconImageUri, String iconImageUrl, Player player, long value, String formattedValue, boolean isVisible) {
        this.BR = versionCode;
        this.Wb = eventId;
        this.mName = name;
        this.Tg = description;
        this.UW = iconImageUri;
        this.Vh = iconImageUrl;
        this.VW = new PlayerEntity(player);
        this.Wc = value;
        this.Wd = formattedValue;
        this.We = isVisible;
    }

    public EventEntity(Event event) {
        this.BR = 1;
        this.Wb = event.getEventId();
        this.mName = event.getName();
        this.Tg = event.getDescription();
        this.UW = event.getIconImageUri();
        this.Vh = event.getIconImageUrl();
        this.VW = (PlayerEntity) event.getPlayer().freeze();
        this.Wc = event.getValue();
        this.Wd = event.getFormattedValue();
        this.We = event.isVisible();
    }

    static int a(Event event) {
        return m.hashCode(event.getEventId(), event.getName(), event.getDescription(), event.getIconImageUri(), event.getIconImageUrl(), event.getPlayer(), Long.valueOf(event.getValue()), event.getFormattedValue(), Boolean.valueOf(event.isVisible()));
    }

    static boolean a(Event event, Object obj) {
        if (!(obj instanceof Event)) {
            return false;
        }
        if (event == obj) {
            return true;
        }
        Event event2 = (Event) obj;
        return m.equal(event2.getEventId(), event.getEventId()) && m.equal(event2.getName(), event.getName()) && m.equal(event2.getDescription(), event.getDescription()) && m.equal(event2.getIconImageUri(), event.getIconImageUri()) && m.equal(event2.getIconImageUrl(), event.getIconImageUrl()) && m.equal(event2.getPlayer(), event.getPlayer()) && m.equal(Long.valueOf(event2.getValue()), Long.valueOf(event.getValue())) && m.equal(event2.getFormattedValue(), event.getFormattedValue()) && m.equal(Boolean.valueOf(event2.isVisible()), Boolean.valueOf(event.isVisible()));
    }

    static String b(Event event) {
        return m.h(event).a("Id", event.getEventId()).a("Name", event.getName()).a("Description", event.getDescription()).a("IconImageUri", event.getIconImageUri()).a("IconImageUrl", event.getIconImageUrl()).a("Player", event.getPlayer()).a("Value", Long.valueOf(event.getValue())).a("FormattedValue", event.getFormattedValue()).a("isVisible", Boolean.valueOf(event.isVisible())).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Event freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.event.Event
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.games.event.Event
    public void getDescription(CharArrayBuffer dataOut) {
        jv.b(this.Tg, dataOut);
    }

    @Override // com.google.android.gms.games.event.Event
    public String getEventId() {
        return this.Wb;
    }

    @Override // com.google.android.gms.games.event.Event
    public String getFormattedValue() {
        return this.Wd;
    }

    @Override // com.google.android.gms.games.event.Event
    public void getFormattedValue(CharArrayBuffer dataOut) {
        jv.b(this.Wd, dataOut);
    }

    @Override // com.google.android.gms.games.event.Event
    public Uri getIconImageUri() {
        return this.UW;
    }

    @Override // com.google.android.gms.games.event.Event
    public String getIconImageUrl() {
        return this.Vh;
    }

    @Override // com.google.android.gms.games.event.Event
    public String getName() {
        return this.mName;
    }

    @Override // com.google.android.gms.games.event.Event
    public void getName(CharArrayBuffer dataOut) {
        jv.b(this.mName, dataOut);
    }

    @Override // com.google.android.gms.games.event.Event
    public Player getPlayer() {
        return this.VW;
    }

    @Override // com.google.android.gms.games.event.Event
    public long getValue() {
        return this.Wc;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.event.Event
    public boolean isVisible() {
        return this.We;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        EventEntityCreator.a(this, out, flags);
    }
}
