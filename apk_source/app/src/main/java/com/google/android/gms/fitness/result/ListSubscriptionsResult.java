package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ListSubscriptionsResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<ListSubscriptionsResult> CREATOR = new e();
    private final int BR;
    private final Status CM;
    private final List<Subscription> UN;

    ListSubscriptionsResult(int versionCode, List<Subscription> subscriptions, Status status) {
        this.BR = versionCode;
        this.UN = subscriptions;
        this.CM = status;
    }

    public ListSubscriptionsResult(List<Subscription> subscriptions, Status status) {
        this.BR = 3;
        this.UN = Collections.unmodifiableList(subscriptions);
        this.CM = (Status) n.b(status, "status");
    }

    public static ListSubscriptionsResult G(Status status) {
        return new ListSubscriptionsResult(Collections.emptyList(), status);
    }

    private boolean b(ListSubscriptionsResult listSubscriptionsResult) {
        return this.CM.equals(listSubscriptionsResult.CM) && m.equal(this.UN, listSubscriptionsResult.UN);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof ListSubscriptionsResult) && b((ListSubscriptionsResult) that));
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    public List<Subscription> getSubscriptions() {
        return this.UN;
    }

    public List<Subscription> getSubscriptions(DataType dataType) {
        ArrayList<Subscription> arrayList = new ArrayList();
        for (Subscription subscription : arrayList) {
            if (subscription.getDataType().equals(dataType)) {
                arrayList.add(subscription);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.UN);
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("dataSets", this.UN).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        e.a(this, dest, flags);
    }
}
