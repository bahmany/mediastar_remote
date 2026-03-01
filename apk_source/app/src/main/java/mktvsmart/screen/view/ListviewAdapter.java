package mktvsmart.screen.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class ListviewAdapter extends BaseAdapter {
    private int backgroundResource;
    private int commonTextColor;
    private Context context;
    private int curPos;
    private int highlightTextColor;
    private ArrayList<String> list;

    private class ViewHolder {
        public LinearLayout spinnerBack;
        public TextView textView;

        private ViewHolder() {
        }

        /* synthetic */ ViewHolder(ListviewAdapter listviewAdapter, ViewHolder viewHolder) {
            this();
        }
    }

    public ListviewAdapter(Context context, ArrayList<String> list) {
        this(context, list, context.getResources().getColor(R.color.red), context.getResources().getColor(R.color.black), R.drawable.dialog_list_view_bg);
        this.context = context;
        this.list = list;
    }

    public ListviewAdapter(Context context, ArrayList<String> list, int backgroundResource) {
        this(context, list, context.getResources().getColor(R.color.red), context.getResources().getColor(R.color.black), backgroundResource);
        this.context = context;
        this.list = list;
    }

    public ListviewAdapter(Context context, ArrayList<String> list, int highlightTextColor, int commonTextColor) {
        this(context, list, highlightTextColor, commonTextColor, R.drawable.dialog_list_view_bg);
        this.context = context;
        this.list = list;
    }

    public ListviewAdapter(Context context, ArrayList<String> list, int highlightTextColor, int commonTextColor, int backgroundResource) {
        this.curPos = -1;
        this.context = context;
        this.list = list;
        this.highlightTextColor = highlightTextColor;
        this.commonTextColor = commonTextColor;
        this.backgroundResource = backgroundResource;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int arg0) {
        return Integer.valueOf(arg0);
    }

    @Override // android.widget.Adapter
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setCurPos(int position) {
        this.curPos = position;
    }

    public void setTextColor(int color) {
        this.commonTextColor = color;
    }

    public void setHighlightTextColor(int color) {
        this.highlightTextColor = color;
    }

    public void setItemBackgroundResource(int res) {
        this.backgroundResource = res;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ViewHolder viewHolder2 = null;
        if (convertView == null && this.list.size() != 0) {
            viewHolder = new ViewHolder(this, viewHolder2);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.spinner_item, (ViewGroup) null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemText);
            viewHolder.spinnerBack = (LinearLayout) convertView.findViewById(R.id.spinner_back);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(this.list.get(position));
        if (position == this.curPos) {
            viewHolder.textView.setTextColor(this.highlightTextColor);
        } else {
            viewHolder.textView.setTextColor(this.commonTextColor);
            viewHolder.spinnerBack.setBackgroundResource(this.backgroundResource);
        }
        return convertView;
    }
}
