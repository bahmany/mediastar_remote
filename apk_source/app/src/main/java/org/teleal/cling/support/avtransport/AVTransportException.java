package org.teleal.cling.support.avtransport;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class AVTransportException extends ActionException {
    public AVTransportException(int errorCode, String message) {
        super(errorCode, message);
    }

    public AVTransportException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public AVTransportException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AVTransportException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AVTransportException(AVTransportErrorCode errorCode, String message) {
        super(errorCode.getCode(), String.valueOf(errorCode.getDescription()) + ". " + message + ".");
    }

    public AVTransportException(AVTransportErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
