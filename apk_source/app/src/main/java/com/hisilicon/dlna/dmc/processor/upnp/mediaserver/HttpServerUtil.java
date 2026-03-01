package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import org.cybergarage.xml.XML;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.Res;

/* loaded from: classes.dex */
public class HttpServerUtil {
    public static final String LOOP = "127.0.0.1";
    public static String HOST = LOOP;
    public static int PORT = 38523;

    public static void updateHostAddress(NetworkInterface ni) {
        HOST = getHostAddressFrom(ni);
    }

    public static String getHostAddressFrom(NetworkInterface ni) {
        if (ni == null) {
            return LOOP;
        }
        List<InetAddress> inets = Collections.list(ni.getInetAddresses());
        for (InetAddress inet : inets) {
            if (inet instanceof Inet4Address) {
                return inet.getHostAddress();
            }
        }
        return LOOP;
    }

    public static String createLinkWithId(String id) {
        return "http://" + HOST + ":" + PORT + ServiceReference.DELIMITER + id;
    }

    public static String makeMediaId(String path) {
        if (path == null) {
            return null;
        }
        String uriPath = path;
        if (path.startsWith(ServiceReference.DELIMITER)) {
            uriPath = path.substring(1);
        }
        try {
            return URLEncoder.encode(uriPath, XML.CHARSET_UTF8);
        } catch (Exception e) {
            e.printStackTrace();
            return uriPath;
        }
    }

    public static boolean isSTBDMR(Device device) {
        if (device instanceof RemoteDevice) {
            URL descriptorURL = ((RemoteDevice) device).getIdentity().getDescriptorURL();
            if (descriptorURL.getHost().equals(HiMultiscreen.getSTBIP())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSTBMultiRoom(Device device) {
        if (device instanceof RemoteDevice) {
            URL descriptorURL = ((RemoteDevice) device).getIdentity().getDescriptorURL();
            if (descriptorURL.getHost().equals(HiMultiscreen.getSTBIP()) && device.getDetails().getFriendlyName().contains("HiMultiRoom")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRemoteMultiRoom(Device device) {
        return (device instanceof RemoteDevice) && device.getDetails().getFriendlyName().contains("HiMultiRoom");
    }

    public static boolean mediaFromLocal(String url) {
        if (url == null) {
            return false;
        }
        Uri uri = Uri.parse(url);
        return mediaFromLocal(uri);
    }

    public static boolean mediaFromLocal(Uri uri) {
        return loopHost() || uriFromLoop(uri) || uriFromLocal(uri);
    }

    private static boolean loopHost() {
        return LOOP.equals(HOST);
    }

    private static boolean uriFromLoop(Uri uri) {
        return uri != null && LOOP.equals(uri.getHost()) && PORT == uri.getPort();
    }

    private static boolean uriFromLocal(Uri uri) {
        return uri != null && HOST.equals(uri.getHost()) && PORT == uri.getPort();
    }

    public static String getUrlFrom(DIDLObject didlObject) {
        List<Res> reses = didlObject.getResources();
        return reses.isEmpty() ? "" : reses.get(0).getValue();
    }

    public static String[] queryImageThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "_data"};
        String[] str = queryThumbnail(uri, projection, "image_id=?", selectionArgs);
        return str;
    }

    public static String[] queryVideoThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "_data"};
        String[] str = queryThumbnail(uri, projection, "video_id=?", selectionArgs);
        return str;
    }

    public static String[] queryAudioThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "album_art"};
        String[] str = queryThumbnail(uri, projection, "_id=?", selectionArgs);
        return str;
    }

    public static String[] queryThumbnail(Uri uri, String[] projection, String selection, String[] selectionArgs) {
        Cursor thumbCursor = HiMultiscreen.getResolver().query(uri, projection, selection, selectionArgs, null);
        String[] str = new String[2];
        if (thumbCursor != null && thumbCursor.getCount() != 0) {
            thumbCursor.moveToFirst();
            int columnId = thumbCursor.getColumnIndex(projection[0]);
            int columnData = thumbCursor.getColumnIndex(projection[1]);
            str[0] = new StringBuilder(String.valueOf(thumbCursor.getLong(columnId))).toString();
            str[1] = thumbCursor.getString(columnData);
        }
        if (thumbCursor != null) {
            thumbCursor.close();
        }
        return str;
    }
}
