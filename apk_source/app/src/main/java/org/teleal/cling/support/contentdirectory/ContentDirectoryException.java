package org.teleal.cling.support.contentdirectory;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class ContentDirectoryException extends ActionException {
    public ContentDirectoryException(int errorCode, String message) {
        super(errorCode, message);
    }

    public ContentDirectoryException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ContentDirectoryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ContentDirectoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ContentDirectoryException(ContentDirectoryErrorCode errorCode, String message) {
        super(errorCode.getCode(), String.valueOf(errorCode.getDescription()) + ". " + message + ".");
    }

    public ContentDirectoryException(ContentDirectoryErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
