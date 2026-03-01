package mktvsmart.screen.json.serialize;

import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import java.io.UnsupportedEncodingException;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.exception.ReportPage;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.client.TransmissionPackage;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonSerializeChatMsg implements JsonSerialize {
    private DataConvertChatMsgModel mChatMsg;
    private GChatLoginInfo mLoginInfo = GChatLoginInfo.getInstance();
    private int mRoomId;

    public GChatJsonSerializeChatMsg(DataConvertChatMsgModel chatMsg, int roomId) {
        this.mChatMsg = chatMsg;
        this.mRoomId = roomId;
    }

    @Override // mktvsmart.screen.json.serialize.JsonSerialize
    public TransmissionPackage serialize() throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();
        try {
            root.put("signature", this.mLoginInfo.getSignature());
            root.put("user_id", this.mLoginInfo.getUserId());
            root.put("to", this.mRoomId);
            root.put(PlaylistSQLiteHelper.COL_TYPE, 0);
            root.put(ReportPage.REPORT_CONTENT, this.mChatMsg.getContent());
            byte[] jsonBody = root.toString().getBytes("UTF-8");
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setType((short) 9);
            transmissionPackage.setBody(jsonBody);
            transmissionPackage.setLength(jsonBody.length + 8);
            return transmissionPackage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
