package mktvsmart.screen.json.serialize;

import java.io.UnsupportedEncodingException;
import mktvsmart.screen.dataconvert.model.DataConvertGChatChannelInfoModel;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeEnterRoom implements JsonSerialize {
    private DataConvertGChatChannelInfoModel mChannelInfo;
    private GChatLoginInfo mLoginInfo = GChatLoginInfo.getInstance();
    private String mlanguage;

    public GChatJsonSerializeEnterRoom(DataConvertGChatChannelInfoModel channelInfo, String language) {
        this.mChannelInfo = channelInfo;
        this.mlanguage = language;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        JSONObject program = new JSONObject();
        try {
            root.put("signature", this.mLoginInfo.getSignature());
            program.put("angle", this.mChannelInfo.getSatAngle());
            program.put("tp", this.mChannelInfo.getTp());
            program.put("sid", this.mChannelInfo.getServiceId());
            program.put("lang", this.mlanguage);
            root.put("program", program);
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 5);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
