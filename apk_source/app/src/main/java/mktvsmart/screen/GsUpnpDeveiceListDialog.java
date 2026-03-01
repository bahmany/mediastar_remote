package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.IOException;
import java.util.ArrayList;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;

/* loaded from: classes.dex */
public class GsUpnpDeveiceListDialog extends Dialog {
    private ArrayList<GsMobileLoginInfo> historyStbInfoList;
    private Activity mActivity;
    private Button mCancelButton;
    private String mConnectIp;
    private int mConnectPort;
    private Context mContext;
    private histroyAdapter mDeveiceListAdapter;
    private ListView mDeveiceListView;
    private Intent mIntent;
    private TextView mTitleTextView;
    private ArrayList<GsMobileLoginInfo> mUpnpDeveiceList;
    private String mUpnpIp;
    private ADSProgressDialog waitDialog;

    public GsUpnpDeveiceListDialog(Activity activity, Context context, ArrayList<GsMobileLoginInfo> upnpDeveiceList, String upnpIp) {
        super(context, R.style.dialog);
        this.mUpnpDeveiceList = new ArrayList<>();
        this.historyStbInfoList = new ArrayList<>();
        this.mContext = context;
        this.mActivity = activity;
        this.mUpnpDeveiceList = (ArrayList) upnpDeveiceList.clone();
        this.mUpnpIp = upnpIp;
        setContentView(R.layout.login_history_layout);
        initView();
        initData();
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        this.mTitleTextView = (TextView) findViewById(R.id.sys_settings_title);
        this.mTitleTextView.setText(this.mUpnpIp);
        this.mCancelButton = (Button) findViewById(R.id.history_cancel_btn);
        this.mCancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsUpnpDeveiceListDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsUpnpDeveiceListDialog.this.dismiss();
            }
        });
        this.mDeveiceListView = (ListView) findViewById(R.id.login_history_list);
        this.mDeveiceListAdapter = new histroyAdapter(this.mContext);
        this.mDeveiceListView.setAdapter((ListAdapter) this.mDeveiceListAdapter);
        this.mDeveiceListView.setOnItemClickListener(new AnonymousClass2());
    }

    /* renamed from: mktvsmart.screen.GsUpnpDeveiceListDialog$2, reason: invalid class name */
    class AnonymousClass2 implements AdapterView.OnItemClickListener {
        AnonymousClass2() {
        }

        /* JADX WARN: Type inference failed for: r1v11, types: [mktvsmart.screen.GsUpnpDeveiceListDialog$2$1] */
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GsUpnpDeveiceListDialog.this.waitDialog = DialogBuilder.showProgressDialog(GsUpnpDeveiceListDialog.this.mContext, GsUpnpDeveiceListDialog.this.mContext.getString(R.string.Logining), GsUpnpDeveiceListDialog.this.mContext.getString(R.string.please_wait), true);
            try {
                GsUpnpDeveiceListDialog.this.mConnectIp = ((GsMobileLoginInfo) GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.get(position)).getStb_ip_address_disp();
                GsUpnpDeveiceListDialog.this.mConnectPort = ((GsMobileLoginInfo) GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.get(position)).getUpnpPort();
                new Thread() { // from class: mktvsmart.screen.GsUpnpDeveiceListDialog.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() throws InterruptedException, IOException {
                        super.run();
                        Looper.prepare();
                        System.out.println("click ip Address : " + GsUpnpDeveiceListDialog.this.mConnectIp);
                        final GsMobileLoginInfo loginInfoTemp = GsConnectToSTB.upnpConnectToServer(GsUpnpDeveiceListDialog.this.mConnectIp, GsUpnpDeveiceListDialog.this.mConnectPort, 0);
                        GMScreenGlobalInfo.setmCurStbInfo(loginInfoTemp);
                        ((Activity) GsUpnpDeveiceListDialog.this.mContext).runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsUpnpDeveiceListDialog.2.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (loginInfoTemp.getmConnectStatus() > 0) {
                                    EditLoginHistoryFile mEditLoginHistoryFile = new EditLoginHistoryFile(GsUpnpDeveiceListDialog.this.mContext);
                                    mEditLoginHistoryFile.getListFromFile(GsUpnpDeveiceListDialog.this.historyStbInfoList);
                                    loginInfoTemp.setStb_ip_address_disp(GsUpnpDeveiceListDialog.this.mConnectIp);
                                    mEditLoginHistoryFile.putListToFile(loginInfoTemp, GsUpnpDeveiceListDialog.this.historyStbInfoList);
                                    GMScreenGlobalInfo.setmCurStbInfo(loginInfoTemp);
                                    Log.d("cur_stb_info Platform_id", new StringBuilder().append(loginInfoTemp.getPlatform_id()).toString());
                                    GsUpnpDeveiceListDialog.this.mIntent = new Intent();
                                    GsUpnpDeveiceListDialog.this.mIntent.putExtra("Address", GsUpnpDeveiceListDialog.this.mConnectIp);
                                    GsUpnpDeveiceListDialog.this.mIntent.putExtra("Port", GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                                    GsUpnpDeveiceListDialog.this.mIntent.setClass(GsUpnpDeveiceListDialog.this.mContext, GsMainMenuActivity.class);
                                    GsUpnpDeveiceListDialog.this.mContext.startActivity(GsUpnpDeveiceListDialog.this.mIntent);
                                    if (GsUpnpDeveiceListDialog.this.waitDialog.isShowing()) {
                                        GsUpnpDeveiceListDialog.this.waitDialog.dismiss();
                                    }
                                    GsUpnpDeveiceListDialog.this.dismiss();
                                    GsUpnpDeveiceListDialog.this.mActivity.finish();
                                    return;
                                }
                                if (loginInfoTemp.getmConnectStatus() < 0) {
                                    if (GsUpnpDeveiceListDialog.this.waitDialog.isShowing()) {
                                        GsUpnpDeveiceListDialog.this.waitDialog.dismiss();
                                    }
                                    GsConnectToSTB.makeTextForConnectError(GsUpnpDeveiceListDialog.this.mContext, loginInfoTemp.getmConnectStatus());
                                }
                            }
                        });
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
    }

    private class histroyAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public histroyAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.auto_login_item, (ViewGroup) null);
            }
            TextView modelString = (TextView) convertView.findViewById(R.id.login_item_model);
            TextView snString = (TextView) convertView.findViewById(R.id.login_item_sn);
            if (position == parent.getChildCount()) {
                ViewGroup.LayoutParams lp = modelString.getLayoutParams();
                lp.width = KeyInfo.KEYCODE_DOUBLE_QUOTATION;
                lp.height = -1;
                modelString.setLayoutParams(lp);
                snString.setLayoutParams(lp);
                modelString.setTextSize(14.0f);
                snString.setTextSize(14.0f);
                modelString.setText(((GsMobileLoginInfo) GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.get(position)).getModel_name());
                snString.setText(((GsMobileLoginInfo) GsUpnpDeveiceListDialog.this.mUpnpDeveiceList.get(position)).getStb_sn_disp());
            }
            return convertView;
        }
    }
}
