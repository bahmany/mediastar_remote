package org.teleal.cling.support.model;

import java.util.Map;
import org.teleal.cling.model.action.ActionArgumentValue;

/* loaded from: classes.dex */
public class TransportInfo {
    private String currentSpeed;
    private TransportState currentTransportState;
    private TransportStatus currentTransportStatus;

    public TransportInfo() {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
    }

    public TransportInfo(Map<String, ActionArgumentValue> args) {
        this(TransportState.valueOrCustomOf((String) args.get("CurrentTransportState").getValue()), TransportStatus.valueOrCustomOf((String) args.get("CurrentTransportStatus").getValue()), (String) args.get("CurrentSpeed").getValue());
    }

    public TransportInfo(TransportState currentTransportState) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
    }

    public TransportInfo(TransportState currentTransportState, String currentSpeed) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentSpeed = currentSpeed;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus, String currentSpeed) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
        this.currentSpeed = currentSpeed;
    }

    public TransportState getCurrentTransportState() {
        return this.currentTransportState;
    }

    public TransportStatus getCurrentTransportStatus() {
        return this.currentTransportStatus;
    }

    public String getCurrentSpeed() {
        return this.currentSpeed;
    }
}
