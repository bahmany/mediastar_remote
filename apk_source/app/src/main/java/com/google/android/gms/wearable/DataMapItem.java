package com.google.android.gms.wearable;

import android.net.Uri;
import com.google.android.gms.internal.pb;
import com.google.android.gms.internal.pc;
import com.google.android.gms.internal.pl;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DataMapItem {
    private final DataMap auM;
    private final Uri mUri;

    private DataMapItem(DataItem source) {
        this.mUri = source.getUri();
        this.auM = a(source.freeze());
    }

    private DataMap a(DataItem dataItem) {
        if (dataItem.getData() == null && dataItem.getAssets().size() > 0) {
            throw new IllegalArgumentException("Cannot create DataMapItem from a DataItem  that wasn't made with DataMapItem.");
        }
        if (dataItem.getData() == null) {
            return new DataMap();
        }
        try {
            ArrayList arrayList = new ArrayList();
            int size = dataItem.getAssets().size();
            for (int i = 0; i < size; i++) {
                DataItemAsset dataItemAsset = dataItem.getAssets().get(Integer.toString(i));
                if (dataItemAsset == null) {
                    throw new IllegalStateException("Cannot find DataItemAsset referenced in data at " + i + " for " + dataItem);
                }
                arrayList.add(Asset.createFromRef(dataItemAsset.getId()));
            }
            return pb.a(new pb.a(pc.n(dataItem.getData()), arrayList));
        } catch (pl e) {
            throw new IllegalStateException("Unable to parse. Not a DataItem.");
        }
    }

    public static DataMapItem fromDataItem(DataItem dataItem) {
        if (dataItem == null) {
            throw new IllegalStateException("provided dataItem is null");
        }
        return new DataMapItem(dataItem);
    }

    public DataMap getDataMap() {
        return this.auM;
    }

    public Uri getUri() {
        return this.mUri;
    }
}
