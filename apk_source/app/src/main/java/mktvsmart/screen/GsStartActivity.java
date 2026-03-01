package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

/* loaded from: classes.dex */
public class GsStartActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws PackageManager.NameNotFoundException {
        String picture;
        super.onCreate(savedInstanceState);
        EditFirstStartGuideFile startGuide = new EditFirstStartGuideFile(this);
        int guide = startGuide.getGuideValue();
        String ipAddress = getIntent().getStringExtra("ipAdress");
        Intent intent = new Intent();
        if (ipAddress != null) {
            intent.putExtra("ipAdress", ipAddress);
            intent.setClass(this, GsLoginActivity.class);
            startActivity(intent);
            return;
        }
        switch (guide) {
            case 0:
                intent.setClass(this, GsStartGuideActivity.class);
                break;
            case 1:
                intent.setClass(this, GsUpdateGuideActivity.class);
                break;
            case 2:
                int pictureShowNum = startGuide.getPictureShowNum();
                if (startGuide.showStartPicture() && (picture = startGuide.getStartPicture()) != null && pictureShowNum < 3) {
                    startGuide.setPictureShowNum(pictureShowNum + 1);
                    intent.putExtra(GsStartPictureActivity.START_PICTURE_PATH, picture);
                    intent.setClass(this, GsStartPictureActivity.class);
                    break;
                } else {
                    intent.setClass(this, GsLoginListActivity.class);
                    break;
                }
        }
        startGuide.retrieveStartPictureInBackground();
        startActivity(intent);
        finish();
    }
}
