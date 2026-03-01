package mktvsmart.screen.hisientry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.hisilicon.multiscreen.mybox.BaseActivity;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.message.VImeTextInfo;
import com.hisilicon.multiscreen.protocol.remote.VImeClientTransfer;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.MultiScreenIntentAction;
import com.hisilicon.multiscreen.vime.EditTextWatcher;
import com.hisilicon.multiscreen.vime.VIMEEditText;
import com.hisilicon.multiscreen.vime.VImeClientControlService;
import com.hisilicon.multiscreen.vime.VImeOption;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class ContentInputActivity extends BaseActivity {
    private static /* synthetic */ int[] $SWITCH_TABLE$mktvsmart$screen$hisientry$ContentInputActivity$EndInputType = null;
    private static final boolean IS_ON_TOP = true;
    private static final String MSG_KEY_END_SELECION = "MSG_END_SELECTION";
    private static final String MSG_KEY_START_SELECION = "MSG_START_SELECTION";
    private static final String MSG_KEY_TEXT = "MSG_KEY_TEXT";
    public static final int MSG_SEND_CURSOR_DELAY = 257;
    private static final long SEND_CURSOR_DELAY = 500;
    private static final int SEND_TEXT_INIT_SEQUENCE = 0;
    private int mSendTextSeq = 0;
    private VImeClientTransfer mVimeClientTransfer = null;
    private VImeClientControlService mVImeClientControlService = null;
    private VIMEEditText mEtInputText = null;
    private Button mBtnComplete = null;
    private VImeOption mImeOption = null;
    private EndInputType mEndInputType = null;
    private Timer openKeyboardTimer = null;
    private SyncTextHandler myHandler = null;
    private HandlerThread mHandlerThread = null;
    private String mLatestText = "";
    private BroadcastReceiver mVImeBroadcastReceiver = new BroadcastReceiver() { // from class: mktvsmart.screen.hisientry.ContentInputActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equals(MultiScreenIntentAction.END_INPUT_BY_STB)) {
                ContentInputActivity.this.mEndInputType = EndInputType.STB;
            } else if (intentAction.equals(MultiScreenIntentAction.END_INPUT_BY_PHONE)) {
                ContentInputActivity.this.mEndInputType = EndInputType.OTHERS;
            }
            LogTool.d("name of VIME broadcast: " + intentAction);
            ContentInputActivity.this.finish();
        }
    };
    private View.OnClickListener mBtnCompleteClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.hisientry.ContentInputActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            ContentInputActivity.this.mEndInputType = EndInputType.BUTTON_COMPLETE;
            ContentInputActivity.this.finish();
        }
    };
    private EditTextWatcher mEtWatcher = new EditTextWatcher() { // from class: mktvsmart.screen.hisientry.ContentInputActivity.3
        @Override // com.hisilicon.multiscreen.vime.EditTextWatcher
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            SendTextRunnable mSendTextRunnable = ContentInputActivity.this.new SendTextRunnable(text.toString(), start + lengthAfter, start + lengthAfter);
            new Thread(mSendTextRunnable).start();
            ContentInputActivity.this.mLatestText = text.toString();
        }

        @Override // com.hisilicon.multiscreen.vime.EditTextWatcher
        public void onSelectionChanged(int selStart, int selEnd) {
            ContentInputActivity.this.sendTextDelay(ContentInputActivity.this.mLatestText, selStart, selEnd, ContentInputActivity.SEND_CURSOR_DELAY);
        }
    };

    private enum EndInputType {
        BUTTON_COMPLETE,
        STB,
        OTHERS;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static EndInputType[] valuesCustom() {
            EndInputType[] endInputTypeArrValuesCustom = values();
            int length = endInputTypeArrValuesCustom.length;
            EndInputType[] endInputTypeArr = new EndInputType[length];
            System.arraycopy(endInputTypeArrValuesCustom, 0, endInputTypeArr, 0, length);
            return endInputTypeArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$mktvsmart$screen$hisientry$ContentInputActivity$EndInputType() {
        int[] iArr = $SWITCH_TABLE$mktvsmart$screen$hisientry$ContentInputActivity$EndInputType;
        if (iArr == null) {
            iArr = new int[EndInputType.valuesCustom().length];
            try {
                iArr[EndInputType.BUTTON_COMPLETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[EndInputType.OTHERS.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[EndInputType.STB.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$mktvsmart$screen$hisientry$ContentInputActivity$EndInputType = iArr;
        }
        return iArr;
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        LogTool.d("on create");
        super.onCreate(savedInstanceState);
        processExtraData();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        LogTool.d("onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        this.mVImeClientControlService = VImeClientControlService.getInstance();
        this.mVImeClientControlService.setInputActivityTopStatus(true);
        if (isInputStatusOnClient()) {
            Intent intent = getIntent();
            initView();
            initData(intent);
            openKeyboard();
            registerVImeReceiver(this.mVImeBroadcastReceiver);
            return;
        }
        LogTool.d("Create fail, to finish activity.");
        finish();
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    protected void onRestart() {
        LogTool.d("on Restart");
        super.onRestart();
        if (this.mHandlerThread == null) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        LogTool.d("onStart.");
        super.onStart();
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    protected void onResume() {
        LogTool.d("on Resume.");
        super.onResume();
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    protected void onPause() throws IOException {
        LogTool.d("on pause.");
        super.onPause();
        unregisterVImeReceiver(this.mVImeBroadcastReceiver);
        finishContentInput(this.mEndInputType);
        deinitHandler();
        if (this.mVImeClientControlService != null) {
            this.mVImeClientControlService.setInputActivityTopStatus(false);
        }
        unregisterVImeReceiver(this.mVImeBroadcastReceiver);
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    protected void onStop() {
        LogTool.d("on stop.");
        super.onStop();
    }

    @Override // com.hisilicon.multiscreen.mybox.BaseActivity, android.app.Activity
    protected void onDestroy() {
        LogTool.d("on destroy.");
        super.onDestroy();
    }

    private boolean isInputStatusOnClient() {
        VImeClientControlService mVImeClientControlService = VImeClientControlService.getInstance();
        if (mVImeClientControlService != null) {
            return mVImeClientControlService.isInputStatusOnClient();
        }
        LogTool.d("mVImeClientControlService is null");
        return false;
    }

    private void finishContentInput(EndInputType endInputType) throws IOException {
        clearMessages();
        cancelKeyboard();
        switch ($SWITCH_TABLE$mktvsmart$screen$hisientry$ContentInputActivity$EndInputType()[endInputType.ordinal()]) {
            case 1:
                this.mVimeClientTransfer.endInput(this.mImeOption.getIndex());
                break;
            case 2:
                break;
            case 3:
                this.mVimeClientTransfer.endInput(VImeOption.HIDE_KEYBOARD.getIndex());
                break;
            default:
                LogTool.e("No default end input type");
                break;
        }
    }

    private void initView() {
        setContentView(R.layout.input_layout_in_mirrorpage);
        this.mEtInputText = (VIMEEditText) findViewById(R.id.et_input_text);
        this.mBtnComplete = (Button) findViewById(R.id.button);
    }

    private void initData(Intent intent) {
        initHandler();
        this.mEndInputType = EndInputType.OTHERS;
        Bundle bundle = intent.getExtras();
        this.mImeOption = VImeOption.getOption(bundle.getInt(VImeTextInfo.INPUT_OPTION));
        int inputType = bundle.getInt(VImeTextInfo.INPUT_TYPE);
        String text = bundle.getString("text");
        int startSelection = bundle.getInt(VImeTextInfo.SRC_START_SELECTION);
        int endSelection = bundle.getInt(VImeTextInfo.SRC_END_SELECTION);
        initCompleteButton(this.mImeOption);
        this.mEtInputText.setInputType(inputType);
        this.mEtInputText.setText(text, TextView.BufferType.EDITABLE);
        this.mLatestText = text;
        this.mEtInputText.setSelection(startSelection, endSelection);
        this.mSendTextSeq = 0;
        this.mEtInputText.addWatcher(this.mEtWatcher);
        this.mVimeClientTransfer = VImeClientTransfer.getInstance(MultiScreenControlService.getInstance().getHiDevice());
    }

    private void initHandler() {
        this.mHandlerThread = new HandlerThread("multiscreen_contentinput_handlerthread");
        this.mHandlerThread.start();
        this.myHandler = new SyncTextHandler(this.mHandlerThread.getLooper());
    }

    private void deinitHandler() {
        if (this.mHandlerThread != null) {
            clearMessages();
            this.mHandlerThread.getLooper().quit();
            this.mHandlerThread = null;
        }
    }

    private void initCompleteButton(VImeOption imeOption) {
        this.mBtnComplete.setOnClickListener(this.mBtnCompleteClickListener);
    }

    private void openKeyboard() {
        this.openKeyboardTimer = new Timer();
        this.openKeyboardTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.hisientry.ContentInputActivity.4
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                InputMethodManager imm = (InputMethodManager) ContentInputActivity.this.getSystemService("input_method");
                imm.showSoftInput(ContentInputActivity.this.mEtInputText, 0);
            }
        }, SEND_CURSOR_DELAY);
    }

    private void cancelKeyboard() {
        if (this.openKeyboardTimer != null) {
            this.openKeyboardTimer.cancel();
            this.openKeyboardTimer = null;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        View focusView = getCurrentFocus();
        if (focusView != null && imm != null) {
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 2);
        }
    }

    private void registerVImeReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MultiScreenIntentAction.END_INPUT_BY_STB);
        filter.addAction(MultiScreenIntentAction.END_INPUT_BY_PHONE);
        registerBroadcastReceiver(receiver, filter);
    }

    private void unregisterVImeReceiver(BroadcastReceiver receiver) {
        unregisterBroadcastReceiver(receiver);
    }

    private void clearMessages() {
        if (this.myHandler.hasMessages(257)) {
            this.myHandler.removeMessages(257);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTextDelay(String text, int start, int end, long delayMillis) {
        clearMessages();
        Message msg = this.myHandler.obtainMessage();
        msg.what = 257;
        Bundle data = new Bundle();
        data.putString(MSG_KEY_TEXT, text);
        data.putInt(MSG_KEY_START_SELECION, start);
        data.putInt(MSG_KEY_END_SELECION, end);
        msg.setData(data);
        this.myHandler.sendMessageDelayed(msg, delayMillis);
    }

    private class SyncTextHandler extends Handler {
        public SyncTextHandler() {
        }

        public SyncTextHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws IOException {
            switch (msg.what) {
                case 257:
                    Bundle data = msg.getData();
                    String text = data.getString(ContentInputActivity.MSG_KEY_TEXT);
                    if (text == null) {
                        text = "";
                        LogTool.e("Get string form data fail, and it will send blank.");
                    }
                    ContentInputActivity.this.sendText(text, data.getInt(ContentInputActivity.MSG_KEY_START_SELECION, 0), data.getInt(ContentInputActivity.MSG_KEY_END_SELECION, 0));
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendText(String text, int start, int end) throws IOException {
        this.mSendTextSeq++;
        this.mVimeClientTransfer.sendText(this.mSendTextSeq, text, start, end);
    }

    private class SendTextRunnable implements Runnable {
        private String mCurrentText;
        private int mEndSelection;
        private int mStartSelection;

        public SendTextRunnable(String currentText, int start, int end) {
            this.mCurrentText = null;
            this.mStartSelection = 0;
            this.mEndSelection = 0;
            this.mCurrentText = currentText;
            this.mStartSelection = start;
            this.mEndSelection = end;
        }

        @Override // java.lang.Runnable
        public void run() throws IOException {
            ContentInputActivity.this.sendText(this.mCurrentText, this.mStartSelection, this.mEndSelection);
        }
    }
}
