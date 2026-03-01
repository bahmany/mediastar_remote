package org.teleal.cling.support.contentdirectory.ui;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Service;

/* loaded from: classes.dex */
public class ContentTreeExpandListener implements TreeWillExpandListener {
    protected final ContentBrowseActionCallbackCreator actionCreator;
    protected final ControlPoint controlPoint;
    protected final Service service;
    protected final DefaultTreeModel treeModel;

    public ContentTreeExpandListener(ControlPoint controlPoint, Service service, DefaultTreeModel treeModel, ContentBrowseActionCallbackCreator actionCreator) {
        this.controlPoint = controlPoint;
        this.service = service;
        this.treeModel = treeModel;
        this.actionCreator = actionCreator;
    }

    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        treeNode.removeAllChildren();
        this.treeModel.nodeStructureChanged(treeNode);
        ActionCallback callback = this.actionCreator.createContentBrowseActionCallback(this.service, this.treeModel, treeNode);
        this.controlPoint.execute(callback);
    }

    public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
    }
}
