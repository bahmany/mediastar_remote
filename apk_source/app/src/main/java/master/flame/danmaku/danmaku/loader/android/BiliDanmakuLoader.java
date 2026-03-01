package master.flame.danmaku.danmaku.loader.android;

import java.io.InputStream;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource;

/* loaded from: classes.dex */
public class BiliDanmakuLoader implements ILoader {
    private static BiliDanmakuLoader _instance;
    private AndroidFileSource dataSource;

    private BiliDanmakuLoader() {
    }

    public static BiliDanmakuLoader instance() {
        if (_instance == null) {
            _instance = new BiliDanmakuLoader();
        }
        return _instance;
    }

    @Override // master.flame.danmaku.danmaku.loader.ILoader
    public void load(String uri) throws IllegalDataException {
        try {
            this.dataSource = new AndroidFileSource(uri);
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    @Override // master.flame.danmaku.danmaku.loader.ILoader
    public void load(InputStream stream) {
        this.dataSource = new AndroidFileSource(stream);
    }

    @Override // master.flame.danmaku.danmaku.loader.ILoader
    public AndroidFileSource getDataSource() {
        return this.dataSource;
    }
}
