package mktvsmart.screen.json.serialize;

import java.io.UnsupportedEncodingException;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeRename implements JsonSerialize {
    private GChatLoginInfo mLoginInfo = GChatLoginInfo.getInstance();
    private String mName;

    public GChatJsonSerializeRename(String name) {
        this.mName = name;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        try {
            root.put("signature", this.mLoginInfo.getSignature());
            root.put("name", this.mName);
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 17);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
