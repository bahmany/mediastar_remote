package org.teleal.cling.support.connectionmanager;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class ConnectionManagerException extends ActionException {
    public ConnectionManagerException(int errorCode, String message) {
        super(errorCode, message);
    }

    public ConnectionManagerException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ConnectionManagerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ConnectionManagerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConnectionManagerException(ConnectionManagerErrorCode errorCode, String message) {
        super(errorCode.getCode(), String.valueOf(errorCode.getDescription()) + ". " + message + ".");
    }

    public ConnectionManagerException(ConnectionManagerErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
