package org.teleal.cling.support.contentdirectory.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.meta.Service;

/* loaded from: classes.dex */
public interface ContentBrowseActionCallbackCreator {
    ActionCallback createContentBrowseActionCallback(Service service, DefaultTreeModel defaultTreeModel, DefaultMutableTreeNode defaultMutableTreeNode);
}
