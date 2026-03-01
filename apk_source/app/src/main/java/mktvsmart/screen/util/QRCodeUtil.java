package mktvsmart.screen.util;

import android.content.Context;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class QRCodeUtil {
    private static final int QR_HEIGHT = 115;
    private static final int QR_WIDTH = 115;

    public static Bitmap createQRCode(Context context, String url) throws UnsupportedEncodingException {
        int qrWidth = DensityUtil.dip2px(context, 115.0f);
        int qrHeight = DensityUtil.dip2px(context, 115.0f);
        try {
            QRCodeWriter writer = new QRCodeWriter();
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            BitMatrix martix = writer.encode(url, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
            System.out.println("w:" + martix.getWidth() + "h:" + martix.getHeight());
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, XML.CHARSET_UTF8);
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            int[] pixels = new int[qrWidth * qrHeight];
            for (int y = 0; y < qrHeight; y++) {
                for (int x = 0; x < qrWidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[(y * qrWidth) + x] = -16777216;
                    } else {
                        pixels[(y * qrWidth) + x] = -1;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
