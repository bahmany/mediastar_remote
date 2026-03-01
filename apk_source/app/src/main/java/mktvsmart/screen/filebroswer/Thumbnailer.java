package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.videolan.libvlc.LibVLC;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.BitmapUtil;
import org.videolan.vlc.util.VLCInstance;

/* loaded from: classes.dex */
public class Thumbnailer implements Runnable {
    public static final String TAG = "VLC/Thumbnailer";
    private final float mDensity;
    private LibVLC mLibVlc;
    private final String mPrefix;
    protected Thread mThread;
    private VideoGridFragment mVideoGridFragment;
    private int totalCount;
    private final Queue<MediaWrapper> mItems = new LinkedList();
    private boolean isStopping = false;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();

    public Thumbnailer(Context context, Display display) {
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        this.mDensity = metrics.density;
        this.mPrefix = "Thumbnail";
    }

    public void start(VideoGridFragment videoGridFragment) {
        if (this.mLibVlc == null) {
            this.mLibVlc = VLCInstance.getLibVlcInstance();
        }
        this.isStopping = false;
        if (this.mThread == null || this.mThread.getState() == Thread.State.TERMINATED) {
            this.mVideoGridFragment = videoGridFragment;
            this.mThread = new Thread(this);
            this.mThread.start();
        }
    }

    public void stop() {
        this.isStopping = true;
        if (this.mThread != null) {
            this.mThread.interrupt();
        }
    }

    public void clearJobs() {
        this.lock.lock();
        this.mItems.clear();
        this.totalCount = 0;
        this.lock.unlock();
    }

    public void addJob(MediaWrapper item) {
        if (BitmapUtil.getPictureFromCache(item) == null && !item.isPictureParsed()) {
            this.lock.lock();
            this.mItems.add(item);
            this.totalCount++;
            this.notEmpty.signal();
            this.lock.unlock();
            Log.i(TAG, "Job added!");
        }
    }

    @Override // java.lang.Runnable
    public void run() throws InterruptedException {
        int count = 0;
        Log.d(TAG, "Thumbnailer started");
        while (true) {
            if (this.isStopping) {
                break;
            }
            this.mVideoGridFragment.resetBarrier();
            this.lock.lock();
            boolean interrupted = false;
            while (this.mItems.size() == 0) {
                try {
                    this.totalCount = 0;
                    this.notEmpty.await();
                } catch (InterruptedException e) {
                    interrupted = true;
                    Log.i(TAG, "interruption probably requested by stop()");
                }
            }
            if (interrupted) {
                this.lock.unlock();
                break;
            }
            int total = this.totalCount;
            MediaWrapper item = this.mItems.poll();
            this.lock.unlock();
            count++;
            int width = (int) (120.0f * this.mDensity);
            int height = (int) (75.0f * this.mDensity);
            Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            byte[] b = this.mLibVlc.getThumbnail(item.getLocation(), width, height);
            if (b == null) {
                MediaDatabase.setPicture(item, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            } else {
                thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
                Log.i(TAG, "Thumbnail created for " + item.getFileName());
                MediaDatabase.setPicture(item, thumbnail);
                this.mVideoGridFragment.setItemToUpdate(item);
                try {
                    this.mVideoGridFragment.await();
                } catch (InterruptedException e2) {
                    Log.i(TAG, "interruption probably requested by stop()");
                } catch (BrokenBarrierException e3) {
                    Log.e(TAG, "Unexpected BrokenBarrierException");
                    e3.printStackTrace();
                }
            }
        }
        this.mVideoGridFragment = null;
        Log.d(TAG, "Thumbnailer stopped");
    }
}
