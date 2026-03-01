package mktvsmart.screen.json.parser;

import java.net.InetSocketAddress;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GChatJsonParseServerInfo implements JsonParser {
    @Override // mktvsmart.screen.json.parser.JsonParser
    public Object parse(byte[] data) throws JSONException {
        int port = 0;
        String ip = null;
        String body = new String(data);
        try {
            JSONObject root = new JSONObject(body);
            if (root.has("port")) {
                port = root.getInt("port");
            }
            if (root.has("ip")) {
                ip = root.getString("ip");
            }
            if (port == 0 || ip == null) {
                return null;
            }
            return new InetSocketAddress(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
