package mktvsmart.screen.json.serialize;

import java.io.UnsupportedEncodingException;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.cybergarage.upnp.Action;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeSetBlackList implements JsonSerialize {
    private GChatLoginInfo mLoginInfo = GChatLoginInfo.getInstance();
    private GsChatUser mUser;

    public GChatJsonSerializeSetBlackList(GsChatUser user) {
        this.mUser = user;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        try {
            if (this.mUser.getBlock()) {
                root.put(Action.ELEM_NAME, 1);
            } else {
                root.put(Action.ELEM_NAME, 0);
            }
            root.put("signature", this.mLoginInfo.getSignature());
            root.put("defriend", this.mUser.getUserID());
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 13);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
