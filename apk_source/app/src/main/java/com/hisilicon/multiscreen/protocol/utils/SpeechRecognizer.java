package com.hisilicon.multiscreen.protocol.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/* loaded from: classes.dex */
public class SpeechRecognizer {
    private static final String FLY_APP_ID = "538d83c8";
    private static final int RECOGNIZE_CORRECT_RESULT_TYPE = 1;
    private static final int RECOGNIZE_ERROR_RESULT_TYPE = 0;
    public ISpeechMsgDealer mISpeechMsgDealer = null;
    public StringBuffer mSpeechResult = null;
    private RecognizerDialog mRecognizerDialog = null;
    private InitListener mInitListener = new InitListener() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecognizer.1
        @Override // com.iflytek.cloud.InitListener
        public void onInit(int arg0) {
            LogTool.d("init arg " + arg0);
        }
    };
    private DialogInterface.OnCancelListener mRecognizerCancelListener = new DialogInterface.OnCancelListener() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecognizer.2
        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface arg0) {
            LogTool.d("");
            SpeechRecognizer.this.mISpeechMsgDealer.speakError("Cancel Dialog");
        }
    };
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecognizer.3
        @Override // com.iflytek.cloud.ui.RecognizerDialogListener
        public void onResult(RecognizerResult results, boolean isLast) {
            LogTool.d("");
            if (SpeechRecognizer.this.mSpeechResult != null) {
                SpeechRecognizer.this.mSpeechResult.append(results.getResultString());
            }
            if (SpeechRecognizer.this.mISpeechMsgDealer != null && isLast) {
                LogTool.d(SpeechRecognizer.this.mSpeechResult.toString());
                SpeechRecognizer.this.mISpeechMsgDealer.pushSpeechInfo(SpeechRecognizer.this.mSpeechResult.toString(), 1);
                SpeechRecognizer.this.mSpeechResult = null;
            }
        }

        @Override // com.iflytek.cloud.ui.RecognizerDialogListener
        public void onError(SpeechError error) {
            LogTool.e(error.getPlainDescription(true));
            SpeechRecognizer.this.mISpeechMsgDealer.pushSpeechInfo(error.getErrorDescription(), 0);
            SpeechRecognizer.this.mRecognizerDialog.dismiss();
        }
    };

    public void setSpeechDeal(ISpeechMsgDealer dealer) {
        this.mISpeechMsgDealer = dealer;
    }

    public void run(Activity ownerActivity) {
        startRecognitionOnUiMode(ownerActivity);
    }

    public static void loginServer(Context context) {
        SpeechUtility.createUtility(context, "appid=538d83c8");
    }

    private void startRecognitionOnUiMode(Activity ownerActivity) {
        new Thread(new Runnable() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecognizer.4
            @Override // java.lang.Runnable
            public void run() {
                SpeechRecognizer.this.mISpeechMsgDealer.startSpeaking();
            }
        }).start();
        if (this.mSpeechResult != null) {
            this.mSpeechResult = null;
        }
        this.mSpeechResult = new StringBuffer();
        this.mRecognizerDialog = new RecognizerDialog(ownerActivity, this.mInitListener);
        this.mRecognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        this.mRecognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        this.mRecognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        this.mRecognizerDialog.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        this.mRecognizerDialog.setParameter(SpeechConstant.ASR_NBEST, "1");
        this.mRecognizerDialog.setParameter("asr_sch", "1");
        this.mRecognizerDialog.setParameter("rst", "json");
        this.mRecognizerDialog.setListener(this.mRecognizerDialogListener);
        this.mRecognizerDialog.setOnCancelListener(this.mRecognizerCancelListener);
        this.mRecognizerDialog.show();
    }
}
