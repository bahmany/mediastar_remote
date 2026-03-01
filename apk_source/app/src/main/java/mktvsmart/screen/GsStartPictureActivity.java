package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import java.io.FileInputStream;
import java.io.IOException;
import mktvsmart.screen.util.WeakHandler;

/* loaded from: classes.dex */
public class GsStartPictureActivity extends Activity implements View.OnClickListener {
    private static final int MSG_ENTER_LOGIN = 0;
    private static final int SHOWING_DELAY = 3000;
    public static final String START_PICTURE_PATH = "start_picture_path";
    private WeakHandler<GsStartPictureActivity> mHandler = new WeakHandler<GsStartPictureActivity>(this) { // from class: mktvsmart.screen.GsStartPictureActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    GsStartPictureActivity.this.enterLogin();
                    break;
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws IOException {
        super.onCreate(savedInstanceState);
        this.mHandler.sendEmptyMessageDelayed(0, 3000L);
        initViews();
    }

    private void initViews() throws IOException {
        View root = View.inflate(this, R.layout.activity_start_picture, null);
        ImageButton closeButton = (ImageButton) root.findViewById(R.id.start_picture_close);
        closeButton.setOnClickListener(this);
        setContentView(root);
        String filename = getIntent().getStringExtra(START_PICTURE_PATH);
        try {
            FileInputStream fis = openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            root.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            fis.close();
        } catch (Exception e) {
            enterLogin();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        enterLogin();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterLogin() {
        this.mHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, (Class<?>) GsLoginListActivity.class);
        startActivity(intent);
        finish();
    }
}
