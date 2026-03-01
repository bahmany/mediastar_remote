package master.flame.danmaku.danmaku.model;

import master.flame.danmaku.danmaku.parser.DanmakuFactory;

/* loaded from: classes.dex */
public class Danmaku extends BaseDanmaku {
    public Danmaku(String text) {
        DanmakuFactory.fillText(this, text);
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public boolean isShown() {
        return false;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public void layout(IDisplayer displayer, float x, float y) {
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float[] getRectAtTime(IDisplayer displayer, long time) {
        return null;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getLeft() {
        return 0.0f;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getTop() {
        return 0.0f;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getRight() {
        return 0.0f;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getBottom() {
        return 0.0f;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public int getType() {
        return 0;
    }
}
