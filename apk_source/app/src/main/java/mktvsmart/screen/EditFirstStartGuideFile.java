package mktvsmart.screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import mktvsmart.screen.util.ConfigUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class EditFirstStartGuideFile {
    private static final int DEFAULT_VERSION_CODE = -1;
    public static final int FIRST_START = 0;
    private static final String KEY_PICTURE_SHOW_NUM = "picture_show_num";
    private static final String KEY_SHOW_START_PICTURE = "show_start_picture";
    private static final String KEY_START_PICTURE_FILE_PATH = "start_picture_file_path";
    private static final String KEY_VERSION_CODE = "version_code";
    public static final int MAX_NUM_OF_PICTURE_SHOW = 3;
    public static final int REMAINED = 2;
    private static final String SHARED_PREFERENCES_NAME = "first_start_pref";
    public static final int UPDATED = 1;
    private Context mContext;

    public EditFirstStartGuideFile(Context context) {
        this.mContext = context;
    }

    public int getGuideValue() throws PackageManager.NameNotFoundException {
        try {
            PackageInfo info = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            int curVersionCode = info.versionCode;
            int lastVersionCode = this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 1).getInt(KEY_VERSION_CODE, -1);
            if (lastVersionCode == -1) {
                setVersionCode(curVersionCode);
                return 0;
            }
            if (curVersionCode != lastVersionCode) {
                setVersionCode(curVersionCode);
                return 1;
            }
            return 2;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Cannot find package name: " + this.mContext.getPackageName(), e);
        }
    }

    private void setVersionCode(int versionCode) {
        SharedPreferences settings = this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_VERSION_CODE, versionCode);
        editor.apply();
    }

    public void setPictureShowNum(int pictureShowNum) {
        SharedPreferences settings = this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_PICTURE_SHOW_NUM, pictureShowNum);
        editor.apply();
    }

    public int getPictureShowNum() {
        return this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 1).getInt(KEY_PICTURE_SHOW_NUM, 0);
    }

    public boolean showStartPicture() {
        return this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 1).getBoolean(KEY_SHOW_START_PICTURE, false);
    }

    public String getStartPicture() {
        return this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 1).getString(KEY_START_PICTURE_FILE_PATH, null);
    }

    public void retrieveStartPictureInBackground() {
        new RetrieveStartPictureThread(this.mContext).start();
    }

    private static final class RetrieveStartPictureThread extends Thread {
        private static final int CONNECT_TIMEOUT = 5000;
        private static final String KEY_IS_SHOW = "isShow";
        private static final String KEY_PICTURE_MD5 = "imgMD5";
        private static final String KEY_PICTURE_URL = "adImageUrl";
        private static final String KEY_PICTURE_VERSION = "imgVersion";
        private static final String SAVED_FILE_PATH_1 = "start-picture1.png";
        private static final String SAVED_FILE_PATH_2 = "start-picture2.png";
        private static final String SHARE_PREFERENCES_KEY_LOCAL_TIME = "PREVIOUS_STARTUP_TIME";
        private static final String SHARE_PREFERENCES_KEY_PICTURE_MD5 = "picture_md5";
        private static final String SHARE_PREFERENCES_KEY_PICTURE_VERSION = "start_picture_version";
        private static final int SO_TIMEOUT = 3000;
        private static final int TIME_INTERVAL = 12;
        private Context mContext;

        public RetrieveStartPictureThread(Context context) {
            this.mContext = context;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws IllegalStateException, JSONException, IOException {
            if (isIntervalOfTwelveHours()) {
                HttpUriRequest httpGet = new HttpGet(ConfigUtil.getInstance(this.mContext).getValue(ConfigUtil.START_PICTURE_URL));
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 3000);
                HttpClient client = new DefaultHttpClient(params);
                try {
                    HttpResponse httpResponse = client.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        InputStream is = httpResponse.getEntity().getContent();
                        String response = inputStream2String(is);
                        JSONObject jsonObject = new JSONObject(response);
                        boolean show = jsonObject.getBoolean(KEY_IS_SHOW);
                        saveShow(show);
                        if (show) {
                            int version = jsonObject.getInt(KEY_PICTURE_VERSION);
                            String imgMD5 = jsonObject.getString(KEY_PICTURE_MD5);
                            Log.d("EditFirstStartGuideFile", "version = " + version);
                            Log.d("EditFirstStartGuideFile", "imgMD5 = " + imgMD5);
                            if (md5Diff(imgMD5) || versionDiff(version)) {
                                String url = jsonObject.getString(KEY_PICTURE_URL);
                                Log.d("EditFirstStartGuideFile", "url = " + url);
                                savePicture(client.execute(new HttpGet(url)));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }

        private boolean versionDiff(int curVersion) {
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            int lastVersion = settings.getInt(SHARE_PREFERENCES_KEY_PICTURE_VERSION, -1);
            boolean diff = lastVersion != curVersion;
            if (diff) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SHARE_PREFERENCES_KEY_PICTURE_VERSION, curVersion);
                editor.apply();
            }
            return diff;
        }

        private boolean isIntervalOfTwelveHours() {
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            long previousDownloadTime = settings.getLong(SHARE_PREFERENCES_KEY_LOCAL_TIME, 0L);
            Date date = new Date();
            long currentTime = date.getTime();
            if (((currentTime - previousDownloadTime) / 3600000) % 24 >= 12 || previousDownloadTime == 0) {
                saveTime(currentTime);
                return true;
            }
            return false;
        }

        private void saveTime(long time) {
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(SHARE_PREFERENCES_KEY_LOCAL_TIME, time);
            editor.apply();
        }

        private boolean md5Diff(String imageMd5) {
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            String lastMD5 = settings.getString(SHARE_PREFERENCES_KEY_PICTURE_MD5, "");
            boolean diff = lastMD5.equals(imageMd5) ? false : true;
            if (diff) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SHARE_PREFERENCES_KEY_PICTURE_MD5, imageMd5);
                editor.apply();
            }
            return diff;
        }

        private String inputStream2String(InputStream inputStream) throws IOException {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    builder.append(line);
                } else {
                    inputStream.close();
                    return builder.toString();
                }
            }
        }

        private void saveShow(boolean show) {
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(EditFirstStartGuideFile.KEY_SHOW_START_PICTURE, show);
            editor.apply();
        }

        private void savePicture(HttpResponse response) throws IOException {
            String savePath;
            SharedPreferences settings = this.mContext.getSharedPreferences(EditFirstStartGuideFile.SHARED_PREFERENCES_NAME, 0);
            String savePath2 = settings.getString(EditFirstStartGuideFile.KEY_START_PICTURE_FILE_PATH, SAVED_FILE_PATH_2);
            if (savePath2.equals(SAVED_FILE_PATH_2)) {
                savePath = SAVED_FILE_PATH_1;
            } else {
                savePath = SAVED_FILE_PATH_2;
            }
            FileOutputStream fos = this.mContext.openFileOutput(savePath, 0);
            response.getEntity().writeTo(fos);
            fos.close();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(EditFirstStartGuideFile.KEY_START_PICTURE_FILE_PATH, savePath);
            editor.putInt(EditFirstStartGuideFile.KEY_PICTURE_SHOW_NUM, 0);
            editor.apply();
        }
    }
}
