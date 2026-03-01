package mktvsmart.screen.json.parser;

import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.gchat.bean.GsChatUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseUserList implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) throws JSONException {
        List<GsChatUser> userList = new ArrayList<>();
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            new JSONArray();
            if (root.has("user_list")) {
                JSONArray userListArray = root.getJSONArray("user_list");
                for (int i = 0; i < userListArray.length(); i++) {
                    JSONObject temp = (JSONObject) userListArray.get(i);
                    if (temp.has("username") && temp.has("user_id")) {
                        GsChatUser tempUser = new GsChatUser();
                        tempUser.setUsername(temp.getString("username"));
                        tempUser.setUserID(temp.getInt("user_id"));
                        userList.add(tempUser);
                    }
                }
                return userList;
            }
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
