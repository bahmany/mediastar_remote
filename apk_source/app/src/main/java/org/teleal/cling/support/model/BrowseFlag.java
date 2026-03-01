package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum BrowseFlag {
    METADATA("BrowseMetadata"),
    DIRECT_CHILDREN("BrowseDirectChildren");

    private String protocolString;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static BrowseFlag[] valuesCustom() {
        BrowseFlag[] browseFlagArrValuesCustom = values();
        int length = browseFlagArrValuesCustom.length;
        BrowseFlag[] browseFlagArr = new BrowseFlag[length];
        System.arraycopy(browseFlagArrValuesCustom, 0, browseFlagArr, 0, length);
        return browseFlagArr;
    }

    BrowseFlag(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.protocolString;
    }

    public static BrowseFlag valueOrNullOf(String s) {
        for (BrowseFlag browseFlag : valuesCustom()) {
            if (browseFlag.toString().equals(s)) {
                return browseFlag;
            }
        }
        return null;
    }
}
