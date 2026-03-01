package mktvsmart.screen.json.parser;

import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseRepeatLogin implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) throws JSONException {
        int reason = 0;
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("reason ")) {
                reason = root.getInt("reason");
            }
            return Integer.valueOf(reason);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
