package mktvsmart.screen.json.parser;

import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseRoomInfo implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) throws JSONException {
        int groupId = 0;
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("group_id")) {
                groupId = root.getInt("group_id");
            }
            return Integer.valueOf(groupId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
