package com.google.android.gms.plus.model.people;

import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.e;
import com.google.android.gms.internal.ny;
import com.google.android.gms.internal.oj;

/* loaded from: classes.dex */
public final class PersonBuffer extends DataBuffer<Person> {
    private final e<ny> any;

    public PersonBuffer(DataHolder dataHolder) {
        super(dataHolder);
        if (dataHolder.gz() == null || !dataHolder.gz().getBoolean("com.google.android.gms.plus.IsSafeParcelable", false)) {
            this.any = null;
        } else {
            this.any = new e<>(dataHolder, ny.CREATOR);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.DataBuffer
    public Person get(int position) {
        return this.any != null ? (Person) this.any.get(position) : new oj(this.IC, position);
    }
}
