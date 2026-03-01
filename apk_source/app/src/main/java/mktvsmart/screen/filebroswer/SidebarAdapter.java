package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class SidebarAdapter extends BaseAdapter {
    static final List<SidebarEntry> entries;
    public static final List<String> sidebarFragments;
    private Context mContext;
    private String mCurrentFragmentId;
    private HashMap<String, Fragment> mFragments = new HashMap<>(entries.size());
    private LayoutInflater mInflater;

    public static class SidebarEntry {
        int drawable;
        int drawable_h;
        String id;
        String name;

        public SidebarEntry(String _id, String _name, int drawable, int drawable_h) {
            this.id = _id;
            this.name = _name;
            this.drawable = drawable;
            this.drawable_h = drawable_h;
        }
    }

    static {
        SidebarEntry[] entries2 = {new SidebarEntry(MultiSettingActivity.VIDEO_STATUS_KEY, "Record", R.drawable.ic_record_n, R.drawable.ic_record_h), new SidebarEntry("folder", "Folder", R.drawable.ic_folder_n, R.drawable.ic_folder_h)};
        entries = Arrays.asList(entries2);
        sidebarFragments = new ArrayList();
        for (SidebarEntry e : entries2) {
            sidebarFragments.add(e.id);
        }
    }

    public SidebarAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return sidebarFragments.size();
    }

    @Override // android.widget.Adapter
    public SidebarEntry getItem(int position) {
        return entries.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) throws Resources.NotFoundException {
        SidebarEntry sidebarEntry = entries.get(position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.sidebar_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(sidebarEntry.name);
        if (this.mCurrentFragmentId != null && this.mCurrentFragmentId.equals(sidebarEntry.id)) {
            Drawable img = this.mContext.getResources().getDrawable(sidebarEntry.drawable_h);
            if (img != null) {
                int dp_36 = convertDpToPx(36, this.mContext);
                img.setBounds(0, 0, dp_36, dp_36);
                textView.setCompoundDrawables(img, null, null, null);
            }
            textView.setTypeface(null, 1);
            textView.setTextColor(-1);
            textView.setBackgroundResource(R.drawable.siderbar_bg_h);
        } else {
            Drawable img2 = this.mContext.getResources().getDrawable(sidebarEntry.drawable);
            if (img2 != null) {
                int dp_362 = convertDpToPx(36, this.mContext);
                img2.setBounds(0, 0, dp_362, dp_362);
                textView.setCompoundDrawables(img2, null, null, null);
            }
            textView.setTypeface(null, 0);
            textView.setTextColor(Color.parseColor("#c1c1c1"));
            textView.setBackgroundResource(R.drawable.siderbar_bg_n);
        }
        return convertView;
    }

    public static int convertDpToPx(int dp, Context context) {
        return Math.round(TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics()));
    }

    public Fragment fetchFragment(int pos) {
        return fetchFragment(entries.get(pos).id);
    }

    public Fragment fetchFragment(String id) {
        Fragment f;
        String prevFragmentId = this.mCurrentFragmentId;
        setCurrentFragment(id);
        if (this.mFragments.containsKey(id) && this.mFragments.get(id) != null) {
            return this.mFragments.get(id);
        }
        if (id.equals(MultiSettingActivity.VIDEO_STATUS_KEY)) {
            f = new VideoGridFragment();
        } else if (id.endsWith("folder")) {
            f = new FolderListFragment();
        } else {
            this.mCurrentFragmentId = prevFragmentId;
            return null;
        }
        f.setRetainInstance(true);
        this.mFragments.put(id, f);
        return f;
    }

    private void setCurrentFragment(String id) {
        this.mCurrentFragmentId = id;
        notifyDataSetChanged();
    }

    public void setCurrentFragment(int pos) {
        setCurrentFragment(entries.get(pos).id);
    }

    public void restoreFragment(String id, Fragment f) {
        if (f != null) {
            this.mFragments.put(id, f);
            setCurrentFragment(id);
        }
    }
}
