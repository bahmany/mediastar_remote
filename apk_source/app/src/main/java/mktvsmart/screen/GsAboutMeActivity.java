package mktvsmart.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdView;
import mktvsmart.screen.util.AdsBinnerView;
import mktvsmart.screen.util.ConfigUtil;
import mktvsmart.screen.util.QRCodeUtil;

/* loaded from: classes.dex */
public class GsAboutMeActivity extends Activity {
    private static final String GMSCREEN_DOWNLOAD_WAYPOINT_ANDROID = "https://play.google.com/store/apps/details?id=mktvsmart.screen";
    private static final String GMSCREEN_DOWNLOAD_WAYPOINT_IOS = "https://itunes.apple.com/app/g-mscreen-multiple-screen/id921933799";
    private ImageView apkIcon;
    private TextView apkName;
    private Button backButton;
    private Button companyWebButton;
    private View copyrightView;
    private View emailView;
    private Button feedbackButton;
    private FrameLayout mAdSpaceFrame;
    private AdView mAdView = null;
    private ImageView mQRCodeAndroid;
    private ImageView mQRCodeIOS;
    private TextView swVerText;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.about_me_layout, (ViewGroup) null);
        setContentView(view);
        this.backButton = (Button) findViewById(R.id.back_about_me);
        this.companyWebButton = (Button) findViewById(R.id.btn_company_website);
        this.feedbackButton = (Button) findViewById(R.id.btn_advice_feedback);
        this.swVerText = (TextView) findViewById(R.id.about_me_software_version);
        this.emailView = findViewById(R.id.about_me_email_layout);
        this.copyrightView = findViewById(R.id.copyright_view);
        this.apkName = (TextView) findViewById(R.id.apk_name);
        this.apkIcon = (ImageView) findViewById(R.id.apk_icon);
        this.mAdSpaceFrame = (FrameLayout) findViewById(R.id.ad_space);
        this.mQRCodeAndroid = (ImageView) findViewById(R.id.imageview_qr_code_android);
        this.mQRCodeIOS = (ImageView) findViewById(R.id.imageview_qr_code_ios);
        this.swVerText.setText(getPackageVersion(this));
        this.apkName.setText(getString(getApplicationInfo().labelRes));
        this.mAdView = new AdsBinnerView(this).getAdView();
        this.mAdSpaceFrame.addView(this.mAdView, -2, -2);
        String releaseEdition = ConfigUtil.getInstance(this).getValue(ConfigUtil.RELEASE_EDITION, ConfigUtil.UNIVERSAL_EDITION);
        if (releaseEdition.equals(ConfigUtil.UNIVERSAL_EDITION)) {
            if (!getResources().getBoolean(R.bool.update_flag)) {
                this.emailView.setVisibility(0);
                this.companyWebButton.setVisibility(0);
                this.copyrightView.setVisibility(0);
            } else {
                this.emailView.setVisibility(8);
                this.companyWebButton.setVisibility(8);
                this.copyrightView.setVisibility(8);
            }
        } else {
            this.emailView.setVisibility(8);
            this.companyWebButton.setVisibility(8);
            this.copyrightView.setVisibility(8);
            this.apkIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.about_icon_witdh), (int) getResources().getDimension(R.dimen.about_icon_height)));
            this.apkIcon.setImageResource(getApplicationInfo().icon);
        }
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsAboutMeActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsAboutMeActivity.this.onBackPressed();
            }
        });
        this.companyWebButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsAboutMeActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Uri uri = Uri.parse(GsAboutMeActivity.this.getString(R.string.contact_ltd_website));
                GsAboutMeActivity.this.startActivity(new Intent("android.intent.action.VIEW", uri));
            }
        });
        this.feedbackButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsAboutMeActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsAboutMeActivity.this.startActivity(new Intent(GsAboutMeActivity.this, (Class<?>) SendFeedbackActivity.class));
            }
        });
        initQRBarcodeImageView();
    }

    private void initQRBarcodeImageView() {
        this.mQRCodeAndroid.setImageBitmap(QRCodeUtil.createQRCode(this, GMSCREEN_DOWNLOAD_WAYPOINT_ANDROID));
        this.mQRCodeIOS.setImageBitmap(QRCodeUtil.createQRCode(this, GMSCREEN_DOWNLOAD_WAYPOINT_IOS));
    }

    private String getPackageVersion(Context context) throws PackageManager.NameNotFoundException {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            String version = pi.versionName;
            return version;
        } catch (Exception e) {
            return "";
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mAdSpaceFrame.removeView(this.mAdView);
    }
}
