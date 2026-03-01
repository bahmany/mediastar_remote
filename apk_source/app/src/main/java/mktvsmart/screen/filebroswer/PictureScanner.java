package mktvsmart.screen.filebroswer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import mktvsmart.screen.util.BitmapUtils;

/* loaded from: classes.dex */
public class PictureScanner {
    public static final int SCAN_FINISH = 0;
    protected Thread mScanThread;
    private static PictureScanner instance = null;
    public static final HashSet<String> PICTURE_EXTENSIONS = new HashSet<>();
    private boolean isStopping = false;
    private boolean mRestart = false;
    private Handler restartHandler = new Handler(Looper.getMainLooper()) { // from class: mktvsmart.screen.filebroswer.PictureScanner.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            PictureScanner.this.scanPictures();
        }
    };
    private final ArrayList<PicInfo> mPicList = new ArrayList<>();
    private final ReadWriteLock mItemListLock = new ReentrantReadWriteLock();
    private final ArrayList<Handler> mUpdateHandler = new ArrayList<>();

    public static class PicInfo {
        public String mFileDate;
        public String mFileName;
        public String mFileSize;
        public String mImagePath;
        public int type = UNKNOWN;
        public static int UNKNOWN = -1;
        public static int PNG = 0;
        public static int JPG = 1;
        public static int GIF = 2;
        public static int BMP = 3;
        public static int TIF = 4;
    }

    static {
        String[] pic_extensions = {".png", ".jpg", ".gif", ".bmp", ".tif"};
        for (String item : pic_extensions) {
            PICTURE_EXTENSIONS.add(item);
        }
    }

    private PictureScanner() {
    }

    public static synchronized PictureScanner obtain() {
        if (instance == null) {
            instance = new PictureScanner();
        }
        return instance;
    }

    public void scanPictures(boolean restart) {
        if (restart && isWorking()) {
            this.mRestart = true;
            this.isStopping = true;
        } else {
            scanPictures();
        }
    }

    public void scanPictures() {
        if (this.mScanThread == null || this.mScanThread.getState() == Thread.State.TERMINATED) {
            this.isStopping = false;
            VideoGridFragment.actionScanStart();
            this.mScanThread = new Thread(new ScanRun(this, null));
            this.mScanThread.start();
        }
    }

    public boolean isWorking() {
        return this.mScanThread != null && this.mScanThread.isAlive();
    }

    public ArrayList<PicInfo> getScanResult() {
        this.mItemListLock.readLock().lock();
        ArrayList<PicInfo> picList = new ArrayList<>(this.mPicList);
        this.mItemListLock.readLock().unlock();
        return picList;
    }

    public void addUpdateHandler(Handler handler) {
        this.mUpdateHandler.add(handler);
    }

    public void removeUpdateHandler(Handler handler) {
        this.mUpdateHandler.remove(handler);
    }

    private class ScanRun implements Runnable, FileFilter {
        private final Stack<File> directories;
        private final HashSet<String> directoriesScanned;

        private ScanRun() {
            this.directories = new Stack<>();
            this.directoriesScanned = new HashSet<>();
        }

        /* synthetic */ ScanRun(PictureScanner pictureScanner, ScanRun scanRun) {
            this();
        }

        /* JADX WARN: Finally extract failed */
        @Override // java.lang.Runnable
        public void run() {
            String[] storageDirs = {String.valueOf(BitmapUtils.getSDPath()) + "/G-MScreen/capture/"};
            for (String str : storageDirs) {
                File f = new File(str);
                if (f.exists()) {
                    this.directories.add(f);
                }
            }
            PictureScanner.this.mItemListLock.writeLock().lock();
            PictureScanner.this.mPicList.clear();
            PictureScanner.this.mItemListLock.writeLock().unlock();
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
                            if (new File(String.valueOf(dirPath) + "/.nopic").exists()) {
                                continue;
                            } else {
                                try {
                                    File[] f2 = dir.listFiles(this);
                                    if (f2 != null) {
                                        for (File file : f2) {
                                            if (file.isFile()) {
                                                mediaToScan.add(file);
                                            } else if (file.isDirectory()) {
                                                this.directories.push(file);
                                            }
                                        }
                                    }
                                    if (PictureScanner.this.isStopping) {
                                        Log.d("PictureScanner", "Stopping scan");
                                        for (int i = 0; i < PictureScanner.this.mUpdateHandler.size(); i++) {
                                            Handler h = (Handler) PictureScanner.this.mUpdateHandler.get(i);
                                            h.sendEmptyMessage(0);
                                        }
                                        PictureScanner.this.restartHandler.sendEmptyMessageDelayed(0, 100L);
                                        return;
                                    }
                                } catch (Exception e2) {
                                }
                            }
                        }
                    }
                } catch (Throwable th) {
                    for (int i2 = 0; i2 < PictureScanner.this.mUpdateHandler.size(); i2++) {
                        Handler h2 = (Handler) PictureScanner.this.mUpdateHandler.get(i2);
                        h2.sendEmptyMessage(0);
                    }
                    PictureScanner.this.restartHandler.sendEmptyMessageDelayed(0, 100L);
                    throw th;
                }
            }
            Iterator<File> it = mediaToScan.iterator();
            while (it.hasNext()) {
                File file2 = it.next();
                PictureScanner.this.mItemListLock.writeLock().lock();
                PicInfo pic = new PicInfo();
                pic.mFileName = file2.getName();
                pic.mImagePath = "file://" + file2.getAbsolutePath();
                PictureScanner.this.mPicList.add(pic);
                PictureScanner.this.mItemListLock.writeLock().unlock();
            }
            if (!PictureScanner.this.isStopping) {
                for (int i3 = 0; i3 < PictureScanner.this.mUpdateHandler.size(); i3++) {
                    Handler h3 = (Handler) PictureScanner.this.mUpdateHandler.get(i3);
                    h3.sendEmptyMessage(0);
                }
                PictureScanner.this.restartHandler.sendEmptyMessageDelayed(0, 100L);
                return;
            }
            Log.d("PictureScanner", "Stopping scan");
            for (int i4 = 0; i4 < PictureScanner.this.mUpdateHandler.size(); i4++) {
                Handler h4 = (Handler) PictureScanner.this.mUpdateHandler.get(i4);
                h4.sendEmptyMessage(0);
            }
            PictureScanner.this.restartHandler.sendEmptyMessageDelayed(0, 100L);
        }

        @Override // java.io.FileFilter
        public boolean accept(File pathname) {
            String fileName;
            int dotIndex;
            if (pathname.isHidden() || (dotIndex = (fileName = pathname.getName().toLowerCase(Locale.ENGLISH)).lastIndexOf(".")) == -1) {
                return false;
            }
            String fileExt = fileName.substring(dotIndex);
            boolean accepted = PictureScanner.PICTURE_EXTENSIONS.contains(fileExt);
            return accepted;
        }
    }
}
