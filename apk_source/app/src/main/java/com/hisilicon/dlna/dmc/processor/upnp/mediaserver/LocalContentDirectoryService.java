package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import android.util.Log;
import java.util.List;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* loaded from: classes.dex */
public class LocalContentDirectoryService extends AbstractContentDirectoryService {
    private static final String LOGTAG = LocalContentDirectoryService.class.getSimpleName();

    @Override // org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby) throws ContentDirectoryException {
        try {
            DIDLContent didlContent = new DIDLContent();
            if (0 == 0) {
                return new BrowseResult(new DIDLParser().generate(didlContent), 0L, 0L);
            }
            ContentNode contentNode = ContentTree.getNode(objectID);
            Log.v(LOGTAG, "someone's browsing id: " + objectID);
            if (contentNode == null) {
                return new BrowseResult(new DIDLParser().generate(didlContent), 0L, 0L);
            }
            if (contentNode.isItem()) {
                didlContent.addItem(contentNode.getItem());
                Log.v(LOGTAG, "returing item: " + contentNode.getItem().getTitle());
                return new BrowseResult(new DIDLParser().generate(didlContent), 1L, 1L);
            }
            if (browseFlag == BrowseFlag.METADATA) {
                didlContent.addContainer(contentNode.getContainer());
                Log.v(LOGTAG, "returning metadata of container: " + contentNode.getContainer().getTitle());
                return new BrowseResult(new DIDLParser().generate(didlContent), 1L, 1L);
            }
            int[] resCount = getResultContent(firstResult, maxResults, didlContent, contentNode);
            return new BrowseResult(new DIDLParser().generate(didlContent), resCount[0], resCount[1]);
        } catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
        }
    }

    private int[] getResultContent(long firstResult, long maxResults, DIDLContent didlContent, ContentNode contentNode) {
        int[] res = new int[2];
        Container containers = contentNode.getContainer();
        int totalMatches = containers.getChildCount().intValue();
        int toIndex = (maxResults != 0 && firstResult + maxResults < ((long) totalMatches)) ? (int) (firstResult + maxResults) : totalMatches;
        List<Item> itemList = containers.getItems();
        int itemSize = itemList.size();
        List<Container> containerList = containers.getContainers();
        if (toIndex <= itemSize) {
            for (Item item : itemList.subList((int) firstResult, toIndex)) {
                didlContent.addItem(item);
                res[0] = res[0] + 1;
            }
        } else if (firstResult >= itemSize) {
            for (Container container : containerList.subList((int) firstResult, toIndex)) {
                didlContent.addContainer(container);
                res[0] = res[0] + 1;
            }
        } else {
            for (Item item2 : itemList.subList((int) firstResult, itemSize)) {
                didlContent.addItem(item2);
                res[0] = res[0] + 1;
            }
            for (Container container2 : containerList.subList(0, (int) (maxResults - (itemSize - firstResult)))) {
                didlContent.addContainer(container2);
                res[0] = res[0] + 1;
            }
        }
        res[1] = totalMatches;
        return res;
    }

    @Override // org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService
    public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy) throws ContentDirectoryException {
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }
}
