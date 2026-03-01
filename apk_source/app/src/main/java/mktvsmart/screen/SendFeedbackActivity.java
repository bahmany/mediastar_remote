package mktvsmart.screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.mail.Mail;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;

/* loaded from: classes.dex */
public class SendFeedbackActivity extends Activity {
    private Button backButton;
    private EditText feedbackEdit;
    private Mail mail;
    private Button sendFeedbackBtn;
    private AsyncTask<Integer, Integer, String> sendFeedbackTask;
    private final String[] sendToAddress = {"temptest987@sina.com"};
    private ADSProgressDialog waitDialog;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_feedback_layout);
        this.feedbackEdit = (EditText) findViewById(R.id.feedback_edit_txt);
        this.sendFeedbackBtn = (Button) findViewById(R.id.btn_send_feedback);
        this.backButton = (Button) findViewById(R.id.back_send_feedback);
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SendFeedbackActivity.1
            AnonymousClass1() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SendFeedbackActivity.this.onBackPressed();
            }
        });
        this.sendFeedbackBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SendFeedbackActivity.2
            AnonymousClass2() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String feedbackStr = SendFeedbackActivity.this.feedbackEdit.getText().toString();
                if (feedbackStr != null && feedbackStr.length() > 0) {
                    SendFeedbackActivity.this.sendFeedbackTask = new AsyncTask<Integer, Integer, String>() { // from class: mktvsmart.screen.SendFeedbackActivity.2.1
                        private final /* synthetic */ String val$feedbackStr;

                        AnonymousClass1(String feedbackStr2) {
                            str = feedbackStr2;
                        }

                        @Override // android.os.AsyncTask
                        protected void onPreExecute() {
                            super.onPreExecute();
                            SendFeedbackActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) SendFeedbackActivity.this, SendFeedbackActivity.this.getResources().getString(R.string.send_advice), SendFeedbackActivity.this.getResources().getString(R.string.progress_dialog_message), false);
                        }

                        @Override // android.os.AsyncTask
                        public void onPostExecute(String result) {
                            super.onPostExecute((AnonymousClass1) result);
                            if (SendFeedbackActivity.this.waitDialog.isShowing()) {
                                SendFeedbackActivity.this.waitDialog.dismiss();
                            }
                            Toast.makeText(SendFeedbackActivity.this, result, 0).show();
                            SendFeedbackActivity.this.onBackPressed();
                        }

                        @Override // android.os.AsyncTask
                        public String doInBackground(Integer... params) throws Resources.NotFoundException {
                            SendFeedbackActivity.this.mail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                            SendFeedbackActivity.this.mail.setTo(SendFeedbackActivity.this.sendToAddress);
                            SendFeedbackActivity.this.mail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                            SendFeedbackActivity.this.mail.setSubject("Advices or problems send from user");
                            SendFeedbackActivity.this.mail.setBody(str);
                            try {
                                String finishStr = SendFeedbackActivity.this.mail.send() ? SendFeedbackActivity.this.getResources().getString(R.string.send_email_success) : SendFeedbackActivity.this.getResources().getString(R.string.send_email_fail);
                                return finishStr;
                            } catch (Exception e) {
                                e.printStackTrace();
                                String finishStr2 = SendFeedbackActivity.this.getResources().getString(R.string.send_email_error);
                                return finishStr2;
                            }
                        }
                    };
                    SendFeedbackActivity.this.sendFeedbackTask.execute(0);
                } else {
                    Toast.makeText(SendFeedbackActivity.this, R.string.send_no_advice, 0).show();
                }
            }

            /* renamed from: mktvsmart.screen.SendFeedbackActivity$2$1 */
            class AnonymousClass1 extends AsyncTask<Integer, Integer, String> {
                private final /* synthetic */ String val$feedbackStr;

                AnonymousClass1(String feedbackStr2) {
                    str = feedbackStr2;
                }

                @Override // android.os.AsyncTask
                protected void onPreExecute() {
                    super.onPreExecute();
                    SendFeedbackActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) SendFeedbackActivity.this, SendFeedbackActivity.this.getResources().getString(R.string.send_advice), SendFeedbackActivity.this.getResources().getString(R.string.progress_dialog_message), false);
                }

                @Override // android.os.AsyncTask
                public void onPostExecute(String result) {
                    super.onPostExecute((AnonymousClass1) result);
                    if (SendFeedbackActivity.this.waitDialog.isShowing()) {
                        SendFeedbackActivity.this.waitDialog.dismiss();
                    }
                    Toast.makeText(SendFeedbackActivity.this, result, 0).show();
                    SendFeedbackActivity.this.onBackPressed();
                }

                @Override // android.os.AsyncTask
                public String doInBackground(Integer... params) throws Resources.NotFoundException {
                    SendFeedbackActivity.this.mail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                    SendFeedbackActivity.this.mail.setTo(SendFeedbackActivity.this.sendToAddress);
                    SendFeedbackActivity.this.mail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                    SendFeedbackActivity.this.mail.setSubject("Advices or problems send from user");
                    SendFeedbackActivity.this.mail.setBody(str);
                    try {
                        String finishStr = SendFeedbackActivity.this.mail.send() ? SendFeedbackActivity.this.getResources().getString(R.string.send_email_success) : SendFeedbackActivity.this.getResources().getString(R.string.send_email_fail);
                        return finishStr;
                    } catch (Exception e) {
                        e.printStackTrace();
                        String finishStr2 = SendFeedbackActivity.this.getResources().getString(R.string.send_email_error);
                        return finishStr2;
                    }
                }
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.SendFeedbackActivity.3
            AnonymousClass3() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) SendFeedbackActivity.this.feedbackEdit.getContext().getSystemService("input_method");
                inputManager.showSoftInput(SendFeedbackActivity.this.feedbackEdit, 0);
            }
        }, 200L);
    }

    /* renamed from: mktvsmart.screen.SendFeedbackActivity$1 */
    class AnonymousClass1 implements View.OnClickListener {
        AnonymousClass1() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            SendFeedbackActivity.this.onBackPressed();
        }
    }

    /* renamed from: mktvsmart.screen.SendFeedbackActivity$2 */
    class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            String feedbackStr2 = SendFeedbackActivity.this.feedbackEdit.getText().toString();
            if (feedbackStr2 != null && feedbackStr2.length() > 0) {
                SendFeedbackActivity.this.sendFeedbackTask = new AsyncTask<Integer, Integer, String>() { // from class: mktvsmart.screen.SendFeedbackActivity.2.1
                    private final /* synthetic */ String val$feedbackStr;

                    AnonymousClass1(String feedbackStr22) {
                        str = feedbackStr22;
                    }

                    @Override // android.os.AsyncTask
                    protected void onPreExecute() {
                        super.onPreExecute();
                        SendFeedbackActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) SendFeedbackActivity.this, SendFeedbackActivity.this.getResources().getString(R.string.send_advice), SendFeedbackActivity.this.getResources().getString(R.string.progress_dialog_message), false);
                    }

                    @Override // android.os.AsyncTask
                    public void onPostExecute(String result) {
                        super.onPostExecute((AnonymousClass1) result);
                        if (SendFeedbackActivity.this.waitDialog.isShowing()) {
                            SendFeedbackActivity.this.waitDialog.dismiss();
                        }
                        Toast.makeText(SendFeedbackActivity.this, result, 0).show();
                        SendFeedbackActivity.this.onBackPressed();
                    }

                    @Override // android.os.AsyncTask
                    public String doInBackground(Integer... params) throws Resources.NotFoundException {
                        SendFeedbackActivity.this.mail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                        SendFeedbackActivity.this.mail.setTo(SendFeedbackActivity.this.sendToAddress);
                        SendFeedbackActivity.this.mail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                        SendFeedbackActivity.this.mail.setSubject("Advices or problems send from user");
                        SendFeedbackActivity.this.mail.setBody(str);
                        try {
                            String finishStr = SendFeedbackActivity.this.mail.send() ? SendFeedbackActivity.this.getResources().getString(R.string.send_email_success) : SendFeedbackActivity.this.getResources().getString(R.string.send_email_fail);
                            return finishStr;
                        } catch (Exception e) {
                            e.printStackTrace();
                            String finishStr2 = SendFeedbackActivity.this.getResources().getString(R.string.send_email_error);
                            return finishStr2;
                        }
                    }
                };
                SendFeedbackActivity.this.sendFeedbackTask.execute(0);
            } else {
                Toast.makeText(SendFeedbackActivity.this, R.string.send_no_advice, 0).show();
            }
        }

        /* renamed from: mktvsmart.screen.SendFeedbackActivity$2$1 */
        class AnonymousClass1 extends AsyncTask<Integer, Integer, String> {
            private final /* synthetic */ String val$feedbackStr;

            AnonymousClass1(String feedbackStr22) {
                str = feedbackStr22;
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                super.onPreExecute();
                SendFeedbackActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) SendFeedbackActivity.this, SendFeedbackActivity.this.getResources().getString(R.string.send_advice), SendFeedbackActivity.this.getResources().getString(R.string.progress_dialog_message), false);
            }

            @Override // android.os.AsyncTask
            public void onPostExecute(String result) {
                super.onPostExecute((AnonymousClass1) result);
                if (SendFeedbackActivity.this.waitDialog.isShowing()) {
                    SendFeedbackActivity.this.waitDialog.dismiss();
                }
                Toast.makeText(SendFeedbackActivity.this, result, 0).show();
                SendFeedbackActivity.this.onBackPressed();
            }

            @Override // android.os.AsyncTask
            public String doInBackground(Integer... params) throws Resources.NotFoundException {
                SendFeedbackActivity.this.mail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                SendFeedbackActivity.this.mail.setTo(SendFeedbackActivity.this.sendToAddress);
                SendFeedbackActivity.this.mail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                SendFeedbackActivity.this.mail.setSubject("Advices or problems send from user");
                SendFeedbackActivity.this.mail.setBody(str);
                try {
                    String finishStr = SendFeedbackActivity.this.mail.send() ? SendFeedbackActivity.this.getResources().getString(R.string.send_email_success) : SendFeedbackActivity.this.getResources().getString(R.string.send_email_fail);
                    return finishStr;
                } catch (Exception e) {
                    e.printStackTrace();
                    String finishStr2 = SendFeedbackActivity.this.getResources().getString(R.string.send_email_error);
                    return finishStr2;
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.SendFeedbackActivity$3 */
    class AnonymousClass3 extends TimerTask {
        AnonymousClass3() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            InputMethodManager inputManager = (InputMethodManager) SendFeedbackActivity.this.feedbackEdit.getContext().getSystemService("input_method");
            inputManager.showSoftInput(SendFeedbackActivity.this.feedbackEdit, 0);
        }
    }
}
