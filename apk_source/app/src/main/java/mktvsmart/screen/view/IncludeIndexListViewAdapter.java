package mktvsmart.screen.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;

/* loaded from: classes.dex */
public class IncludeIndexListViewAdapter extends BaseAdapter {
    private Context context;
    private int curPos = -1;
    boolean flag;
    private List<DataConvertChannelModel> list;

    private class ViewHolder {
        public TextView indexView;
        public LinearLayout linear_layout_index;
        public TextView progNameView;
        public RelativeLayout relative_layout_name;
        public ImageView scrambleIcon;

        private ViewHolder() {
        }

        /* synthetic */ ViewHolder(IncludeIndexListViewAdapter includeIndexListViewAdapter, ViewHolder viewHolder) {
            this();
        }
    }

    public IncludeIndexListViewAdapter(Context context, List<DataConvertChannelModel> curTypeChannelListModelss, boolean flag) {
        this.context = context;
        this.list = curTypeChannelListModelss;
        this.flag = flag;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void setCurPos(int position) {
        this.curPos = position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ViewHolder viewHolder2 = null;
        if (convertView == null && this.list.size() != 0) {
            viewHolder = new ViewHolder(this, viewHolder2);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.include_index_item_layout, (ViewGroup) null);
            viewHolder.linear_layout_index = (LinearLayout) convertView.findViewById(R.id.linear_layout_index);
            viewHolder.indexView = (TextView) convertView.findViewById(R.id.index);
            viewHolder.relative_layout_name = (RelativeLayout) convertView.findViewById(R.id.relative_layout_name);
            viewHolder.progNameView = (TextView) convertView.findViewById(R.id.itemText);
            viewHolder.scrambleIcon = (ImageView) convertView.findViewById(R.id.scramble_icon);
            if (this.flag) {
                viewHolder.scrambleIcon.setVisibility(0);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.indexView.setText(new StringBuilder(String.valueOf(position + 1)).toString());
        viewHolder.progNameView.setText(this.list.get(position).getProgramName());
        if (this.list.get(position).GetIsProgramScramble() == 1) {
            viewHolder.scrambleIcon.setBackgroundResource(R.drawable.scramble_icon);
        } else {
            viewHolder.scrambleIcon.setBackgroundResource(R.drawable.scramble_icon_gray);
        }
        if (position == this.curPos) {
            viewHolder.linear_layout_index.setBackgroundResource(R.drawable.list_item_index_focus);
            viewHolder.relative_layout_name.setBackgroundResource(R.drawable.list_item_focus);
        } else {
            viewHolder.linear_layout_index.setBackgroundResource(R.drawable.disp_index);
            viewHolder.relative_layout_name.setBackgroundResource(R.drawable.disp_channel);
        }
        return convertView;
    }
}
