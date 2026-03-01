package org.teleal.cling.support.renderingcontrol;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class RenderingControlException extends ActionException {
    public RenderingControlException(int errorCode, String message) {
        super(errorCode, message);
    }

    public RenderingControlException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public RenderingControlException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RenderingControlException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RenderingControlException(RenderingControlErrorCode errorCode, String message) {
        super(errorCode.getCode(), String.valueOf(errorCode.getDescription()) + ". " + message + ".");
    }

    public RenderingControlException(RenderingControlErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
