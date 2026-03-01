package mktvsmart.screen.vlc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.List;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertPvrInfoModel;

/* loaded from: classes.dex */
public class ProgramListDrawer extends Fragment {
    private static final String TAG = ProgramListDrawer.class.getSimpleName();
    private AdapterView.OnItemClickListener channelClickListener = new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.vlc.ProgramListDrawer.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) throws SocketException, UnsupportedEncodingException {
            Log.d(ProgramListDrawer.TAG, "--> LiveChannelList onItemClick");
            ProgramListDrawer.this.hide();
            if (!ProgramListDrawer.this.owner.isPlaying(position)) {
                if (GMScreenGlobalInfo.playType == 2) {
                    if (ProgramListDrawer.this.owner.checkChannelLock(position)) {
                        ProgramListDrawer.this.owner.inputPermissionPassword(position);
                        return;
                    } else {
                        ProgramListDrawer.this.owner.askPlayUrl(position);
                        return;
                    }
                }
                if (GMScreenGlobalInfo.playType == 1) {
                    ProgramListDrawer.this.owner.askPlayUrl(position);
                }
            }
        }
    };
    private AdView mAdview;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mFragmentContainerView;
    private LivePlayActivity owner;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_program_list, container, false);
        this.mDrawerListView = (ListView) view.findViewById(R.id.channel_list_view);
        this.mDrawerListView.setOnItemClickListener(this.channelClickListener);
        this.mAdview = (AdView) view.findViewById(R.id.adView);
        this.mAdview.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.vlc.ProgramListDrawer.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ProgramListDrawer.this.hide();
            }
        });
        this.mAdview.loadAd(new AdRequest.Builder().build());
        return view;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        this.mFragmentContainerView = getActivity().findViewById(fragmentId);
        this.mDrawerLayout = drawerLayout;
        this.mDrawerToggle = new ActionBarDrawerToggle(getActivity(), this.mDrawerLayout, R.drawable.ic_drawer, R.string.abc_action_bar_home_description, R.string.abc_action_bar_up_description);
        this.mDrawerLayout.setScrimColor(0);
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
    }

    public void setAdapter(ProgramListAdapter adapter) {
        if (this.mDrawerListView != null) {
            this.mDrawerListView.setAdapter((ListAdapter) adapter);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LivePlayActivity) {
            this.owner = (LivePlayActivity) activity;
        }
    }

    public void setViewSize() {
    }

    public boolean isDrawerOpen() {
        return this.mDrawerLayout != null && this.mDrawerLayout.isDrawerOpen(this.mFragmentContainerView);
    }

    public boolean isShowing() {
        return isDrawerOpen();
    }

    public void show() {
        this.mDrawerLayout.openDrawer(this.mFragmentContainerView);
    }

    public void hide() {
        this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
    }

    public boolean onBackPressed() {
        if (!isDrawerOpen()) {
            return false;
        }
        this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
        return true;
    }

    public void setSelectedItem(int selectedItem) {
        if (this.mDrawerListView.getAdapter() != null) {
            ((ProgramListAdapter) this.mDrawerListView.getAdapter()).setSelectedItem(selectedItem);
            ((ProgramListAdapter) this.mDrawerListView.getAdapter()).notifyDataSetChanged();
        }
        this.mDrawerListView.setSelection(selectedItem);
    }

    public static class ProgramListAdapter extends BaseAdapter {
        private List<?> data;
        private Context mCtx;
        private int mCurSelect = -1;

        public ProgramListAdapter(Context ctx, List<?> data) {
            this.mCtx = ctx;
            this.data = data;
        }

        public void setListData(List<?> data) {
            this.data = data;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.data == null) {
                return 0;
            }
            return this.data.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            if (this.data == null) {
                return null;
            }
            return this.data.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void setSelectedItem(int selectedItem) {
            this.mCurSelect = selectedItem;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) throws NumberFormatException {
            ViewHolerChannel viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolerChannel();
                convertView = LayoutInflater.from(this.mCtx).inflate(R.layout.vlc_list_item_layout, parent, false);
                viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_name);
                viewHolder.ivScramble = (ImageView) convertView.findViewById(R.id.scramble);
                viewHolder.ivLock = (ImageView) convertView.findViewById(R.id.lock);
                viewHolder.sizeText = (TextView) convertView.findViewById(R.id.pvr_size);
                viewHolder.timeText = (TextView) convertView.findViewById(R.id.pvr_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolerChannel) convertView.getTag();
            }
            if (position == this.mCurSelect) {
                convertView.setBackgroundResource(R.drawable.list_item_pressed_bg);
            } else {
                convertView.setBackgroundResource(R.color.transparent);
            }
            if (GMScreenGlobalInfo.playType == 2) {
                viewHolder.sizeText.setVisibility(8);
                viewHolder.timeText.setVisibility(8);
                DataConvertChannelModel item = (DataConvertChannelModel) getItem(position);
                if (item != null) {
                    viewHolder.nameText.setText(String.valueOf(position + 1) + ". " + item.getProgramName());
                    if (item.GetIsProgramScramble() == 1) {
                        viewHolder.ivScramble.setVisibility(0);
                    } else {
                        viewHolder.ivScramble.setVisibility(8);
                    }
                    if (item.getLockMark() != 0) {
                        viewHolder.ivLock.setVisibility(0);
                    } else {
                        viewHolder.ivLock.setVisibility(8);
                    }
                }
            } else if (GMScreenGlobalInfo.playType == 1) {
                viewHolder.ivScramble.setVisibility(8);
                viewHolder.ivLock.setVisibility(8);
                viewHolder.sizeText.setVisibility(0);
                viewHolder.timeText.setVisibility(0);
                DataConvertPvrInfoModel item2 = (DataConvertPvrInfoModel) getItem(position);
                if (item2 != null) {
                    viewHolder.nameText.setText(String.valueOf(position + 1) + ". " + item2.getProgramName());
                    viewHolder.timeText.setText(item2.getmPvrTime());
                    int totalSecond = Integer.parseInt(item2.getmPvrDuration());
                    int hour = totalSecond / 3600;
                    int minute = (totalSecond % 3600) / 60;
                    int second = totalSecond % 60;
                    String duration = String.format("%02d:%02d:%02d", Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
                    viewHolder.sizeText.setText(duration);
                }
            }
            convertView.setFocusable(false);
            return convertView;
        }

        class ViewHolerChannel {
            ImageView ivLock;
            ImageView ivScramble;
            TextView nameText;
            TextView sizeText;
            TextView timeText;

            ViewHolerChannel() {
            }
        }
    }
}
