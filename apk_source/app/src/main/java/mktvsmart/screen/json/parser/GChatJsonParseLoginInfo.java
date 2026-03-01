package mktvsmart.screen.json.parser;

import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseLoginInfo implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) {
        GChatLoginInfo loginInfo = GChatLoginInfo.getInstance();
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("signature")) {
                loginInfo.setSignature(root.getString("signature"));
            }
            if (root.has("username")) {
                loginInfo.setUsername(root.getString("username"));
            }
            if (root.has("user_id")) {
                loginInfo.setUserId(root.getInt("user_id"));
                return loginInfo;
            }
            return loginInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
