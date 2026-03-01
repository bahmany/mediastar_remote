package com.google.android.gms.auth;

import android.os.Parcel;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class AccountChangeEventsResponse implements SafeParcelable {
    public static final AccountChangeEventsResponseCreator CREATOR = new AccountChangeEventsResponseCreator();
    final int Di;
    final List<AccountChangeEvent> me;

    AccountChangeEventsResponse(int version, List<AccountChangeEvent> events) {
        this.Di = version;
        this.me = (List) n.i(events);
    }

    public AccountChangeEventsResponse(List<AccountChangeEvent> events) {
        this.Di = 1;
        this.me = (List) n.i(events);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<AccountChangeEvent> getEvents() {
        return this.me;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        AccountChangeEventsResponseCreator.a(this, dest, flags);
    }
}
