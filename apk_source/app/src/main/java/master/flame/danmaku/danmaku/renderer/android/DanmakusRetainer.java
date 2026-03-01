package master.flame.danmaku.danmaku.renderer.android;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/* loaded from: classes.dex */
public class DanmakusRetainer {
    private static IDanmakusRetainer rldrInstance = null;
    private static IDanmakusRetainer lrdrInstance = null;
    private static IDanmakusRetainer ftdrInstance = null;
    private static IDanmakusRetainer fbdrInstance = null;

    public interface IDanmakusRetainer {
        void clear();

        void fix(BaseDanmaku baseDanmaku, IDisplayer iDisplayer);
    }

    public static void fix(BaseDanmaku danmaku, IDisplayer disp) {
        FBDanmakusRetainer fBDanmakusRetainer = null;
        int type = danmaku.getType();
        switch (type) {
            case 1:
                if (rldrInstance == null) {
                    rldrInstance = new RLDanmakusRetainer(fBDanmakusRetainer, fBDanmakusRetainer);
                }
                rldrInstance.fix(danmaku, disp);
                break;
            case 4:
                if (fbdrInstance == null) {
                    fbdrInstance = new FBDanmakusRetainer(fBDanmakusRetainer);
                }
                fbdrInstance.fix(danmaku, disp);
                break;
            case 5:
                if (ftdrInstance == null) {
                    ftdrInstance = new FTDanmakusRetainer(fBDanmakusRetainer, fBDanmakusRetainer);
                }
                ftdrInstance.fix(danmaku, disp);
                break;
            case 6:
                if (lrdrInstance == null) {
                    lrdrInstance = new RLDanmakusRetainer(fBDanmakusRetainer, fBDanmakusRetainer);
                }
                lrdrInstance.fix(danmaku, disp);
                break;
            case 7:
                danmaku.layout(disp, 0.0f, 0.0f);
                break;
        }
    }

    public static void clear() {
        if (rldrInstance != null) {
            rldrInstance.clear();
        }
        if (lrdrInstance != null) {
            lrdrInstance.clear();
        }
        if (ftdrInstance != null) {
            ftdrInstance.clear();
        }
        if (fbdrInstance != null) {
            fbdrInstance.clear();
        }
    }

    public static void release() {
        clear();
        rldrInstance = null;
        lrdrInstance = null;
        ftdrInstance = null;
        fbdrInstance = null;
    }

    private static class RLDanmakusRetainer implements IDanmakusRetainer {
        protected boolean mCancelFixingFlag;
        protected Danmakus mVisibleDanmakus;

        private RLDanmakusRetainer() {
            this.mVisibleDanmakus = new Danmakus(1);
            this.mCancelFixingFlag = false;
        }

        /* synthetic */ RLDanmakusRetainer(RLDanmakusRetainer rLDanmakusRetainer) {
            this();
        }

        /* synthetic */ RLDanmakusRetainer(RLDanmakusRetainer rLDanmakusRetainer, RLDanmakusRetainer rLDanmakusRetainer2) {
            this();
        }

        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.IDanmakusRetainer
        public void fix(BaseDanmaku drawItem, IDisplayer disp) {
            float topPos;
            boolean overwriteInsert;
            float topPos2;
            if (!drawItem.isOutside()) {
                boolean shown = drawItem.isShown();
                if (shown) {
                    topPos = 0.0f;
                } else {
                    this.mCancelFixingFlag = false;
                    IDanmakuIterator it = this.mVisibleDanmakus.iterator();
                    BaseDanmaku insertItem = null;
                    BaseDanmaku firstItem = null;
                    BaseDanmaku lastItem = null;
                    BaseDanmaku minRightRow = null;
                    while (!this.mCancelFixingFlag && it.hasNext()) {
                        BaseDanmaku item = it.next();
                        if (item == drawItem) {
                            insertItem = item;
                            lastItem = null;
                            shown = true;
                            overwriteInsert = false;
                            break;
                        }
                        if (firstItem == null) {
                            firstItem = item;
                        }
                        if (drawItem.paintHeight + item.getTop() > disp.getHeight()) {
                            overwriteInsert = true;
                            break;
                        }
                        if (minRightRow == null || minRightRow.getRight() >= item.getRight()) {
                            minRightRow = item;
                        }
                        boolean willHit = DanmakuUtils.willHitInDuration(disp, item, drawItem, drawItem.getDuration(), drawItem.getTimer().currMillisecond);
                        if (!willHit) {
                            insertItem = item;
                            overwriteInsert = false;
                            break;
                        }
                        lastItem = item;
                    }
                    overwriteInsert = false;
                    if (insertItem != null) {
                        if (lastItem != null) {
                            topPos2 = lastItem.getBottom();
                        } else {
                            topPos2 = insertItem.getTop();
                        }
                        if (insertItem != drawItem) {
                            this.mVisibleDanmakus.removeItem(insertItem);
                            shown = false;
                        }
                    } else if (overwriteInsert) {
                        if (minRightRow == null) {
                            topPos2 = 0.0f;
                        } else {
                            topPos2 = minRightRow.getTop();
                            if (minRightRow.paintWidth < drawItem.paintWidth) {
                                this.mVisibleDanmakus.removeItem(minRightRow);
                                shown = false;
                            }
                        }
                    } else if (lastItem != null) {
                        topPos2 = lastItem.getBottom();
                    } else if (firstItem != null) {
                        topPos2 = firstItem.getTop();
                        this.mVisibleDanmakus.removeItem(firstItem);
                        shown = false;
                    } else {
                        topPos2 = 0.0f;
                    }
                    topPos = checkVerticalEdge(overwriteInsert, drawItem, disp, topPos2, firstItem, lastItem);
                    if (topPos == 0.0f && this.mVisibleDanmakus.size() == 0) {
                        shown = false;
                    }
                }
                drawItem.layout(disp, drawItem.getLeft(), topPos);
                if (!shown) {
                    this.mVisibleDanmakus.addItem(drawItem);
                }
            }
        }

        protected float checkVerticalEdge(boolean overwriteInsert, BaseDanmaku drawItem, IDisplayer disp, float topPos, BaseDanmaku firstItem, BaseDanmaku lastItem) {
            if (topPos < 0.0f || ((firstItem != null && firstItem.getTop() > 0.0f) || drawItem.paintHeight + topPos > disp.getHeight())) {
                clear();
                return 0.0f;
            }
            return topPos;
        }

        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.IDanmakusRetainer
        public void clear() {
            this.mCancelFixingFlag = true;
            this.mVisibleDanmakus.clear();
        }
    }

    private static class FTDanmakusRetainer extends RLDanmakusRetainer {
        private FTDanmakusRetainer() {
            super(null);
        }

        /* synthetic */ FTDanmakusRetainer(FTDanmakusRetainer fTDanmakusRetainer) {
            this();
        }

        /* synthetic */ FTDanmakusRetainer(FTDanmakusRetainer fTDanmakusRetainer, FTDanmakusRetainer fTDanmakusRetainer2) {
            this();
        }

        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.RLDanmakusRetainer
        protected float checkVerticalEdge(boolean overwriteInsert, BaseDanmaku drawItem, IDisplayer disp, float topPos, BaseDanmaku firstItem, BaseDanmaku lastItem) {
            if (drawItem.paintHeight + topPos > disp.getHeight()) {
                clear();
                return 0.0f;
            }
            return topPos;
        }
    }

    private static class FBDanmakusRetainer extends FTDanmakusRetainer {
        protected Danmakus mVisibleDanmakus;

        private FBDanmakusRetainer() {
            super(null);
            this.mVisibleDanmakus = new Danmakus(2);
        }

        /* synthetic */ FBDanmakusRetainer(FBDanmakusRetainer fBDanmakusRetainer) {
            this();
        }

        /* JADX WARN: Removed duplicated region for block: B:25:0x0081 A[PHI: r10
  0x0081: PHI (r10v3 'firstItem' master.flame.danmaku.danmaku.model.BaseDanmaku) = 
  (r10v1 'firstItem' master.flame.danmaku.danmaku.model.BaseDanmaku)
  (r10v4 'firstItem' master.flame.danmaku.danmaku.model.BaseDanmaku)
 binds: [B:22:0x0071, B:24:0x007f] A[DONT_GENERATE, DONT_INLINE]] */
        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.RLDanmakusRetainer, master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.IDanmakusRetainer
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void fix(master.flame.danmaku.danmaku.model.BaseDanmaku r17, master.flame.danmaku.danmaku.model.IDisplayer r18) {
            /*
                r16 = this;
                boolean r3 = r17.isOutside()
                if (r3 == 0) goto L7
            L6:
                return
            L7:
                boolean r13 = r17.isShown()
                float r14 = r17.getTop()
                r3 = 0
                int r3 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
                if (r3 >= 0) goto L1f
                int r3 = r18.getHeight()
                float r3 = (float) r3
                r0 = r17
                float r5 = r0.paintHeight
                float r14 = r3 - r5
            L1f:
                r12 = 0
                r10 = 0
                if (r13 != 0) goto L49
                r3 = 0
                r0 = r16
                r0.mCancelFixingFlag = r3
                r0 = r16
                master.flame.danmaku.danmaku.model.android.Danmakus r3 = r0.mVisibleDanmakus
                master.flame.danmaku.danmaku.model.IDanmakuIterator r2 = r3.iterator()
            L30:
                r0 = r16
                boolean r3 = r0.mCancelFixingFlag
                if (r3 != 0) goto L3c
                boolean r3 = r2.hasNext()
                if (r3 != 0) goto L67
            L3c:
                r6 = 0
                r11 = 0
                r5 = r16
                r7 = r17
                r8 = r18
                r9 = r14
                float r14 = r5.checkVerticalEdge(r6, r7, r8, r9, r10, r11)
            L49:
                float r3 = r17.getLeft()
                r0 = r17
                r1 = r18
                r0.layout(r1, r3, r14)
                if (r13 != 0) goto L6
                r0 = r16
                master.flame.danmaku.danmaku.model.android.Danmakus r3 = r0.mVisibleDanmakus
                r3.removeItem(r12)
                r0 = r16
                master.flame.danmaku.danmaku.model.android.Danmakus r3 = r0.mVisibleDanmakus
                r0 = r17
                r3.addItem(r0)
                goto L6
            L67:
                master.flame.danmaku.danmaku.model.BaseDanmaku r4 = r2.next()
                r0 = r17
                if (r4 != r0) goto L71
                r12 = 0
                goto L3c
            L71:
                if (r10 != 0) goto L81
                r10 = r4
                float r3 = r10.getBottom()
                int r5 = r18.getHeight()
                float r5 = (float) r5
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 != 0) goto L3c
            L81:
                r3 = 0
                int r3 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
                if (r3 >= 0) goto L88
                r12 = 0
                goto L3c
            L88:
                long r6 = r17.getDuration()
                master.flame.danmaku.danmaku.model.DanmakuTimer r3 = r17.getTimer()
                long r8 = r3.currMillisecond
                r3 = r18
                r5 = r17
                boolean r15 = master.flame.danmaku.danmaku.util.DanmakuUtils.willHitInDuration(r3, r4, r5, r6, r8)
                if (r15 != 0) goto L9e
                r12 = r4
                goto L3c
            L9e:
                float r3 = r4.getTop()
                r0 = r17
                float r5 = r0.paintHeight
                float r14 = r3 - r5
                goto L30
            */
            throw new UnsupportedOperationException("Method not decompiled: master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.FBDanmakusRetainer.fix(master.flame.danmaku.danmaku.model.BaseDanmaku, master.flame.danmaku.danmaku.model.IDisplayer):void");
        }

        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.FTDanmakusRetainer, master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.RLDanmakusRetainer
        protected float checkVerticalEdge(boolean overwriteInsert, BaseDanmaku drawItem, IDisplayer disp, float topPos, BaseDanmaku firstItem, BaseDanmaku lastItem) {
            if (topPos < 0.0f || (firstItem != null && firstItem.getBottom() != disp.getHeight())) {
                float topPos2 = disp.getHeight() - drawItem.paintHeight;
                clear();
                return topPos2;
            }
            return topPos;
        }

        @Override // master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.RLDanmakusRetainer, master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer.IDanmakusRetainer
        public void clear() {
            this.mCancelFixingFlag = true;
            this.mVisibleDanmakus.clear();
        }
    }
}
