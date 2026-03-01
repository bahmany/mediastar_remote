package mktvsmart.screen.vlc;

import android.support.v4.internal.view.SupportMenu;
import android.util.Log;
import android.view.View;
import java.io.InputStream;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import mktvsmart.screen.R;

/* compiled from: LivePlayActivity.java */
/* loaded from: classes.dex */
class GChatControllor {
    private static final String TAG = GChatControllor.class.getSimpleName();
    private BaseDanmakuParser mDanmakuParser;
    private IDanmakuView mDanmakuView;
    private LivePlayActivity owner;
    private int[] mDanmakuStyle = {1, 1, 1, 4, 4};
    private int[] mDanmakuColor = {-1, SupportMenu.CATEGORY_MASK, -1, -1, -256, SupportMenu.CATEGORY_MASK, -7829368, -256, -16711936, -16776961};

    public GChatControllor(LivePlayActivity owner) {
        this.owner = owner;
        initView();
    }

    public View findViewById(int id) {
        return this.owner.findViewById(id);
    }

    public void setDanmakuViewVisibility(boolean show) {
        if (this.mDanmakuView != null) {
            if (show) {
                this.mDanmakuView.show();
            } else {
                this.mDanmakuView.hide();
            }
        }
    }

    public void changeChatViewVisibility() {
        if (this.owner.mChatView.getVisibility() == 8) {
            this.owner.mChatView.setVisibility(0);
            this.owner.setRequestedOrientation(1);
            this.owner.mChatFragment.setHide(false);
            this.mDanmakuView.hide();
            return;
        }
        if (this.owner.mChatView.getVisibility() == 0) {
            this.owner.mChatView.setVisibility(8);
            this.owner.mChatFragment.setHide(true);
            this.owner.setRequestedOrientation(0);
            this.mDanmakuView.show();
        }
    }

    public boolean isChatViewVisibility() {
        return this.owner.mChatView.getVisibility() == 0;
    }

    private void initView() {
        this.mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
        DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(1, 3.0f);
        DanmakuGlobalConfig.DEFAULT.setDuplicateMergingEnabled(false);
        if (this.mDanmakuView != null) {
            this.mDanmakuParser = createParser(null);
            this.mDanmakuView.setCallback(new DrawHandler.Callback() { // from class: mktvsmart.screen.vlc.GChatControllor.1
                @Override // master.flame.danmaku.controller.DrawHandler.Callback
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override // master.flame.danmaku.controller.DrawHandler.Callback
                public void prepared() {
                    GChatControllor.this.mDanmakuView.start();
                }
            });
            this.mDanmakuView.prepare(this.mDanmakuParser);
            this.mDanmakuView.showFPS(false);
            this.mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() { // from class: mktvsmart.screen.vlc.GChatControllor.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // master.flame.danmaku.danmaku.parser.BaseDanmakuParser
                public Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    public void addDanmaku(boolean islive, String content) {
        BaseDanmaku danmaku = DanmakuFactory.createDanmaku(this.mDanmakuStyle[((int) (Math.random() * 100.0d)) % this.mDanmakuStyle.length]);
        if (danmaku != null && this.mDanmakuView.isShown()) {
            danmaku.text = content;
            danmaku.padding = 5;
            danmaku.priority = (byte) 1;
            danmaku.isLive = islive;
            danmaku.time = this.mDanmakuView.getCurrentTime() + 100;
            danmaku.textSize = 35.0f * (this.mDanmakuParser.getDisplayer().getDensity() - 0.6f);
            danmaku.textColor = this.mDanmakuColor[((int) (Math.random() * 100.0d)) % this.mDanmakuColor.length];
            danmaku.textShadowColor = this.mDanmakuColor[((int) (Math.random() * 100.0d)) % this.mDanmakuColor.length];
            danmaku.borderColor = 0;
            this.mDanmakuView.addDanmaku(danmaku);
            return;
        }
        Log.d(TAG, "Dmaku is null or mDanmakuView is not show");
    }
}
