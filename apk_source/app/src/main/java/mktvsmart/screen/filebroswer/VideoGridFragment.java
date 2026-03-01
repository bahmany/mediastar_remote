package mktvsmart.screen.filebroswer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import mktvsmart.screen.GMScreenApp;
import mktvsmart.screen.R;
import mktvsmart.screen.util.CommonDialogs;
import mktvsmart.screen.util.GRunnable;
import mktvsmart.screen.vlc.LocalPlayActivity;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaGroup;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public class VideoGridFragment extends SherlockGridFragment implements ISortable, IRefreshable {
    protected static final String ACTION_SCAN_START = "org.videolan.vlc.gui.ScanStart";
    protected static final String ACTION_SCAN_STOP = "org.videolan.vlc.gui.ScanStop";
    private static final int GRID_HORIZONTAL_SPACING_DP = 20;
    private static final int GRID_ITEM_WIDTH_DP = 156;
    private static final int GRID_STRETCH_MODE = 2;
    private static final int GRID_VERTICAL_SPACING_DP = 20;
    private static final int LIST_HORIZONTAL_SPACING_DP = 0;
    private static final int LIST_STRETCH_MODE = 2;
    private static final int LIST_VERTICAL_SPACING_DP = 10;
    public static final String TAG = VideoGridFragment.class.getSimpleName();
    protected static final int UPDATE_ITEM = 0;
    private VideoGridAnimator mAnimator;
    private int mGVFirstVisiblePos;
    protected GridView mGridView;
    protected String mGroup;
    protected MediaWrapper mItemToUpdate;
    protected LinearLayout mLayoutFlipperLoading;
    private MediaLibrary mMediaLibrary;
    protected TextView mTextViewNomedia;
    private Thumbnailer mThumbnailer;
    private VideoListAdapter mVideoAdapter;
    protected final CyclicBarrier mBarrier = new CyclicBarrier(2);
    private Handler mHandler = new VideoListHandler(this);
    private final BroadcastReceiver messageReceiverVideoListFragment = new BroadcastReceiver() { // from class: mktvsmart.screen.filebroswer.VideoGridFragment.1
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(VideoGridFragment.ACTION_SCAN_START)) {
                VideoGridFragment.this.mLayoutFlipperLoading.setVisibility(0);
                VideoGridFragment.this.mTextViewNomedia.setVisibility(4);
            } else if (action.equalsIgnoreCase(VideoGridFragment.ACTION_SCAN_STOP)) {
                VideoGridFragment.this.mLayoutFlipperLoading.setVisibility(4);
                VideoGridFragment.this.mTextViewNomedia.setVisibility(0);
            }
        }
    };

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mVideoAdapter = new VideoListAdapter(getActivity(), this);
        this.mMediaLibrary = MediaLibrary.getInstance();
        this.mMediaLibrary.loadMediaItems();
        setListAdapter(this.mVideoAdapter);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mThumbnailer = new Thumbnailer(activity, activity.getWindowManager().getDefaultDisplay());
        }
    }

    @Override // mktvsmart.screen.filebroswer.GridFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_grid, container, false);
        this.mLayoutFlipperLoading = (LinearLayout) v.findViewById(R.id.layout_flipper_loading);
        this.mTextViewNomedia = (TextView) v.findViewById(R.id.textview_nomedia);
        this.mGridView = (GridView) v.findViewById(android.R.id.list);
        return v;
    }

    @Override // mktvsmart.screen.filebroswer.GridFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getGridView());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCAN_START);
        filter.addAction(ACTION_SCAN_STOP);
        getActivity().registerReceiver(this.messageReceiverVideoListFragment, filter);
        Log.i(TAG, "mMediaLibrary.isWorking() " + Boolean.toString(this.mMediaLibrary.isWorking()));
        if (this.mMediaLibrary.isWorking()) {
            actionScanStart();
        }
        this.mAnimator = new VideoGridAnimator(getGridView());
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.mGVFirstVisiblePos = this.mGridView.getFirstVisiblePosition();
        this.mMediaLibrary.removeUpdateHandler(this.mHandler);
        if (this.mThumbnailer != null) {
            this.mThumbnailer.stop();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        HashMap<String, Long> times = MediaDatabase.getInstance().getVideoTimes(getActivity());
        this.mVideoAdapter.setTimes(times);
        this.mVideoAdapter.notifyDataSetChanged();
        updateList();
        this.mMediaLibrary.addUpdateHandler(this.mHandler);
        this.mGridView.setSelection(this.mGVFirstVisiblePos);
        updateViewMode();
        this.mAnimator.animate();
        if (this.mThumbnailer != null) {
            this.mThumbnailer.start(this);
        }
        ((GsFileBroswerActivity) getActivity()).updateOptionView();
    }

    @Override // mktvsmart.screen.filebroswer.GridFragment, android.support.v4.app.Fragment
    public void onDestroyView() {
        getActivity().unregisterReceiver(this.messageReceiverVideoListFragment);
        super.onDestroyView();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mThumbnailer != null) {
            this.mThumbnailer.clearJobs();
        }
        this.mBarrier.reset();
        this.mVideoAdapter.clear();
    }

    private boolean hasSpaceForGrid(View v) {
        Activity activity = getActivity();
        if (activity == null) {
            return true;
        }
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int itemWidth = Util.convertDpToPx(156);
        int horizontalspacing = Util.convertDpToPx(20);
        int width = this.mGridView.getPaddingLeft() + this.mGridView.getPaddingRight() + horizontalspacing + (itemWidth * 2);
        return width < outMetrics.widthPixels;
    }

    private void updateViewMode() {
        if (getView() == null || getActivity() == null) {
            Log.w(TAG, "Unable to setup the view");
            return;
        }
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int sidePadding = Math.max(0, Math.min(100, (int) (((outMetrics.widthPixels / 100.0d) * Math.pow(outMetrics.density, 3.0d)) / 2.0d)));
        this.mGridView.setPadding(sidePadding, this.mGridView.getPaddingTop(), sidePadding, this.mGridView.getPaddingBottom());
        if (hasSpaceForGrid(getView())) {
            Log.d(TAG, "Switching to grid mode");
            this.mGridView.setNumColumns(-1);
            this.mGridView.setStretchMode(2);
            this.mGridView.setHorizontalSpacing(Util.convertDpToPx(20));
            this.mGridView.setVerticalSpacing(Util.convertDpToPx(20));
            this.mGridView.setColumnWidth(Util.convertDpToPx(156));
            this.mVideoAdapter.setListMode(false);
            return;
        }
        Log.d(TAG, "Switching to list mode");
        this.mGridView.setNumColumns(1);
        this.mGridView.setStretchMode(2);
        this.mGridView.setHorizontalSpacing(0);
        this.mGridView.setVerticalSpacing(Util.convertDpToPx(10));
        this.mVideoAdapter.setListMode(true);
    }

    @Override // android.support.v4.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2 || newConfig.orientation == 1) {
            updateViewMode();
        }
    }

    @Override // mktvsmart.screen.filebroswer.GridFragment
    public void onGridItemClick(GridView l, View v, int position, long id) {
        MediaWrapper media = (MediaWrapper) getListAdapter().getItem(position);
        if (media instanceof MediaGroup) {
            GsFileBroswerActivity activity = (GsFileBroswerActivity) getActivity();
            VideoGridFragment frag = (VideoGridFragment) activity.fetchSecondaryFragment("videoGroupList");
            if (frag != null) {
                frag.setGroup(media.getTitle());
                activity.switchSecondaryFragment("videoGroupList", frag);
            }
        } else {
            playVideo(media, false);
        }
        super.onGridItemClick(l, v, position, id);
    }

    protected void playVideo(MediaWrapper media, boolean fromStart) {
        String location = media.getLocation();
        Intent intent = new Intent(getActivity(), (Class<?>) LocalPlayActivity.class);
        intent.setData(Uri.parse(location));
        startActivity(intent);
    }

    public boolean handleContextItemSelected(MenuItem menu, int position) {
        MediaWrapper media = this.mVideoAdapter.getItem(position);
        switch (menu.getItemId()) {
            case R.id.video_list_play_from_start /* 2131493544 */:
                playVideo(media, true);
                break;
            case R.id.video_list_info /* 2131493545 */:
                GsFileBroswerActivity activity = (GsFileBroswerActivity) getActivity();
                MediaInfoFragment frag = (MediaInfoFragment) activity.fetchSecondaryFragment("mediaInfo");
                if (frag != null) {
                    frag.setMediaLocation(media.getLocation());
                    activity.switchSecondaryFragment("mediaInfo", frag);
                    break;
                }
                break;
            case R.id.video_list_delete /* 2131493546 */:
                AlertDialog alertDialog = CommonDialogs.deleteMedia(getActivity(), media.getLocation(), new GRunnable(media) { // from class: mktvsmart.screen.filebroswer.VideoGridFragment.2
                    AnonymousClass2(Object media2) {
                        super(media2);
                    }

                    @Override // mktvsmart.screen.util.GRunnable
                    public void run(Object o) {
                        MediaWrapper media2 = (MediaWrapper) o;
                        VideoGridFragment.this.mMediaLibrary.getMediaItems().remove(media2);
                        VideoGridFragment.this.mVideoAdapter.remove(media2);
                    }
                });
                alertDialog.show();
                break;
        }
        return true;
    }

    /* renamed from: mktvsmart.screen.filebroswer.VideoGridFragment$2 */
    class AnonymousClass2 extends GRunnable {
        AnonymousClass2(Object media2) {
            super(media2);
        }

        @Override // mktvsmart.screen.util.GRunnable
        public void run(Object o) {
            MediaWrapper media2 = (MediaWrapper) o;
            VideoGridFragment.this.mMediaLibrary.getMediaItems().remove(media2);
            VideoGridFragment.this.mVideoAdapter.remove(media2);
        }
    }

    @Override // android.support.v4.app.Fragment, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MediaWrapper media = this.mVideoAdapter.getItem(info.position);
        if (!(media instanceof MediaGroup)) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.video_list, menu);
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onContextItemSelected(MenuItem menu) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menu.getMenuInfo();
        if (info == null || !handleContextItemSelected(menu, info.position)) {
            return super.onContextItemSelected(menu);
        }
        return true;
    }

    @TargetApi(11)
    public void onContextPopupMenu(View anchor, int position) {
        if (!LibVlcUtil.isHoneycombOrLater()) {
            anchor.performLongClick();
            return;
        }
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.video_list, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: mktvsmart.screen.filebroswer.VideoGridFragment.3
            private final /* synthetic */ int val$position;

            AnonymousClass3(int position2) {
                i = position2;
            }

            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem item) {
                return VideoGridFragment.this.handleContextItemSelected(item, i);
            }
        });
        popupMenu.show();
    }

    /* renamed from: mktvsmart.screen.filebroswer.VideoGridFragment$3 */
    class AnonymousClass3 implements PopupMenu.OnMenuItemClickListener {
        private final /* synthetic */ int val$position;

        AnonymousClass3(int position2) {
            i = position2;
        }

        @Override // android.widget.PopupMenu.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            return VideoGridFragment.this.handleContextItemSelected(item, i);
        }
    }

    private static class VideoListHandler extends WeakHandler<VideoGridFragment> {
        public VideoListHandler(VideoGridFragment owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws InterruptedException, BrokenBarrierException {
            VideoGridFragment fragment = getOwner();
            if (fragment == null) {
            }
            switch (msg.what) {
                case 0:
                    fragment.updateItem();
                    break;
                case 100:
                    if (!fragment.mAnimator.isAnimationDone()) {
                        sendEmptyMessageDelayed(msg.what, 500L);
                        break;
                    } else {
                        fragment.updateList();
                        break;
                    }
            }
        }
    }

    public void updateItem() throws InterruptedException, BrokenBarrierException {
        this.mVideoAdapter.update(this.mItemToUpdate);
        try {
            this.mBarrier.await();
        } catch (InterruptedException e) {
        } catch (BrokenBarrierException e2) {
        }
    }

    public void updateList() {
        List<MediaWrapper> itemList = this.mMediaLibrary.getVideoItems();
        if (this.mThumbnailer != null) {
            this.mThumbnailer.clearJobs();
        } else {
            Log.w(TAG, "Can't generate thumbnails, the thumbnailer is missing");
        }
        this.mVideoAdapter.clear();
        if (itemList.size() > 0) {
            if (this.mGroup != null || itemList.size() <= 10) {
                for (MediaWrapper item : itemList) {
                    if (this.mGroup == null || item.getTitle().startsWith(this.mGroup)) {
                        this.mVideoAdapter.add(item);
                        if (this.mThumbnailer != null) {
                            this.mThumbnailer.addJob(item);
                        }
                    }
                }
            } else {
                List<MediaGroup> groups = MediaGroup.group(itemList);
                for (MediaGroup item2 : groups) {
                    this.mVideoAdapter.add(item2.getMedia());
                    if (this.mThumbnailer != null) {
                        this.mThumbnailer.addJob(item2);
                    }
                }
            }
            this.mVideoAdapter.sort();
            this.mGVFirstVisiblePos = this.mGridView.getFirstVisiblePosition();
            this.mGridView.setSelection(this.mGVFirstVisiblePos);
            this.mGridView.requestFocus();
        }
    }

    @Override // mktvsmart.screen.filebroswer.ISortable
    public void sortBy(int sortby) {
        this.mVideoAdapter.sortBy(sortby);
    }

    public void setItemToUpdate(MediaWrapper item) {
        this.mItemToUpdate = item;
        this.mHandler.sendEmptyMessage(0);
    }

    public void setGroup(String prefix) {
        this.mGroup = prefix;
    }

    public void await() throws InterruptedException, BrokenBarrierException {
        this.mBarrier.await();
    }

    public void resetBarrier() {
        this.mBarrier.reset();
    }

    /* renamed from: mktvsmart.screen.filebroswer.VideoGridFragment$1 */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(VideoGridFragment.ACTION_SCAN_START)) {
                VideoGridFragment.this.mLayoutFlipperLoading.setVisibility(0);
                VideoGridFragment.this.mTextViewNomedia.setVisibility(4);
            } else if (action.equalsIgnoreCase(VideoGridFragment.ACTION_SCAN_STOP)) {
                VideoGridFragment.this.mLayoutFlipperLoading.setVisibility(4);
                VideoGridFragment.this.mTextViewNomedia.setVisibility(0);
            }
        }
    }

    public static void actionScanStart() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SCAN_START);
        GMScreenApp.getAppContext().sendBroadcast(intent);
    }

    public static void actionScanStop() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SCAN_STOP);
        GMScreenApp.getAppContext().sendBroadcast(intent);
    }

    @Override // mktvsmart.screen.filebroswer.IRefreshable
    public void refresh() {
        MediaLibrary.getInstance().loadMediaItems(getActivity(), true);
    }
}
