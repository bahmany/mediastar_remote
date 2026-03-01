package mktvsmart.screen.json.serialize;

import java.io.UnsupportedEncodingException;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeRequestUserList implements JsonSerialize {
    private int mGroupId;
    private GChatLoginInfo mLoginInfo = GChatLoginInfo.getInstance();

    public GChatJsonSerializeRequestUserList(int groupId) {
        this.mGroupId = groupId;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        try {
            root.put("signature", this.mLoginInfo.getSignature());
            root.put("group_id", this.mGroupId);
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 7);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
