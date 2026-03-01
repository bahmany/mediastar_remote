package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertInputMethodModel;
import mktvsmart.screen.dataconvert.model.DataConvertRcuModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.KeyboardLayout;

/* loaded from: classes.dex */
public class SoftKeyboardActivity extends Activity {
    InputMethodManager inputManager;
    private MessageProcessor msgProc;
    private DataParser parser;
    View softKeyboardLayout;
    private Socket tcpSocket;

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) throws UnsupportedEncodingException {
        if (keyCode != 4 && keyCode != 3 && keyCode != 82 && keyCode != 84 && keyCode != 59 && keyCode != 60) {
            if (keyCode == 67) {
                sendKeyValue(10);
            } else if (keyCode == 66) {
                switch (GMScreenGlobalInfo.getCurStbInfo().getPlatform_id()) {
                    case 20:
                    case 21:
                    case 25:
                        sendKeyValue(8);
                        break;
                    case 22:
                    case 23:
                    case 24:
                    default:
                        sendKeyValue(11);
                        break;
                }
            } else if (GMScreenGlobalInfo.getCurStbPlatform() != 32 && GMScreenGlobalInfo.getCurStbPlatform() != 71 && GMScreenGlobalInfo.getCurStbPlatform() != 72 && GMScreenGlobalInfo.getCurStbPlatform() != 74) {
                int actualKeyCode = event.getUnicodeChar();
                sendKeyCode(actualKeyCode);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(2012, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SoftKeyboardActivity.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                SoftKeyboardActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SoftKeyboardActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SoftKeyboardActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(SoftKeyboardActivity.this, GsLoginListActivity.class);
                SoftKeyboardActivity.this.startActivity(intent);
                SoftKeyboardActivity.this.finish();
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        LayoutInflater inflater = LayoutInflater.from(this);
        this.softKeyboardLayout = inflater.inflate(R.layout.soft_keyboard_input_layout, (ViewGroup) null);
        setContentView(this.softKeyboardLayout);
        KeyboardLayout keyboardLayout = (KeyboardLayout) findViewById(R.id.keyboard_layout);
        keyboardLayout.setOnKeyboardStateListener(new KeyboardLayout.OnKeyboardStateChangedListener() { // from class: mktvsmart.screen.SoftKeyboardActivity.3
            @Override // mktvsmart.screen.view.KeyboardLayout.OnKeyboardStateChangedListener
            public void onKeyBoardStateChanged(int state) {
                switch (state) {
                    case -2:
                        SoftKeyboardActivity.this.finish();
                        break;
                }
            }
        });
        setMessageProcess();
        try {
            CreateSocket cSocket = new CreateSocket(null, 0);
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parser = ParserFactory.getParser();
        this.inputManager = (InputMethodManager) getSystemService("input_method");
        this.inputManager.toggleSoftInput(0, 2);
    }

    @Override // android.app.Activity
    protected void onDestroy() throws UnsupportedEncodingException {
        super.onDestroy();
        this.inputManager.hideSoftInputFromWindow(this.softKeyboardLayout.getWindowToken(), 0);
        cancelInputCharacters();
        this.msgProc.recycle();
    }

    private void cancelInputCharacters() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_INPUT_METHOD_DISMISS);
    }

    private void sendKeyCode(int keyCode) throws UnsupportedEncodingException {
        List<DataConvertInputMethodModel> modelList = new ArrayList<>();
        DataConvertInputMethodModel model = new DataConvertInputMethodModel();
        model.setKeyCode(keyCode);
        modelList.add(model);
        try {
            byte[] dataBuffer = this.parser.serialize(modelList, GlobalConstantValue.GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET).getBytes("UTF-8");
            GsSendSocket.sendSocketToStb(dataBuffer, this.tcpSocket, 0, dataBuffer.length, GlobalConstantValue.GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void sendKeyValue(int keyValue) {
        DataParser parser = ParserFactory.getParser();
        List<DataConvertRcuModel> rcuModels = new ArrayList<>();
        DataConvertRcuModel rcuModel = new DataConvertRcuModel();
        if (keyValue > 0) {
            rcuModel.setKeyValue(keyValue);
            rcuModels.add(rcuModel);
            try {
                byte[] byteArrays = parser.serialize(rcuModels, GlobalConstantValue.GMS_MSG_DO_REMOTE_CONTROL).getBytes();
                GsSendSocket.sendSocketToStb(byteArrays, this.tcpSocket, 0, byteArrays.length, GlobalConstantValue.GMS_MSG_DO_REMOTE_CONTROL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
