package org.cybergarage.xml;

import java.util.Vector;
import org.cybergarage.util.Mutex;

/* loaded from: classes.dex */
public class NodeList extends Vector {
    private Mutex mutex = new Mutex();

    public Node getNode(int n) {
        if (n < size()) {
            return (Node) get(n);
        }
        return null;
    }

    public Node getNode(String name) {
        if (name == null) {
            return null;
        }
        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            if (node != null) {
                String nodeName = node.getName();
                if (name.compareTo(nodeName) == 0) {
                    return node;
                }
            }
        }
        return null;
    }

    public Node getEndsWith(String name) {
        String nodeName;
        if (name == null) {
            return null;
        }
        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            if (node != null && (nodeName = node.getName()) != null && nodeName.endsWith(name)) {
                unlock();
                return node;
            }
        }
        return null;
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }
}
