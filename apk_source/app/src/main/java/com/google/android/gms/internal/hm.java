package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class hm {

    public static class a implements SafeParcelable {
        public static final hn CREATOR = new hn();
        final int BR;
        public final Account Cj;

        public a() {
            this(null);
        }

        a(int i, Account account) {
            this.BR = i;
            this.Cj = account;
        }

        public a(Account account) {
            this(1, account);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            hn hnVar = CREATOR;
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            hn hnVar = CREATOR;
            hn.a(this, out, flags);
        }
    }

    public static class b implements Result, SafeParcelable {
        public static final ho CREATOR = new ho();
        final int BR;
        public Status Ck;
        public List<hs> Cl;

        public b() {
            this.BR = 1;
        }

        b(int i, Status status, List<hs> list) {
            this.BR = i;
            this.Ck = status;
            this.Cl = list;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            ho hoVar = CREATOR;
            return 0;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.Ck;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            ho hoVar = CREATOR;
            ho.a(this, out, flags);
        }
    }
}
