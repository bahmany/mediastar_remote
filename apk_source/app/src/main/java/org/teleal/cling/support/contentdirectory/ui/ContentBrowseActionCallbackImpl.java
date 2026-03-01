package org.teleal.cling.support.contentdirectory.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.ContentItem;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* loaded from: classes.dex */
public class ContentBrowseActionCallbackImpl extends Browse {
    public static final int FAILED = 1002;
    public static final int SUCCESS = 1001;
    private static Logger log = Logger.getLogger(ContentBrowseActionCallbackImpl.class.getName());
    private Activity activity;
    private Container container;
    private Handler handler;
    private List list;
    private Service service;

    public ContentBrowseActionCallbackImpl(Activity activity, Service service, Container container, List list, Handler handler) {
        super(service, container.getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0L, null, new SortCriterion(true, "dc:title"));
        this.activity = activity;
        this.service = service;
        this.container = container;
        this.list = list;
        this.handler = handler;
    }

    @Override // org.teleal.cling.support.contentdirectory.callback.Browse
    public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {
        log.fine("Received browse action DIDL descriptor, creating tree nodes");
        this.activity.runOnUiThread(new Runnable() { // from class: org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallbackImpl.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ContentBrowseActionCallbackImpl.this.list.clear();
                    for (Container childContainer : didl.getContainers()) {
                        ContentBrowseActionCallbackImpl.log.fine("add child container " + childContainer.getTitle());
                        ContentBrowseActionCallbackImpl.this.list.add(new ContentItem(childContainer, ContentBrowseActionCallbackImpl.this.service));
                    }
                    for (Item childItem : didl.getItems()) {
                        ContentBrowseActionCallbackImpl.log.fine("add child item" + childItem.getTitle());
                        ContentBrowseActionCallbackImpl.this.list.add(new ContentItem(childItem, ContentBrowseActionCallbackImpl.this.service));
                    }
                } catch (Exception ex) {
                    ContentBrowseActionCallbackImpl.log.fine("Creating DIDL tree nodes failed: " + ex);
                    actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't create list childs: " + ex, ex));
                    ContentBrowseActionCallbackImpl.this.failure(actionInvocation, null);
                }
            }
        });
        Message message = new Message();
        message.what = 1001;
        this.handler.sendMessage(message);
    }

    @Override // org.teleal.cling.support.contentdirectory.callback.Browse
    public void updateStatus(Browse.Status status) {
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
        Message message = new Message();
        message.what = 1002;
        this.handler.sendMessage(message);
        String s1 = "failure why ? " + defaultMsg;
        Log.e("content brwwser action call back", s1);
    }
}
