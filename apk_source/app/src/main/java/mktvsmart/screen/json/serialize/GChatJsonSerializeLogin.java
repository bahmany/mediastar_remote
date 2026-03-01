package mktvsmart.screen.json.serialize;

import com.google.android.gms.fitness.FitnessActivities;
import java.io.UnsupportedEncodingException;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeLogin implements JsonSerialize {
    private String mMac;

    public GChatJsonSerializeLogin(String mac) {
        this.mMac = mac;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        JSONObject phoneInfo = new JSONObject();
        try {
            phoneInfo.put("mac", this.mMac);
            root.put(FitnessActivities.OTHER_STRING, phoneInfo);
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 3);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
