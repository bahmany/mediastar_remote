package mktvsmart.screen.exception;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsStartActivity;
import mktvsmart.screen.R;
import mktvsmart.screen.mail.Mail;

/* loaded from: classes.dex */
public class ReportPage extends Activity {
    public static final String REPORT_CONTENT = "content";
    private Mail mail;
    protected StringBuilder reportContent;
    private TextView reportTextview;
    protected StringBuilder reportTitle;
    private AsyncTask<Integer, Integer, String> sendFeedbackTask;
    private ProgressDialog waitDialog;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String bugContent = getIntent().getStringExtra(REPORT_CONTENT);
        buildContent(bugContent);
        setContentView(R.layout.activity_report);
        this.reportTextview = (TextView) findViewById(R.id.report_text);
        this.reportTextview.setText(Html.fromHtml(this.reportContent.toString()));
    }

    public String getVersionName() throws PackageManager.NameNotFoundException {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info == null) {
            return "Unknow";
        }
        String version = info.versionName;
        return version;
    }

    private void buildContent(String bugContent) {
        this.reportContent = new StringBuilder();
        this.reportContent.append("<br><b>Sorry, app has stoped.You can help to fix this bug by sending the report below to developers.</b></br><br></br>");
        this.reportContent.append("<br>").append("Platform: ").append(getIntent().getIntExtra("PID", 0)).append("</br>");
        this.reportContent.append("<br>").append("Model: ").append(Build.MODEL).append("</br>");
        this.reportContent.append("<br>").append("Device: ").append(Build.DEVICE).append("</br>");
        this.reportContent.append("<br>").append("Product: ").append(Build.PRODUCT).append("</br>");
        this.reportContent.append("<br>").append("Manufacturer: ").append(Build.MANUFACTURER).append("</br>");
        this.reportContent.append("<br>").append("Version: ").append(Build.VERSION.RELEASE).append("</br>");
        this.reportContent.append("<br>").append("SW Version: ").append(getVersionName()).append("</br>");
        this.reportContent.append("<br style=\"text-align:justify;\">").append(bugContent).append("</br>");
    }

    public void send(View view) {
        if (view.getId() == R.id.restart) {
            Intent i = new Intent(this, (Class<?>) GsStartActivity.class);
            i.addFlags(335544320);
            startActivity(i);
            finish();
            return;
        }
        if (view.getId() == R.id.send) {
            this.sendFeedbackTask = new AsyncTask<Integer, Integer, String>() { // from class: mktvsmart.screen.exception.ReportPage.1
                @Override // android.os.AsyncTask
                protected void onPreExecute() {
                    super.onPreExecute();
                    ReportPage.this.waitDialog = ProgressDialog.show(ReportPage.this, ReportPage.this.getResources().getString(R.string.send_advice), ReportPage.this.getResources().getString(R.string.progress_dialog_message), true, false);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(String result) {
                    super.onPostExecute((AnonymousClass1) result);
                    if (ReportPage.this.waitDialog.isShowing()) {
                        ReportPage.this.waitDialog.dismiss();
                    }
                    Toast.makeText(ReportPage.this, result, 1).show();
                    if (result.equals(ReportPage.this.getResources().getString(R.string.send_email_success))) {
                        ReportPage.this.findViewById(R.id.send).setEnabled(false);
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public String doInBackground(Integer... params) throws Resources.NotFoundException {
                    String finishStr;
                    ReportPage.this.mail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                    ReportPage.this.mail.setTo(new String[]{"temptest987@sina.com", "xiangwu_yang@163.com"});
                    ReportPage.this.mail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                    ReportPage.this.mail.setSubject("mscreen has stop");
                    ReportPage.this.mail.setBody(Html.fromHtml(ReportPage.this.reportContent.toString()).toString());
                    try {
                        if (ReportPage.this.mail.send()) {
                            finishStr = ReportPage.this.getResources().getString(R.string.send_email_success);
                        } else {
                            finishStr = ReportPage.this.getResources().getString(R.string.send_email_fail);
                        }
                        return finishStr;
                    } catch (Exception e) {
                        e.printStackTrace();
                        String finishStr2 = ReportPage.this.getResources().getString(R.string.send_email_error);
                        return finishStr2;
                    }
                }
            };
            this.sendFeedbackTask.execute(0);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }
}
