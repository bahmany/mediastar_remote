package mktvsmart.screen.json.parser;

import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.exception.ReportPage;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseNewMessage implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) {
        DataConvertChatMsgModel newMsg = new DataConvertChatMsgModel();
        newMsg.setMsgType(0);
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("user_name")) {
                newMsg.setUsername(root.getString("user_name"));
            }
            if (root.has("user_id")) {
                newMsg.setUserID(root.getInt("user_id"));
            }
            if (root.has("create_time")) {
                newMsg.setTimestamp(root.getLong("create_time"));
            }
            if (root.has(ReportPage.REPORT_CONTENT)) {
                newMsg.setContent(root.getString(ReportPage.REPORT_CONTENT));
                return newMsg;
            }
            return newMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
