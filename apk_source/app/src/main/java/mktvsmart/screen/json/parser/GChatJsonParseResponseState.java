package mktvsmart.screen.json.parser;

import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseResponseState implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) throws JSONException {
        int state = 0;
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("result")) {
                state = root.getInt("result");
            }
            return Integer.valueOf(state);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
