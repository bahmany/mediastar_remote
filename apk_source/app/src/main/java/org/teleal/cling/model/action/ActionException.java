package org.teleal.cling.model.action;

import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class ActionException extends Exception {
    private int errorCode;

    public ActionException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ActionException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ActionException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDescription());
    }

    public ActionException(ErrorCode errorCode, String message) {
        this(errorCode, message, true);
    }

    public ActionException(ErrorCode errorCode, String message, boolean concatMessages) {
        this(errorCode.getCode(), concatMessages ? String.valueOf(errorCode.getDescription()) + ". " + message + "." : message);
    }

    public ActionException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.getCode(), String.valueOf(errorCode.getDescription()) + ". " + message + ".", cause);
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
