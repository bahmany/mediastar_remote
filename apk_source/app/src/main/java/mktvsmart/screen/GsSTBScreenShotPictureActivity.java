package mktvsmart.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import mktvsmart.screen.util.GmscreenDataFolderUtil;

/* loaded from: classes.dex */
public class GsSTBScreenShotPictureActivity extends Activity implements View.OnClickListener {
    public static final String START_SCREENSHOT_PATH = "start_screenshot_path";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws Throwable {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() throws Throwable {
        View root = View.inflate(this, R.layout.activity_start_picture, null);
        ImageButton closeButton = (ImageButton) root.findViewById(R.id.start_picture_close);
        closeButton.setOnClickListener(this);
        setContentView(root);
        String filename = getIntent().getStringExtra(START_SCREENSHOT_PATH);
        FileInputStream fis = null;
        try {
            try {
                File file = new File(GmscreenDataFolderUtil.getGmscreenDataFolderPath(), filename);
                FileInputStream fis2 = new FileInputStream(file);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(fis2);
                    root.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                    if (fis2 != null) {
                        try {
                            fis2.close();
                            fis = fis2;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        fis = fis2;
                    }
                } catch (Exception e2) {
                    fis = fis2;
                    finish();
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    fis = fis2;
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        finish();
    }
}
