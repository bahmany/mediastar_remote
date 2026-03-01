package mktvsmart.screen.vlc;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.tagmanager.DataLayer;
import master.flame.danmaku.danmaku.parser.IDataSource;
import org.videolan.libvlc.EventHandler;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public abstract class BaseVLCPlayActivity extends FragmentActivity {
    private static final String LOG_TAG = BaseVLCPlayActivity.class.getSimpleName();

    public abstract void onHardwareAccelerationError(Bundle bundle);

    public abstract void onMediaPlayerEncounteredError(Bundle bundle);

    public abstract void onMediaPlayerEndReached(Bundle bundle);

    public abstract void onMediaPlayerPlaying(Bundle bundle);

    public abstract void onMediaPlayerVout(Bundle bundle);

    public static class VLCEventHandler extends WeakHandler<BaseVLCPlayActivity> {
        public VLCEventHandler(BaseVLCPlayActivity owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            BaseVLCPlayActivity activity = getOwner();
            if (activity != null) {
                Bundle data = msg.getData();
                int event = data.getInt(DataLayer.EVENT_KEY);
                switch (event) {
                    case 3:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaParsedChanged");
                        activity.onMediaParsedChanged(data);
                        break;
                    case 259:
                        activity.onMediaPlayerBuffering(data);
                        break;
                    case EventHandler.MediaPlayerPlaying /* 260 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerPlaying");
                        activity.onMediaPlayerPlaying(data);
                        break;
                    case EventHandler.MediaPlayerPaused /* 261 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerPaused");
                        activity.onMediaPlayerPaused(data);
                        break;
                    case EventHandler.MediaPlayerStopped /* 262 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerStopped");
                        activity.onMediaPlayerStopped(data);
                        break;
                    case EventHandler.MediaPlayerEndReached /* 265 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerEndReached");
                        activity.onMediaPlayerEndReached(data);
                        break;
                    case EventHandler.MediaPlayerEncounteredError /* 266 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerEncounteredError");
                        activity.onMediaPlayerEncounteredError(data);
                        break;
                    case EventHandler.MediaPlayerTimeChanged /* 267 */:
                        activity.onMediaPlayerTimeChanged(data);
                        break;
                    case EventHandler.MediaPlayerPositionChanged /* 268 */:
                        activity.onMediaPlayerPositionChanged(data);
                        break;
                    case EventHandler.MediaPlayerVout /* 274 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerVout");
                        activity.onMediaPlayerVout(data);
                        break;
                    case EventHandler.MediaPlayerScrambledChanged /* 275 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerScrambledChanged media Scrambled " + data.getBoolean("scrambled"));
                        activity.onMediaScrambledChanged(data);
                        break;
                    case EventHandler.MediaPlayerRecordableChanged /* 276 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerRecordableChanged " + data);
                        activity.onMediaPlayerRecordableChanged(data);
                        break;
                    case EventHandler.MediaPlayerRecordingFinished /* 277 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "MediaPlayerRecordingFinished save file " + data.getString(IDataSource.SCHEME_FILE_TAG));
                        activity.onMediaPlayerRecordingFinished(data);
                        break;
                    case EventHandler.HardwareAccelerationError /* 12288 */:
                        Log.i(BaseVLCPlayActivity.LOG_TAG, "HardwareAccelerationError");
                        activity.onHardwareAccelerationError(data);
                        break;
                    default:
                        Log.e(BaseVLCPlayActivity.LOG_TAG, String.format("Event not handled (0x%x)", Integer.valueOf(msg.getData().getInt(DataLayer.EVENT_KEY))));
                        break;
                }
            }
        }
    }

    public void onMediaParsedChanged(Bundle data) {
    }

    public void onMediaPlayerPaused(Bundle data) {
    }

    public void onMediaPlayerStopped(Bundle data) {
    }

    public void onMediaPlayerPositionChanged(Bundle data) {
    }

    public void onMediaPlayerTimeChanged(Bundle data) {
    }

    public void onMediaPlayerBuffering(Bundle data) {
        Log.d(LOG_TAG, "media buffering " + data.getFloat("data"));
    }

    public void onMediaPlayerRecordableChanged(Bundle data) {
    }

    public void onMediaPlayerRecordingFinished(Bundle data) {
    }

    public void onMediaScrambledChanged(Bundle data) {
    }
}
