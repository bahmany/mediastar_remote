package com.hisilicon.dlna.dmc.processor.interfaces;

import java.util.List;
import java.util.Map;
import org.teleal.cling.support.model.DIDLObject;

/* loaded from: classes.dex */
public interface DMSProcessor {

    public interface DMSProcessorListner {
        void onBrowseComplete(String str, boolean z, Map<String, List<? extends DIDLObject>> map);

        void onBrowseFail(String str);
    }

    boolean back(List<String> list, int i, DMSProcessorListner dMSProcessorListner);

    void browse(String str, int i, int i2, DMSProcessorListner dMSProcessorListner);

    void dispose();
}
