package org.teleal.cling.support.contentdirectory;

/* loaded from: classes.dex */
public enum ContentDirectoryErrorCode {
    NO_SUCH_OBJECT(701, "The specified ObjectID is invalid"),
    UNSUPPORTED_SORT_CRITERIA(709, "Unsupported or invalid sort criteria"),
    CANNOT_PROCESS(720, "Cannot process the request");

    private int code;
    private String description;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static ContentDirectoryErrorCode[] valuesCustom() {
        ContentDirectoryErrorCode[] contentDirectoryErrorCodeArrValuesCustom = values();
        int length = contentDirectoryErrorCodeArrValuesCustom.length;
        ContentDirectoryErrorCode[] contentDirectoryErrorCodeArr = new ContentDirectoryErrorCode[length];
        System.arraycopy(contentDirectoryErrorCodeArrValuesCustom, 0, contentDirectoryErrorCodeArr, 0, length);
        return contentDirectoryErrorCodeArr;
    }

    ContentDirectoryErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public static ContentDirectoryErrorCode getByCode(int code) {
        for (ContentDirectoryErrorCode errorCode : valuesCustom()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
