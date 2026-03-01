package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/* loaded from: classes.dex */
public class he implements SafeParcelable {
    public static final hf CREATOR = new hf();
    final int BR;
    final hi[] BS;
    public final String BT;
    public final boolean BU;
    public final Account account;

    public static class a {
        private List<hi> BV;
        private String BW;
        private boolean BX;
        private Account BY;

        public a D(boolean z) {
            this.BX = z;
            return this;
        }

        public a a(hi hiVar) {
            if (this.BV == null) {
                this.BV = new ArrayList();
            }
            this.BV.add(hiVar);
            return this;
        }

        public a ar(String str) {
            this.BW = str;
            return this;
        }

        public he fk() {
            return new he(this.BW, this.BX, this.BY, this.BV != null ? (hi[]) this.BV.toArray(new hi[this.BV.size()]) : null);
        }
    }

    he(int i, hi[] hiVarArr, String str, boolean z, Account account) {
        this.BR = i;
        this.BS = hiVarArr;
        this.BT = str;
        this.BU = z;
        this.account = account;
    }

    he(String str, boolean z, Account account, hi... hiVarArr) {
        this(1, hiVarArr, str, z, account);
        BitSet bitSet = new BitSet(hp.fm());
        for (hi hiVar : hiVarArr) {
            int i = hiVar.Cg;
            if (i != -1) {
                if (bitSet.get(i)) {
                    throw new IllegalArgumentException("Duplicate global search section type " + hp.O(i));
                }
                bitSet.set(i);
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        hf hfVar = CREATOR;
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        hf hfVar = CREATOR;
        hf.a(this, dest, flags);
    }
}
