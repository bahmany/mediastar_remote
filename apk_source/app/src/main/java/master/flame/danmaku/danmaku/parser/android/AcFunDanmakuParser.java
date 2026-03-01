package master.flame.danmaku.danmaku.parser.android;

import android.support.v4.view.ViewCompat;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* loaded from: classes.dex */
public class AcFunDanmakuParser extends BaseDanmakuParser {
    @Override // master.flame.danmaku.danmaku.parser.BaseDanmakuParser
    public Danmakus parse() {
        if (this.mDataSource == null || !(this.mDataSource instanceof JSONSource)) {
            return new Danmakus();
        }
        JSONSource jsonSource = (JSONSource) this.mDataSource;
        return doParse(jsonSource.data());
    }

    private Danmakus doParse(JSONArray danmakuListData) throws JSONException, NumberFormatException {
        Danmakus danmakus = new Danmakus();
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus;
        }
        for (int i = 0; i < danmakuListData.length(); i++) {
            try {
                JSONArray danmakuArray = danmakuListData.getJSONArray(i);
                if (danmakuArray != null) {
                    danmakus = _parse(danmakuArray, danmakus);
                }
            } catch (JSONException e) {
            }
        }
        return danmakus;
    }

    private Danmakus _parse(JSONArray jsonArray, Danmakus danmakus) throws JSONException, NumberFormatException {
        int type;
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonArray != null && jsonArray.length() != 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String c = obj.getString("c");
                    String[] values = c.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
                    if (values.length > 0 && (type = Integer.parseInt(values[2])) != 7) {
                        long time = (long) (Float.parseFloat(values[0]) * 1000.0f);
                        int color = Integer.parseInt(values[1]) | ViewCompat.MEASURED_STATE_MASK;
                        float textSize = Float.parseFloat(values[3]);
                        BaseDanmaku item = DanmakuFactory.createDanmaku(type, this.mDisp);
                        if (item != null) {
                            item.time = time;
                            item.textSize = (this.mDispDensity - 0.6f) * textSize;
                            item.textColor = color;
                            item.textShadowColor = color <= -16777216 ? -1 : -16777216;
                            DanmakuFactory.fillText(item, obj.optString(MessageElement.XPATH_PREFIX, "...."));
                            item.index = i;
                            item.setTimer(this.mTimer);
                            danmakus.addItem(item);
                        }
                    }
                } catch (NumberFormatException e) {
                } catch (JSONException e2) {
                }
            }
        }
        return danmakus;
    }
}
