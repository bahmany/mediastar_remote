package org.slf4j.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.slf4j.Marker;

/* loaded from: classes.dex */
public class BasicMarker implements Marker {
    private static final long serialVersionUID = 1803952589649545191L;
    private final String name;
    private List<Marker> refereceList;
    private static String OPEN = "[ ";
    private static String CLOSE = " ]";
    private static String SEP = ", ";

    BasicMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }

    @Override // org.slf4j.Marker
    public String getName() {
        return this.name;
    }

    @Override // org.slf4j.Marker
    public synchronized void add(Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
        }
        if (!contains(reference) && !reference.contains(this)) {
            if (this.refereceList == null) {
                this.refereceList = new Vector();
            }
            this.refereceList.add(reference);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0010  */
    @Override // org.slf4j.Marker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean hasReferences() {
        /*
            r1 = this;
            monitor-enter(r1)
            java.util.List<org.slf4j.Marker> r0 = r1.refereceList     // Catch: java.lang.Throwable -> L12
            if (r0 == 0) goto L10
            java.util.List<org.slf4j.Marker> r0 = r1.refereceList     // Catch: java.lang.Throwable -> L12
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L12
            if (r0 <= 0) goto L10
            r0 = 1
        Le:
            monitor-exit(r1)
            return r0
        L10:
            r0 = 0
            goto Le
        L12:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.slf4j.helpers.BasicMarker.hasReferences():boolean");
    }

    @Override // org.slf4j.Marker
    public boolean hasChildren() {
        return hasReferences();
    }

    @Override // org.slf4j.Marker
    public synchronized Iterator<Marker> iterator() {
        return this.refereceList != null ? this.refereceList.iterator() : Collections.EMPTY_LIST.iterator();
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x001f, code lost:
    
        r5.refereceList.remove(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0024, code lost:
    
        r3 = true;
     */
    @Override // org.slf4j.Marker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean remove(org.slf4j.Marker r6) {
        /*
            r5 = this;
            r3 = 0
            monitor-enter(r5)
            java.util.List<org.slf4j.Marker> r4 = r5.refereceList     // Catch: java.lang.Throwable -> L29
            if (r4 != 0) goto L8
        L6:
            monitor-exit(r5)
            return r3
        L8:
            java.util.List<org.slf4j.Marker> r4 = r5.refereceList     // Catch: java.lang.Throwable -> L29
            int r2 = r4.size()     // Catch: java.lang.Throwable -> L29
            r0 = 0
        Lf:
            if (r0 >= r2) goto L6
            java.util.List<org.slf4j.Marker> r4 = r5.refereceList     // Catch: java.lang.Throwable -> L29
            java.lang.Object r1 = r4.get(r0)     // Catch: java.lang.Throwable -> L29
            org.slf4j.Marker r1 = (org.slf4j.Marker) r1     // Catch: java.lang.Throwable -> L29
            boolean r4 = r6.equals(r1)     // Catch: java.lang.Throwable -> L29
            if (r4 == 0) goto L26
            java.util.List<org.slf4j.Marker> r3 = r5.refereceList     // Catch: java.lang.Throwable -> L29
            r3.remove(r0)     // Catch: java.lang.Throwable -> L29
            r3 = 1
            goto L6
        L26:
            int r0 = r0 + 1
            goto Lf
        L29:
            r3 = move-exception
            monitor-exit(r5)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.slf4j.helpers.BasicMarker.remove(org.slf4j.Marker):boolean");
    }

    @Override // org.slf4j.Marker
    public boolean contains(Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (equals(other)) {
            return true;
        }
        if (hasReferences()) {
            for (int i = 0; i < this.refereceList.size(); i++) {
                Marker ref = this.refereceList.get(i);
                if (ref.contains(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // org.slf4j.Marker
    public boolean contains(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (hasReferences()) {
            for (int i = 0; i < this.refereceList.size(); i++) {
                Marker ref = this.refereceList.get(i);
                if (ref.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // org.slf4j.Marker
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Marker)) {
            return false;
        }
        Marker other = (Marker) obj;
        return this.name.equals(other.getName());
    }

    @Override // org.slf4j.Marker
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        if (!hasReferences()) {
            return getName();
        }
        Iterator<Marker> it = iterator();
        StringBuffer sb = new StringBuffer(getName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            Marker reference = it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
                sb.append(SEP);
            }
        }
        sb.append(CLOSE);
        return sb.toString();
    }
}
