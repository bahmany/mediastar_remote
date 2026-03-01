package org.teleal.cling.support.model;

import java.util.Map;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public class MediaInfo {
    private String currentURI;
    private String currentURIMetaData;
    private String mediaDuration;
    private String nextURI;
    private String nextURIMetaData;
    private UnsignedIntegerFourBytes numberOfTracks;
    private StorageMedium playMedium;
    private StorageMedium recordMedium;
    private RecordMediumWriteStatus writeStatus;

    public MediaInfo() {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
    }

    public MediaInfo(Map<String, ActionArgumentValue> args) {
        this((String) args.get("CurrentURI").getValue(), (String) args.get("CurrentURIMetaData").getValue(), (String) args.get("NextURI").getValue(), (String) args.get("NextURIMetaData").getValue(), (UnsignedIntegerFourBytes) args.get("NrTracks").getValue(), (String) args.get("MediaDuration").getValue(), StorageMedium.valueOrVendorSpecificOf((String) args.get("PlayMedium").getValue()), StorageMedium.valueOrVendorSpecificOf((String) args.get("RecordMedium").getValue()), RecordMediumWriteStatus.valueOrUnknownOf((String) args.get("WriteStatus").getValue()));
    }

    public MediaInfo(String currentURI, String currentURIMetaData) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
    }

    public MediaInfo(String currentURI, String currentURIMetaData, UnsignedIntegerFourBytes numberOfTracks, String mediaDuration, StorageMedium playMedium) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }

    public MediaInfo(String currentURI, String currentURIMetaData, UnsignedIntegerFourBytes numberOfTracks, String mediaDuration, StorageMedium playMedium, StorageMedium recordMedium, RecordMediumWriteStatus writeStatus) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }

    public MediaInfo(String currentURI, String currentURIMetaData, String nextURI, String nextURIMetaData, UnsignedIntegerFourBytes numberOfTracks, String mediaDuration, StorageMedium playMedium) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }

    public MediaInfo(String currentURI, String currentURIMetaData, String nextURI, String nextURIMetaData, UnsignedIntegerFourBytes numberOfTracks, String mediaDuration, StorageMedium playMedium, StorageMedium recordMedium, RecordMediumWriteStatus writeStatus) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }

    public String getCurrentURI() {
        return this.currentURI;
    }

    public String getCurrentURIMetaData() {
        return this.currentURIMetaData;
    }

    public String getNextURI() {
        return this.nextURI;
    }

    public String getNextURIMetaData() {
        return this.nextURIMetaData;
    }

    public UnsignedIntegerFourBytes getNumberOfTracks() {
        return this.numberOfTracks;
    }

    public String getMediaDuration() {
        return this.mediaDuration;
    }

    public StorageMedium getPlayMedium() {
        return this.playMedium;
    }

    public StorageMedium getRecordMedium() {
        return this.recordMedium;
    }

    public RecordMediumWriteStatus getWriteStatus() {
        return this.writeStatus;
    }

    public void setMediaInfo(String s) {
        this.mediaDuration = s;
    }
}
