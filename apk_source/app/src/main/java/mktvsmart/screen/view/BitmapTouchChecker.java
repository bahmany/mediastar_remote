package mktvsmart.screen.view;

import android.graphics.Bitmap;
import mktvsmart.screen.view.IrregularButton;

/* loaded from: classes.dex */
public class BitmapTouchChecker implements IrregularButton.TouchChecker {
    private Bitmap bitmap;

    public BitmapTouchChecker(Bitmap bitmap) {
        this.bitmap = null;
        this.bitmap = bitmap;
    }

    @Override // mktvsmart.screen.view.IrregularButton.TouchChecker
    public boolean isInTouchArea(int x, int y, int width, int height) {
        if (this.bitmap == null || x < 0 || x >= Math.min(width, this.bitmap.getWidth()) || y < 0 || y >= Math.min(height, this.bitmap.getHeight())) {
            return false;
        }
        int pixel = this.bitmap.getPixel(x, y);
        return ((pixel >> 24) & 255) > 0;
    }
}
