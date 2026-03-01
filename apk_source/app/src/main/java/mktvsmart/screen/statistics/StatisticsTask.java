package mktvsmart.screen.statistics;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
public class StatisticsTask extends Thread {
    private Boolean bInterrupt;
    List<NameValuePair> nameValuePairs;
    private String TAG = StatisticsTask.class.getSimpleName();
    private String mSessionID = null;
    private final String mHttpLoginUrl = "http://www.g-appmarket.com:8080/Statistics/terminal_login.do";
    private final String mHttpLogoutUrl = "http://www.g-appmarket.com:8080/Statistics/terminal_logout.do";
    private final String VALUE_MODULE = "moduleName";
    private final String VALUE_SN = "sn";
    private final String VALUE_IMEI = "imei";
    private final String VALUE_REGION = "region";
    private final String mCookieName = "JSESSIONID";

    public StatisticsTask(Context context, String sn) {
        this.nameValuePairs = null;
        this.bInterrupt = false;
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        this.nameValuePairs = new ArrayList(4);
        this.nameValuePairs.add(new BasicNameValuePair("moduleName", Build.MODEL));
        this.nameValuePairs.add(new BasicNameValuePair("sn", sn));
        if (tm.getDeviceId() != null) {
            this.nameValuePairs.add(new BasicNameValuePair("imei", tm.getDeviceId()));
        }
        if (tm.getNetworkCountryIso() != null) {
            this.nameValuePairs.add(new BasicNameValuePair("region", tm.getNetworkCountryIso()));
        }
        this.bInterrupt = false;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws ParseException, IOException {
        HttpPost request;
        while (true) {
            if (!this.bInterrupt.booleanValue()) {
                request = new HttpPost("http://www.g-appmarket.com:8080/Statistics/terminal_login.do");
                request.setHeader("x-requested-with", "XMLHttpRequest");
                if (this.mSessionID != null) {
                    request.setHeader("Cookie", "JSESSIONID=" + this.mSessionID);
                    Log.i(this.TAG, "JSESSIONID = " + this.mSessionID);
                }
                try {
                    request.setEntity(new UrlEncodedFormEntity(this.nameValuePairs));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                request = new HttpPost("http://www.g-appmarket.com:8080/Statistics/terminal_logout.do");
                request.setHeader("x-requested-with", "XMLHttpRequest");
                if (this.mSessionID != null) {
                    request.setHeader("Cookie", "JSESSIONID=" + this.mSessionID);
                    Log.i(this.TAG, "JSESSIONID=" + this.mSessionID);
                }
            }
            DefaultHttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(request);
                int code = response.getStatusLine().getStatusCode();
                Log.i(this.TAG, "************  " + code);
                HttpEntity entity = response.getEntity();
                String ret = EntityUtils.toString(entity);
                Log.i(this.TAG, "&&&&&&&&&&&&&  " + ret);
                List<Cookie> cookies = client.getCookieStore().getCookies();
                if (cookies.isEmpty()) {
                    Log.i(this.TAG, "-------Cookie NONE---------");
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= cookies.size()) {
                            break;
                        }
                        if (!"JSESSIONID".equals(cookies.get(i).getName())) {
                            i++;
                        } else {
                            this.mSessionID = cookies.get(i).getValue();
                            break;
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            synchronized (this.bInterrupt) {
                if (this.bInterrupt.booleanValue()) {
                    Log.i(this.TAG, "exit statistics = " + this.bInterrupt);
                    return;
                }
                try {
                    this.bInterrupt.wait(300000L);
                } catch (InterruptedException e3) {
                }
            }
        }
    }

    public void stopTask() {
        synchronized (this.bInterrupt) {
            this.bInterrupt = true;
            this.bInterrupt.notify();
        }
    }
}
