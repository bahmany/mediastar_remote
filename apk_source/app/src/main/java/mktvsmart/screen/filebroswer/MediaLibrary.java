package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import mktvsmart.screen.util.BitmapUtils;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.util.Extensions;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public class MediaLibrary {
    public static final HashSet<String> FOLDER_BLACKLIST = new HashSet<>();
    public static final int MEDIA_ITEMS_UPDATED = 100;
    public static final String TAG = "VLC/MediaLibrary";
    private static MediaLibrary mInstance;
    private final ArrayList<MediaWrapper> mItemList;
    private final ReadWriteLock mItemListLock;
    protected Thread mLoadingThread;
    private final ArrayList<Handler> mUpdateHandler;
    private boolean isStopping = false;
    private boolean mRestart = false;
    private Handler restartHandler = new RestartHandler(this);

    static {
        String[] folder_blacklist = {"/alarms", "/notifications", "/ringtones", "/media/alarms", "/media/notifications", "/media/ringtones", "/media/audio/alarms", "/media/audio/notifications", "/media/audio/ringtones", "/Android/data/"};
        for (String item : folder_blacklist) {
            FOLDER_BLACKLIST.add(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + item);
        }
    }

    private MediaLibrary() {
        mInstance = this;
        this.mItemList = new ArrayList<>();
        this.mUpdateHandler = new ArrayList<>();
        this.mItemListLock = new ReentrantReadWriteLock();
    }

    public void loadMediaItems(Context context, boolean restart) {
        if (restart && isWorking()) {
            this.mRestart = true;
            this.isStopping = true;
        } else {
            loadMediaItems();
        }
    }

    public void loadMediaItems() {
        if (this.mLoadingThread == null || this.mLoadingThread.getState() == Thread.State.TERMINATED) {
            this.isStopping = false;
            VideoGridFragment.actionScanStart();
            this.mLoadingThread = new Thread(new GetMediaItemsRunnable());
            this.mLoadingThread.start();
        }
    }

    public void stop() {
        this.isStopping = true;
    }

    public boolean isWorking() {
        return (this.mLoadingThread == null || !this.mLoadingThread.isAlive() || this.mLoadingThread.getState() == Thread.State.TERMINATED || this.mLoadingThread.getState() == Thread.State.NEW) ? false : true;
    }

    public static synchronized MediaLibrary getInstance() {
        if (mInstance == null) {
            mInstance = new MediaLibrary();
        }
        return mInstance;
    }

    public void addUpdateHandler(Handler handler) {
        this.mUpdateHandler.add(handler);
    }

    public void removeUpdateHandler(Handler handler) {
        this.mUpdateHandler.remove(handler);
    }

    public ArrayList<MediaWrapper> getVideoItems() {
        ArrayList<MediaWrapper> videoItems = new ArrayList<>();
        this.mItemListLock.readLock().lock();
        for (int i = 0; i < this.mItemList.size(); i++) {
            MediaWrapper item = this.mItemList.get(i);
            if (item != null && item.getType() == 0) {
                videoItems.add(item);
            }
        }
        this.mItemListLock.readLock().unlock();
        return videoItems;
    }

    public ArrayList<MediaWrapper> getAudioItems() {
        ArrayList<MediaWrapper> audioItems = new ArrayList<>();
        this.mItemListLock.readLock().lock();
        for (int i = 0; i < this.mItemList.size(); i++) {
            MediaWrapper item = this.mItemList.get(i);
            if (item.getType() == 1) {
                audioItems.add(item);
            }
        }
        this.mItemListLock.readLock().unlock();
        return audioItems;
    }

    public ArrayList<MediaWrapper> getMediaItems() {
        return this.mItemList;
    }

    public MediaWrapper getMediaItem(String location) {
        this.mItemListLock.readLock().lock();
        for (int i = 0; i < this.mItemList.size(); i++) {
            MediaWrapper item = this.mItemList.get(i);
            if (item.getLocation().equals(location)) {
                this.mItemListLock.readLock().unlock();
                return item;
            }
        }
        this.mItemListLock.readLock().unlock();
        return null;
    }

    public ArrayList<MediaWrapper> getMediaItems(List<String> pathList) {
        ArrayList<MediaWrapper> items = new ArrayList<>();
        for (int i = 0; i < pathList.size(); i++) {
            MediaWrapper item = getMediaItem(pathList.get(i));
            items.add(item);
        }
        return items;
    }

    private class GetMediaItemsRunnable implements Runnable {
        private final Stack<File> directories = new Stack<>();
        private final HashSet<String> directoriesScanned = new HashSet<>();

        public GetMediaItemsRunnable() {
        }

        /* JADX WARN: Finally extract failed */
        @Override // java.lang.Runnable
        public void run() throws NoSuchMethodException, NumberFormatException, ClassNotFoundException, SecurityException {
            LibVLC libVlcInstance = VLCInstance.getLibVlcInstance();
            MediaDatabase DBManager = MediaDatabase.getInstance();
            List<File> mediaDirs = DBManager.getMediaDirs();
            if (mediaDirs.size() == 0) {
                String[] storageDirs = {String.valueOf(BitmapUtils.getSDPath()) + "/G-MScreen/video/"};
                for (String str : storageDirs) {
                    File f = new File(str);
                    if (f.exists()) {
                        mediaDirs.add(f);
                    }
                }
            }
            this.directories.addAll(mediaDirs);
            HashMap<String, MediaWrapper> existingMedias = DBManager.getMedias();
            HashSet<String> addedLocations = new HashSet<>();
            MediaLibrary.this.mItemListLock.writeLock().lock();
            MediaLibrary.this.mItemList.clear();
            MediaLibrary.this.mItemListLock.writeLock().unlock();
            MediaItemFilter mediaFileFilter = new MediaItemFilter(null);
            int count = 0;
            ArrayList<File> mediaToScan = new ArrayList<>();
            while (!this.directories.isEmpty()) {
                try {
                    File dir = this.directories.pop();
                    String dirPath = dir.getAbsolutePath();
                    if (!dirPath.startsWith("/proc/") && !dirPath.startsWith("/sys/") && !dirPath.startsWith("/dev/")) {
                        try {
                            dirPath = dir.getCanonicalPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (this.directoriesScanned.contains(dirPath)) {
                            continue;
                        } else {
                            this.directoriesScanned.add(dirPath);
                            if (new File(String.valueOf(dirPath) + "/.nomedia").exists()) {
                                continue;
                            } else {
                                try {
                                    File[] f2 = dir.listFiles(mediaFileFilter);
                                    if (f2 != null) {
                                        for (File file : f2) {
                                            if (file.isFile()) {
                                                mediaToScan.add(file);
                                            } else if (file.isDirectory()) {
                                                this.directories.push(file);
                                            }
                                        }
                                    }
                                    if (MediaLibrary.this.isStopping) {
                                        Log.d(MediaLibrary.TAG, "Stopping scan");
                                        for (int i = 0; i < MediaLibrary.this.mUpdateHandler.size(); i++) {
                                            Handler h = (Handler) MediaLibrary.this.mUpdateHandler.get(i);
                                            h.sendEmptyMessage(100);
                                        }
                                        if (!MediaLibrary.this.isStopping && Environment.getExternalStorageState().equals("mounted")) {
                                            Iterator<String> it = addedLocations.iterator();
                                            while (it.hasNext()) {
                                                String fileURI = it.next();
                                                existingMedias.remove(fileURI);
                                            }
                                            DBManager.removeMedias(existingMedias.keySet());
                                            for (File file2 : DBManager.getMediaDirs()) {
                                                if (!file2.isDirectory()) {
                                                    DBManager.removeDir(file2.getAbsolutePath());
                                                }
                                            }
                                        }
                                        VideoGridFragment.actionScanStop();
                                        if (MediaLibrary.this.mRestart) {
                                            Log.d(MediaLibrary.TAG, "Restarting scan");
                                            MediaLibrary.this.mRestart = false;
                                            MediaLibrary.this.restartHandler.sendEmptyMessageDelayed(1, 200L);
                                            return;
                                        }
                                        return;
                                    }
                                } catch (Exception e2) {
                                }
                            }
                        }
                    }
                } catch (Throwable th) {
                    for (int i2 = 0; i2 < MediaLibrary.this.mUpdateHandler.size(); i2++) {
                        Handler h2 = (Handler) MediaLibrary.this.mUpdateHandler.get(i2);
                        h2.sendEmptyMessage(100);
                    }
                    if (!MediaLibrary.this.isStopping && Environment.getExternalStorageState().equals("mounted")) {
                        Iterator<String> it2 = addedLocations.iterator();
                        while (it2.hasNext()) {
                            String fileURI2 = it2.next();
                            existingMedias.remove(fileURI2);
                        }
                        DBManager.removeMedias(existingMedias.keySet());
                        for (File file3 : DBManager.getMediaDirs()) {
                            if (!file3.isDirectory()) {
                                DBManager.removeDir(file3.getAbsolutePath());
                            }
                        }
                    }
                    VideoGridFragment.actionScanStop();
                    if (MediaLibrary.this.mRestart) {
                        Log.d(MediaLibrary.TAG, "Restarting scan");
                        MediaLibrary.this.mRestart = false;
                        MediaLibrary.this.restartHandler.sendEmptyMessageDelayed(1, 200L);
                    }
                    throw th;
                }
            }
            Iterator<File> it3 = mediaToScan.iterator();
            while (it3.hasNext()) {
                File file4 = it3.next();
                String fileURI3 = LibVLC.PathToURI(file4.getPath());
                count++;
                if (!existingMedias.containsKey(fileURI3)) {
                    MediaLibrary.this.mItemListLock.writeLock().lock();
                    Media m = new Media(libVlcInstance, fileURI3);
                    m.parse();
                    m.release();
                    if ((m.getDuration() == 0 || (m.getTrackCount() != 0 && TextUtils.isEmpty(m.getTrack(0).codec))) && fileURI3.endsWith(".mod")) {
                        MediaLibrary.this.mItemListLock.writeLock().unlock();
                    } else {
                        MediaWrapper mw = new MediaWrapper(m);
                        mw.setLastModified(file4.lastModified());
                        MediaLibrary.this.mItemList.add(mw);
                        MediaDatabase db = MediaDatabase.getInstance();
                        db.addMedia(mw);
                        MediaLibrary.this.mItemListLock.writeLock().unlock();
                    }
                } else if (!addedLocations.contains(fileURI3)) {
                    MediaLibrary.this.mItemListLock.writeLock().lock();
                    MediaLibrary.this.mItemList.add(existingMedias.get(fileURI3));
                    MediaLibrary.this.mItemListLock.writeLock().unlock();
                    addedLocations.add(fileURI3);
                }
                if (MediaLibrary.this.isStopping) {
                    Log.d(MediaLibrary.TAG, "Stopping scan");
                    for (int i3 = 0; i3 < MediaLibrary.this.mUpdateHandler.size(); i3++) {
                        Handler h3 = (Handler) MediaLibrary.this.mUpdateHandler.get(i3);
                        h3.sendEmptyMessage(100);
                    }
                    if (!MediaLibrary.this.isStopping && Environment.getExternalStorageState().equals("mounted")) {
                        Iterator<String> it4 = addedLocations.iterator();
                        while (it4.hasNext()) {
                            String fileURI4 = it4.next();
                            existingMedias.remove(fileURI4);
                        }
                        DBManager.removeMedias(existingMedias.keySet());
                        for (File file5 : DBManager.getMediaDirs()) {
                            if (!file5.isDirectory()) {
                                DBManager.removeDir(file5.getAbsolutePath());
                            }
                        }
                    }
                    VideoGridFragment.actionScanStop();
                    if (MediaLibrary.this.mRestart) {
                        Log.d(MediaLibrary.TAG, "Restarting scan");
                        MediaLibrary.this.mRestart = false;
                        MediaLibrary.this.restartHandler.sendEmptyMessageDelayed(1, 200L);
                        return;
                    }
                    return;
                }
            }
            for (int i4 = 0; i4 < MediaLibrary.this.mUpdateHandler.size(); i4++) {
                Handler h4 = (Handler) MediaLibrary.this.mUpdateHandler.get(i4);
                h4.sendEmptyMessage(100);
            }
            if (!MediaLibrary.this.isStopping && Environment.getExternalStorageState().equals("mounted")) {
                Iterator<String> it5 = addedLocations.iterator();
                while (it5.hasNext()) {
                    String fileURI5 = it5.next();
                    existingMedias.remove(fileURI5);
                }
                DBManager.removeMedias(existingMedias.keySet());
                for (File file6 : DBManager.getMediaDirs()) {
                    if (!file6.isDirectory()) {
                        DBManager.removeDir(file6.getAbsolutePath());
                    }
                }
            }
            VideoGridFragment.actionScanStop();
            if (MediaLibrary.this.mRestart) {
                Log.d(MediaLibrary.TAG, "Restarting scan");
                MediaLibrary.this.mRestart = false;
                MediaLibrary.this.restartHandler.sendEmptyMessageDelayed(1, 200L);
            }
        }
    }

    private static class RestartHandler extends WeakHandler<MediaLibrary> {
        public RestartHandler(MediaLibrary owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            MediaLibrary owner = getOwner();
            if (owner != null) {
                owner.loadMediaItems();
            }
        }
    }

    private static class MediaItemFilter implements FileFilter {
        private MediaItemFilter() {
        }

        /* synthetic */ MediaItemFilter(MediaItemFilter mediaItemFilter) {
            this();
        }

        @Override // java.io.FileFilter
        public boolean accept(File f) {
            if (f.isHidden()) {
                return false;
            }
            if (f.isDirectory() && !MediaLibrary.FOLDER_BLACKLIST.contains(f.getPath().toLowerCase(Locale.ENGLISH))) {
                return true;
            }
            String fileName = f.getName().toLowerCase(Locale.ENGLISH);
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex == -1) {
                return false;
            }
            String fileExt = fileName.substring(dotIndex);
            return Extensions.AUDIO.contains(fileExt) || Extensions.VIDEO.contains(fileExt);
        }
    }
}
