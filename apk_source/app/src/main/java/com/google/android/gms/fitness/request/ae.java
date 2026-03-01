package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.Subscription;

/* loaded from: classes.dex */
public class ae implements SafeParcelable {
    public static final Parcelable.Creator<ae> CREATOR = new af();
    private final int BR;
    private final Subscription UH;
    private final boolean UI;

    public static class a {
        private Subscription UH;
        private boolean UI = false;

        public a b(Subscription subscription) {
            this.UH = subscription;
            return this;
        }

        public ae jD() {
            com.google.android.gms.common.internal.n.a(this.UH != null, "Must call setSubscription()");
            return new ae(this);
        }
    }

    ae(int i, Subscription subscription, boolean z) {
        this.BR = i;
        this.UH = subscription;
        this.UI = z;
    }

    private ae(a aVar) {
        this.BR = 1;
        this.UH = aVar.UH;
        this.UI = aVar.UI;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    public Subscription jB() {
        return this.UH;
    }

    public boolean jC() {
        return this.UI;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("subscription", this.UH).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        af.a(this, dest, flags);
    }
}
