package org.teleal.cling.support.contentdirectory.ui;

import javax.swing.JTree;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.container.Container;

/* loaded from: classes.dex */
public abstract class ContentTree extends JTree implements ContentBrowseActionCallbackCreator {
    private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$contentdirectory$callback$Browse$Status;
    protected final Container rootContainer;
    protected final DefaultMutableTreeNode rootNode;

    public abstract void failure(String str);

    static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$contentdirectory$callback$Browse$Status() {
        int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$contentdirectory$callback$Browse$Status;
        if (iArr == null) {
            iArr = new int[Browse.Status.valuesCustom().length];
            try {
                iArr[Browse.Status.LOADING.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Browse.Status.NO_CONTENT.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Browse.Status.OK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$org$teleal$cling$support$contentdirectory$callback$Browse$Status = iArr;
        }
        return iArr;
    }

    public ContentTree(ControlPoint controlPoint, Service service) {
        this.rootContainer = createRootContainer(service);
        this.rootNode = new DefaultMutableTreeNode(this.rootContainer) { // from class: org.teleal.cling.support.contentdirectory.ui.ContentTree.1
            public boolean isLeaf() {
                return false;
            }
        };
        DefaultTreeModel treeModel = new DefaultTreeModel(this.rootNode);
        setModel(treeModel);
        getSelectionModel().setSelectionMode(1);
        addTreeWillExpandListener(createContainerTreeExpandListener(controlPoint, service, treeModel));
        setCellRenderer(createContainerTreeCellRenderer());
        controlPoint.execute(createContentBrowseActionCallback(service, treeModel, getRootNode()));
    }

    public Container getRootContainer() {
        return this.rootContainer;
    }

    public DefaultMutableTreeNode getRootNode() {
        return this.rootNode;
    }

    protected Container createRootContainer(Service service) {
        Container rootContainer = new Container();
        rootContainer.setId("0");
        rootContainer.setTitle("Content Directory on " + service.getDevice().getDisplayString());
        return rootContainer;
    }

    protected TreeWillExpandListener createContainerTreeExpandListener(ControlPoint controlPoint, Service service, DefaultTreeModel treeModel) {
        return new ContentTreeExpandListener(controlPoint, service, treeModel, this);
    }

    protected DefaultTreeCellRenderer createContainerTreeCellRenderer() {
        return new ContentTreeCellRenderer();
    }

    @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallbackCreator
    public ActionCallback createContentBrowseActionCallback(Service service, DefaultTreeModel treeModel, DefaultMutableTreeNode treeNode) {
        return new ContentBrowseActionCallback(service, treeModel, treeNode) { // from class: org.teleal.cling.support.contentdirectory.ui.ContentTree.2
            @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback
            public void updateStatusUI(Browse.Status status, DefaultMutableTreeNode treeNode2, DefaultTreeModel treeModel2) {
                ContentTree.this.updateStatus(status, treeNode2, treeModel2);
            }

            @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback
            public void failureUI(String failureMessage) {
                ContentTree.this.failure(failureMessage);
            }
        };
    }

    public void updateStatus(Browse.Status status, DefaultMutableTreeNode treeNode, DefaultTreeModel treeModel) {
        switch ($SWITCH_TABLE$org$teleal$cling$support$contentdirectory$callback$Browse$Status()[status.ordinal()]) {
            case 1:
            case 2:
                treeNode.removeAllChildren();
                int index = treeNode.getChildCount() <= 0 ? 0 : treeNode.getChildCount();
                treeModel.insertNodeInto(new DefaultMutableTreeNode(status.getDefaultMessage()), treeNode, index);
                treeModel.nodeStructureChanged(treeNode);
                break;
        }
    }
}
