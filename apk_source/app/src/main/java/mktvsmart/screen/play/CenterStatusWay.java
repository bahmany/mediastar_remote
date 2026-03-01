package mktvsmart.screen.play;

import android.view.KeyEvent;

/* loaded from: classes.dex */
public interface CenterStatusWay {
    void init(boolean z);

    boolean keyDownBack();

    boolean onKeyDown(int i, KeyEvent keyEvent);

    boolean onKeyUp(int i, KeyEvent keyEvent);

    void out(boolean z);
}
