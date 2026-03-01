package mktvsmart.screen.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.FitnessStatusCodes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import mktvsmart.screen.R;
import mktvsmart.screen.util.ConfigUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class UpdateManager {
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private static final int MAX_PROGRESS = 100;
    private static final String TAG = "UpdateManager";
    private static boolean cancelUpdate = false;
    private static boolean isUpdating = false;
    private Context mContext;
    private TextView mCurrProgressText;
    private Dialog mDownloadDialog;

    @SuppressLint({"HandlerLeak"})
    private Handler mHandler = new Handler() { // from class: mktvsmart.screen.update.UpdateManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    UpdateManager.this.mProgress.setProgress(UpdateManager.this.progress);
                    UpdateManager.this.mCurrProgressText.setText(String.valueOf(String.valueOf(UpdateManager.this.progress)) + "%");
                    break;
                case 2:
                    UpdateManager.setUpdateCancle(true);
                    UpdateManager.isUpdating = false;
                    UpdateManager.this.installApk();
                    break;
            }
        }
    };
    HashMap<String, String> mHashMap;
    private ProgressBar mProgress;
    private String mSavePath;
    Dialog noticeDialog;
    private int progress;

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public static boolean isUpdateCancle() {
        return cancelUpdate;
    }

    public static boolean isUpdateEnable(Context context) {
        return Boolean.valueOf(ConfigUtil.getInstance(context).getValue(ConfigUtil.UPDATE_ENABLE, "false")).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setUpdateCancle(boolean cancelUpdate2) {
        cancelUpdate = cancelUpdate2;
    }

    public void checkUpdate(final Activity activity, final boolean quiet) {
        if (!isUpdating) {
            setUpdateCancle(false);
            new Thread(new Runnable() { // from class: mktvsmart.screen.update.UpdateManager.2
                @Override // java.lang.Runnable
                public void run() throws ProtocolException, PackageManager.NameNotFoundException {
                    boolean bCanUpdate = UpdateManager.this.isUpdate();
                    System.out.println("bCanUpdate = " + bCanUpdate);
                    if (bCanUpdate) {
                        activity.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.update.UpdateManager.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                UpdateManager.this.showNoticeDialog();
                            }
                        });
                    } else if (!quiet) {
                        activity.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.update.UpdateManager.2.2
                            @Override // java.lang.Runnable
                            public void run() {
                                Toast.makeText(UpdateManager.this.mContext, R.string.str_latest_ver_tips, 0).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static int versionCompare(String serverVersion, String localVersion) {
        String[] sv = serverVersion.split("\\.");
        String[] lv = localVersion.split("\\.");
        if (3 != sv.length) {
            return -1;
        }
        if (3 != lv.length) {
            return 1;
        }
        int sMajor = Integer.valueOf(sv[0]).intValue();
        int sMinor = Integer.valueOf(sv[1]).intValue();
        int sRevision = Integer.valueOf(sv[2]).intValue();
        int lMajor = Integer.valueOf(lv[0]).intValue();
        int lMinor = Integer.valueOf(lv[1]).intValue();
        int lRevision = Integer.valueOf(lv[2]).intValue();
        if (sMajor > lMajor || sMinor > lMinor || sRevision > lRevision) {
            return 1;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUpdate() throws ProtocolException, PackageManager.NameNotFoundException {
        String path;
        String localCode = getVersionName(this.mContext);
        Log.i(TAG, "local version : " + localCode);
        ParseXmlService service = new ParseXmlService();
        try {
            path = ConfigUtil.getInstance(this.mContext).getValue(ConfigUtil.UPDATE_INFO_XML);
            Log.i(TAG, "check : " + path);
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            Log.e(TAG, "HttpHostConnectException");
        } catch (ConnectException e2) {
            e2.printStackTrace();
            Log.e(TAG, "ConnectException");
        } catch (SocketTimeoutException e3) {
            Log.e(TAG, "SocketTimeoutException");
            return false;
        } catch (ClientProtocolException e4) {
            e4.printStackTrace();
            Log.e(TAG, "ClientProtocolException");
        } catch (IOException e5) {
            e5.printStackTrace();
            Log.e(TAG, "IOException");
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        if (path == null) {
            return false;
        }
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(1000);
        conn.setRequestMethod("GET");
        conn.setReadTimeout(FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS);
        InputStream inStream = conn.getInputStream();
        this.mHashMap = service.parseXml(inStream);
        System.out.println("lmHashMap = " + this.mHashMap);
        if (this.mHashMap == null) {
            return false;
        }
        String serverCode = String.valueOf(this.mHashMap.get("version_name"));
        return versionCompare(serverCode, localCode) > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(R.string.str_software_update);
        builder.setMessage(R.string.str_sure_update_sw);
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.update.UpdateManager.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (UpdateManager.this.checkUpdateOnMarket()) {
                    return;
                }
                UpdateManager.this.showDownloadDialog();
                UpdateManager.isUpdating = true;
            }
        });
        builder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.update.UpdateManager.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.setUpdateCancle(true);
            }
        });
        builder.setCancelable(false);
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkUpdateOnMarket() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=" + this.mContext.getPackageName()));
        intent.setFlags(268435456);
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE);
        try {
            this.mContext.startActivity(intent);
            setUpdateCancle(true);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "ActivityNotFoundException");
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(R.string.str_software_update);
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View v = inflater.inflate(R.layout.update_progress_layout, (ViewGroup) null);
        this.mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        this.mCurrProgressText = (TextView) v.findViewById(R.id.cur_progress_text);
        builder.setView(v);
        builder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.update.UpdateManager.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.setUpdateCancle(true);
                UpdateManager.isUpdating = false;
            }
        });
        builder.setCancelable(false);
        this.mDownloadDialog = builder.create();
        this.mDownloadDialog.show();
        downloadApk();
    }

    private void downloadApk() {
        new downloadApkThread(this, null).start();
    }

    private class downloadApkThread extends Thread {
        private downloadApkThread() {
        }

        /* synthetic */ downloadApkThread(UpdateManager updateManager, downloadApkThread downloadapkthread) {
            this();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws IOException {
            try {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    String sdpath = Environment.getExternalStorageDirectory() + ServiceReference.DELIMITER;
                    UpdateManager.this.mSavePath = String.valueOf(sdpath) + "download";
                    URL url = new URL(UpdateManager.this.mHashMap.get("url"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(UpdateManager.this.mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(UpdateManager.this.mSavePath, UpdateManager.this.mHashMap.get("name"));
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte[] buf = new byte[1024];
                    while (true) {
                        int numread = is.read(buf);
                        count += numread;
                        UpdateManager.this.progress = (int) ((count / length) * 100.0f);
                        UpdateManager.this.mHandler.sendEmptyMessage(1);
                        if (numread <= 0) {
                            UpdateManager.this.mHandler.sendEmptyMessage(2);
                            break;
                        } else {
                            fos.write(buf, 0, numread);
                            if (UpdateManager.isUpdateCancle()) {
                                break;
                            }
                        }
                    }
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            UpdateManager.this.mDownloadDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void installApk() {
        File apkfile = new File(this.mSavePath, this.mHashMap.get("name"));
        if (apkfile.exists()) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.addFlags(268435456);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            this.mContext.startActivity(i);
        }
    }

    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packInfo = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo.versionName;
    }

    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packInfo = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo.versionCode;
    }
}
