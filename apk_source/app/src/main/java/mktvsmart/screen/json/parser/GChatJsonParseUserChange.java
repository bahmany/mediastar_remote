package mktvsmart.screen.json.parser;

import mktvsmart.screen.gchat.bean.GsChatUser;
import org.cybergarage.upnp.Action;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseUserChange implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) {
        GsChatUser user = new GsChatUser();
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has(Action.ELEM_NAME)) {
                user.setState(root.getInt(Action.ELEM_NAME));
            }
            if (root.has("username")) {
                user.setUsername(root.getString("username"));
            }
            if (root.has("user_id")) {
                user.setUserID(root.getInt("user_id"));
                return user;
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
