package com.google.android.gms.auth;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class AccountChangeEventsRequest implements SafeParcelable {
    public static final AccountChangeEventsRequestCreator CREATOR = new AccountChangeEventsRequestCreator();
    String Dd;
    final int Di;
    int Dl;

    public AccountChangeEventsRequest() {
        this.Di = 1;
    }

    AccountChangeEventsRequest(int version, int eventIndex, String accountName) {
        this.Di = version;
        this.Dl = eventIndex;
        this.Dd = accountName;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAccountName() {
        return this.Dd;
    }

    public int getEventIndex() {
        return this.Dl;
    }

    public AccountChangeEventsRequest setAccountName(String accountName) {
        this.Dd = accountName;
        return this;
    }

    public AccountChangeEventsRequest setEventIndex(int eventIndex) {
        this.Dl = eventIndex;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        AccountChangeEventsRequestCreator.a(this, dest, flags);
    }
}
