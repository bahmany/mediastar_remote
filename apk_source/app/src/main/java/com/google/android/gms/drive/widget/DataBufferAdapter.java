package com.google.android.gms.drive.widget;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.drive.internal.v;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class DataBufferAdapter<T> extends BaseAdapter {
    private final int RJ;
    private int RK;
    private final int RL;
    private final List<DataBuffer<T>> RM;
    private final LayoutInflater RN;
    private boolean RO;
    private final Context mContext;

    public DataBufferAdapter(Context context, int resource) {
        this(context, resource, 0, new ArrayList());
    }

    public DataBufferAdapter(Context context, int resource, int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList());
    }

    public DataBufferAdapter(Context context, int resource, int textViewResourceId, List<DataBuffer<T>> objects) {
        this.RO = true;
        this.mContext = context;
        this.RK = resource;
        this.RJ = resource;
        this.RL = textViewResourceId;
        this.RM = objects;
        this.RN = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public DataBufferAdapter(Context context, int resource, int textViewResourceId, DataBuffer<T>... buffers) {
        this(context, resource, textViewResourceId, Arrays.asList(buffers));
    }

    public DataBufferAdapter(Context context, int resource, List<DataBuffer<T>> objects) {
        this(context, resource, 0, objects);
    }

    public DataBufferAdapter(Context context, int resource, DataBuffer<T>... buffers) {
        this(context, resource, 0, Arrays.asList(buffers));
    }

    private View a(int i, View view, ViewGroup viewGroup, int i2) throws CursorIndexOutOfBoundsException {
        View viewInflate = view == null ? this.RN.inflate(i2, viewGroup, false) : view;
        try {
            TextView textView = this.RL == 0 ? (TextView) viewInflate : (TextView) viewInflate.findViewById(this.RL);
            T item = getItem(i);
            if (item instanceof CharSequence) {
                textView.setText((CharSequence) item);
            } else {
                textView.setText(item.toString());
            }
            return viewInflate;
        } catch (ClassCastException e) {
            v.a("DataBufferAdapter", e, "You must supply a resource ID for a TextView");
            throw new IllegalStateException("DataBufferAdapter requires the resource ID to be a TextView", e);
        }
    }

    public void append(DataBuffer<T> buffer) {
        this.RM.add(buffer);
        if (this.RO) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        Iterator<DataBuffer<T>> it = this.RM.iterator();
        while (it.hasNext()) {
            it.next().release();
        }
        this.RM.clear();
        if (this.RO) {
            notifyDataSetChanged();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int count = 0;
        Iterator<DataBuffer<T>> it = this.RM.iterator();
        while (true) {
            int i = count;
            if (!it.hasNext()) {
                return i;
            }
            count = it.next().getCount() + i;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return a(position, convertView, parent, this.RK);
    }

    @Override // android.widget.Adapter
    public T getItem(int position) throws CursorIndexOutOfBoundsException {
        int i = position;
        for (DataBuffer<T> dataBuffer : this.RM) {
            int count = dataBuffer.getCount();
            if (count > i) {
                try {
                    return dataBuffer.get(i);
                } catch (CursorIndexOutOfBoundsException e) {
                    throw new CursorIndexOutOfBoundsException(position, getCount());
                }
            }
            i -= count;
        }
        throw new CursorIndexOutOfBoundsException(position, getCount());
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        return a(position, convertView, parent, this.RJ);
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.RO = true;
    }

    public void setDropDownViewResource(int resource) {
        this.RK = resource;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.RO = notifyOnChange;
    }
}
