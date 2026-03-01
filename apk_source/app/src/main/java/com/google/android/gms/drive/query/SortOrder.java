package com.google.android.gms.drive.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.metadata.SortableMetadataField;
import com.google.android.gms.drive.query.internal.FieldWithSortOrder;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class SortOrder implements SafeParcelable {
    public static final Parcelable.Creator<SortOrder> CREATOR = new b();
    final int BR;
    final List<FieldWithSortOrder> QA;
    final boolean QB;

    public static class Builder {
        private final List<FieldWithSortOrder> QA = new ArrayList();
        private boolean QB = false;

        public Builder addSortAscending(SortableMetadataField sortField) {
            this.QA.add(new FieldWithSortOrder(sortField.getName(), true));
            return this;
        }

        public Builder addSortDescending(SortableMetadataField sortField) {
            this.QA.add(new FieldWithSortOrder(sortField.getName(), false));
            return this;
        }

        public SortOrder build() {
            return new SortOrder(this.QA, this.QB);
        }
    }

    SortOrder(int versionCode, List<FieldWithSortOrder> sortingFields, boolean sortFolderFirst) {
        this.BR = versionCode;
        this.QA = sortingFields;
        this.QB = sortFolderFirst;
    }

    private SortOrder(List<FieldWithSortOrder> sortingFields, boolean sortFolderFirst) {
        this(1, sortingFields, sortFolderFirst);
    }

    /* synthetic */ SortOrder(List x0, boolean x1, AnonymousClass1 x2) {
        this(x0, x1);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.format(Locale.US, "SortOrder[%s, %s]", TextUtils.join(ClientInfo.SEPARATOR_BETWEEN_VARS, this.QA), Boolean.valueOf(this.QB));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        b.a(this, out, flags);
    }
}
