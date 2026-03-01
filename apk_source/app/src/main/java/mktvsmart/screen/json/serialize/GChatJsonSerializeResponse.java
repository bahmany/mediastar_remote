package mktvsmart.screen.json.serialize;

import java.io.UnsupportedEncodingException;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeResponse implements JsonSerialize {
    private int mResponseState;
    private short mResponseType;

    public GChatJsonSerializeResponse(short responseType, int responseState) {
        this.mResponseType = responseType;
        this.mResponseState = responseState;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        try {
            root.put("result", this.mResponseState);
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType(this.mResponseType);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
