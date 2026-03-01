package mktvsmart.screen.filebroswer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.nio.ByteBuffer;
import mktvsmart.screen.R;
import mktvsmart.screen.vlc.LocalPlayActivity;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.BitmapUtil;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public class MediaInfoFragment extends ListFragment {
    private static final int NEW_IMAGE = 0;
    private static final int NEW_TEXT = 1;
    public static final String TAG = "VLC/MediaInfoFragment";
    private MediaInfoAdapter mAdapter;
    private Bitmap mImage;
    private MediaWrapper mItem;
    private TextView mLengthView;
    private Media mMedia;
    private ImageButton mPlayButton;
    private TextView mTitleView;
    private Media.Track[] mTracks;
    Runnable mLoadImage = new Runnable() { // from class: mktvsmart.screen.filebroswer.MediaInfoFragment.1
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() throws NoSuchMethodException, NumberFormatException, ClassNotFoundException, SecurityException {
            LibVLC mLibVlc = VLCInstance.getLibVlcInstance();
            MediaInfoFragment.this.mMedia = new Media(mLibVlc, MediaInfoFragment.this.mItem.getLocation());
            MediaInfoFragment.this.mMedia.parse();
            MediaInfoFragment.this.mMedia.release();
            MediaInfoFragment.this.mHandler.sendEmptyMessage(1);
            DisplayMetrics screen = new DisplayMetrics();
            MediaInfoFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(screen);
            int width = Math.min(screen.widthPixels, screen.heightPixels);
            int height = (width * 9) / 16;
            MediaInfoFragment.this.mImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            byte[] b = mLibVlc.getThumbnail(MediaInfoFragment.this.mItem.getLocation(), width, height);
            if (b != null) {
                MediaInfoFragment.this.mImage.copyPixelsFromBuffer(ByteBuffer.wrap(b));
                MediaInfoFragment.this.mImage = BitmapUtil.cropBorders(MediaInfoFragment.this.mImage, width, height);
                MediaInfoFragment.this.mHandler.sendMessage(MediaInfoFragment.this.mHandler.obtainMessage(0, true));
                return;
            }
            MediaInfoFragment.this.mHandler.sendMessage(MediaInfoFragment.this.mHandler.obtainMessage(0, false));
        }
    };
    private Handler mHandler = new MediaInfoHandler(this);

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.media_info, container, false);
        this.mTitleView = (TextView) v.findViewById(R.id.title);
        this.mLengthView = (TextView) v.findViewById(R.id.length);
        this.mPlayButton = (ImageButton) v.findViewById(R.id.play);
        this.mPlayButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.filebroswer.MediaInfoFragment.2
            AnonymousClass2() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v2) {
                MediaInfoFragment.this.playVideo(MediaInfoFragment.this.mItem, false);
            }
        });
        this.mAdapter = new MediaInfoAdapter(getActivity());
        setListAdapter(this.mAdapter);
        return v;
    }

    /* renamed from: mktvsmart.screen.filebroswer.MediaInfoFragment$2 */
    class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v2) {
            MediaInfoFragment.this.playVideo(MediaInfoFragment.this.mItem, false);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mItem != null) {
            this.mTitleView.setText(this.mItem.getTitle());
            this.mLengthView.setText(Strings.millisToString(this.mItem.getLength()));
            new Thread(this.mLoadImage).start();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        ((GsFileBroswerActivity) getActivity()).updateOptionView();
    }

    public void setMediaLocation(String MRL) {
        if (MRL != null) {
            this.mItem = MediaLibrary.getInstance().getMediaItem(MRL);
        }
    }

    protected void playVideo(MediaWrapper media, boolean fromStart) {
        String location = media.getLocation();
        Intent intent = new Intent(getActivity(), (Class<?>) LocalPlayActivity.class);
        intent.setData(Uri.parse(location));
        startActivity(intent);
    }

    /* renamed from: mktvsmart.screen.filebroswer.MediaInfoFragment$1 */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() throws NoSuchMethodException, NumberFormatException, ClassNotFoundException, SecurityException {
            LibVLC mLibVlc = VLCInstance.getLibVlcInstance();
            MediaInfoFragment.this.mMedia = new Media(mLibVlc, MediaInfoFragment.this.mItem.getLocation());
            MediaInfoFragment.this.mMedia.parse();
            MediaInfoFragment.this.mMedia.release();
            MediaInfoFragment.this.mHandler.sendEmptyMessage(1);
            DisplayMetrics screen = new DisplayMetrics();
            MediaInfoFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(screen);
            int width = Math.min(screen.widthPixels, screen.heightPixels);
            int height = (width * 9) / 16;
            MediaInfoFragment.this.mImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            byte[] b = mLibVlc.getThumbnail(MediaInfoFragment.this.mItem.getLocation(), width, height);
            if (b != null) {
                MediaInfoFragment.this.mImage.copyPixelsFromBuffer(ByteBuffer.wrap(b));
                MediaInfoFragment.this.mImage = BitmapUtil.cropBorders(MediaInfoFragment.this.mImage, width, height);
                MediaInfoFragment.this.mHandler.sendMessage(MediaInfoFragment.this.mHandler.obtainMessage(0, true));
                return;
            }
            MediaInfoFragment.this.mHandler.sendMessage(MediaInfoFragment.this.mHandler.obtainMessage(0, false));
        }
    }

    public void updateImage(boolean bHaveNew) {
        if (getView() != null) {
            ImageView imageView = (ImageView) getView().findViewById(R.id.image);
            if (bHaveNew) {
                imageView.setImageBitmap(this.mImage);
            } else {
                imageView.setImageResource(R.drawable.video_icon);
            }
            this.mPlayButton.setVisibility(0);
        }
    }

    public void updateText() {
        if (this.mMedia != null) {
            int trackCount = this.mMedia.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                Media.Track track = this.mMedia.getTrack(i);
                if (track.type == 2) {
                    this.mAdapter.add(track);
                }
            }
        }
    }

    private static class MediaInfoHandler extends WeakHandler<MediaInfoFragment> {
        public MediaInfoHandler(MediaInfoFragment owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            MediaInfoFragment owner = getOwner();
            if (owner != null) {
                switch (msg.what) {
                    case 0:
                        if (((Boolean) msg.obj).booleanValue()) {
                            owner.updateImage(true);
                            break;
                        } else {
                            owner.updateImage(false);
                            break;
                        }
                    case 1:
                        owner.updateText();
                        break;
                }
            }
        }
    }
}
