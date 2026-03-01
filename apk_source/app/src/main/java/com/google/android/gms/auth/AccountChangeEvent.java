package com.google.android.gms.auth;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import org.teleal.cling.model.ServerClientTokens;

/* loaded from: classes.dex */
public class AccountChangeEvent implements SafeParcelable {
    public static final AccountChangeEventCreator CREATOR = new AccountChangeEventCreator();
    final String Dd;
    final int Di;
    final long Dj;
    final int Dk;
    final int Dl;
    final String Dm;

    AccountChangeEvent(int version, long id, String accountName, int changeType, int eventIndex, String changeData) {
        this.Di = version;
        this.Dj = id;
        this.Dd = (String) n.i(accountName);
        this.Dk = changeType;
        this.Dl = eventIndex;
        this.Dm = changeData;
    }

    public AccountChangeEvent(long id, String accountName, int changeType, int eventIndex, String changeData) {
        this.Di = 1;
        this.Dj = id;
        this.Dd = (String) n.i(accountName);
        this.Dk = changeType;
        this.Dl = eventIndex;
        this.Dm = changeData;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (!(that instanceof AccountChangeEvent)) {
            return false;
        }
        AccountChangeEvent accountChangeEvent = (AccountChangeEvent) that;
        return this.Di == accountChangeEvent.Di && this.Dj == accountChangeEvent.Dj && m.equal(this.Dd, accountChangeEvent.Dd) && this.Dk == accountChangeEvent.Dk && this.Dl == accountChangeEvent.Dl && m.equal(this.Dm, accountChangeEvent.Dm);
    }

    public String getAccountName() {
        return this.Dd;
    }

    public String getChangeData() {
        return this.Dm;
    }

    public int getChangeType() {
        return this.Dk;
    }

    public int getEventIndex() {
        return this.Dl;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.Di), Long.valueOf(this.Dj), this.Dd, Integer.valueOf(this.Dk), Integer.valueOf(this.Dl), this.Dm);
    }

    public String toString() {
        String str = ServerClientTokens.UNKNOWN_PLACEHOLDER;
        switch (this.Dk) {
            case 1:
                str = "ADDED";
                break;
            case 2:
                str = "REMOVED";
                break;
            case 3:
                str = "RENAMED_FROM";
                break;
            case 4:
                str = "RENAMED_TO";
                break;
        }
        return "AccountChangeEvent {accountName = " + this.Dd + ", changeType = " + str + ", changeData = " + this.Dm + ", eventIndex = " + this.Dl + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        AccountChangeEventCreator.a(this, dest, flags);
    }
}
