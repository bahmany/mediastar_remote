package mktvsmart.screen.spectrum;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.games.GamesStatusCodes;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.R;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class SpeTestActivity extends Activity {
    private CreateSocket cSocket;
    private Button mButton;
    private Button mButton22k;
    private Button mButtonDiseqc;
    private Button mButtonREF;
    private Button mButtonSetting;
    private Button mButtonSpanAndCentFre;
    private Button mButtonVH;
    private CurSpeInfoManager mSpeInfoManager;
    private SpeRequestManager mSpectrumRequestManager;
    private TextView mTextView;
    private MessageProcessor msgProc;
    private Socket tcpSocket;
    private int mDvbsSpeVH = 0;
    private int mDvbsSpe22k = 0;
    private int mDvbsSpeSpan = 100;
    private int mCentFre = 1150;
    private int mOsdLen = 1024;
    private int mRef = 60;
    private int mDvbsSpeDiseqc = 0;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) throws UnsupportedEncodingException {
            switch (v.getId()) {
                case R.id.button_1 /* 2131493481 */:
                    if (SpeTestActivity.this.mOsdLen == 500) {
                        SpeTestActivity.this.mOsdLen = 1024;
                    } else {
                        SpeTestActivity.this.mOsdLen = 500;
                    }
                    SpeTestActivity.this.mSpectrumRequestManager.sendSpeInfoRequest(SpeTestActivity.this.mOsdLen);
                    break;
                case R.id.button_setting /* 2131493482 */:
                    SpeTestActivity.this.mSpectrumRequestManager.sendSpeSettingRequest();
                    break;
                case R.id.button_spe_vh /* 2131493483 */:
                    SpeTestActivity.this.mDvbsSpeVH = SpeTestActivity.this.mDvbsSpeVH != 0 ? 0 : 1;
                    SpeTestActivity.this.mSpectrumRequestManager.sendSetSpeVHRequest(SpeTestActivity.this.mDvbsSpeVH);
                    break;
                case R.id.button_spe_22k /* 2131493484 */:
                    SpeTestActivity.this.mDvbsSpe22k = SpeTestActivity.this.mDvbsSpe22k != 0 ? 0 : 1;
                    SpeTestActivity.this.mSpectrumRequestManager.sendSetSpe22kRequset(SpeTestActivity.this.mDvbsSpe22k);
                    break;
                case R.id.button_spe_ref /* 2131493485 */:
                    if (SpeTestActivity.this.mRef == 60) {
                        SpeTestActivity.this.mRef = KeyInfo.KEYCODE_ASK;
                    } else {
                        SpeTestActivity.this.mRef = 60;
                    }
                    SpeTestActivity.this.mSpectrumRequestManager.sendSetSpeRefRequest(SpeTestActivity.this.mRef);
                    break;
                case R.id.button_spe_span_and_centfre /* 2131493486 */:
                    if (SpeTestActivity.this.mCentFre == 1150) {
                        SpeTestActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(200, 1200);
                        SpeTestActivity.this.mCentFre = 1200;
                        break;
                    } else {
                        SpeTestActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(100, 1150);
                        SpeTestActivity.this.mCentFre = 1150;
                        break;
                    }
                case R.id.button_diseqc /* 2131493487 */:
                    SpeTestActivity.this.mDvbsSpeDiseqc++;
                    if (SpeTestActivity.this.mDvbsSpeDiseqc > 4) {
                        SpeTestActivity.this.mDvbsSpeDiseqc = 1;
                    }
                    SpeTestActivity.this.mSpectrumRequestManager.sendSetSpeDiseqcRequest(SpeTestActivity.this.mDvbsSpeDiseqc);
                    break;
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spe_test_activity);
        this.mButton = (Button) findViewById(R.id.button_1);
        this.mButtonSetting = (Button) findViewById(R.id.button_setting);
        this.mButtonVH = (Button) findViewById(R.id.button_spe_vh);
        this.mButtonREF = (Button) findViewById(R.id.button_spe_ref);
        this.mButtonDiseqc = (Button) findViewById(R.id.button_diseqc);
        this.mButton22k = (Button) findViewById(R.id.button_spe_22k);
        this.mButtonSpanAndCentFre = (Button) findViewById(R.id.button_spe_span_and_centfre);
        this.mTextView = (TextView) findViewById(R.id.textview_1);
        this.mSpectrumRequestManager = SpeRequestManager.getInstance();
        this.mSpeInfoManager = CurSpeInfoManager.getInstance();
        setMessageProcess();
        try {
            this.cSocket = new CreateSocket(null, 0);
            this.tcpSocket = this.cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mButton.setOnClickListener(this.mOnClickListener);
        this.mButtonSetting.setOnClickListener(this.mOnClickListener);
        this.mButtonVH.setOnClickListener(this.mOnClickListener);
        this.mButtonREF.setOnClickListener(this.mOnClickListener);
        this.mButtonDiseqc.setOnClickListener(this.mOnClickListener);
        this.mButton22k.setOnClickListener(this.mOnClickListener);
        this.mButtonSpanAndCentFre.setOnClickListener(this.mOnClickListener);
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_REQUEST_SPECTRUM_INFO, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                SpeTestActivity.this.mSpeInfoManager.setCurrentSpectrumInfo(recvData);
                Toast.makeText(SpeTestActivity.this, "get spetestActivity info", 1).show();
                Log.i("", "spetest currentStartFre = " + SpeTestActivity.this.mSpeInfoManager.getCurrentStartFre());
                Log.i("", "spetest currentEndFre = " + SpeTestActivity.this.mSpeInfoManager.getCurrentEndFre());
                Log.i("", "spetest currentRetDbuv length = " + SpeTestActivity.this.mSpeInfoManager.getCurrentRetDbuv().length);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_REF, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SpeTestActivity.this, "GMS_MSG_SPE_DO_SET_REF , " + msg.arg2, 1).show();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_22K, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SpeTestActivity.this, "GMS_MSG_SPE_DO_SET_STATE_22K , " + msg.arg2, 1).show();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_VH, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.5
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SpeTestActivity.this, "GMS_MSG_SPE_DO_SET_STATE_VH , " + msg.arg2, 1).show();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SpeTestActivity.this, "GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE , " + msg.arg2, 1).show();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_DISEQC, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.7
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SpeTestActivity.this, "GMS_MSG_SPE_DO_SET_STATE_DISEQC , " + msg.arg2, 1).show();
            }
        });
        this.msgProc.setOnMessageProcess(302, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.SpeTestActivity.8
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                SpeTestActivity.this.mSpeInfoManager.setCurrentSpectrumSetting(recvData);
                Toast.makeText(SpeTestActivity.this, "get spetrumsetting", 1).show();
                Log.i("", "spetest currentspevh = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpeVH());
                Log.i("", "spetest currentspe22kon = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpe22kOn());
                Log.i("", "spetest currentspediseqc = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpeDiseqc());
                Log.i("", "spetest currentspespan = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpeSpan());
                Log.i("", "spetest currentspecentfre = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpeCentFre());
                Log.i("", "spetest currentsperef = " + SpeTestActivity.this.mSpeInfoManager.getCurrentSpeRef());
            }
        });
    }
}
