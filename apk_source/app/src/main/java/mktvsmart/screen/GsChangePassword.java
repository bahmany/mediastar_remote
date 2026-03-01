package mktvsmart.screen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.dataconvert.model.DataConvertControlModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class GsChangePassword extends Activity {
    private Button cancelButton;
    private List<DataConvertControlModel> controlModels;
    private MessageProcessor msgProc;
    private EditText newEdit;
    private EditText oldEdit;
    private DataParser parser;
    private Button saveButton;
    private Socket tcpSocket;
    private EditText verifyEdit;

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_NEW_PASSWORD_SET, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsChangePassword.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsChangePassword.this.parser = ParserFactory.getParser();
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                new ArrayList();
                try {
                    InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    if (Integer.parseInt((String) GsChangePassword.this.parser.parse(inStream, 15).get(0)) == 1) {
                        Toast.makeText(GsChangePassword.this, R.string.change_psw_success, 0).show();
                        GsChangePassword.this.onBackPressed();
                    } else {
                        Toast.makeText(GsChangePassword.this, R.string.change_psw_error_toast, 0).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMessageProcess();
        this.oldEdit = (EditText) findViewById(R.id.old_psw_edit);
        this.newEdit = (EditText) findViewById(R.id.new_psw_edit);
        this.verifyEdit = (EditText) findViewById(R.id.verify_psw_edit);
        this.saveButton = (Button) findViewById(R.id.change_psw_save_btn);
        this.cancelButton = (Button) findViewById(R.id.change_psws_cancel_btn);
        this.saveButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsChangePassword.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                if (GsChangePassword.this.oldEdit.getText().toString().equals("")) {
                    Toast.makeText(GsChangePassword.this, R.string.old_psw_null_error_toast, 0).show();
                    return;
                }
                if (GsChangePassword.this.checkPassword(GsChangePassword.this.newEdit.getText().toString(), GsChangePassword.this.verifyEdit.getText().toString())) {
                    try {
                        DataConvertControlModel oldPasswordModel = new DataConvertControlModel();
                        DataConvertControlModel newPasswordModel = new DataConvertControlModel();
                        GsChangePassword.this.controlModels = new ArrayList();
                        oldPasswordModel.SetPassword(GsChangePassword.this.oldEdit.getText().toString());
                        newPasswordModel.SetPassword(GsChangePassword.this.verifyEdit.getText().toString());
                        GsChangePassword.this.controlModels.add(oldPasswordModel);
                        GsChangePassword.this.controlModels.add(newPasswordModel);
                        GsChangePassword.this.parser = ParserFactory.getParser();
                        byte[] dataBuffer = GsChangePassword.this.parser.serialize(GsChangePassword.this.controlModels, GlobalConstantValue.GMS_MSG_DO_NEW_PASSWORD_SET).getBytes("UTF-8");
                        GsChangePassword.this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(dataBuffer, GsChangePassword.this.tcpSocket, 0, dataBuffer.length, GlobalConstantValue.GMS_MSG_DO_NEW_PASSWORD_SET);
                        return;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                Toast.makeText(GsChangePassword.this, R.string.change_psw_error_toast, 0).show();
            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsChangePassword.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsChangePassword.this.onBackPressed();
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsChangePassword.4
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) GsChangePassword.this.oldEdit.getContext().getSystemService("input_method");
                inputManager.showSoftInput(GsChangePassword.this.oldEdit, 0);
            }
        }, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPassword(String newPwd, String verifyPwd) {
        return newPwd.length() == GMScreenGlobalInfo.getmMaxPasswordNum() && newPwd.equals(verifyPwd);
    }
}
