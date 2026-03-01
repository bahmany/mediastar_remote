package com.hisilicon.dlna.dmc.utility;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.cybergarage.http.HTTP;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.DIDLAttribute;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

/* loaded from: classes.dex */
public class Utility {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type = null;
    private static final int baseHeight = 1280;
    private static final int baseWidth = 720;
    public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String CACHE_ROOT = HiMultiscreen.getApplication().getCacheDir().getAbsolutePath();
    private static final String TAG = Utility.class.getName();

    static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type() {
        int[] iArr = $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type;
        if (iArr == null) {
            iArr = new int[PlaylistItem.Type.valuesCustom().length];
            try {
                iArr[PlaylistItem.Type.AUDIO_LOCAL.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[PlaylistItem.Type.AUDIO_REMOTE.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[PlaylistItem.Type.IMAGE_LOCAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[PlaylistItem.Type.IMAGE_REMOTE.ordinal()] = 6;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[PlaylistItem.Type.UNKNOW.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[PlaylistItem.Type.VIDEO_LOCAL.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[PlaylistItem.Type.VIDEO_REMOTE.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type = iArr;
        }
        return iArr;
    }

    public static int getBaseHeight() {
        return 1280;
    }

    public static int getBaseWidth() {
        return baseWidth;
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) HiMultiscreen.getApplication().getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) HiMultiscreen.getApplication().getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static float getScreenDensity() {
        WindowManager windowManager = (WindowManager) HiMultiscreen.getApplication().getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    public static int getMaxImageDimension() {
        int height = getScreenHeight();
        int width = getScreenWidth();
        return height > width ? height : width;
    }

    public static int getImageDimension() {
        return getMaxImageDimension() / 2;
    }

    public static int getSmallImageDimension() {
        return getImageDimension() / 2;
    }

    public static String getDMRPushName(String dmrName) {
        return "/“ " + getDeviceFriendlyName(dmrName) + "/” Playing";
    }

    public static String getDMSToastName(String dmsName) {
        return String.valueOf(getDeviceFriendlyName(dmsName)) + " already on-line";
    }

    public static String getDeviceFriendlyName(String deviceFriendlyName) {
        String friendlyName = deviceFriendlyName;
        int lastIndex = deviceFriendlyName.lastIndexOf(":");
        if (lastIndex > 0) {
            if (deviceFriendlyName.endsWith(":")) {
                friendlyName = deviceFriendlyName.substring(0, lastIndex);
                lastIndex = friendlyName.lastIndexOf(":");
            }
            if (lastIndex > 0) {
                return friendlyName.substring(lastIndex + 1);
            }
            return friendlyName;
        }
        return friendlyName;
    }

    public static float getScaleWithBaseScreen() {
        int height = getScreenHeight();
        int width = getScreenWidth();
        if (height > width) {
            float heightScale = (height * 1.0f) / 1280.0f;
            float widthScale = (width * 1.0f) / 720.0f;
            return heightScale < widthScale ? heightScale : widthScale;
        }
        float heightScale2 = (width * 1.0f) / 1280.0f;
        float widthScale2 = (height * 1.0f) / 720.0f;
        return heightScale2 < widthScale2 ? heightScale2 : widthScale2;
    }

    public static float getWidthScaleWithBaseScreen(int width) {
        int h = getScreenHeight();
        int w = getScreenWidth();
        return h > w ? ((width * 1.0f) * w) / 720.0f : ((width * 1.0f) * h) / 720.0f;
    }

    public static String getString(int resId) {
        return HiMultiscreen.getApplication().getString(resId);
    }

    public static String[] getStringArray(int resId) {
        return HiMultiscreen.getApplication().getResources().getStringArray(resId);
    }

    public static void downToast(Toast toast, Context context, int resId) {
        downToast(toast, context, context.getString(resId));
    }

    public static void downToast(Toast toast, Context context, String msg) {
        TextView text = new TextView(context);
        text.setText(msg);
        LinearLayout layout = new LinearLayout(context.getApplicationContext());
        layout.setGravity(17);
        layout.setBackgroundResource(R.drawable.toast_frame);
        layout.addView(text);
        toast.setView(layout);
        toast.setGravity(80, 0, 100);
        toast.show();
    }

    public static float getRawSize(int unit, float size, Context context) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public static CheckResult checkItemURL(PlaylistItem item) throws ProtocolException {
        CheckResult result = new CheckResult(item, false);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(item.getUrl()).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(HTTP.HEAD);
            result.setReachable(connection.getResponseCode() == 200);
        } catch (Exception e) {
            Log.w(TAG, "check fail, url = " + item.getUrl());
        }
        return result;
    }

    public static boolean checkURL(String url) throws ProtocolException {
        if (url == null || url.trim().equals("")) {
            return false;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(HTTP.HEAD);
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            Log.w(TAG, "check fail, url = " + url);
            return false;
        }
    }

    public static String createMetaData(String title, PlaylistItem.Type type, String url) {
        Item item = null;
        Res res = new Res(new ProtocolInfo("*:*:*:*"), (Long) 0L, url);
        switch ($SWITCH_TABLE$com$hisilicon$dlna$dmc$processor$model$PlaylistItem$Type()[type.ordinal()]) {
            case 1:
            case 5:
                item = new VideoItem("0", "0", title, "", res);
                break;
            case 2:
            case 4:
                item = new AudioItem("0", "0", title, "", res);
                break;
            case 3:
            case 6:
                item = new ImageItem("0", "0", title, "", res);
                break;
        }
        return createMetaData(item);
    }

    public static String createVideoMetaData(String title, String url, String thumbUrl) {
        Res res = new Res(new ProtocolInfo("*:*:*:*"), (Long) 0L, url);
        Item item = new VideoItem("0", "0", title, "", res);
        if (thumbUrl != null && thumbUrl.trim().equals("")) {
            addObjectAlbumArtProperty(item, thumbUrl);
        }
        return createMetaData(item);
    }

    public static void addObjectAlbumArtProperty(DIDLObject didlobject, String link) {
        try {
            URI uri = new URI(link);
            ArrayList arraylist = new ArrayList();
            arraylist.add(new DIDLObject.Property.DLNA.PROFILE_ID(new DIDLAttribute(DIDLObject.Property.DLNA.NAMESPACE.URI, Descriptor.Device.DLNA_PREFIX, "PNG_TN")));
            didlobject.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(uri, arraylist));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String createMetaData(DIDLObject didlObject) {
        if (didlObject != null) {
            DIDLParser ps = new DIDLParser();
            DIDLContent ct = new DIDLContent();
            if (didlObject instanceof Item) {
                ct.addItem((Item) didlObject);
            } else if (didlObject instanceof Container) {
                ct.addContainer((Container) didlObject);
            }
            try {
                return ps.generate(ct);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static DIDLContent createDIDL(String MetaData) {
        DIDLParser parser = new DIDLParser();
        try {
            return parser.parse(MetaData);
        } catch (Exception e) {
            return null;
        }
    }

    public static MimeType getMimeType(Item item) {
        ProtocolInfo protocolInfo = item.getFirstResource().getProtocolInfo();
        return protocolInfo.getContentFormatMimeType();
    }

    public static class CheckResult {
        private PlaylistItem item;
        private boolean reachable;

        public CheckResult(PlaylistItem item, boolean reachable) {
            this.item = item;
            this.reachable = reachable;
        }

        public PlaylistItem getItem() {
            return this.item;
        }

        public void setItem(PlaylistItem item) {
            this.item = item;
        }

        public boolean isReachable() {
            return this.reachable;
        }

        public void setReachable(boolean reachable) {
            this.reachable = reachable;
        }
    }

    public static long timeStringToMinute(String s) {
        if (s == null) {
            return 0L;
        }
        String[] split = s.split(":");
        if (split.length == 1) {
            return 0L;
        }
        if (split.length == 2) {
            try {
                return Integer.parseInt(split[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
                return 0L;
            }
        }
        if (split.length < 3) {
            return 0L;
        }
        try {
            long hour = Integer.parseInt(split[0]);
            long minite = Integer.parseInt(split[1]);
            return (60 * hour) + minite;
        } catch (Exception ex2) {
            ex2.printStackTrace();
            return 0L;
        }
    }

    public static final byte[] readStreamAsByte(InputStream inputStream) throws MyException, IOException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            while (true) {
                int len = inputStream.read(bytes);
                if (len != -1) {
                    output.write(bytes, 0, len);
                } else {
                    inputStream.close();
                    output.flush();
                    output.close();
                    return output.toByteArray();
                }
            }
        } catch (Error ex) {
            throw new MyException(ex);
        } catch (Exception ex2) {
            throw new MyException(ex2);
        }
    }

    public static boolean isNetWork(Context context) {
        NetworkInfo[] info;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager != null && (info = connectivityManager.getAllNetworkInfo()) != null) {
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
