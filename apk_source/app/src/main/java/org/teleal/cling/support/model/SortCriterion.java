package org.teleal.cling.support.model;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Marker;

/* loaded from: classes.dex */
public class SortCriterion {
    protected final boolean ascending;
    protected final String propertyName;

    public SortCriterion(boolean ascending, String propertyName) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    public SortCriterion(String criterion) {
        this(criterion.startsWith(Marker.ANY_NON_NULL_MARKER), criterion.substring(1));
        if (!criterion.startsWith("-") && !criterion.startsWith(Marker.ANY_NON_NULL_MARKER)) {
            throw new IllegalArgumentException("Missing sort prefix +/- on criterion: " + criterion);
        }
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public static SortCriterion[] valueOf(String s) {
        if (s == null || s.length() == 0) {
            return new SortCriterion[0];
        }
        List<SortCriterion> list = new ArrayList<>();
        String[] criteria = s.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
        for (String criterion : criteria) {
            list.add(new SortCriterion(criterion.trim()));
        }
        return (SortCriterion[]) list.toArray(new SortCriterion[list.size()]);
    }

    public static String toString(SortCriterion[] criteria) {
        if (criteria == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (SortCriterion sortCriterion : criteria) {
            sb.append(sortCriterion.toString()).append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        }
        if (sb.toString().endsWith(ClientInfo.SEPARATOR_BETWEEN_VARS)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.ascending ? Marker.ANY_NON_NULL_MARKER : "-");
        sb.append(this.propertyName);
        return sb.toString();
    }
}
