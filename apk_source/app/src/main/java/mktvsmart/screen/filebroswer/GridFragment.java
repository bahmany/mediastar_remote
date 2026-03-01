package mktvsmart.screen.filebroswer;

import android.R;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/* loaded from: classes.dex */
public class GridFragment extends Fragment {
    static final int INTERNAL_EMPTY_ID = 16711681;
    static final int INTERNAL_GRID_CONTAINER_ID = 16711683;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 16711682;
    ListAdapter mAdapter;
    CharSequence mEmptyText;
    View mEmptyView;
    GridView mGrid;
    View mGridContainer;
    boolean mGridShown;
    View mProgressContainer;
    TextView mStandardEmptyView;
    private final Handler mHandler = new Handler();
    private final Runnable mRequestFocus = new Runnable() { // from class: mktvsmart.screen.filebroswer.GridFragment.1
        @Override // java.lang.Runnable
        public void run() {
            GridFragment.this.mGrid.focusableViewAvailable(GridFragment.this.mGrid);
        }
    };
    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.filebroswer.GridFragment.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            GridFragment.this.onGridItemClick((GridView) parent, v, position, id);
        }
    };

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout pframe = new LinearLayout(context);
        pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
        pframe.setOrientation(1);
        pframe.setVisibility(8);
        pframe.setGravity(17);
        ProgressBar progress = new ProgressBar(context, null, R.attr.progressBarStyleLarge);
        pframe.addView(progress, new FrameLayout.LayoutParams(-2, -2));
        frameLayout.addView(pframe, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout gframe = new FrameLayout(context);
        gframe.setId(INTERNAL_GRID_CONTAINER_ID);
        TextView tv2 = new TextView(context);
        tv2.setId(INTERNAL_EMPTY_ID);
        tv2.setGravity(17);
        gframe.addView(tv2, new FrameLayout.LayoutParams(-1, -1));
        GridView gv = new GridView(context);
        gv.setId(R.id.list);
        gv.setDrawSelectorOnTop(false);
        gframe.addView(gv, new FrameLayout.LayoutParams(-1, -1));
        frameLayout.addView(gframe, new FrameLayout.LayoutParams(-1, -1));
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        return frameLayout;
    }

    @Override // android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureGrid();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mGrid = null;
        this.mGridShown = false;
        this.mGridContainer = null;
        this.mProgressContainer = null;
        this.mEmptyView = null;
        this.mStandardEmptyView = null;
        super.onDestroyView();
    }

    public void onGridItemClick(GridView gv, View v, int position, long id) {
    }

    public void setListAdapter(ListAdapter adapter) {
        boolean hadAdapter = this.mAdapter != null;
        this.mAdapter = adapter;
        if (this.mGrid != null) {
            this.mGrid.setAdapter(adapter);
            if (!this.mGridShown && !hadAdapter) {
                setGridShown(true, getView().getWindowToken() != null);
            }
        }
    }

    public void setSelection(int position) {
        ensureGrid();
        this.mGrid.setSelection(position);
    }

    public int getSelectedItemPosition() {
        ensureGrid();
        return this.mGrid.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        ensureGrid();
        return this.mGrid.getSelectedItemId();
    }

    public GridView getGridView() {
        ensureGrid();
        return this.mGrid;
    }

    public void setEmptyText(CharSequence text) {
        ensureGrid();
        if (this.mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        this.mStandardEmptyView.setText(text);
        if (this.mEmptyText == null) {
            this.mGrid.setEmptyView(this.mStandardEmptyView);
        }
        this.mEmptyText = text;
    }

    public void setGridShown(boolean shown) {
        setGridShown(shown, true);
    }

    public void setGridShownNoAnimation(boolean shown) {
        setGridShown(shown, false);
    }

    private void setGridShown(boolean shown, boolean animate) {
        ensureGrid();
        if (this.mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (this.mGridShown != shown) {
            this.mGridShown = shown;
            if (shown) {
                if (animate) {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
                    this.mGridContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                } else {
                    this.mProgressContainer.clearAnimation();
                    this.mGridContainer.clearAnimation();
                }
                this.mProgressContainer.setVisibility(8);
                this.mGridContainer.setVisibility(0);
                return;
            }
            if (animate) {
                this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                this.mGridContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
            } else {
                this.mProgressContainer.clearAnimation();
                this.mGridContainer.clearAnimation();
            }
            this.mProgressContainer.setVisibility(0);
            this.mGridContainer.setVisibility(8);
        }
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureGrid() {
        if (this.mGrid == null) {
            View root = getView();
            if (root == null) {
                throw new IllegalStateException("Content view not yet created");
            }
            if (root instanceof GridView) {
                this.mGrid = (GridView) root;
            } else {
                this.mStandardEmptyView = (TextView) root.findViewById(INTERNAL_EMPTY_ID);
                if (this.mStandardEmptyView == null) {
                    this.mEmptyView = root.findViewById(R.id.empty);
                } else {
                    this.mStandardEmptyView.setVisibility(8);
                }
                this.mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
                this.mGridContainer = root.findViewById(INTERNAL_GRID_CONTAINER_ID);
                View rawGridView = root.findViewById(R.id.list);
                if (!(rawGridView instanceof GridView)) {
                    if (rawGridView == null) {
                        throw new RuntimeException("Your content must have a GridView whose id attribute is 'android.R.id.list'");
                    }
                    throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a GridView class");
                }
                this.mGrid = (GridView) rawGridView;
                if (this.mEmptyView != null) {
                    this.mGrid.setEmptyView(this.mEmptyView);
                } else if (this.mEmptyText != null && this.mStandardEmptyView != null) {
                    this.mStandardEmptyView.setText(this.mEmptyText);
                    this.mGrid.setEmptyView(this.mStandardEmptyView);
                }
            }
            this.mGridShown = true;
            this.mGrid.setOnItemClickListener(this.mOnClickListener);
            if (this.mAdapter != null) {
                ListAdapter adapter = this.mAdapter;
                this.mAdapter = null;
                setListAdapter(adapter);
            } else if (this.mProgressContainer != null) {
                setGridShown(false, false);
            }
            this.mHandler.post(this.mRequestFocus);
        }
    }
}
