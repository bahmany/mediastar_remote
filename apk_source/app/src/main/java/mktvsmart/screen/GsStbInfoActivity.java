package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertStbInfoModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.AdsBinnerView;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class GsStbInfoActivity extends Activity {
    private String NetAddress;
    private int NetPort;
    private Button backButton;
    private FrameLayout mAdSpaceFrame;
    private TextView mChannelNumText;
    private TextView mProductedNameText;
    private TextView mSerialNumberText;
    private TextView mSoftwareVersionText;
    private TextView mStbStateText;
    private MessageProcessor msgProc;
    private DataParser parser;
    private Socket tcpSocket;
    private List<DataConvertStbInfoModel> mStbInfoModeList = null;
    Handler handler = null;
    private AdView mAdView = null;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.NetAddress = intent.getStringExtra("Address");
        this.NetPort = intent.getIntExtra("Port", GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        try {
            CreateSocket cSocket = new CreateSocket(this.NetAddress, this.NetPort);
            this.tcpSocket = cSocket.GetSocket();
            GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.handler = new Handler(new Handler.Callback() { // from class: mktvsmart.screen.GsStbInfoActivity.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                DataConvertStbInfoModel model = (DataConvertStbInfoModel) GsStbInfoActivity.this.mStbInfoModeList.get(0);
                GsStbInfoActivity.this.mProductedNameText.setText(model.getmProductName());
                GsStbInfoActivity.this.mSoftwareVersionText.setText(model.getmSoftwareVersion());
                GsStbInfoActivity.this.mSerialNumberText.setText(model.getmSerialNumber());
                GsStbInfoActivity.this.mChannelNumText.setText(String.valueOf(model.getmChannelNum()) + ServiceReference.DELIMITER + model.getmMaxNumOfPrograms());
                GsStbInfoActivity.this.mStbStateText.setText(model.getmStbStatus() == 0 ? R.string.standby_mode : R.string.working_mode);
                return false;
            }
        });
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(15, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsStbInfoActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        GsStbInfoActivity.this.parser = ParserFactory.getParser();
                        Bundle data = msg.getData();
                        byte[] recv_data = data.getByteArray("ReceivedData");
                        InputStream istream = new ByteArrayInputStream(recv_data, 0, recv_data.length);
                        GsStbInfoActivity.this.mStbInfoModeList = GsStbInfoActivity.this.parser.parse(istream, 14);
                        GsStbInfoActivity.this.handler.sendEmptyMessage(0);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsStbInfoActivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsStbInfoActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent2 = new Intent();
                intent2.setClass(GsStbInfoActivity.this, GsLoginListActivity.class);
                GsStbInfoActivity.this.startActivity(intent2);
                GsStbInfoActivity.this.finish();
            }
        });
        setContentView(R.layout.stb_info_layout);
        this.mProductedNameText = (TextView) findViewById(R.id.stb_info_product_name);
        this.mSoftwareVersionText = (TextView) findViewById(R.id.stb_info_software_version);
        this.mSerialNumberText = (TextView) findViewById(R.id.stb_info_serial_number);
        this.mChannelNumText = (TextView) findViewById(R.id.stb_info_channel_number);
        this.mStbStateText = (TextView) findViewById(R.id.stb_info_status);
        this.backButton = (Button) findViewById(R.id.back_stb_info);
        this.mAdSpaceFrame = (FrameLayout) findViewById(R.id.ad_space);
        this.mAdView = new AdsBinnerView(this).getAdView();
        this.mAdSpaceFrame.addView(this.mAdView, -2, -2);
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsStbInfoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsStbInfoActivity.this.onBackPressed();
            }
        });
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mAdSpaceFrame.removeView(this.mAdView);
    }
}
