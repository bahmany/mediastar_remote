package com.hisilicon.dlna.dmc.processor.impl;

import com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* loaded from: classes.dex */
public class DMSProcessorImpl implements DMSProcessor {
    private ControlPoint m_controlPoint;
    private Device m_device;

    public DMSProcessorImpl(Device device, ControlPoint controlPoint) {
        this.m_device = device;
        this.m_controlPoint = controlPoint;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public void dispose() {
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public void browse(String objectID, int startIndex, int maxResult, DMSProcessor.DMSProcessorListner listener) {
        executeBrowse(objectID, startIndex, maxResult, listener);
    }

    private void executeBrowse(String objectID, int startIndex, int defaultMaxResult, DMSProcessor.DMSProcessorListner listener) {
        Service contentDirectoryService = this.m_device.findService(new ServiceType("schemas-upnp-org", "ContentDirectory"));
        if (contentDirectoryService != null) {
            int maxResults = defaultMaxResult + 1;
            Browse browse = new Browse(contentDirectoryService, objectID, BrowseFlag.DIRECT_CHILDREN, "*", startIndex, Long.valueOf(maxResults), new SortCriterion[]{new SortCriterion(true, "dc:title")}) { // from class: com.hisilicon.dlna.dmc.processor.impl.DMSProcessorImpl.1
                private final /* synthetic */ int val$defaultMaxResult;
                private final /* synthetic */ DMSProcessor.DMSProcessorListner val$listener;
                private final /* synthetic */ String val$objectID;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass1(Service contentDirectoryService2, String objectID2, BrowseFlag $anonymous2, String $anonymous3, long startIndex2, Long $anonymous5, SortCriterion[] $anonymous6, int defaultMaxResult2, DMSProcessor.DMSProcessorListner listener2, String objectID22) {
                    super(contentDirectoryService2, objectID22, $anonymous2, $anonymous3, startIndex2, $anonymous5, $anonymous6);
                    i = defaultMaxResult2;
                    dMSProcessorListner = listener2;
                    str = objectID22;
                }

                @Override // org.teleal.cling.support.contentdirectory.callback.Browse
                public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                    try {
                        Map<String, List<? extends DIDLObject>> result = new HashMap<>();
                        List<Container> containers = didlContent.getContainers();
                        List<Item> items = didlContent.getItems();
                        boolean haveNext = false;
                        if (containers.size() > i) {
                            haveNext = true;
                            containers.remove(containers.size() - 1);
                        } else if (items.size() > i || items.size() + containers.size() > i) {
                            haveNext = true;
                            items.remove(items.size() - 1);
                        }
                        result.put("Containers", containers);
                        result.put("Items", items);
                        dMSProcessorListner.onBrowseComplete(str, haveNext, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dMSProcessorListner.onBrowseFail(e.getMessage());
                    }
                }

                @Override // org.teleal.cling.support.contentdirectory.callback.Browse
                public void updateStatus(Browse.Status status) {
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    dMSProcessorListner.onBrowseFail(defaultMsg);
                }
            };
            this.m_controlPoint.execute(browse);
            return;
        }
        listener2.onBrowseFail("");
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.DMSProcessorImpl$1 */
    class AnonymousClass1 extends Browse {
        private final /* synthetic */ int val$defaultMaxResult;
        private final /* synthetic */ DMSProcessor.DMSProcessorListner val$listener;
        private final /* synthetic */ String val$objectID;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Last argument in varargs method is not array: java.lang.String objectID2 */
        AnonymousClass1(Service contentDirectoryService2, String objectID22, BrowseFlag $anonymous2, String $anonymous3, long startIndex2, Long $anonymous5, SortCriterion[] $anonymous6, int defaultMaxResult2, DMSProcessor.DMSProcessorListner listener2, String objectID222) {
            super(contentDirectoryService2, objectID222, $anonymous2, $anonymous3, startIndex2, $anonymous5, $anonymous6);
            i = defaultMaxResult2;
            dMSProcessorListner = listener2;
            str = objectID222;
        }

        @Override // org.teleal.cling.support.contentdirectory.callback.Browse
        public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
            try {
                Map<String, List<? extends DIDLObject>> result = new HashMap<>();
                List<Container> containers = didlContent.getContainers();
                List<Item> items = didlContent.getItems();
                boolean haveNext = false;
                if (containers.size() > i) {
                    haveNext = true;
                    containers.remove(containers.size() - 1);
                } else if (items.size() > i || items.size() + containers.size() > i) {
                    haveNext = true;
                    items.remove(items.size() - 1);
                }
                result.put("Containers", containers);
                result.put("Items", items);
                dMSProcessorListner.onBrowseComplete(str, haveNext, result);
            } catch (Exception e) {
                e.printStackTrace();
                dMSProcessorListner.onBrowseFail(e.getMessage());
            }
        }

        @Override // org.teleal.cling.support.contentdirectory.callback.Browse
        public void updateStatus(Browse.Status status) {
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            dMSProcessorListner.onBrowseFail(defaultMsg);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor
    public boolean back(List<String> m_traceID, int maxResult, DMSProcessor.DMSProcessorListner listener) {
        int traceSize = m_traceID.size();
        if (traceSize <= 2) {
            return false;
        }
        String parentID = m_traceID.get(traceSize - 2);
        m_traceID.remove(m_traceID.size() - 1);
        browse(parentID, 0, maxResult, listener);
        return true;
    }
}
