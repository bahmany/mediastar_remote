package com.hisilicon.dlna.dmc.processor.impl;

import com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.container.Container;

/* loaded from: classes.dex */
public class LocalDMSProcessorImpl implements DMSProcessor {
    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public void browse(String objectID, int startIndex, int maxResult, DMSProcessor.DMSProcessorListner listener) {
        executeBrowse(objectID, startIndex, maxResult, listener);
    }

    private void executeBrowse(String objectID, int startIndex, int defaultMaxResult, DMSProcessor.DMSProcessorListner listener) {
        int endIndex = startIndex + defaultMaxResult;
        try {
            Container container = ContentTree.getContainer(objectID);
            Map<String, List<? extends DIDLObject>> result = new HashMap<>();
            List<? extends DIDLObject> containers = container.getContainers();
            List<? extends DIDLObject> items = container.getItems();
            boolean haveNext = false;
            if (containers.size() > endIndex) {
                haveNext = true;
                containers = containers.subList(startIndex, endIndex);
            } else if (items.size() > endIndex) {
                haveNext = true;
                items = items.subList(startIndex, endIndex);
            } else if (items.size() + containers.size() > endIndex) {
                haveNext = true;
                items = items.subList(0, endIndex - containers.size());
            }
            result.put("Containers", containers);
            result.put("Items", items);
            listener.onBrowseComplete(objectID, haveNext, result);
        } catch (Exception ex) {
            listener.onBrowseFail(ex.getMessage());
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public boolean back(List<String> traceID, int maxResult, DMSProcessor.DMSProcessorListner listener) {
        int traceSize = traceID.size();
        if (traceSize <= 2) {
            return false;
        }
        String parentID = traceID.get(traceSize - 2);
        traceID.remove(traceID.size() - 1);
        browse(parentID, 0, maxResult, listener);
        return true;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public void dispose() {
    }
}
