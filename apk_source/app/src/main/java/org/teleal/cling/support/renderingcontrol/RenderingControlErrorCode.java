package org.teleal.cling.support.renderingcontrol;

/* loaded from: classes.dex */
public enum RenderingControlErrorCode {
    INVALID_PRESET_NAME(701, "The specified name is not a valid preset name"),
    INVALID_INSTANCE_ID(702, "The specified instanceID is invalid for this RenderingControl");

    private int code;
    private String description;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static RenderingControlErrorCode[] valuesCustom() {
        RenderingControlErrorCode[] renderingControlErrorCodeArrValuesCustom = values();
        int length = renderingControlErrorCodeArrValuesCustom.length;
        RenderingControlErrorCode[] renderingControlErrorCodeArr = new RenderingControlErrorCode[length];
        System.arraycopy(renderingControlErrorCodeArrValuesCustom, 0, renderingControlErrorCodeArr, 0, length);
        return renderingControlErrorCodeArr;
    }

    RenderingControlErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public static RenderingControlErrorCode getByCode(int code) {
        for (RenderingControlErrorCode errorCode : valuesCustom()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
