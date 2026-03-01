package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import mktvsmart.screen.R;
import org.videolan.vlc.MediaGroup;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.BitmapUtil;
import org.videolan.vlc.util.Strings;

/* loaded from: classes.dex */
public class VideoListAdapter extends ArrayAdapter<MediaWrapper> implements Comparator<MediaWrapper> {
    public static final int SORT_BY_LENGTH = 1;
    public static final int SORT_BY_TITLE = 0;
    public static final String TAG = "VideoListAdapter";
    private Context mContext;
    private VideoGridFragment mFragment;
    private boolean mListMode;
    private int mSortBy;
    private int mSortDirection;

    public VideoListAdapter(Context context, VideoGridFragment fragment) {
        super(context, 0);
        this.mSortDirection = 1;
        this.mSortBy = 0;
        this.mListMode = false;
        this.mContext = context;
        this.mFragment = fragment;
    }

    public synchronized void update(MediaWrapper item) {
        int position = getPosition(item);
        if (position != -1) {
            remove(item);
            insert(item, position);
        }
    }

    public void setTimes(HashMap<String, Long> times) {
        for (int i = 0; i < getCount(); i++) {
            MediaWrapper media = getItem(i);
            Long time = times.get(media.getLocation());
            if (time != null) {
                media.setTime(time.longValue());
            }
        }
    }

    public void sortBy(int sortby) {
        switch (sortby) {
            case 0:
                if (this.mSortBy == 0) {
                    this.mSortDirection *= -1;
                    break;
                } else {
                    this.mSortBy = 0;
                    this.mSortDirection = 1;
                    break;
                }
            case 1:
                if (this.mSortBy == 1) {
                    this.mSortDirection *= -1;
                    break;
                } else {
                    this.mSortBy = 1;
                    this.mSortDirection *= 1;
                    break;
                }
            default:
                this.mSortBy = 0;
                this.mSortDirection = 1;
                break;
        }
        sort();
    }

    public void sort() {
        super.sort(this);
    }

    @Override // java.util.Comparator
    public int compare(MediaWrapper item1, MediaWrapper item2) {
        int compare = 0;
        switch (this.mSortBy) {
            case 0:
                compare = item1.getTitle().toUpperCase(Locale.ENGLISH).compareTo(item2.getTitle().toUpperCase(Locale.ENGLISH));
                break;
            case 1:
                compare = Long.valueOf(item1.getLength()).compareTo(Long.valueOf(item2.getLength()));
                break;
        }
        return this.mSortDirection * compare;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(final int position, View convertView, ViewGroup parent) throws Resources.NotFoundException {
        ViewHolder holder;
        View v = convertView;
        if (v == null || ((ViewHolder) v.getTag()).listmode != this.mListMode) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            if (!this.mListMode) {
                v = inflater.inflate(R.layout.video_grid_item, parent, false);
            } else {
                v = inflater.inflate(R.layout.video_list_item, parent, false);
            }
            holder = new ViewHolder();
            holder.layout = v.findViewById(R.id.layout_item);
            holder.thumbnail = (ImageView) v.findViewById(R.id.ml_item_thumbnail);
            holder.title = (TextView) v.findViewById(R.id.ml_item_title);
            holder.subtitle = (TextView) v.findViewById(R.id.ml_item_subtitle);
            holder.progress = (ProgressBar) v.findViewById(R.id.ml_item_progress);
            holder.more = (ImageView) v.findViewById(R.id.item_more);
            holder.listmode = this.mListMode;
            v.setTag(holder);
            holder.more.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.filebroswer.VideoListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    if (VideoListAdapter.this.mFragment != null) {
                        VideoListAdapter.this.mFragment.onContextPopupMenu(v2, position);
                    }
                }
            });
            v.setLayoutParams(new AbsListView.LayoutParams(v.getLayoutParams().width, v.getLayoutParams().height));
        } else {
            holder = (ViewHolder) v.getTag();
        }
        MediaWrapper media = getItem(position);
        Bitmap thumbnail = BitmapUtil.getPictureFromCache(media);
        if (thumbnail == null) {
            holder.thumbnail.setImageResource(R.drawable.video_icon);
        } else if (thumbnail.getWidth() == 1 && thumbnail.getHeight() == 1) {
            holder.thumbnail.setImageResource(R.drawable.video_icon);
        } else {
            holder.thumbnail.setImageBitmap(thumbnail);
        }
        ColorStateList titleColor = v.getResources().getColorStateList(R.color.list_title);
        holder.title.setTextColor(titleColor);
        if (media instanceof MediaGroup) {
            fillGroupView(holder, media);
        } else {
            fillVideoView(holder, media);
        }
        return v;
    }

    private void fillGroupView(ViewHolder holder, MediaWrapper media) throws Resources.NotFoundException {
        MediaGroup mediaGroup = (MediaGroup) media;
        int size = mediaGroup.size();
        String text = getContext().getResources().getQuantityString(R.plurals.videos_quantity, size, Integer.valueOf(size));
        holder.subtitle.setText(text);
        holder.title.setText(String.valueOf(media.getTitle()) + "…");
        holder.more.setVisibility(4);
        holder.progress.setVisibility(8);
    }

    private void fillVideoView(ViewHolder holder, MediaWrapper media) {
        String text;
        long lastTime = media.getTime();
        if (lastTime > 0) {
            text = String.format("%s / %s", Strings.millisToText(lastTime), Strings.millisToText(media.getLength()));
            holder.progress.setVisibility(0);
            holder.progress.setMax((int) (media.getLength() / 1000));
            holder.progress.setProgress((int) (lastTime / 1000));
        } else {
            text = Strings.millisToText(media.getLength());
            holder.progress.setVisibility(8);
        }
        if (media.getWidth() > 0 && media.getHeight() > 0) {
            text = String.valueOf(text) + String.format(" - %dx%d", Integer.valueOf(media.getWidth()), Integer.valueOf(media.getHeight()));
        }
        holder.subtitle.setText(text);
        holder.title.setText(media.getTitle());
        holder.more.setVisibility(0);
    }

    static class ViewHolder {
        View layout;
        boolean listmode;
        ImageView more;
        ProgressBar progress;
        TextView subtitle;
        ImageView thumbnail;
        TextView title;

        ViewHolder() {
        }
    }

    public void setListMode(boolean value) {
        this.mListMode = value;
    }

    public boolean isListMode() {
        return this.mListMode;
    }
}
