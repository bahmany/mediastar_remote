package mktvsmart.screen.json.serialize;

import mktvsmart.screen.gchat.client.TransmissionPackage;

/* loaded from: classes.dex */
public class GChatJsonSerializeCommand implements JsonSerialize {
    private short mCommandType;

    public GChatJsonSerializeCommand(short commandType) {
        this.mCommandType = commandType;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() {
        TransmissionPackage transmissionPackage = new TransmissionPackage();
        transmissionPackage.setType(this.mCommandType);
        return transmissionPackage;
    }
}
